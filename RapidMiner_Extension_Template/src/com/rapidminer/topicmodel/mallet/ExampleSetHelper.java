package com.rapidminer.topicmodel.mallet;

import com.rapidminer.example.Example;

public class ExampleSetHelper 
{
	private ExampleSetHelper()
	{
	}
	
	
	/**
	 * metadata_file
	 * @param _example
	 * @return
	 */
	public static String getInstanceTarget(Example _example)
	{
		String ret =(String) _example.get("metadata_file");
		
		return (ret != null) ? ret : "unknown_target";
	}
	
	
	/**
	 * metadata_path
	 * @param _example
	 * @return
	 */
	public static String getInstanceSource(Example _example)
	{
		String ret = (String) _example.get("metadata_path");
		
		return (ret != null) ? ret : "unknown_source";
	}
	
	
	/**
	 * label
	 * @param _example
	 * @return
	 */
	public static String getInstanceName(Example _example)
	{
		String ret = (String) _example.get("label");
		
		return (ret != null) ? ret : "unknown_name";
	}
}