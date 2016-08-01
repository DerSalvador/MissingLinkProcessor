package ch.bjb.MissingLinkProcessor.fileprocessors;

/**
 * abstract base class for all file processors
 * 
 *
 * @author u37792
 * @version  $Revision: #19 $, $Date: 2016/07/13 $
 */
public abstract class BaseFileProcessor 
{
	public static final String VARIABLE_IDENTIFIER 	= "$";
	public static final String VARIABLE_IDENTIFIER2 = "${";
	public static final String COMMENT_IDENTIFIER 	= "#";
	public static final String EUQALS_SIGN 			= "=";
	public static final String DOUBLE_QUOTE 		= "\"";


	protected static boolean keyIsNotAVariable(String line, String key)
	{
		if(line.contains(VARIABLE_IDENTIFIER + key))
			return false;
		else
			return true;
	}
}
