package ch.dersalvador.MissingLinkProcessor.model;

import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * 
 * representation of the File tag of the delivery.xml
 *
 * @author u37792
 * @version  $Revision: #20 $, $Date: 2016/07/13 $
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DeployableFile
{
	@XmlAttribute
	int id;
	@XmlAttribute
	String file;
	@XmlAttribute
	String name;

	Boolean scanPlaceholders;

	@XmlElement(name="ci")
	List<Ci> cis;

	String targetPath;

	public Boolean getTargetPathShared() {
		return targetPathShared;
	}

	public void setTargetPathShared(Boolean targetPathShared) {
		this.targetPathShared = targetPathShared;
	}

	Boolean targetPathShared;
	String targetFileName;
	Deployable parent;

	public Boolean getScanPlaceholders() {
		return scanPlaceholders;
	}

	public void setScanPlaceholders(Boolean scanPlaceholders) {
		this.scanPlaceholders = scanPlaceholders;
	}

	Boolean createTargetPath;
	String displayFileName;
	HashMap<String,String>replacements;

	public DeployableFile() {
	    super();
	    // TODO Auto-generated constructor stub
    }

	public DeployableFile(int id, String file, String name, List<Ci> cis) {
	    super();
	    this.id = id;
	    this.file = file;
	    this.name = name;
	    this.cis = cis;
    }

	public DeployableFile(int id, String file, String name, List<Ci> cis, String targetPath, String targetFileName, Boolean createTargetPath, String displayFileName, HashMap<String, String> replacements) {
		this.id = id;
		this.file = file;
		this.name = name;
		this.cis = cis;
		this.targetPath = targetPath;
		this.targetFileName = targetFileName;
		this.createTargetPath = createTargetPath;
		this.displayFileName = displayFileName;
		this.replacements = replacements;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Ci> getCis() {
		return cis;
	}

	public void setCis(List<Ci> cis) {
		this.cis = cis;
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
	
	

	public String getDisplayFileName() {
		return displayFileName;
	}

	public void setDisplayFileName(String displayFileName) {
		this.displayFileName = displayFileName;
	}
	
	

	public HashMap<String, String> getReplacements() {
		return replacements;
	}

	public void setReplacements(HashMap<String, String> replacements) {
		this.replacements = replacements;
	}

	@Override
    public String toString() {
	    return "File [id=" + id + ", file=" + file + ", name=" + name + ", cis=" + cis + ", targetPath=" + targetPath + ", targetFileName="
	                    + targetFileName + ", createTargetPath=" + createTargetPath + ", displayFileName=" + displayFileName
	                    + ", replacements=" + replacements + "]";
    }


	public void setParent(Deployable parent) {
		this.parent = parent;
	}

	public Deployable getParent() {
		return parent;
	}
}
