package ch.bjb.MissingLinkProcessor.dictionaryprocessor;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

import ch.bjb.MissingLinkProcessor.model.DeployableFile;
import org.apache.commons.io.IOUtils;

import ch.bjb.MissingLinkProcessor.configuration.DeliveryProcessorModell;
import ch.bjb.MissingLinkProcessor.deployablepackageuploader.BaseARAConnector;
import ch.bjb.MissingLinkProcessor.model.Ci;
import ch.bjb.MissingLinkProcessor.model.ConfigurationSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class that models access to the XLDeploys dictionaries 
 * 
 *
 * @author u37792
 * @version  $Revision: #20 $, $Date: 2016/07/13 $
 */
public class XLDeployDictionaryProcessor extends BaseARAConnector {

	public static String ENDPOINT = "deployit/deployment/dictionary";
	// Log log = LogFactory.getLog(getClass());
	static Logger log = LogManager.getLogger(XLDeployDictionaryProcessor.class);

	Charset charset;

	/*
	 * curl -u admin:PASWORD http://localhost:4516/deployit/deployment/dictionary?environment=Environments/EB2/Test/UAT2
	 * curl -u admin:PASWORD http://localhost:4516/deployit/repository/ci/{ID:Environments/EB2/CH/default}
	 * https
	 * ://docs.xebialabs.com/generated/xl-deploy/4.5.x/rest-api/com.xebialabs.deployit.engine.api.DeploymentService.html#/deployment/dictionary
	 * :GET
	 * https://docs.xebialabs.com/generated/xl-deploy/4.5.x/rest-api/com.xebialabs.deployit.engine.api.RepositoryService.html#/repository
	 * /query:GET
	 */
/*
 *  <map>
  <entry>
    <string>ch.clx.config.login.token.vasco.domain</string>
    <string>ebk</string>
  </entry>
  <entry>
    <string>ch.clx.config.bjb.ldap1.principal</string>
    <string>CN=s_EBK-ADRead,OU=Service Accounts,OU=Global,OU=Production,DC=DerSalvador,DC=com</string>
  </entry>
  <entry>
    <string>ch.clx.config.bjb.ldap3.url</string>
    <string>ldaps://dczrh03.DerSalvador.com/</string>
  </entry>
  <entry>
    <string>ch.clx.config.bjb.ldap3.principal</string>
    <string>CN=s_EBK-ADRead,OU=Service Accounts,OU=Global,OU=Production,DC=DerSalvador,DC=com</string>
  </entry>
  <entry>
    <string>ch.clx.config.bjb.ldap4.principal</string>
    <string>CN=s_EBK-ADRead,OU=Service Accounts,OU=Global,OU=Production,DC=DerSalvador,DC=com</string>
  </entry>
  <entry>
    <string>soap.host.backup</string>
    <string>https://ebs-v-vasco01.ebs.crealogix.net:8888</string>
  </entry>
  <entry>
    <string>ch.clx.config.environment.type</string>
    <string>uat</string>
  </entry>
  <entry>
    <string>ch.clx.config.bjb.ldap1.url</string>
    <string>ldaps://dczrh01.DerSalvador.com/</string>
  </entry>
  <entry>
    <string>h.clx.config.login.token.vasco.admin.domain</string>
    <string>master</string>
  </entry>
  <entry>
    <string>DerSalvador.BackendConfiguration.javaHome</string>
    <string>/opt/java</string>
  </entry>
  <entry>
    <string>soap.host.primary</string>
    <string>https://ebs-v-vasco01.ebs.crealogix.net:8888</string>
  </entry>
  <entry>
    <string>ch.clx.config.customercare.messaging.personalizedMessagingDefaultEmail</string>
    <string>av-DerSalvador-ch@DerSalvador.lab</string>
  </entry>
  <entry>
    <string>DerSalvador.BackendConfiguration.databaseHost</string>
    <string>hostdb</string>
  </entry>
  <entry>
    <string>DerSalvador.BackendConfiguration.databasePort</string>
    <string>2005</string>
  </entry>
  <entry>
    <string>ch.clx.config.login.token.vasco.admin.domain</string>
    <string>ebk</string>
  </entry>
  <entry>
    <string>ch.clx.config.bjb.ldap1.credentials</string>
    <string>********</string>
  </entry>
  <entry>
    <string>ch.crealogix.DerSalvador.ondemand.REMOTEHOST</string>
    <string>oms_test.DerSalvador.com</string>
  </entry>
  <entry>
    <string>ch.clx.config.environment.configuration</string>
    <string>bjb-generic</string>
  </entry>
  <entry>
    <string>ch.clx.config.login.token.vasco.admin.userID</string>
    <string>crealogix</string>
  </entry>
  <entry>
    <string>ch.clx.config.login.token.vasco.admin.password</string>
    <string>********</string>
  </entry>
  <entry>
    <string>ch.clx.config.campaigner.bannerUri</string>
    <string>localhost</string>
  </entry>
  <entry>
    <string>ch.clx.config.bjb.ldap2.url</string>
    <string>ldaps://dczrh02.DerSalvador.com/</string>
  </entry>
  <entry>
    <string>ch.clx.config.bjb.ldap4.credentials</string>
    <string>********</string>
  </entry>
  <entry>
    <string>component.type.authentication</string>
    <string>Authentication Client BJBREF18</string>
  </entry>
  <entry>
    <string>ch.clx.config.bjb.ldap3.credentials</string>
    <string>********</string>
  </entry>
  <entry>
    <string>ch.clx.config.environment.project</string>
    <string>mdb</string>
  </entry>
  <entry>
    <string>DerSalvador.BackendConfiguration.javaCommand</string>
    <string>/opt/commqnd</string>
  </entry>
  <entry>
    <string>ch.clx.config.bjb.ldap2.principal</string>
    <string>CN=s_EBK-ADRead,OU=Service Accounts,OU=Global,OU=Production,DC=DerSalvador,DC=com</string>
  </entry>
  <entry>
    <string>DerSalvador.BackendConfiguration.databaseSid</string>
    <string>sid</string>
  </entry>
  <entry>
    <string>ch.clx.config.bjb.ldap2.credentials</string>
    <string>********</string>
  </entry>
  <entry>
    <string>ch.clx.config.bjb.ldap4.url</string>
    <string>ldaps://dczrh04.DerSalvador.com/</string>
  </entry>
</map>
 */
	/**
	 * 
	 * Creates a new <code>XLDeployDictionaryProcessor</code> instance.
	 *
	 */
	public XLDeployDictionaryProcessor() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * Creates a new <code>XLDeployDictionaryProcessor</code> instance.
	 *
	 * @param configuration
	 */
	public XLDeployDictionaryProcessor(DeliveryProcessorModell configuration) {
		super();
		this.user = configuration.getXldeploy().getUser();
		this.password = configuration.getXldeploy().getPassword();
		this.url = configuration.getXldeploy().getURL();
		this.charset = Charset.forName(configuration.getCharset());
	}
	
