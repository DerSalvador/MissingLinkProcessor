package ch.bjb.MissingLinkProcessor;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import ch.bjb.MissingLinkProcessor.model.ARCHIVE;
import ch.bjb.MissingLinkProcessor.model.ConfigurationSet;
import ch.bjb.MissingLinkProcessor.model.Deployable;
import ch.bjb.MissingLinkProcessor.model.DeployableFile;
import ch.bjb.MissingLinkProcessor.model.SimpleFile;
import ch.bjb.MissingLinkProcessor.model.localproperty.DeliveryLocationLookup;
import ch.bjb.MissingLinkProcessor.model.localproperty.LocalPropertyContainer;
import ch.bjb.MissingLinkProcessor.model.localproperty.Replacement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class loads and grants access to properties needed to provide values for 
 * - the target path, 
 * - the target file name, 
 * - the option if a target dir needs to be created, and
 * - and text replacements needed in a file to be processed.
 * 
 * The classes tries to load xml files from a subdirectory relative to the directory the main application is started within. 
 * The name of the subdirectory is localproperties.
 * The name of the file starts with deliverylocationlookup- a variable named applicationname (from delivery.xml), and the ending .xml. 
 * 
 *
 * @author u37792
 * @version  $Revision: #26 $, $Date: 2016/07/18 $
 */
public class LocalPropertiesLocator 
{
	// Log log = LogFactory.getLog(getClass());
	static Logger log = LogManager.getLogger(LocalPropertiesLocator.class);


	public String getLocalpropertiesFileName() {
		return localpropertiesFileName;
	}

	public void setLocalpropertiesFileName(String localpropertiesFileName) {
		this.localpropertiesFileName = localpropertiesFileName;
	}

	String localpropertiesFileName = "localproperties/deliverylocationlookup-%1s.xml";

	DeliveryLocationLookup deliveryLocationLookup;
	
	/**
	 * public constructor
	 * Creates a new <code>LocalPropertiesLocator</code> instance.
	 *
	 * @param applicationName
	 */
	public LocalPropertiesLocator(String applicationName) 
	{
		deliveryLocationLookup = readFile(applicationName);
    }
	
