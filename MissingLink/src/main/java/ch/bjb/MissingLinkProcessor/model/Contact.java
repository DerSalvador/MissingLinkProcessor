package ch.bjb.MissingLinkProcessor.model;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
/**
 * 
 * representation of the Contact tag of the delivery.xml
 *
 * @author u37792
 * @version  $Revision: #18 $, $Date: 2016/07/13 $
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Contact 
{
	@XmlAttribute
	String role;
	@XmlElement
	String name;
	@XmlElement
	String adress;
	@XmlElement
	String email;
	@XmlElement
	String phone;
	
	
	
	
	public Contact() {
	    super();
	    // TODO Auto-generated constructor stub
    }

	public Contact(String role, String name, String adress, String email, String phone) {
	    super();
	    this.role = role;
	    this.name = name;
	    this.adress = adress;
	    this.email = email;
	    this.phone = phone;
    }

	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAdress() {
		return adress;
	}
	public void setAdress(String adress) {
		this.adress = adress;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Override
    public String toString() {
	    return "Contact [role=" + role + ", name=" + name + ", adress=" + adress + ", email=" + email + ", phone=" + phone + "]";
    }
	
	

}