	/**
	 *  main method to fetch the dictionaries from the server
	 * @param args: first argument is the server, second argument is the environment
	 */
	public static void main(String[] args) 
	{
		String server = args[0];
		String environment = args[1];
		XLDeployDictionaryProcessor dp = new XLDeployDictionaryProcessor();
		dp.setUser("admin");
		dp.setPassword("");
		
		dp.setUrl(server);

		dp.fetchDictionariesForEnvironment(environment);
	}
	
	/**
	 * this method updates the dictionary in XLDeploy (current not implemented)  	
	 * @param additions
	 */
	public void updateDictionary(List <String> additions)
	{
		
	}
	
	/**
	 * this method issues a request for configuration if a delivery contains less config items than the dictionary in XLDeploy 
	 * @param removals
	 */
	public void issueConfigurationRequest(List <String> removals)
	{
		
	}

	/**
	 * the method access the XLDeploy server, downloads the default dictionary and checks whether or not there are differences  
	 * @param application the application for which a dictionary be checked for
	 * @param configurationSets the list of configuration sets from delivery.xml
	 * @return
	 */
	public List <String> validateDictionaries(String application, List<ConfigurationSet> configurationSets) 
	{
		List <String> additions = Collections.<String> emptyList();
		List <String> removals  = Collections.<String> emptyList();
		
		HashMap<String, String> dictionaryForDeliverable = compileDictionaryForDelivery(configurationSets);

		log.info(dictionaryForDeliverable);
		
		Map<String, String> dictionaryFromXLDeploy = fetchDictionariesForEnvironment(application);
		

		log.info(dictionaryFromXLDeploy);

		
		if(dictionaryForDeliverable.keySet().equals(dictionaryFromXLDeploy.keySet()))
		{
			log.info("no new keys identified");
			return Collections.<String> emptyList();
		}
		

		// union
		Set<String> union = new HashSet<String>(dictionaryForDeliverable.keySet());
		union.addAll(dictionaryFromXLDeploy.keySet());
		log.info("unions" + union); 
		// dictionaryForDeliverable - dictionaryFromXLDeploy
		Set<String> diff1 = new HashSet<String>(dictionaryForDeliverable.keySet());
		additions.removeAll(dictionaryFromXLDeploy.keySet());
		log.info("additions" + additions); 
		// dictionaryFromXLDeploy - dictionaryForDeliverable
		Set<String> diff2 = new HashSet<String>(dictionaryFromXLDeploy.keySet());
		removals.removeAll(dictionaryForDeliverable.keySet());
		log.info("removals" + removals); 
		// intersection
		Set<String> intersect = new HashSet<String>(dictionaryForDeliverable.keySet());
		intersect.retainAll(dictionaryFromXLDeploy.keySet());
		log.info("intersects" + intersect);
		return additions;
	}

