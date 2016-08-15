package ch.dersalvador.MissingLinkProcessor.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
/**
 * 
 * representation of the Ci tag of the delivery.xml
 *
 * @author u37792
 * @version  $Revision: #18 $, $Date: 2016/07/13 $
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Ci 
{
	@XmlAttribute
	String key;
	@XmlElement
	Value value;
	
	
	private Ci() {
	    super();
	    // TODO Auto-generated constructor stub
    }

	public Ci(String key, Value value) {
	    super();
	    this.key = key;
	    this.value = value;
    }

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	@Override
    public String toString() {
	    return "Ci [key=" + key + ", value=" + value + "]";
    }

	
}
