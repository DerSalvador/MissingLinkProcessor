package ch.bjb.MissingLinkProcessor.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
/**
 * 
 * representation of the file.File tag of the delivery.xml
 *
 * @author u37792
 * @version  $Revision: #21 $, $Date: 2016/07/18 $
 */
@XmlRootElement(name="file.File")
public class SimpleFile extends Deployable 
{

	String targetPath;

	public Boolean getTargetPathShared() {
		return targetPathShared;
	}

	public void setTargetPathShared(Boolean targetPathShared) {
		this.targetPathShared = targetPathShared;
	}

	Boolean targetPathShared;
	String targetFileName;
	Boolean createTargetPath;

	public Boolean getScanPlaceHolders() {
		return scanPlaceHolders;
	}

	public void setScanPlaceHolders(Boolean scanPlaceHolders) {
		this.scanPlaceHolders = scanPlaceHolders;
	}

	Boolean scanPlaceHolders;


	public SimpleFile() {
	    super();
	    // TODO Auto-generated constructor stub
    }

	public SimpleFile(int id, String groupid, String tag, String file, String name, String version, List<Integer> configurationset, String extraSteps) {
	    super(id, groupid, tag, file, name, version, configurationset, extraSteps);
	    // TODO Auto-generated constructor stub
    }

	public SimpleFile(int id, String groupid, String file, String name, String version, List<Integer> configurationset) {
	    super(id, groupid, file, name, version, configurationset);
	    // TODO Auto-generated constructor stub
    }
	

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	public String getTargetFileName() {
		return targetFileName;
	}

	public void setTargetFileName(String targetFileName) {
		this.targetFileName = targetFileName;
	}

	public Boolean getCreateTargetPath() {
		return createTargetPath;
	}

	public void setCreateTargetPath(Boolean createTargetPath) {
		this.createTargetPath = createTargetPath;
	}

	@Override
    public String getType() {
	    // TODO Auto-generated method stub
	    return "file.File";
    }

}
