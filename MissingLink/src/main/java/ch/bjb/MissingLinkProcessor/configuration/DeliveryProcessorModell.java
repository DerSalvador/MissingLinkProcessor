package ch.bjb.MissingLinkProcessor.configuration;

/**
 * Class that models the MissingLinkProcessors configuration file
 * 
 *
 * @author u37792
 * @version  $Revision: #18 $, $Date: 2016/07/13 $
 */
public class DeliveryProcessorModell 
{

	boolean repackTGZ = false;
	boolean adjustDictionary = false;
	boolean createUniqeNameForFiles = false;
	
	String charset;
	
	XLDeploy xldeploy;

	/**
	 * 
	 * Creates a new <code>DeliveryProcessorModell</code> instance.
	 *
	 */
	public DeliveryProcessorModell() {
	    super();
	    // TODO Auto-generated constructor stub
    }

	/**
	 * getter method for XLDeploy configuration part of the config file
	 * @return
	 */
	public XLDeploy getXldeploy() {
		return xldeploy;
	}

	/**
	 * setter method  for XLDeploy configuration part of the config file
	 * @param xldeploy
	 */
	public void setXldeploy(XLDeploy xldeploy) {
		this.xldeploy = xldeploy;
	}

	public boolean isRepackTGZ() {
		return repackTGZ;
	}

	public void setRepackTGZ(boolean repackTGZ) {
		this.repackTGZ = repackTGZ;
	}

	public boolean isAdjustDictionary() {
		return adjustDictionary;
	}

	public void setAdjustDictionary(boolean adjustDictionary) {
		this.adjustDictionary = adjustDictionary;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public boolean isCreateUniqeNameForFiles() {
		return createUniqeNameForFiles;
	}

	public void setCreateUniqeNameForFiles(boolean createUniqeNameForFiles) {
		this.createUniqeNameForFiles = createUniqeNameForFiles;
	}
	
	
	
}
