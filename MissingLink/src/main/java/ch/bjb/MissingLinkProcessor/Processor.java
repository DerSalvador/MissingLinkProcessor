package ch.bjb.MissingLinkProcessor;

import ch.bjb.MissingLinkProcessor.configuration.MissingLinkProcessorModell;
import ch.bjb.MissingLinkProcessor.dictionaryprocessor.XLDeployDictionaryProcessor;
import ch.bjb.MissingLinkProcessor.fileprocessors.*;
import ch.bjb.MissingLinkProcessor.framework.ARAWebserviceWrapper;
import ch.bjb.MissingLinkProcessor.framework.XLDeployDeployablePackageUploader;
import ch.bjb.MissingLinkProcessor.model.ConfigurationSet;
import ch.bjb.MissingLinkProcessor.model.Deployable;
import ch.bjb.MissingLinkProcessor.model.DeployableFile;
import ch.bjb.MissingLinkProcessor.model.VendorDelivery;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.*;
import org.apache.commons.cli.*;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;



/**
 * Main class of the application.
 * 
 *
 * @author u37792
 * @version  $Revision: #25 $, $Date: 2016/07/21 $
 */
public class Processor 
{
	public static final String VERSION = "0.0.9-SNAPSHOT";
	public ARAWebserviceWrapper ara = null;
	private static String INPUT_ZIP_FILE = null;
	private static String OUTPUT_FOLDER = null;
	private static String CONFIG_FILE = null;

	private static String DEPLOYABLE_PACKAGE_FILE_EXTENSION = ".dar";
	private static boolean CLEANUP_ON_EXIT = false;
	private static boolean UPLOAD_TO_DEPLOYMENTOOL = false;
	
	private static final String FILE_EXTENSION_PROPERTY = ".properties";
	private static final String FILE_EXTENSION_XML 		= ".xml";
	private static final String FILE_EXTENSION_YML 		= ".yml";
	private static final String FILE_EXTENSION_SHELL	= ".sh";
	private static final String FILE_EXTENSION_CONF		= ".conf";

	LocalPropertiesLocator localPropertiesLocator;
	
	Charset charset;
	
	MissingLinkProcessorModell MissingLinkProcessorModell;
	
	List<String> fileList;

	// static Logger log = LogManager.getRootLogger();
	static Logger log = LogManager.getLogger(Processor.class);


	/**
	 * main method of the application, when a user starts the program, this method is called.
	 * The method parses the command line arguments, constructs an instance of Processor, 
	 * extracts the zip file (Vendor delivery) and starts the processing.  	
	 * @param args
	 */
	public static void main(String[] args) throws XLDeployUploadPackageException {
		try 
		{
		    // parse the command line arguments
		    CommandLine line = (new DefaultParser()).parse( createCLIProcessorOptions(), args );

		    if( line.hasOption( "help" ) ) 
		    {
		    	displayHelp(createCLIProcessorOptions());
		    	System.exit(0);
		    }

		    if( line.hasOption( "version" ) ) 
		    {
		    	System.out.println(VERSION);
		    	System.exit(0);
		    }

		    if( !line.hasOption( "inputfile" ) ) 
		    {
		    	displayHelp(createCLIProcessorOptions());
		    	System.exit(0);
		    }
		    else
		    {
		        INPUT_ZIP_FILE =  line.getOptionValue( "inputfile" ) ;
		    }
		    
		    if( !line.hasOption( "outputDir" ) ) 
		    {
		    	displayHelp(createCLIProcessorOptions());
		    	System.exit(0);
		    }
		    else
		    {
		        OUTPUT_FOLDER =  line.getOptionValue( "outputDir" ) ;
		    }

		    if( !line.hasOption( "config" ) ) 
		    {
		    	displayHelp(createCLIProcessorOptions());
		    	System.exit(0);
		    }
		    else
		    {
		        CONFIG_FILE =  line.getOptionValue( "config" ) ;
		    }

		    if( !line.hasOption( "upload" ) ) 
		    {
		    	displayHelp(createCLIProcessorOptions());
		    	System.exit(0);
		    }
		    else
		    {
		        UPLOAD_TO_DEPLOYMENTOOL =  Boolean.parseBoolean(line.getOptionValue( "upload" )) ;
		    }

		    if( line.hasOption( "format" ) ) 
		    {
		    	DEPLOYABLE_PACKAGE_FILE_EXTENSION = line.getOptionValue("format");
		    }

		    if( line.hasOption( "tabularasa" ) ) 
		    {
		    	CLEANUP_ON_EXIT = Boolean.getBoolean(line.getOptionValue("tabularasa"));
		    }

		}
		catch( ParseException exp ) 
		{
		    log.error( "Unexpected exception:" + exp.getMessage() );
		}		
		
		Processor processor = new Processor();

		String path = processor.unzipFile(INPUT_ZIP_FILE, OUTPUT_FOLDER);
        log.info("Unmarshalling delivery.xml from path: " + path);
		VendorDelivery delivery = processor.readDeliveryXML(path);
		
		processor.setLocalPropertiesLocator(delivery.getApplication());

		processor.generateDeployablePackages(delivery);
	}
	
