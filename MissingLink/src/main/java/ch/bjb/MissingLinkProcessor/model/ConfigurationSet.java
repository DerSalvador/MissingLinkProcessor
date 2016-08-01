package ch.bjb.MissingLinkProcessor.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * 
 * representation of the ConfigurationSet tag of the delivery.xml
 *
 * @author u37792
 * @version  $Revision: #18 $, $Date: 2016/07/13 $
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ConfigurationSet 
{
	@XmlAttribute
	int id;
	@XmlAttribute
	String name;

	@XmlElement(name="file")
	List<DeployableFile> files;
	private Deployable parent;

	public boolean isProcessedToManifest() {
		return processedToManifest;
	}

	public void setProcessedToManifest(boolean processedToManifest) {
		this.processedToManifest = processedToManifest;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	boolean processedToManifest = false;

	List<String> tags = new ArrayList<String>();

	public ConfigurationSet() {
	    super();
	    // TODO Auto-generated constructor stub
    }


	public ConfigurationSet(int id, String name, List<DeployableFile> files) {
	    super();
	    this.id = id;
	    this.name = name;
	    this.files = files;
    }


	public int getId() {
		return id;
	}
	
	
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<DeployableFile> getFiles() {
		return files;
	}


	public void setFiles(List<DeployableFile> files) {
		this.files = files;
	}


	@Override
    public String toString() {
	    return "ConfigurationSet [id=" + id + ", name=" + name + ", files=" + files + "]";
    }


	public void setParent(Deployable parent) {
		this.parent = parent;
	}

	public Deployable getParent() {
		return parent;
	}
}
