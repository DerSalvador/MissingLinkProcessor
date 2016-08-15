package ch.dersalvador.MissingLinkProcessor.model.xldeploy.dictionary;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="map")
public class Map 
{
	@XmlElement(name="entry")
	//@XmlJavaTypeAdapter(XLDeployDictionaryEntryMapAdapter.class)
    public List<Entry> entries;

	
	
	public List<Entry> getEntries() {
		return entries;
	}



	@Override
    public String toString() {
	    return "Map [entries=" + entries + "]";
    }
	
	
}
