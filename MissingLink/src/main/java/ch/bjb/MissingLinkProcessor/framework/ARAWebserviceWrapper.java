package ch.bjb.MissingLinkProcessor.framework;

import ch.bjb.MissingLinkProcessor.configuration.DeliveryProcessorModell;
import ch.bjb.MissingLinkProcessor.deployablepackageuploader.BaseARAConnector;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Class that models access to the XLDeploys dictionaries 
 * 
 *
 * @author Michael Roepke
 * @version  $Revision: #20 $, $Date: 2016/07/13 $
 */
public class ARAWebserviceWrapper extends BaseARAConnector {

	// 	http://localhost:4516/deployit/repository/exists/Applications/CST/CST_NPF/CST_NPF_PACKAGES/0.0.0.1
	public static String ENDPOINT_REPOSITORY_CI_EXIST = "deployit/repository/exists/${PATH}";
	public static String ENDPOINT_UNDEPLOY_PREPARE = "deployit/deployment/prepare/undeploy?deployedApplication=Environments/${PATH}";
	public static String ENDPOINT_DEPLOY_UNDEPLOY = "deployit/deployment";
	public static String ENDPOINT_STATUS_TASK = "deployit/task/${TASKID}";
	public static String ENDPOINT_START_TASK = "deployit/task/${TASKID}/start";
	public static String ENDPOINT_ARCHIVE_TASK = "deployit/task/${TASKID}/archive";

	public static String XLD_CI_APPLICATIONS = "Applications";
	public static String XLD_CI_INFRASTRUCTURE = "Infrastructure";
	public static String XLD_CI_ENVIRONMENTS = "Environments";
	public static String XLD_CI_CONFIGURATION = "Configuration";
	public static String ENDPOINT_XLDCI_CREATE_DELETE_QUERY = "deployit/repository/ci/${XLD_CI}/${PATH}";
	public static String ENDPOINT_REPOSITOY_QUERY = "deployit/repository/query";
	public static String CREATE_UNIX_INFRASTRUCTURE_SSH_LOCALHOST = "<overthere.LocalHost id=\"Infrastructure/${PATH}\"><os>UNIX</os></overthere.LocalHost>";
	public static String CREATE_UNIX_INFRASTRUCTURE_OVERTHERE_SSHHOST = "<overthere.SshHost id=\"Infrastructure/${PATH}\"><tags><value>TEST</value></tags><os>UNIX</os><connectionType>INTERACTIVE_SUDO</connectionType><address>${SSH_HOSTNAME}</address><port>22</port><username>u80872</username><password></password><sudoUsername></sudoUsername><sudoCommandPrefix>sudo su - {0} -c</sudoCommandPrefix><sudoQuoteCommand>true</sudoQuoteCommand></overthere.SshHost>";
	public static String CREATE_DIRECTORY = "<core.Directory id=\"${XLD_CI}/${PATH}\"/>";
	public static String CREATE_DEPLOYMENTPACKAGE = "<udm.DeploymentPackage  id=\"Applications/${PATH}/${VERSION}\"><application ref=\"Applications/${PATH}\"/><orchestrator/><deployables/><applicationDependencies/></udm.DeploymentPackage>";
	public static String CREATE_APPLICATION = "<udm.Application id=\"Applications/${PATH}\"><lastVersion/></udm.Application>";
	static Logger log = LogManager.getLogger(ARAWebserviceWrapper.class);

	// static Log log = LogFactory.getLog(ARAWebserviceWrapper.class);
	static Charset charset;

	/**
	 *
	 * Creates a new <code>XLDeployDictionaryProcessor</code> instance.
	 *
	 * @param configuration
	 */
	public ARAWebserviceWrapper(DeliveryProcessorModell configuration) {
		super();
		this.user = configuration.getXldeploy().getUser();
		this.password = configuration.getXldeploy().getPassword();
		this.url = configuration.getXldeploy().getURL();
		this.charset = Charset.forName(configuration.getCharset());
	}
	