	/**
	 * the method generates the options available as command line arguments
	 * @return org.apache.commons.cli.Options
	 */
	private static Options createCLIProcessorOptions()
	{

		// create the Options
		Options options = new Options();
		options.addOption( "i", "inputfile", true, "the full qualified path of the vendor delivery (a .zip File)" );
		options.addOption( "o", "outputDir", true, "the full qualified path of the output directory for deployable packages. The outputDir must contain a trailing /" );
		options.addOption( "c", "config", true, "the program loads its configuration from the provided yaml file." );
		options.addOption( "u", "upload", true, "if set to true, the program uploads the deployable packages to the configured deployment tool." );
		options.addOption( "f", "format", false, "the target format for deployable packages, currently only the .dar file format for XLDeploy is supported" );
		options.addOption( "t", "tabularasa", false, "if set to true, the MissingLinkProcessor cleans up the target directory after processing" );

		Option help = new Option( "help", "print this message" );
		options.addOption(help);

		Option version = new Option( "version", "displays the version of the program" );
		options.addOption(version);
		
		return options;
	}
	
	/**
	 * displays the options to the user when the user uses the --help command line key
	 * @param options
	 */
	private static void displayHelp(Options options)
	{
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "MissingLinkProcessor", options );		
	}
	
	/**
	 * main class of the application
	 * here, the configuration {@link} is parsed and transferred into an internal object structure
	 * Creates a new <code>Processor</code> instance.
	 *
	 */
	public Processor() 
	{
		super();
		
		//Load configuration from config file
		java.io.File yamlFile = new File(CONFIG_FILE);
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		try 
		{
	        MissingLinkProcessorModell = mapper.readValue(yamlFile, MissingLinkProcessorModell.class);
			ara = new ARAWebserviceWrapper(MissingLinkProcessorModell);

		}
		catch (Exception e) 
		{
	        log.fatal("unable to process config file, exiting",e);
	        System.exit(1);
        }
		charset =  Charset.forName(MissingLinkProcessorModell.getCharset());
	}
	
	/**
	 * sets the {@link LocalPropertiesLocator} depending on the name of the application
	 * current pattern is:  deliverylocationlookup-applicationName.xml
	 * @param applicationName
	 */
	private void setLocalPropertiesLocator(String applicationName)
	{
		localPropertiesLocator = new LocalPropertiesLocator(applicationName);
	}
	
	/**
	 * unzips a delivery into the outputfolder. The outputfolder is defined by the user with the -o option.
	 * The procedure returns the path to the delivery.xml as result.
	 * @param filePath
	 * @param outputFolder
	 * @return
	 */
	public String unzipFile(String filePath, String outputFolder) 
	{
		String pathToDeliveryXML = null;
		java.io.File archive = new java.io.File(filePath);
		java.io.File destination = new java.io.File(outputFolder);

		URL url;
		log.info("unzipping file " + filePath + " to  folder " + outputFolder);
		if(filePath.startsWith("http"))
		{
			try
			{
				
				url = new URL(filePath);
				String urlfile = url.getFile();
				String localLocation = outputFolder + "/download/" + urlfile;
				archive = new java.io.File(localLocation);
				log.info("Storing file to localfolder " + localLocation);
				FileUtils.copyURLToFile(url, new File(localLocation));
			}
			catch(Exception e)
			{
				log.fatal("unable to download package from " + filePath,e) ;
				System.exit(-1);
			}
		}

		Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.ZIP);
		try 
		{
	        archiver.extract(archive, destination);
        }
		catch (IOException e) 
		{
			log.fatal("unable to extract delivery, aborting",e);
	        System.exit(1);
        }
		
		File deliveryXML = new java.io.File(outputFolder + java.io.File.separator + "delivery.xml");
		if(deliveryXML.exists())
		{
			pathToDeliveryXML = deliveryXML.getAbsolutePath();
		}
		else
		{
			log.fatal("unable to find delivery.xml at " + outputFolder + File.separator + "delivery.xml, aborting");
	        System.exit(1);
		}
		return pathToDeliveryXML;
	}
	/**
	 * reads the delivery.xml file, marshalls it to an internal structure and returns it to the calling procedure. 
	 * @param path
	 * @return
	 */
	private VendorDelivery readDeliveryXML(String path) 
	{
		log.info("trying to read file " + path);
		Path f = new java.io.File(path).toPath();

		
		try (BufferedReader reader = Files.newBufferedReader(f, charset)) 
		{
			String line = null;
			while ((line = reader.readLine()) != null) 
			{
				log.debug(line);
			}
		} 
		catch (IOException x) 
		{
			log.error("IOException: ", x);
		}
		
		try 
		{

			JAXBContext jaxbContext = JAXBContext.newInstance(VendorDelivery.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			VendorDelivery vendorDelivery = (VendorDelivery) jaxbUnmarshaller.unmarshal(new java.io.File(path));
			return vendorDelivery;
		} 
		catch (JAXBException e) 
		{
			log.fatal("unable to unmarshall delivery.xml file, exiting",e);
			System.exit(1);
		}
		return null;

	}

	/**
	 * TODO check if the comments are still valid
	 * identify file type
	 * check what environments exists
	 * identify tokens
	 * check what tokens are security relevant (e.g. passwords)
	 * tokenize file
	 * saven tokenized file to target location
	 * create new version of set of tokens 
	 * add new tokens to new version
	 * 1. file exists, file does not need to be renamed, file is propertyfile
	 * 2. file does not exist, file does not need to be renamed, file is propertyfile
	 * 3. file exists, file needs to be renamed, file is propertyfile
	 * 4. file does not exists, file needs to be renamed, file is propertyfile
	 * 5. file exists, file does not need to be renamed, file is xmlfile
	 * 6. file does not exist, file does not need to be renamed, file is xmlfile
	 * 7. file exists, file needs to be renamed, file is xmlfile
	 * 8. file does not exists, file needs to be renamed, file is xmlfile
	 * 9. file exists, file does not need to be renamed, file is shellscript
	 * 10. file does not exist, file does not need to be renamed, file is shellscript
	 * 11. file exists, file needs to be renamed, file is shellscript
	 * 12. file does not exists, file needs to be renamed, file is shellscript
	 * 
	 */
	private void processConfigurationFiles(String workingDir, Deployable deployable, ConfigurationSet configurationSet)
	{
		
		for(DeployableFile file: configurationSet.getFiles())
		{
			String extension = file.getName().substring(file.getName().lastIndexOf("."),file.getName().length());
			
			if(extension.equalsIgnoreCase(FILE_EXTENSION_PROPERTY))
			{
				PropertyFileProcessor.process(file, workingDir, OUTPUT_FOLDER, charset);
			}
			else if(extension.equalsIgnoreCase(FILE_EXTENSION_XML))
			{
				// log.error("processing of " + FILE_EXTENSION_XML + " is not yet implemented");
				XMLFileProcessor.process(file, workingDir, OUTPUT_FOLDER, charset);
			}
			else if(extension.equalsIgnoreCase(FILE_EXTENSION_YML))
			{
				YMLFileProcessor.process(file, workingDir, OUTPUT_FOLDER, charset);
			}
			else if(extension.equalsIgnoreCase(FILE_EXTENSION_SHELL))
			{
				SHFileProcessor.process(file, workingDir, OUTPUT_FOLDER, charset);
			}
			else if(extension.equalsIgnoreCase(FILE_EXTENSION_CONF))
			{
				ConfFileProcessor.process(file, workingDir, OUTPUT_FOLDER, charset);
			}
			else
			{
				log.error("processing of " + extension + " is not yet implemented");
			}
		}
	}

	/**
	 * generates the XLDeploy manifest for a group of (one or more) deployables 
	 * @param application: the application name as per delivery.xml
	 * @param version: the application version as per delivery.xml
	 * @param workingdir: the directory the user has specified via -o commandline
	 * @param tag: the tag as per delivery.xml
	 * @param ListOfdeployablesPerGroup the list of deployables that share the same groupid
	 * @param configurationSetPerDeployable configuration set per deployable (as per delivery.xml)
	 */
	private void generateManifest(String application, String version, String workingdir, String tag, List<Deployable> ListOfdeployablesPerGroup, List<ConfigurationSet> configurationSetPerDeployable)
	{
		log.info("generating manifest for a list of deployables");
		Configuration cfg = new Configuration();
		cfg.setIncompatibleImprovements(new Version(2, 3, 20));
		cfg.setDefaultEncoding("UTF-8");
		cfg.setLocale(Locale.US);
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

		try 
		{
			ClassTemplateLoader loader = new ClassTemplateLoader(getClass(),"/");
			cfg.setTemplateLoader(loader);
			cfg.setObjectWrapper(new DefaultObjectWrapper());
			Template template = cfg.getTemplate("templates/deployit-manifest.xml.ftl");
			Map input = new HashMap();
			input.put("deployables", ListOfdeployablesPerGroup);
			input.put("configurationSetPerDeployable", configurationSetPerDeployable);
			
			input.put("tag", tag);


			input.put("version", version);
			input.put("UUID", new UUID(10,1000));

			// Dumping Objects
			ListOfdeployablesPerGroup.iterator().forEachRemaining( d -> { log.info(d); });

			//input.put("APPLICATION_PATH", constants.get(application + "_APPLICATION_PATH") + generateDeploymentApplicationPackageNameFromDeployables(deployables));
			String appPath = localPropertiesLocator.getApplicationPathForARATool() + generateDeploymentApplicationPackageNameFromDeployables(ListOfdeployablesPerGroup);
			List<String> res = ara.callRESTApiDELETEApplication(ARAWebserviceWrapper.XLD_CI_APPLICATIONS, appPath, version);
			log.info("Response from ARA Tool from DELETE App: " + appPath + " = " + res.toString());
			// Repository entity Applications/DerSalvador/DER/DerSalvador-der-web/1.0 is still referenced by Applications/DerSalvador/DER/CompositePackages/1.0
			Optional.ofNullable(res.toString()).filter(s -> s.toUpperCase().contains("SUCCESSFUL")).orElseThrow(new Supplier<RuntimeException>() {
				@Override
				public RuntimeException get() {
					return new RuntimeException(res.toString());
				}
			});
			input.put("APPLICATION_PATH", appPath);
			

			//Writer consoleWriter = new OutputStreamWriter(System.out);
			//template.process(input, consoleWriter);
			//consoleWriter.close();

			Writer fileWriter = new FileWriter(new File(workingdir + File.separatorChar + "deployit-manifest.xml"));
			template.process(input, fileWriter);
			fileWriter.close();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("unable to generate manifest",e);
		}

		log.info("generating manifest for deployables to " + workingdir + File.separatorChar + "deployit-manifest.xml" + "... done");
		
	}
	
	/**
	 * generates the name of the deployable visible in XLDeploy
	 * in case more that one Deployable is provided (as part of a group, then the group id is used as name 
	 * @param deployables
	 * @return
	 */
	private String generateDeploymentApplicationPackageNameFromDeployables(List<Deployable> deployables)
	{
		//String packageName="";
		
		if(deployables.size() > 1)
		{
			String tmp = deployables.get(0).getGroupid();
			return extractApplicationName(tmp);
		}
		else
		{
			String tmp = deployables.get(0).getName();
			return extractApplicationName(tmp);

		}
		
		
	}

	private String extractApplicationName(String tmp) {
		if (!tmp.contains("."))
            return tmp;
        else
            return tmp.substring(tmp.lastIndexOf(".") + 1,tmp.length());
	}


	/**
	 * The tag of a deployable is the last part of the name of a deployable in lowercase
	 * @param deployable
	 * @return
	 */
	private String getTag(Deployable deployable) 
	{
		if(deployable.getTag() != null && deployable.getTag().trim().length()>0)
			return deployable.getTag();
		
		return getLastPartofDeployableName(deployable,true);
		/*
		if (deployable.getName().contains("."))
			return deployable.getName().substring(deployable.getName().lastIndexOf(".") + 1, deployable.getName().length()).toLowerCase();
		else
			return deployable.getName().toLowerCase();
			*/
	}
	
	/**
	 * Helpermethod to get the last part (after a possible .) of the name of a deployable  
	 * @param deployable
	 * @param lowercase
	 * @return
	 */
	
	private String getLastPartofDeployableName(Deployable deployable,boolean lowercase)
	{
		String tmp = "";
		if (deployable.getName().contains("."))
			tmp = deployable.getName().substring(deployable.getName().lastIndexOf(".") + 1, deployable.getName().length());
		else
			tmp = deployable.getName();
		
		if(lowercase)
			return tmp.toLowerCase();
		else
			return tmp;
		
	}

	/**
	 * helpermethod, copies all files from a deliverable to the working directory 
	 * @param workingdir: as user specified it with the -o directive
	 * @param deployable : as specified by delivery.xml
	 */
	private void performFileCopy(String workingdir, Deployable deployable) 
	{
		String directoryToBeCreated = workingdir + File.separatorChar + deployable.getName();
		String filename = deployable.getFile().substring(deployable.getFile().lastIndexOf("/")+1,deployable.getFile().length());

		String extension = FilenameUtils.getExtension(deployable.getFile());

		try 
		{
			boolean success = (new java.io.File(directoryToBeCreated)).mkdirs();

			if(deployable.getType().equalsIgnoreCase("file.Folder") && extension.equalsIgnoreCase("gz") && MissingLinkProcessorModell.isRepackTGZ())
			{
				log.info("need to repack " + deployable.getFile() + " to ZIP File");
				repack(new File(OUTPUT_FOLDER + File.separatorChar + deployable.getFile()),new File(workingdir + File.separatorChar + deployable.getName()),filename);
			}
			else
			{
				log.info("performFileCopy " + deployable.getFile() + " to " + workingdir);
				Files.copy(new File(OUTPUT_FOLDER + File.separatorChar + deployable.getFile()).toPath(), 
							new File(workingdir + File.separatorChar + deployable.getName() + File.separatorChar + filename).toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING,
			                java.nio.file.StandardCopyOption.COPY_ATTRIBUTES, java.nio.file.LinkOption.NOFOLLOW_LINKS);
			}
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			log.error("unable to copy file",e);
		}

	}
	
	/**
	 * XLDeploy does currently not recognize .tar.gz files as archives to be unpacked automatically on the target server
	 * We are repacking (if the configuration repackTGZ is true in the MissingLinkProcessors configuration file ) tgz files to zip files. 
	 * @param archiveFile
	 * @param destDir
	 * @param targetArchiveName
	 * @throws IOException
	 */
	private void repack(File archiveFile, File destDir,String targetArchiveName) throws IOException
	{
		
		String tempdir = System.getProperty("java.io.tmpdir");

		if ( !(tempdir.endsWith("/") || tempdir.endsWith("\\")) )
		   tempdir = tempdir + System.getProperty("file.separator");
		
		File tmpDir = new File(tempdir + "MissingLinkProcessor");
		tmpDir.mkdirs();
		
		Archiver archiver = ArchiverFactory.createArchiver("tar", "gz");
		archiver.extract(archiveFile, tmpDir);
		
		archiver = ArchiverFactory.createArchiver(ArchiveFormat.ZIP);
		File archive = archiver.create(targetArchiveName, destDir, tmpDir);
		
		FileUtils.deleteDirectory(tmpDir);
	}
	/**
	 * zips all files found in the working dir to create one (with the current implementation) .dar file
	 * currently not in use
	 * @param targetFile
	 * @param workingdir
	 */
	private void createDeployablePackage(String targetFile, String workingdir) 
	{
		log.info("creating deployable package " + targetFile);
		Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		// locate file system by using the syntax
		// defined in java.net.JarURLConnection
		URI uri = URI.create("jar:file:" + File.separatorChar + targetFile);

		try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) 
		{
			File f = new File(workingdir);
			for (File aFile : f.listFiles()) {
				log.info("processing" + aFile.getName());
				Path externalTxtFile = Paths.get(aFile.getPath());
				Path pathInZipfile = zipfs.getPath(aFile.getParent().substring(workingdir.length(), aFile.getPath().length()));
				// copy a file into the zip file
				Files.copy(externalTxtFile, pathInZipfile, StandardCopyOption.REPLACE_EXISTING);
			}
			zipfs.close();
		}
		catch (Exception e) 
		{
			log.error("unable to create deployable package",e);
		}
	}

	/**
	 * zips all files found in the working dir to create one (with the current implementation) .dar file 
	 * @param zipFileName
	 * @param dir
	 * @throws Exception
	 */
	private static void zipDir(String zipFileName, String dir) throws Exception 
	{
	    File dirObj = new File(dir);
	    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
	    log.info("Creating : " + zipFileName);
	    addDir(dirObj, out,dir);
	    out.close();
	  }
	
	/**
	 * helper method for {@link }
	 * @param dirObj
	 * @param out
	 * @param dir
	 * @throws IOException
	 */
	  static void addDir(File dirObj, ZipOutputStream out,String dir) throws IOException 
	  {
	    File[] files = dirObj.listFiles();
	    byte[] tmpBuf = new byte[1024];

	    for (int i = 0; i < files.length; i++) 
	    {
	      if (files[i].isDirectory()) 
	      {
	        addDir(files[i], out,dir);
	        continue;
	      }
	      FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
		  // MR: not working for all cases
	      String relativePath = files[i].getAbsolutePath().substring(dir.length(),files[i].getAbsolutePath().length());
		  if (relativePath.startsWith("/")) {
			   relativePath = relativePath.replaceFirst("/","");
		  }
	      log.info(" Adding: " + files[i].getAbsolutePath() + " to " + relativePath);
	      out.putNextEntry(new ZipEntry(relativePath));
	      int len;
	      while ((len = in.read(tmpBuf)) > 0) {
	        out.write(tmpBuf, 0, len);
	      }
	      out.closeEntry();
	      in.close();
	    }
	  }
	  
	  /**
	   * creates a list of configurationsets for a deployable.
	   * @param delivery: representation of delivery.xml
	   * @param deployable representation of deployable within a delivery.xml
	   * @return
	   */
	  private List<ConfigurationSet> getConfigurationSetForDeployable(VendorDelivery delivery, Deployable deployable)
	  {
		  List<ConfigurationSet> returnSet = new ArrayList <ConfigurationSet>();
			if (deployable.getConfigurationset() != null && !deployable.getConfigurationset().isEmpty()) 
			{
				log.info("looking up ConfigurationSets for " + deployable.getName());
				for(Integer configurationsSetId:deployable.getConfigurationset())
				{
					log.info("looking up configurationset with id " + configurationsSetId);
					for(ConfigurationSet configurationSet: delivery.getConfigurations())
					{
						if(configurationSet.getId() == configurationsSetId)
						{
							log.info("ConfigurationSet found" + configurationSet);
							returnSet.add(configurationSet);
							
						}
					}
				}
			}
			return returnSet;
	  }
	  
	/**
	 * Main method .... this method calls all other methods in the class to transfor a vendor delivery into on or more deployable packages
	 * 1. group deployable items into deployable packages (deployment group)
	 * 2. get all the configurationssets per deployable
	 * 3. copy over all deployable items and configuration files into a separate directory per deployment group
	 * 4. process each configuration file per configuration set and replace the key value pairs
	 * 5. generate one deployit-manifest per deployment group (deployable package)
	 * 6. generate / zip the deployable package    
	 * @param delivery
	 */
	private void generateDeployablePackages(VendorDelivery delivery) throws XLDeployUploadPackageException {
		long start = System.currentTimeMillis();
		String initialdirectory = null;
		boolean oneDirOnly = true;
		int numSimpleFiles=0;
		
		
		// identify groups of deployables, and process these
		HashMap <String,List<Deployable>> deployablesPerGroup = new HashMap<>();
		for (Deployable deployable : delivery.getDeployables())
		{
			if(!deployablesPerGroup.containsKey(deployable.getGroupid()))
			{
				List <Deployable> dpl = new ArrayList<>();
				dpl.add(deployable);
				deployablesPerGroup.put(deployable.getGroupid(), dpl);
			}
			else
			{
				deployablesPerGroup.get(deployable.getGroupid()).add(deployable);
			}
			// Set Parent on each configurationSet
			if (deployable.getConfigurationset() != null)
				for ( Integer id : deployable.getConfigurationset() )
					delivery.getConfigurations().forEach( c -> { if (c.getId() == id ) c.setParent(deployable); });
		}
		
		log.info(deployablesPerGroup);

		//now, process each deployable per group, but create only one manifest file per group
		for (Map.Entry<String, List<Deployable>> entry : deployablesPerGroup.entrySet()) 
		{
		    String groupid = entry.getKey();
		    List<Deployable> ListOfdeployablesPerGroup = entry.getValue();

		    List<ConfigurationSet> configurationsPerDeployalesPerGroup = new ArrayList<>();

			String workingDir = null;
			String tag = null;
			
			for(Deployable deployable: ListOfdeployablesPerGroup)
			{
				workingDir = OUTPUT_FOLDER + File.separatorChar + delivery.getApplication() + File.separatorChar + delivery.getVersion()
				                + File.separatorChar + groupid;
				tag = deployable.getTag();
				log.info("creating working Dir " + workingDir);
				(new File(workingDir)).mkdirs();
				
				List <ConfigurationSet> configSet = getConfigurationSetForDeployable(delivery,deployable);
				
				configSet = localPropertiesLocator.getLocalPropertiesforConfigurationFile(deployable, configSet);
				
				configurationsPerDeployalesPerGroup.addAll(configSet);
				
				performFileCopy(workingDir, deployable);
				
				if(configurationsPerDeployalesPerGroup != null && configurationsPerDeployalesPerGroup.size()>0)
				{
					for(ConfigurationSet configurationSet: configurationsPerDeployalesPerGroup)
					{
						processConfigurationFiles(workingDir, deployable,configurationSet);
					}
				}

				// work on deployables, need to change the file attribute for file.Folder with tar.gz files when repackTGZ is true
				if(deployable.getType().equalsIgnoreCase("file.Folder") && FilenameUtils.getExtension(deployable.getFile()).equalsIgnoreCase("gz") && MissingLinkProcessorModell.isRepackTGZ())
				{
					log.info("need to change the name of the file to as it was repacked to zip " + deployable.getFile());
					deployable.updateFile(deployable.getFile() + ".zip");
				}


				if(MissingLinkProcessorModell.isCreateUniqeNameForFiles())
				{
					if(configurationsPerDeployalesPerGroup != null && configurationsPerDeployalesPerGroup.size()>0)
					{
						for(ConfigurationSet configurationSet: configurationsPerDeployalesPerGroup)
						{
							for (DeployableFile file:configurationSet.getFiles())
							{
								// file.setDisplayFileName(getLastPartofDeployableName(deployable,false) + "." + file.getName());
								file.setDisplayFileName(file.getName());
							}
						}
					}
				}
			
			}
			
			try 
			{
				generateManifest(delivery.getApplication(), delivery.getVersion(),workingDir,tag, ListOfdeployablesPerGroup,configurationsPerDeployalesPerGroup);
			}
			catch (Exception e) 
			{
				log.error(e);
				throw e;
			}

			String targetDeployablePackageFileName = OUTPUT_FOLDER + File.separatorChar + groupid + "-"
			                + delivery.getVersion() + DEPLOYABLE_PACKAGE_FILE_EXTENSION;

			// create DAR File
			try 
			{
	            zipDir(targetDeployablePackageFileName,workingDir);
            }
			catch (Exception e) 
			{
	            log.error("unable to zip directory", e);
            }
			
			//upload package to server
			if(UPLOAD_TO_DEPLOYMENTOOL)
			{
				XLDeployDeployablePackageUploader deployablePackageUploader = new XLDeployDeployablePackageUploader(MissingLinkProcessorModell);
				
				log.info("uploading file " + groupid + "-" + delivery.getVersion() + DEPLOYABLE_PACKAGE_FILE_EXTENSION + " - " + targetDeployablePackageFileName + " to server " + MissingLinkProcessorModell.getXldeploy().getURL() + " with user " + MissingLinkProcessorModell.getXldeploy().getUser());
				try 
				{
	                String result = deployablePackageUploader.uploadDepoyablePackage(groupid + "-"+ delivery.getVersion() + DEPLOYABLE_PACKAGE_FILE_EXTENSION, targetDeployablePackageFileName);
					if (result.indexOf("token=") > 0 )
					{
						// Create JIRA Deployment Ticket on successful upload
						createJIRADeploymentTicket(delivery, MissingLinkProcessorModell, ListOfdeployablesPerGroup);
					}
					else
					{
						log.error("Not creating JIRA Ticket because of error in deployablePackageUploader, see next lines");
						log.error(result);
						throw new XLDeployUploadPackageException(result);
					}
					log.info(result);
                }
				catch (IOException e)
                {
	                log.error("unable to upload deployable package to server",e);
                }
			}
		}
		
		
		if(MissingLinkProcessorModell.isAdjustDictionary())
		{
			
			XLDeployDictionaryProcessor dictionaryProcessor = new XLDeployDictionaryProcessor(MissingLinkProcessorModell);
			log.info(dictionaryProcessor.validateDictionaries("Environments/" + localPropertiesLocator.getApplicationPathForARATool(),delivery.getConfigurations()));
			
			//create initial dictionary and compare it with the latest one in the deployment tool (as a start XLDeploy)
			log.info("uploading properties to server");
			//XLDeployDictionaryProcessor
			//dumpInitialDictionary(delivery.getConfigurations(),OUTPUT_FOLDER);
			
		}

		log.info("vendor delivery package processed in " + (System.currentTimeMillis() - start) + " ms");
		
	}
	private void createJIRADeploymentTicket(VendorDelivery delivery, MissingLinkProcessorModell MissingLinkProcessorModell, List<Deployable> ListOfdeployablesPerGroup) throws FileNotFoundException {
		// ./createDeploymentTicket.sh Summay AppDerSalvador AT '10.10.2017 10:10' Environments/DerSalvador/DER/AT3 Applications/DerSalvador/DER/abs/13.3.0.BJB.12
		int iExitValue;
		String sCommandString;
		Optional<String> foa_dep_home = Optional.ofNullable(System.getenv("FOA_DEP_HOME"));
		String env_foa_dep_home = foa_dep_home.orElse("ENV_FOA_DEP_HOME_NOT_DEFINED").toString();
		sCommandString = env_foa_dep_home + "/bin/createDeploymentTicket.sh";
		if (!new File(sCommandString).exists())
			log.error("FileNotFound: cannot call " + sCommandString);
		org.apache.commons.exec.CommandLine oCmdLine = org.apache.commons.exec.CommandLine.parse(sCommandString);
		DefaultExecutor oDefaultExecutor = new DefaultExecutor();
		oDefaultExecutor.setWorkingDirectory(new File(env_foa_dep_home + "/bin"));
		oDefaultExecutor.setExitValue(0);
		String summary=delivery.toString();
		oCmdLine.addArgument(summary.substring(0,200));
		String application=delivery.getApplication();
		oCmdLine.addArgument(application);
		String targetEnv="AT";
		oCmdLine.addArgument(targetEnv);
		SimpleDateFormat sdf=new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.US);
		String deploymentTime=sdf.format(new Date());
		// String deploymentTime="10.10.2016 10:10";
		oCmdLine.addArgument(deploymentTime,false);
		String environment_path="/Environments/" + localPropertiesLocator.getApplicationPathForARATool();
		oCmdLine.addArgument(environment_path);
		String application_path="/Applications/"+localPropertiesLocator.getApplicationPathForARATool();
		String app_paths = "";
		if (!application_path.endsWith("/"))
			application_path += "/";
