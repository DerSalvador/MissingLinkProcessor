package ch.dersalvador.MissingLinkProcessor.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
/**
 * 
 * representation of the jee.Ear tag of the delivery.xml
 *
 * @author u37792
 * @version  $Revision: #19 $, $Date: 2016/07/18 $
 */
@XmlRootElement(name="j2ee.EAR")
public class EAR extends Deployable 
{

	public EAR() {
	    super();
	    // TODO Auto-generated constructor stub
    }

	public EAR(int id, String groupid, String tag, String file, String name, String version, List<Integer> configurationset, String extraSteps) {
	    super(id, groupid, tag, file, name, version, configurationset, extraSteps);
	    // TODO Auto-generated constructor stub
    }

	public EAR(int id, String groupid, String file, String name, String version, List<Integer> configurationset) {
	    super(id, groupid, file, name, version, configurationset);
	    // TODO Auto-generated constructor stub
    }

	@Override
    public String getType() {
	    // TODO Auto-generated method stub
	    return "jee.Ear";
    }

}