	/**
	 * reads a local property file and returns an object representation
	 * @param applicationName from delivery.xml
	 * @return {@link ch.bjb.MissingLinkProcessor.model.localproperty.DeliveryLocationLookup}
	 */
	private DeliveryLocationLookup readFile(String applicationName)
	{
		localpropertiesFileName = String.format(localpropertiesFileName, applicationName);
		Path currentRelativePath = Paths.get(localpropertiesFileName);
		//Path currentRelativePath = Paths.get("Y:\\jbossdev\\workspaces\\workspace_template\\MissingLinkProcessor\\src\\main\\resources\\localproperties\\deliverylocationlookup-" + applicationName + ".xml");
		String s = currentRelativePath.toAbsolutePath().toString();
		log.info("trying to load localproperties file from : " + s );
		
		try 
		{

			JAXBContext jaxbContext = JAXBContext.newInstance(DeliveryLocationLookup.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			DeliveryLocationLookup deliveryLocationLookup = (DeliveryLocationLookup) jaxbUnmarshaller.unmarshal(currentRelativePath.toFile());
			return deliveryLocationLookup;
		}
		catch (JAXBException e) 
		{
			log.fatal("unable to load local properties file " + s,e);
		}
		return null;
		
	}

	/**
	 * returns the path a delivery (here a dar file) is stored in XLDeploy
	 * @return
	 */
	public String getApplicationPathForARATool()
	{
		return deliveryLocationLookup.getPath();
	}
	
	/**
	 * 
	 * @param deployable: The deployable as per delivery.xml
	 * @param configSet: The List of configrationSets as per delivery.xml
	 * @return a provisioned List {@link ch.bjb.MissingLinkProcessor.model.ConfigurationSet}
	 */
	public List <ConfigurationSet> getLocalPropertiesforConfigurationFile(Deployable deployable, List <ConfigurationSet> configSet)
	{
		//check if we find local properties for the deployable itself. Note, the file must be only the filename, no path attached.
		// we need to remove any path information from the file.
		String filename = new java.io.File(deployable.getFile()).getName();
		LocalPropertyContainer deployableTmp = new LocalPropertyContainer(deployable.getName(), 
																		  filename, "", "", true,null,false,"");
		int deployableLocation = -1;
		
		try
		{
			deployableLocation = deliveryLocationLookup.getLocalPropertyContainer().indexOf(deployableTmp);
		}
		catch(Exception e)
		{
			log.error(String.format("Local properties not found for deployable/name= %1s, filename=%2s in file %3s", deployable.getName(), getLocalpropertiesFileName() ));
			// log.error("No local properties found for " + deployable.getName() + " and " + deployable.getFile());
		}
		
		if(deployableLocation != -1)
		{
			LocalPropertyContainer deployableLoc = deliveryLocationLookup.getLocalPropertyContainer().get(deployableLocation);
			setLocalPropertyValues((ARCHIVE) deployable, deployableLoc, deliveryLocationLookup);
		}

		for (ConfigurationSet configurationSet:configSet)
		{
			for (DeployableFile file : configurationSet.getFiles())
			{
				log.info("checking for local Properties for the combination: " + deployable + " "  + file.getName());
				LocalPropertyContainer tmp = new LocalPropertyContainer(deployable.getName(), file.getName(), "", "", true,null,false,"");

				int location = -1;
				
				try
				{
					location = deliveryLocationLookup.getLocalPropertyContainer().indexOf(tmp);
				}
				catch(Exception e)
				{
					log.error("ERRO: no local properties found for " + deployable.getName() + " and " + file.getName());
				}
				
				if(location != -1)
				{
					log.debug("found local property");
					LocalPropertyContainer loc = deliveryLocationLookup.getLocalPropertyContainer().get(location);
					file.setTargetFileName(loc.getTargetFileName());
					file.setTargetPath(loc.getTargetPath());
					file.setTargetPathShared(loc.getTargetPathShared());
					file.setCreateTargetPath(loc.getTargetPathCreate());
					file.setScanPlaceholders(loc.getScanPlaceholders());

					if(loc.getReplacements() !=null)
					{
						HashMap<String,String> replacements = new HashMap<String,String>();
						for (Replacement rep :loc.getReplacements())
						{
							log.info("populating replacements HashMap with " + rep.getSearchtext() + " and " + rep.getSubstitution());
							replacements.put(rep.getSearchtext(), rep.getSubstitution());
						}
						log.info("replacementes for this file "+ file.getName() + " are " + replacements);
						file.setReplacements(replacements);
					}
				}
				else
				{
					file.setTargetFileName(file.getName());
					log.error(String.format("File %1s does not have a mapping in local properties with deployable/name=%2s, file=%3s ", file.getName(), tmp.getDeployable(), tmp.getFile()) );
					log.error(String.format("Deployable and Filename in local properties %1s must match name of deployable in and file name delivery.xml", getLocalpropertiesFileName()));
					deliveryLocationLookup.getLocalPropertyContainer().forEach( l -> log.error(String.format("Deployable/Name=%1s-Filename=%2s",l.getDeployable(),l.getFile())));
				}
				file.setParent(deployable);
			}
		}
		
		return configSet;
	}

	private void setLocalPropertyValues(ARCHIVE deployable, LocalPropertyContainer deployableLoc, DeliveryLocationLookup deliveryLocationLookup) {
		deployable.setTargetPath(deployableLoc.getTargetPath());
		deployable.setCreateTargetPath(deployableLoc.getTargetPathCreate());
		deployable.setTargetPathShared(deployableLoc.getTargetPathShared());
		deployable.setScanPlaceHolders(deployableLoc.getScanPlaceholders());
		deployable.setExtraSteps(deliveryLocationLookup.getExtraSteps());

	}
}