	/**
	 * the method that encapsulates the functionality to access and model the 
	 * @return exists
	 */
	public boolean checkExistance(String ci_path)
	{

		boolean exists = false;
		String encoding;

		log.info("Checking if " + ci_path + " exist" );
		
		try {
			List<String> lines = callRESTApiGET(ENDPOINT_REPOSITORY_CI_EXIST, ci_path);
			
			if (lines.size() > 0 && lines.get(0).equals("<boolean>true</boolean>"))
				exists = true;
			else
				exists = false;
			log.info(lines);
		}
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			log.error("unable to check existance of " + ci_path, e);
		}
		return exists;
	}

	private List<String> callRESTApiGET(String APIUrl, String ci_path) throws IOException {
		String encoding = DatatypeConverter.printBase64Binary((user + ":" + password).getBytes("UTF-8"));

		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(getBaseUrl());

		WebTarget resourceWebTarget = webTarget.path(APIUrl.replace("${PATH}", ci_path));
		// WebTarget apiWebTarget = resourceWebTarget.queryParam("boolean", exists);

		Invocation.Builder invocationBuilder = resourceWebTarget.request(MediaType.APPLICATION_XML);
		invocationBuilder.header("Authorization", "Basic " + encoding);

		Response response = invocationBuilder.get();

		String xml = response.readEntity(String.class);
		log.info(xml);

		return IOUtils.readLines(new StringReader(xml));
	}

	public List<String> createSSHLocalHost(String id_host) throws IOException {

		List<String> lines = null;
		log.info("Creating CI " + id_host );
		try {
			lines = callRESTApiPUT(ENDPOINT_XLDCI_CREATE_DELETE_QUERY, XLD_CI_INFRASTRUCTURE, CREATE_UNIX_INFRASTRUCTURE_SSH_LOCALHOST, id_host);
			log.info(lines);
		}
		catch (Exception e)
		{
			log.error("unable to create CI " + id_host, e);
			throw e;
		}
		return lines;
	}

	public List<String> createDirectory(String xld_ci, String id_dir) throws IOException {

		List<String> lines = null;
		log.info("Creating CI " + id_dir + " in " + xld_ci);
		try {
			lines = callRESTApiPUT(ENDPOINT_XLDCI_CREATE_DELETE_QUERY, xld_ci, CREATE_DIRECTORY, id_dir);
			log.info(lines);
		}
		catch (Exception e)
		{
			log.error("unable to create CI " + id_dir, e);
			throw e;
		}
		return lines;
	}


	public List<String> callRESTApiPUT(String APIUrl, String xld_ci, String putString, String id_suffix)  throws IOException {
		String encoding = DatatypeConverter.printBase64Binary((user + ":" + password).getBytes("UTF-8"));

		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(getBaseUrl() + "/" + APIUrl.replace("${PATH}", id_suffix).replace("${XLD_CI}", xld_ci));

		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML);
		invocationBuilder.header("Authorization", "Basic " + encoding);
		Response response = invocationBuilder.post(Entity.xml(putString.replace("${PATH}", id_suffix).replace("${XLD_CI}", xld_ci)));

		String xml = response.readEntity(String.class);
		log.info(xml);

		return IOUtils.readLines(new StringReader(xml));
	}

	public String callRESTApiDeployUndeployTask(String taskXML)  throws IOException {
		String encoding = DatatypeConverter.printBase64Binary((user + ":" + password).getBytes("UTF-8"));

		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(getBaseUrl() + "/" + ENDPOINT_DEPLOY_UNDEPLOY);

		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML);
		invocationBuilder.header("Authorization", "Basic " + encoding);
		Invocation inv = invocationBuilder.buildPost(Entity.xml(taskXML));
		Response response = inv.invoke();
		// Response response = invocationBuilder..post(Entity.xml(taskXML));
		log.info(response.toString());
		String xml = response.readEntity(String.class);
		log.info(xml);

		return xml;
	}

	public String callRESTApiStartTask(String taskid)  throws IOException {
		String encoding = DatatypeConverter.printBase64Binary((user + ":" + password).getBytes("UTF-8"));

		Client client = ClientBuilder.newClient();
		WebTarget webTargetStart = client.target(getBaseUrl() + "/" + ENDPOINT_START_TASK.replace("${TASKID}", taskid));
		WebTarget webTargetStatus = client.target(getBaseUrl() + "/" + ENDPOINT_STATUS_TASK.replace("${TASKID}",taskid));
		WebTarget webTargetArchive = client.target(getBaseUrl() + "/" + ENDPOINT_ARCHIVE_TASK.replace("${TASKID}",taskid));

		Invocation.Builder invocationBuilderStart = webTargetStart.request(MediaType.APPLICATION_XML);
		Invocation.Builder invocationBuilderStatus = webTargetStatus.request(MediaType.APPLICATION_XML);
		Invocation.Builder invocationBuilderArchive = webTargetArchive.request(MediaType.APPLICATION_XML);
		// STart Task
		invocationBuilderStart.header("Authorization", "Basic " + encoding);
		Invocation inv = invocationBuilderStart.buildPost(Entity.xml(taskid));
		Response response = inv.invoke();
		// Response response = invocationBuilder..post(Entity.xml(taskXML));
		log.info(response.toString());
		String xml = response.readEntity(String.class);
		log.info(xml);
		// Status Task
		invocationBuilderStatus.header("Authorization", "Basic " + encoding);
		inv = invocationBuilderStart.buildPost(Entity.xml(taskid));
		response = inv.invoke();
		// Response response = invocationBuilder..post(Entity.xml(taskXML));
		log.info(response.toString());
		xml = response.readEntity(String.class);
		log.info(xml);
		// Status Archive
		invocationBuilderArchive.header("Authorization", "Basic " + encoding);
		inv = invocationBuilderArchive.buildPost(Entity.xml(taskid));
		response = inv.invoke();
		// Response response = invocationBuilder..post(Entity.xml(taskXML));
		log.info(response.toString());
		xml = response.readEntity(String.class);
		log.info(xml);


		return xml;
	}

	public String unDeployApplication(String applicationPath) throws IOException {
		String lines = callRESTApiUndeployPrepare(ARAWebserviceWrapper.XLD_CI_APPLICATIONS, applicationPath);
		log.info(lines);
		String taskID = callRESTApiDeployUndeployTask(lines);
		// lines.toStream.toString() should include regex "SUCCESSFUL-204"
		String res = callRESTApiStartTask(taskID);
		log.info("unDeployApplication Task: " + taskID);
		return res;
	}


	public List<String> callRESTApiDELETEApplication(String xld_ci, String id_suffix, String version) throws IOException {
		String encoding = DatatypeConverter.printBase64Binary((user + ":" + password).getBytes("UTF-8"));

		Client client = ClientBuilder.newClient();
		String appVersion = getBaseUrl() + "/" + ENDPOINT_XLDCI_CREATE_DELETE_QUERY.replace("${PATH}", id_suffix).replace("${XLD_CI}", xld_ci) + "/" + version;
		log.info("Trying to remove Application Version from ARA Tool: " + appVersion);
		WebTarget webTarget = client.target(appVersion);

		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML);
		invocationBuilder.header("Authorization", "Basic " + encoding);
		Response response = invocationBuilder.buildDelete().invoke();

		String xml = response.readEntity(String.class);
		if (xml.length() == 0)
			xml = response.getStatusInfo().getFamily().name() + "-" + response.getStatusInfo().getStatusCode();
		log.info(xml);

		return IOUtils.readLines(new StringReader(xml));
	}

	public String callRESTApiUndeployPrepare(String xld_ci, String appPath) throws IOException {
		String encoding = DatatypeConverter.printBase64Binary((user + ":" + password).getBytes("UTF-8"));

		Client client = ClientBuilder.newClient();
		String appVersion = getBaseUrl() + "/" + ENDPOINT_UNDEPLOY_PREPARE.replace("${PATH}", appPath);
		log.info("Trying to undeploy Application Version from ARA Tool: " + appVersion);
		WebTarget webTarget = client.target(appVersion);

		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML);
		invocationBuilder.header("Authorization", "Basic " + encoding);
		Response response = invocationBuilder.buildGet().invoke();

		String xml = response.readEntity(String.class);
		if (xml.length() == 0)
			xml = response.getStatusInfo().getFamily().name() + "-" + response.getStatusInfo().getStatusCode();
		log.info(xml);

		return xml;
	}



}
