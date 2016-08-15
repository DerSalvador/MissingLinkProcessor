package ch.dersalvador.MissingLinkProcessor.model;

import javax.xml.bind.annotation.XmlSeeAlso;
/**
 * 
 * abstract base class for Documentation
 * {@link ReleaseNotes}
 * {@link InstallationManual}
 *
 * @author u37792
 * @version  $Revision: #18 $, $Date: 2016/07/13 $
 */
@XmlSeeAlso({ReleaseNotes.class,InstallationManual.class})
public abstract class Documentation {

}
