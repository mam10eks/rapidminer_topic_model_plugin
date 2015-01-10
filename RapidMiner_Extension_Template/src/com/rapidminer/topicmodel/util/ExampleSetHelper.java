package com.rapidminer.topicmodel.util;

import com.rapidminer.example.Example;


/**
 * A helper to unsure that some informations of an {@link com.rapidminer.example.Example Example}
 * are accessed in a right and consist way.<br>
 * This informations are used to store besides instances.
 * <br>
 * These informations are:<br>
 * <br>
 * FILE_NAME -> filename of the document<br>
 * FILE_PATH -> total path of the document<br>
 * DOCUMENT_LABEL -> label of a document<br>
 * 
 * @author maik
 *
 */
public class ExampleSetHelper 
{
	private ExampleSetHelper()
	{
	}

	/** name of the document file. */
	public static final String
		FILE_NAME = "metadata_file";
	
	/** full path of the document file. */
	public static final String
		FILE_PATH = "metadata_path";
	
	/** label of the document. */
	public static final String
		DOCUMENT_LABEL = "label";
	
	/**
	 * This information is will typically be stored in the target value.
	 * 
	 * @param _example document
	 * @return the target value, in this case the filename.
	 */
	public static String getInstanceTarget(Example _example)
	{
		String ret =(String) _example.get(FILE_NAME);
		
		return (ret != null) ? ret : "unknown_target";
	}
	
	
	/**
	 * This information is will typically be stored in the source value.
	 * 
	 * @param _example document
	 * @return the source value, in this case the full file path.
	 */
	public static String getInstanceSource(Example _example)
	{
		String ret = (String) _example.get(FILE_PATH);
		
		return (ret != null) ? ret : "unknown_source";
	}
	
	
	/**
	 * This information is will typically be stored in the name value.
	 * 
	 * @param _example document
	 * @return the target value, in this case the label.
	 */
	public static String getInstanceName(Example _example)
	{
		String ret = (String) _example.get(DOCUMENT_LABEL);
		
		return (ret != null) ? ret : "unknown_name";
	}
}