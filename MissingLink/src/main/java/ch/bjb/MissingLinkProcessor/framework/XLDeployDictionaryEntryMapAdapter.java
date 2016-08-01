package ch.bjb.MissingLinkProcessor.framework;

import java.util.HashMap;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.bjb.MissingLinkProcessor.model.xldeploy.dictionary.Entry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * 
 * this class encapsulates an XLDeploy dictionary
 *
 * @author u37792
 * @version  $Revision: #19 $, $Date: 2016/07/13 $
 */
public class XLDeployDictionaryEntryMapAdapter extends
                XmlAdapter<ch.bjb.MissingLinkProcessor.model.xldeploy.dictionary.Map, java.util.Map<String, String>> 
{
	// Log log = LogFactory.getLog(getClass());
	static Logger log = LogManager.getLogger(XLDeployDictionaryEntryMapAdapter.class);


	@Override
	public ch.bjb.MissingLinkProcessor.model.xldeploy.dictionary.Map marshal(java.util.Map<String, String> arg0) throws Exception 
	{
		log.info(arg0);
		
		ch.bjb.MissingLinkProcessor.model.xldeploy.dictionary.Map myMapType = new ch.bjb.MissingLinkProcessor.model.xldeploy.dictionary.Map();
		/*
		for (java.util.Map.Entry<String, String> entry : arg0.entrySet()) 
		{
			log.info(entry);
			Entry myMapEntryType = new Entry();
			myMapEntryType.key = entry.getKey();
			myMapEntryType.value = entry.getValue();
			myMapType.entries.add(myMapEntryType);
		}
		*/
		return myMapType;
	}

	@Override
	public java.util.Map<String, String> unmarshal(ch.bjb.MissingLinkProcessor.model.xldeploy.dictionary.Map arg0) throws Exception 
	{
		HashMap<String, String> hashMap = new HashMap<String, String>();
		/*
		for (Entry myEntryType : arg0.entries) 
		{
			log.info(myEntryType);
			hashMap.put(myEntryType.key, myEntryType.value);
		}
		*/
		return hashMap;
	}
}
