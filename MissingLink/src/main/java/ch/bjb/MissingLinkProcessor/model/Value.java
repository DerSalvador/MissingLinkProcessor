package ch.bjb.MissingLinkProcessor.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;
/**
 * 
 * representation of the Value tag of the delivery.xml
 *
 * @author u37792
 * @version  $Revision: #18 $, $Date: 2016/07/13 $
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Value 
{
	@XmlAttribute
	String type;
	@XmlValue
	String value;
	
	
	
	public Value() {
	    super();
	    // TODO Auto-generated constructor stub
    }



	public Value(String type, String value) {
	    super();
	    this.type = type;
	    this.value = value;
    }



	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}



	public String getValue() {
		return value;
	}



	public void setValue(String value) {
		this.value = value;
	}



	@Override
    public String toString() {
	    return "Value [type=" + type + ", value=" + value + "]";
    }
	
	
}
