package ch.dersalvador.MissingLinkProcessor.deployablepackageuploader;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

public abstract class BaseARAConnector
{
	protected static String user;
	protected static String password;
	protected static String url;
	
	
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public static String getBaseUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}


}
