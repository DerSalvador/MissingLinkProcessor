package ch.dersalvador.MissingLinkProcessor.fileprocessors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import ch.dersalvador.MissingLinkProcessor.model.DeployableFile;
import ch.dersalvador.MissingLinkProcessor.model.Ci;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class encapsulates functionality that processes yml based configuration files (.yml)
 * 
 *
 * @author u37792
 * @version  $Revision: #20 $, $Date: 2016/07/13 $
 */
public class YMLFileProcessor extends BaseFileProcessor
{
	public static final String COMMENT_IDENTIFIER 	= "---";
	public static final String COLON_SIGN 			= ":";

	
	// static Log log = LogFactory.getLog(YMLFileProcessor.class);
	static Logger log = LogManager.getLogger(YMLFileProcessor.class);


	public static void process(DeployableFile file,String workingDir, String OUTPUT_FOLDER, Charset charset)
	{
		
		/*
		 * 
		 * ---
		repackTGZ: true
		
		adjustDictionary: true
		
		createUniqeNameForFiles : true
		
		charset: US-ASCII
		
		xldeploy:
		 url: http://localhost:4516
		 user: admin
		 password: admin123 
		 */
		log.info("performFileCopy " + file.getName() + " to " + workingDir);

		//create target directory
		(new File(workingDir + File.separatorChar + file.getName())).mkdirs();
		
		
		//check if file is a 'virtual file', not being delivered, but to be created on the fly
		if(file.getFile() == null || file.getFile().trim().length() == 0)
		{
			log.error("processing of virtual .yml files " + file.getFile() + " is not yet implemented");			
		}
		// property file within a compressed file
		else if(file.getFile().contains(":"))
		{
			StringBuffer buf = new StringBuffer();
			try 
			{
				FileSystemManager fsManager = VFS.getManager();

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
						if(!line.startsWith(COMMENT_IDENTIFIER) && line.contains(ci.getKey()) && keyIsNotAVariable(line,ci.getKey()))
						{
							log.info("found ci in line " + line + " : " + ci.getKey());
							String value = "UNKNOWN";
							String replacement = "{{" + ci.getKey() + "}}";
							
							//KEY : VARIABLE
							if(line.replaceAll("^\\s+", "").startsWith(ci.getKey()))
							{
								//try to find the = sign and check if the variable is in double quotes " " 
								if (line.contains(COLON_SIGN) && line.contains(DOUBLE_QUOTE))
								{
									value = line.substring(line.indexOf(DOUBLE_QUOTE) ,line.lastIndexOf(DOUBLE_QUOTE)+1);
									replacement = DOUBLE_QUOTE + "{{" + ci.getKey() + "}}" + DOUBLE_QUOTE;
								}
								else if (line.contains(COLON_SIGN))
								{
									value = line.substring(line.indexOf(COLON_SIGN) ,line.length());
									replacement = COLON_SIGN + " {{" + ci.getKey() + "}}";
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
					log.info("adding line to buffer: " + line);
					buf.append(line).append("\n");
				}
            }
			catch (Exception e) 
			{
	            // TODO Auto-generated catch block
	            log.error("unable to tokenize line",e);
            }
			// store tokenized file
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
                log.error("unable to persists file " + file.getName(), e);
            }
		}
		// simple properties file
		else
		{
			log.error("processing of plain .yml files " + file.getFile() + " is not yet implemented");
		}
	}
	
}
