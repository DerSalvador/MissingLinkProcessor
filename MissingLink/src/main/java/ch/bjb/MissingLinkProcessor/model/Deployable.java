package ch.bjb.MissingLinkProcessor.model;

import scala.Int;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
/**
 *
 * abstract base class for 
 * {@link ch.bjb.MissingLinkProcessor.model.EAR}
 * {@link ch.bjb.MissingLinkProcessor.model.WAR}
 * {@link ch.bjb.MissingLinkProcessor.model.ARCHIVE}
 * {@link ch.bjb.MissingLinkProcessor.model.SimpleFile}
 *
 * @author u37792
 * @version  $Revision: #21 $, $Date: 2016/07/18 $
 */
@XmlSeeAlso({EAR.class,WAR.class,ARCHIVE.class,SimpleFile.class})
public abstract class Deployable
{
	@XmlAttribute
	int id;
	@XmlAttribute
	String groupid;
	@XmlAttribute
	String tag;
	@XmlAttribute
	String file;
	@XmlAttribute
	String name;
	@XmlAttribute
	String version;
	@XmlAttribute
	Boolean scanPlaceholders;


	@XmlElement(name="configurationset")
	List<Integer>configurationset;

	public String getExtraSteps() {
		return extraSteps;
	}

	String extraSteps = "";

	public Deployable() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Deployable(int id, String groupid, String tag, String file, String name, String version, Boolean scanPlaceholders, List<Integer> configurationset, String extraSteps) {
		this.id = id;
		this.groupid = groupid;
		this.tag = tag;
		this.file = file;
		this.name = name;
		this.version = version;
		this.scanPlaceholders = scanPlaceholders;
		this.configurationset = configurationset;
		this.extraSteps = extraSteps;
	}

	public Deployable(int id, String groupid, String tag, String file, String name, String version, List<Integer> configurationset, String extraSteps) {
		super();
		this.id = id;
		this.groupid = groupid;
		this.tag = tag;
		this.file = file;
		this.name = name;
		this.version = version;
		this.configurationset = configurationset;
		this.extraSteps = extraSteps;

		//extract tag from name
		if(tag == null)
			this.tag = getTagFromName(name);
	}



	public Deployable(int id, String groupid, String file, String name, String version, List<Integer> configurationset) {
		super();
		this.id = id;
		this.groupid = groupid;
		this.file = file;
		this.name = name;
		this.version = version;
		this.configurationset = configurationset;

		this.tag = getTagFromName(name);
	}

	private String getTagFromName(String name) {
		if (name.contains("."))
			return name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase();
		else
			return name.toLowerCase();

	}

	public abstract String getType();


	public int getId() {
		return id;
	}

	public String getGroupid()
	{
		return groupid;
	}

	public String getTag()
	{
		if(tag == null)
			tag = getTagFromName(name);
		return tag;
	}

	public String getFile() {
		return file;
	}

	public String getFileName() {
		return file.substring(file.lastIndexOf("/")+1,file.length());
	}

	public void updateFile(String file)
	{
		this.file = file;
	}


	public String getName() {
		return name;
	}

	public Boolean getScanPlaceholders() {
		return scanPlaceholders;
	}

	public String getVersion() {
		return version;
	}

	public List<Integer> getConfigurationset() {
		return configurationset;
	}

	@Override
	public String toString() {
		return "Deployable [id=" + id + ", groupid=" + groupid + ", tag=" + tag + ", file=" + file + ", name=" + name + ", version="
				+ version + ", scanPlaceholders=" + scanPlaceholders + ", configurationset=" + configurationset + "]";
	}


	public void setExtraSteps(String extraSteps) {
		this.extraSteps = extraSteps;
	}
}
