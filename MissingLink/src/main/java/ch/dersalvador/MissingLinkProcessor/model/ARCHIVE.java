package ch.dersalvador.MissingLinkProcessor.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
/**
 * 
 * representation of the file.Folder tag of the delivery.xml
 *
 * @author u37792
 * @version  $Revision: #21 $, $Date: 2016/07/18 $
 */
@XmlRootElement(name="file.ARCHIVE")
public class ARCHIVE extends Deployable 
{

	String targetPath;

	public Boolean getTargetPathShared() {
		return targetPathShared;
	}

	public void setTargetPathShared(Boolean targetPathShared) {
		this.targetPathShared = targetPathShared;
	}

	Boolean targetPathShared;
	Boolean createTargetPath;

	public Boolean getScanPlaceHolders() {
		return scanPlaceHolders;
	}

	public void setScanPlaceHolders(Boolean scanPlaceHolders) {
		this.scanPlaceHolders = scanPlaceHolders;
	}

	Boolean scanPlaceHolders;

	public ARCHIVE() {
	    super();
	    // TODO Auto-generated constructor stub
    }

	public ARCHIVE(int id, String groupid, String tag, String file, String name, String version, List<Integer> configurationset,String extaSteps) {
	    super(id, groupid, tag, file, name, version, configurationset,extaSteps);
	    // TODO Auto-generated constructor stub
    }

	public ARCHIVE(int id, String groupid, String file, String name, String version, List<Integer> configurationset) {
	    super(id, groupid, file, name, version, configurationset);
	    // TODO Auto-generated constructor stub
    }
	
	

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
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
	    return "file.Folder";
    }

}