/*
		for (Deployable dpl : ListOfdeployablesPerGroup)
			if (ListOfdeployablesPerGroup.size() > 0 )
				app_paths = app_paths.concat(application_path + generateDeploymentApplicationPackageNameFromDeployables(ListOfdeployablesPerGroup) + "/" + delivery.getVersion() + "/" + "; ");
			else
				app_paths = app_paths.concat(application_path + generateDeploymentApplicationPackageNameFromDeployables(Arrays.asList(dpl)) + "/" + delivery.getVersion() + "/" + "; ");
*/
		app_paths = application_path + generateDeploymentApplicationPackageNameFromDeployables(ListOfdeployablesPerGroup) + "/" + delivery.getVersion();

		oCmdLine.addArgument(app_paths, false);
		log.info("Executing script: " + oCmdLine.toString());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
			oDefaultExecutor.setStreamHandler(streamHandler);
			iExitValue = oDefaultExecutor.execute(oCmdLine);
			log.info(outputStream.toString());
			if (iExitValue != 0) {
				log.error("Error executing command line " + oCmdLine.toString());
			}
		} catch (Exception e) {
			log.error(outputStream.toString(),e);
			e.printStackTrace();
		}
	}

	public String execToString(String command) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		org.apache.commons.exec.CommandLine commandline = org.apache.commons.exec.CommandLine.parse(command);
		DefaultExecutor exec = new DefaultExecutor();
		PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
		exec.setStreamHandler(streamHandler);
		exec.execute(commandline);
		return(outputStream.toString());
	}

}