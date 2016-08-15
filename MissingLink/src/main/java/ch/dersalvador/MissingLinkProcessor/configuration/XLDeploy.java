package ch.dersalvador.MissingLinkProcessor.configuration;

/**
 * 
 * Class that models the XLDeploy part of the MissingLinkProcessors configuration file 
 *
 * @author u37792
 * @version  $Revision: #18 $, $Date: 2016/07/13 $
 */
public class XLDeploy 
{
	String URL;
	String user;
	String password;
	
	
	
	public XLDeploy() {
	    super();
	    // TODO Auto-generated constructor stub
    }
	
	public String getURL() {
		return URL;
	}
	public void setURL(String uRL) {
		URL = uRL;
	}
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
	
	

}
