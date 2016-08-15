package ch.dersalvador.MissingLinkProcessor.fileprocessors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map.Entry;

import ch.dersalvador.MissingLinkProcessor.model.DeployableFile;
import ch.dersalvador.MissingLinkProcessor.model.Ci;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * This class encapsulates functionality that processes shell scripts files (.sh)
 * 
 *
 * @author u37792
 * @version  $Revision: #20 $, $Date: 2016/07/13 $
 */
public class SHFileProcessor extends BaseFileProcessor
{
	// static Log log = LogFactory.getLog(SHFileProcessor.class);
	static Logger log = LogManager.getLogger(SHFileProcessor.class);




	public static void process(DeployableFile file,String workingDir, String OUTPUT_FOLDER, Charset charset)
	{
		HashMap<String,String> tokens = new HashMap<>();
		log.info("performFileCopy " + file.getName() + " to " + workingDir);

		//create target directory
		(new File(workingDir + File.separatorChar + file.getName())).mkdirs();
		
		
		//check if file is a 'virtual file', not being delivered, but to be created on the fly
		if(file.getFile() == null || file.getFile().trim().length() == 0)
		{
			log.error("processing of virtual .sh files " + file.getFile() + " is not yet implemented");			
		}
		// property file within a compressed file
		else if(file.getFile().contains(":"))
		{
			StringBuffer buf = new StringBuffer();
			try 
			{
				FileSystemManager fsManager = VFS.getManager();
				//tar:gz:
				//"tar:gz:18.0.20.0.0/backend/backend.tar.gz!/backend.tar!/apps/config_specific.sh"
				String theFile = file.getFile().substring(0,file.getFile().lastIndexOf(":") + 1) + OUTPUT_FOLDER + File.separatorChar + file.getFile().substring(file.getFile().lastIndexOf(":") + 1,file.getFile().length());
				log.info("lookig up file" + theFile );
				FileObject configFile = fsManager.resolveFile(theFile);
				
				InputStream inStream = configFile.getContent().getInputStream();
				
				String myString = IOUtils.toString(inStream, StandardCharsets.UTF_8);
						
				inStream.close();
				
				log.info(myString);
				String[] data = myString.split("\n");
				for (String line:data)
				{
					//check if a Ci matches, and tokenize the file
					log.info("processing line: " + line);
					for(Ci ci:file.getCis())
					{
						// found a valid line with a configuration item
						if(!line.startsWith(COMMENT_IDENTIFIER) && line.contains(ci.getKey()) && keyIsNotAVariable2(line,ci.getKey()))
						{
							log.info("found ci in line " + ci.getKey());
						
							//setEnv KEY VARIABLE
							if(line.replaceAll("^\\s+", "").startsWith("setEnv"))
							{
								//String value = line.substring(line.indexOf(ci.getKey()) + ci.getKey().length(),line.length()).trim();
								
								//LOOKUPSTR
								String[] lookupstr = line.split("\\s+");
								log.info("SearchString = "+lookupstr[1]);
								String value = line.substring(line.indexOf(lookupstr[1]) + lookupstr[1].length(),line.length());
								log.info("value " + value);								
								//LOOKUPSTR
								
								//###
								//String token="{{" + ci.getKey() + "}}";
								String token="{{" + lookupstr[1] + "}}";
								//pad the token by adding 1 char as a whitespace to put a gap between the key and the token
								int prepad = token.length()+1;
								String prepaddedToken = String.format("%"+prepad + "s", token);
								
								line = line.replace(value, prepaddedToken );
								//#####

							}
							//export KEY VARIABLE, e.g. export PATH=$PATH:/usr/local/bin or export EDITOR=/usr/bin/vim
							// or
							//KEY=VARIABLE
							if(line.startsWith(ci.getKey()) || line.replaceAll("^\\s+", "").startsWith("export"))
							{
								//try to find the = sign and check if the variable is in double quotes " " 
								if (line.contains(EUQALS_SIGN) && line.contains(DOUBLE_QUOTE))
								{
									String value = line.substring(line.indexOf(DOUBLE_QUOTE) + 1,line.lastIndexOf(DOUBLE_QUOTE));
									log.info("value " + value);
									line = line.replace(value, "{{" + ci.getKey() + "}}");
								}
								else if (line.contains(EUQALS_SIGN))
								{
									String value = line.substring(line.indexOf(EUQALS_SIGN) + 1,line.length());
									log.info("value " + value);
									line = line.replace(value, "{{" + ci.getKey() + "}}");
								}
							}
						}
						else
						{
							log.debug("not processing line as it is either comment or the key is used as variable " + line);
						}
					}
					buf.append(line).append("\n");
				}
            }
			catch (Exception e) 
			{
	            // TODO Auto-generated catch block
	            log.error("unable to tokenize line",e);
            }
			String contentToStore = buf.toString();

			//run the replacements ... from deliverylocationlookup-APPLICATIONNAME.xml
			if(file.getReplacements() != null)
			{
				for (Entry<String, String> entry : file.getReplacements().entrySet()) 
				{
				    String key = entry.getKey();
				    String value = entry.getValue();
				    contentToStore = contentToStore.replaceAll(key, value);
				    log.info(contentToStore);
				}
			}
			// store tokenized file
			try 
			{
				BufferedWriter writer = Files.newBufferedWriter(new File(workingDir + File.separatorChar + file.getName() + File.separatorChar + file.getName()).toPath(),charset);
				writer.write(contentToStore);
				writer.flush();
				writer.close();
            }
			catch (IOException e) 
			{
                // TODO Auto-generated catch block
                log.error("unable to persists file " + file.getName(), e);
            }
		}
		// simple properties file
		else
		{
			log.error("processing of plain .sh files " + file.getFile() + " is not yet implemented");
		}
	}
	
	protected static boolean keyIsNotAVariable2(String line,String key)
	{
		if(line.contains(VARIABLE_IDENTIFIER + key) || 
		   line.contains(VARIABLE_IDENTIFIER2 + key))
			return false;
		else 
			return true;
	}
	

}
