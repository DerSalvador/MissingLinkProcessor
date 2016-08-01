package ch.bjb.MissingLinkProcessor.model.localproperty;

import javax.xml.bind.annotation.XmlElement;
/**
 * 
 * representation of the Replacement tag of the deliverylocationlookup-APPLICATIONNAME.xml
 *
 * @author u37792
 * @version  $Revision: #18 $, $Date: 2016/07/13 $
 */
public class Replacement 
{
	
	@XmlElement
	String searchtext;
	@XmlElement
	String substitution;
	
	
	public Replacement() 
	{
	    super();
	    // TODO Auto-generated constructor stub
    }


	public Replacement(String searchtext, String substitution) {
	    super();
	    this.searchtext = searchtext;
	    this.substitution = substitution;
    }

	

	public String getSearchtext() {
		return searchtext;
	}


	public String getSubstitution() {
		return substitution;
	}

	

	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((searchtext == null) ? 0 : searchtext.hashCode());
	    result = prime * result + ((substitution == null) ? 0 : substitution.hashCode());
	    return result;
    }


	@Override
    public boolean equals(Object obj) {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    Replacement other = (Replacement) obj;
	    if (searchtext == null) {
		    if (other.searchtext != null)
			    return false;
	    } else if (!searchtext.equals(other.searchtext))
		    return false;
	    if (substitution == null) {
		    if (other.substitution != null)
			    return false;
	    } else if (!substitution.equals(other.substitution))
		    return false;
	    return true;
    }


	@Override
    public String toString() {
	    return "Replacement [searchtext=" + searchtext + ", substitution=" + substitution + "]";
    }
	
	
	

}
