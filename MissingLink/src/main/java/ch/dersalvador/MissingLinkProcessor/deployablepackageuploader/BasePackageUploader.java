package ch.dersalvador.MissingLinkProcessor.deployablepackageuploader;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

/**
 * abstract base class, just holding username and password.
 * 
 *
 * @author u37792
 * @version  $Revision: #18 $, $Date: 2016/07/13 $
 */
public abstract class BasePackageUploader 
{
	String user;
	String password;
	String url;
	
	abstract public String uploadDepoyablePackage(String filename, String file) throws ClientProtocolException,
    IOException;
}
