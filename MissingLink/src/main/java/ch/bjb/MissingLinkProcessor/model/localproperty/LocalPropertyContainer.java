package ch.bjb.MissingLinkProcessor.model.localproperty;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
/**
 * 
 * representation of the LocalPropertyContainer tag of the deliverylocationlookup-APPLICATIONNAME.xml
 *
 * @author u37792
 * @version  $Revision: #23 $, $Date: 2016/07/18 $
 */
public class LocalPropertyContainer 
{
	@XmlAttribute
	String deployable;
	@XmlAttribute
	String file;
	
	@XmlElement
	String targetPath;

	public Boolean getTargetPathShared() {
		return targetPathShared;
	}

	@XmlElement
	Boolean targetPathShared;

	@XmlElement
	String targetFileName;
	@XmlElement
	Boolean targetPathCreate;
	
	@XmlElement(name="replacement")
	List <Replacement>replacements;

	public boolean getScanPlaceholders() {
		return scanPlaceholders;
	}

	@XmlElement
	boolean scanPlaceholders;
	
	public LocalPropertyContainer() 
	{
    }


	public LocalPropertyContainer(String deployable, String file, String targetPath, String targetFileName, Boolean targetPathCreate, List<Replacement> replacements, boolean scanPlaceholders, String extraSteps) {
		this.deployable = deployable;
		this.file = file;
		this.targetPath = targetPath;
		this.targetFileName = targetFileName;
		this.targetPathCreate = targetPathCreate;
		this.replacements = replacements;
		this.scanPlaceholders = scanPlaceholders;
	}

	public String getDeployable() {
		return deployable;
	}


	public String getFile() {
		return file;
	}


	public String getTargetPath() {
		return targetPath;
	}


	public String getTargetFileName() {
		return targetFileName;
	}
	


	public Boolean getTargetPathCreate() {
		return targetPathCreate;
	}
	

	public List<Replacement> getReplacements() {
		return replacements;
	}


	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((deployable == null) ? 0 : deployable.hashCode());
	    result = prime * result + ((file == null) ? 0 : file.hashCode());
	    return result;
    }


	@Override
    public boolean equals(Object obj) {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    LocalPropertyContainer other = (LocalPropertyContainer) obj;
	    if (deployable == null) {
		    if (other.deployable != null)
			    return false;
	    } else if (!deployable.equals(other.deployable))
		    return false;
	    if (file == null) {
		    if (other.file != null)
			    return false;
	    } else if (!file.equals(other.file))
		    return false;
	    return true;
    }


	@Override
    public String toString() {
	    return "LocalPropertyContainer [deployable=" + deployable + ", file=" + file + ", targetPath=" + targetPath + ", targetFileName="
	                    + targetFileName + ", targetPathCreate=" + targetPathCreate + ", replacements=" + replacements + "]";
    }


}
