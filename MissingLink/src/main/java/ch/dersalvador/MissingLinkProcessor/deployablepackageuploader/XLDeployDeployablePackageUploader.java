package ch.dersalvador.MissingLinkProcessor.deployablepackageuploader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;


import javax.xml.bind.DatatypeConverter;

import ch.dersalvador.MissingLinkProcessor.configuration.XLDeploy;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class encapsulates the functionality to upload a .dar file to the XLdeploy server
 * 
 *
 * @author u37792
 * @version  $Revision: #19 $, $Date: 2016/07/13 $
 */
public class XLDeployDeployablePackageUploader extends BasePackageUploader
{
	public static final String PACKAGE_UPLOAD_PATH = "/deployit/package/upload/";
	
	public static final String CORRUPT_PACKAGE 			= "INFO: response from server The selected file does not have the expected format for an importable package"; 
	public static final String VERSION_ALREADY_IMPORTED = "Already imported version"; 
	
	// static Log log = LogFactory.getLog(XLDeployDeployablePackageUploader.class);
	static Logger log = LogManager.getLogger(XLDeployDeployablePackageUploader.class);

	XLDeploy xldeploy;
	
	/**
	 * 
	 * Creates a new <code>XLDeployDeployablePackageUploader</code> instance.
	 *
	 * @param user: user to connect to XLDeploy
	 * @param password: password to connect to XLDeploy
	 * @param url: url of the XLDeploy server
	 */
	public XLDeployDeployablePackageUploader(String user, String password, String url) 
	{
	    super();
	    this.user = user;
	    this.password = password;
	    this.url = url;
    }

	/**
	 * the actual method uploading the dar file to the server
	 */
	public String uploadDepoyablePackage(String filename, String file) throws ClientProtocolException,
		            IOException 
    {
		
        String encoding = DatatypeConverter.printBase64Binary((user + ":" + password).getBytes("UTF-8"));

		        CloseableHttpResponse response = null;
		        InputStream is = null;
		        String results = null;
		        CloseableHttpClient httpclient = HttpClients.createDefault();
		        
		        try 
		        {

		            HttpPost httppost = new HttpPost(url + PACKAGE_UPLOAD_PATH + filename);
		            httppost.setHeader("Authorization", "Basic " + encoding);

		            
		            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		            
		            FileBody body = new FileBody(new File(file));
		            builder.addPart("fileData", body);
		            
		            HttpEntity reqEntity = builder.build();
		            
		            httppost.setEntity(reqEntity);

		            response = httpclient.execute(httppost);

		            HttpEntity entity = response.getEntity();
		            if (entity != null) 
		            {
		                is = entity.getContent();
		                StringWriter writer = new StringWriter();
		                IOUtils.copy(is, writer, "UTF-8");
		                results = writer.toString();
		                log.info("response from server " + results);
		            }

		        } finally {
		            try {
		                if (is != null) {
		                    is.close();
		                }
		            } catch (Throwable t) {
		                // No-op
		            }

		            try {
		                if (response != null) {
		                    response.close();
		                }
		            } catch (Throwable t) {
		                // No-op
		            }

		            httpclient.close();
		        }

		        return results;
		    }

	
	public boolean uploadDeployablePackage(String path)
	{
		return false;
	}
}
