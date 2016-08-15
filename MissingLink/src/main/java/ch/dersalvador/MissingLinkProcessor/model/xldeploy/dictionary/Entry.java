package ch.dersalvador.MissingLinkProcessor.model.xldeploy.dictionary;

import javax.xml.bind.annotation.XmlElement;

public class Entry 
{
	@XmlElement(name="string")
	public String key;
	@XmlElement(name="string")
	public String value;
	
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}

	@Override
    public String toString() {
	    return "Entry [key=" + key + ", value=" + value + "]";
    }

	
	
}
