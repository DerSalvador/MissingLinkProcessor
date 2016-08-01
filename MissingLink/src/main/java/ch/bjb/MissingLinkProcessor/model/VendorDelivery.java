package ch.bjb.MissingLinkProcessor.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlAccessType;
/**
 * 
 * representation of the VendorDelivery tag of the delivery.xml
 *
 * @author u37792
 * @version  $Revision: #18 $, $Date: 2016/07/13 $
 */
@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
@XmlSeeAlso({Contact.class,ConfigurationSet.class,Deployable.class})
public class VendorDelivery 
{
	@XmlAttribute
	String version;
	
	@XmlAttribute
	String application;
	
	@XmlElementWrapper
	@XmlElement(name="contact")
	List<Contact> contacts;
	
	@XmlElementWrapper
	@XmlElement(name="configurationSet")
	List<ConfigurationSet> configurations;
	
	@XmlElementWrapper
	@XmlElementRef()
	List<Deployable> deployables;
	
	@XmlElementWrapper
	@XmlElementRef()
	List<Documentation> documentation;

	public String getVersion() {
		return version;
	}
	
	
	public void setVersion(String version) {
		this.version = version;
	}
	public String getApplication() {
		return application;
	}
	
	
	public void setApplication(String application) {
		this.application = application;
	}

	
	public List<Contact> getContacts() {
		return contacts;
	}
	
	
	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}
	
	
	
	public List<ConfigurationSet> getConfigurations() {
		return configurations;
	}

	
	public void setConfigurations(List<ConfigurationSet> configurations) {
		this.configurations = configurations;
	}
	
	

	public List<Deployable> getDeployables() {
		return deployables;
	}


	public void setDeployables(List<Deployable> deployables) {
		this.deployables = deployables;
	}


	public List<Documentation> getDocumentation() {
		return documentation;
	}


	public void setDocumentation(List<Documentation> documentation) {
		this.documentation = documentation;
	}


	@Override
    public String toString() {
	    return "VendorDelivery [version=" + version + ", application=" + application + ", contacts=" + contacts + ", configurations="
	                    + configurations + ", deployables=" + deployables + ", documentation=" + documentation + "]";
    }
	
	

}