	/**
	 * the method that encapsulates the functionality to access and model the 
	 * @param application the application for which a dictionary be checked for
	 * @return
	 */
	private Map<String, String> fetchDictionariesForEnvironment(String application) 
	{
		
		String encoding;
		Map<String, String> map = Collections.<String, String> emptyMap();
		
		log.info("getting dictionary for " + application );
		
		try {
			encoding = DatatypeConverter.printBase64Binary((user + ":" + password).getBytes("UTF-8"));

			Client client = ClientBuilder.newClient();
			WebTarget webTarget = client.target(getBaseUrl());
			WebTarget resourceWebTarget = webTarget.path(ENDPOINT);
			WebTarget dictionaryWebTarget = resourceWebTarget.queryParam("environment", application);

			Invocation.Builder invocationBuilder = dictionaryWebTarget.request(MediaType.APPLICATION_XML);
			invocationBuilder.header("Authorization", "Basic " + encoding);

			Response response = invocationBuilder.get();
			
			String xml = response.readEntity(String.class);
			log.info(xml);
			
			List<String> lines = IOUtils.readLines(new StringReader(xml));
			
			map = new HashMap<String,String>();
			
			boolean isKey = false;
			String key = "";
			String value = "";
			
			for(String line:lines)
			{
				
				if(line.contains("<map>"))
				{
					
				}
				else if(line.contains("</map>"))
				{
					
				}
				else if(line.contains("<entry>"))
				{
					
				}
				else if(line.contains("</entry>"))
				{
					
				}
				else if(line.contains("<string>"))
				{
					log.info("processing"+ line);
					line = line.replace("<string>", "").replace("</string>", "").trim();
					isKey = !isKey;
					if (isKey)
						key = line;
					else
					{
						value = line;
						map.put(key, value);
					}
				}
				else
				{
					
				}
				
			}
			
			log.info(map);

		}
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			log.error("unable to fetch dictionaries", e);
		}
		return map;
	}

	/**
	 * Prints out all the CIs as per delivery.xml
	 * 
	 * @param configurationSets
	 */
	private HashMap <String,String> compileDictionaryForDelivery(List<ConfigurationSet> configurationSets/*, String workingDir*/) 
	{
		HashMap <String,String> props = new HashMap<String,String>();
		
		StringBuffer csv = new StringBuffer();
		
		if(configurationSets!=null && configurationSets.size()>0)
		{
			for (ConfigurationSet configurationSet : configurationSets) 
			{
				for (DeployableFile f : configurationSet.getFiles())
				{
					if (f.getCis() != null) 
					{
						for (Ci ci : f.getCis()) 
						{
							props.put(ci.getKey(), ci.getValue().getValue());
							csv.append(ci.getKey()).append(";").append(ci.getValue().getType()).append(";").append(ci.getValue().getValue())
							                .append("\r\n");
						}
					}
				}
			}
		}
		return props;
/*
		BufferedWriter writer;
		try {
			log.info(workingDir + File.separatorChar + "initialDictionary.txt");
			writer = Files.newBufferedWriter(new File(workingDir + File.separatorChar + "initialDictionary.txt").toPath(), charset);
			writer.write(csv.toString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("Error while writing initial dictionary to file " + e.toString());
		}
		*/
	}
}
