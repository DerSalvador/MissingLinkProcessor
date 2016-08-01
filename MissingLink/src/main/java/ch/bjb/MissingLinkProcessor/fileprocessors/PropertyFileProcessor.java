package ch.bjb.MissingLinkProcessor.fileprocessors;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import ch.bjb.MissingLinkProcessor.model.DeployableFile;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.bjb.MissingLinkProcessor.model.Ci;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * This class encapsulates functionality that processes property files (.properties)
 * 
 *
 * @author u37792
 * @version  $Revision: #22 $, $Date: 2016/07/16 $
 */
public class PropertyFileProcessor  extends BaseFileProcessor
{
	
	// static Log log = LogFactory.getLog(PropertyFileProcessor.class);
	static Logger log = LogManager.getLogger(PropertyFileProcessor.class);


	public static void process(DeployableFile file,String workingDir, String OUTPUT_FOLDER, Charset charset)
	{
		HashMap<String,String> tokens = new HashMap<>();
		log.info("performFileCopy " + file.getName() + " to " + workingDir);

		//create target directory
		(new File(workingDir + File.separatorChar + file.getName())).mkdirs();
		
		
		//check if file is a 'virtual file', not being delivered, but to be created on the fly
		if(file.getFile() == null || file.getFile().trim().length() == 0)
		{
			StringBuffer buf = new StringBuffer();
			if(file.getCis() != null)
			{
				for(Ci ci : file.getCis())
				{
					buf.append(ci.getKey()).append("=").append("{{" + ci.getKey() + "}}").append("\n");
					tokens.put(ci.getKey(), ci.getValue().toString());
				}
			}
            
			try 
			{
				BufferedWriter writer = Files.newBufferedWriter(new File(workingDir + File.separatorChar + file.getName() + File.separatorChar + file.getName()).toPath(),charset);
				writer.write(buf.toString());
				writer.flush();
				writer.close();
            }
			catch (IOException e) 
			{
                // TODO Auto-generated catch block
                log.error("unable to persist file",e);
            }
		}
		// property file within a compressed file
		else if(file.getFile().contains(":"))
		{
			StringBuffer buf = new StringBuffer();
			Optional<BufferedWriter> writer = Optional.empty();
			Optional<InputStream> inStream = Optional.empty();
			try
			{
				//tar:gz:
				//"tar:gz:18.0.20.0.0/backend/backend.tar.gz!/backend.tar!/apps/config_specific.sh"
				String theFile = file.getFile().substring(0,file.getFile().lastIndexOf(":") + 1) + OUTPUT_FOLDER + File.separatorChar + file.getFile().substring(file.getFile().lastIndexOf(":") + 1,file.getFile().length());
				FileSystemManager fsManager = VFS.getManager();
				FileObject configFile = fsManager.resolveFile(theFile);

				inStream = Optional.ofNullable(configFile.getContent().getInputStream());
				// log.error("processing of properties files in compressed containers " + file.getFile() + " is not yet implemented");
				log.info("lookig up file" + theFile );

				String myString = IOUtils.toString(inStream.get(), StandardCharsets.UTF_8);

				inStream.get().close();

				log.info(myString);
				String[] data = myString.split("\n");
				for (String line:data)
				{
					//check if a Ci matches, and tokenize the file
					log.info("processing line: " + line);
					for(Ci ci:file.getCis())
					{
						// found a valid line with a configuration item
						if(!line.startsWith(COMMENT_IDENTIFIER) && line.contains(ci.getKey()) && keyIsNotAVariable(line,ci.getKey()))
						{
							log.info("found ci in line " + line + " : " + ci.getKey());
							String value = "UNKNOWN";
							String replacement = "{{" + ci.getKey() + "}}";

							//setEnv KEY VARIABLE
							if(line.startsWith("setEnv"))
							{
								value = line.substring(line.indexOf(ci.getKey()) + ci.getKey().length(),line.length()).trim();
							}
							//KEY=VARIABLE
							else if(line.startsWith(ci.getKey()))
							{
								//try to find the = sign and check if the variable is in double quotes " "
								if (line.contains(EUQALS_SIGN) && line.contains(DOUBLE_QUOTE))
								{
									value = line.substring(line.indexOf(DOUBLE_QUOTE) ,line.lastIndexOf(DOUBLE_QUOTE)+1);
									replacement = DOUBLE_QUOTE + "{{" + ci.getKey() + "}}" + DOUBLE_QUOTE;
								}
								else if (line.contains(EUQALS_SIGN))
								{
									value = line.substring(line.indexOf(EUQALS_SIGN) ,line.length());
									replacement = EUQALS_SIGN + "{{" + ci.getKey() + "}}";
								}
							}
							else
							{
								log.warn("unable to process line " + line);
							}

							log.info("replacing value " + value + " with " + replacement);
							line = line.replace(value, replacement);
						}
					}
					buf.append(line).append("\n");
				}
				String contentToStore = buf.toString();

				//run the replacements ... from deliverylocationlookup-APPLICATIONNAME.xml
				if(file.getReplacements() != null)
				{
					for (Map.Entry<String, String> entry : file.getReplacements().entrySet())
					{
						String key = entry.getKey();
						String value = entry.getValue();
						contentToStore = contentToStore.replaceAll(key, value);
						log.info(contentToStore);
					}
				}
			// store tokenized file
				writer = Optional.ofNullable(Files.newBufferedWriter(new File(workingDir + File.separatorChar + file.getName() + File.separatorChar + file.getName()).toPath(),charset));
				writer.get().write(contentToStore);
				writer.get().flush();
			}
			catch (Exception e)
			{
				log.error("unable to tokenize line",e);
			}
			finally
			{
				try {
					writer.get().close();
					inStream.get().close();
				} catch (Exception e) {
					log.error("Unable to close streams",e);
				}
			}

		}
		// simple properties file
		else
		{
			Path path = new File(OUTPUT_FOLDER + File.separatorChar + file.getFile()).toPath();
			Properties prop = new Properties();
			
			try 
			{
				log.info(path);
				prop.load(Files.newBufferedReader(path, charset));
				if(file.getCis() != null)
				{
					for(Ci ci : file.getCis())
					{
						String value = prop.getProperty(ci.getKey());
						tokens.put(ci.getKey(), value);
						log.debug(ci.getKey() + " => " + value);
		            	prop.setProperty(ci.getKey(), "{{" + ci.getKey() + "}}");
					}
				}
				log.info("Storing property file " + workingDir + File.separatorChar + file.getName() + File.separatorChar + file.getName());
	            prop.store(Files.newBufferedWriter(new File(workingDir + File.separatorChar + file.getName() + File.separatorChar + file.getName()).toPath(),charset), "Property file tokenized by MissingLinkProcessor");
            }
			catch (IOException e1) 
			{
	            // TODO Auto-generated catch block
	            log.error("unable to read file ", e1);
            }
		}
	}
	
}
