package ch.dersalvador.MissingLinkProcessor.model.localproperty;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * representation of the DeliveryLocationLookup tag of the deliverylocationlookup-APPLICATIONNAME.xml
 *
 * @author u37792
 * @version  $Revision: #19 $, $Date: 2016/07/18 $
 */
@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class DeliveryLocationLookup 
{
	@XmlAttribute
	String name;

	@XmlAttribute
	String path;

	@XmlElement(name="localPropertyContainer")
	List<LocalPropertyContainer> localPropertyContainer;

	public String getExtraSteps() {
		return extraSteps;
	}

	@XmlElement(name="extraSteps")
	String extraSteps;

	public String getName() {
		return name;
	}

	
	public String getPath() {
		return path;
	}


	public List<LocalPropertyContainer> getLocalPropertyContainer() {
		return localPropertyContainer;
	}


	
}
