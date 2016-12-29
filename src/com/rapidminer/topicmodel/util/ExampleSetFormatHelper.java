package com.rapidminer.topicmodel.util;

import static com.rapidminer.topicmodel.TabbedWriterHelper.write;

import java.io.IOException;
import java.io.Writer;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;


/**
 * This util class prepare some basic methods to write an {@link com.rapidminer.example.ExampleSet ExampleSet} formatted to an arpitrary place.
 * E.g. to a file or stdout.
 * 
 * 
 * 
 * @author maik
 *
 */
public class ExampleSetFormatHelper
{

	private ExampleSetFormatHelper()
	{
	}
	
	
	/**
	 * 
	 * @param _exampleSet 
	 * @param _writer _exampleSet will be written to this.
	 */
	public static void writeExampleSetFormatted(ExampleSet _exampleSet, Writer _writer)
	{
		int 
			rowCount = _exampleSet.size() +1,
			columnCount = _exampleSet.getAttributes().size() +1;

		String[][] rows = new String[rowCount][];

		int i=1;
	
		String[] tmp = new String[columnCount];
		tmp[0] = "";
		for(Attribute attr : _exampleSet.getAttributes())
		{
			tmp[i++] = attr.getName();
		}
		rows[0] = tmp;
		
		for(i= 1;i <rowCount; i++)
		{
			Example example = _exampleSet.getExample(i-1);
			tmp = new String[columnCount];
			int j = 1;
			tmp[0] = "Row "+ i;
		
			for(Attribute attr : _exampleSet.getAttributes())
			{
				tmp[j++] = String.valueOf(example.getNumericalValue(attr));
			}	
			
			rows[i] = tmp;
		}
	
		try 
		{
			write(rows, _writer);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}	
	}

	

//	/**
//	 * Writes _exampleSet to the File "/home/maik/Schreibtisch/wordVector"
//	 * 
//	 * @param _exampleSet table to write to the file.
//	 */
//	public static void writeExampleSetFormattedToMyFile(ExampleSet _exampleSet)
//	{	
//		try 
//		{
//			File file = new File("/home/maik/Schreibtisch/wordVector");
//			FileWriter fileWriter = new FileWriter(file);
//			writeExampleSetFormatted(_exampleSet, fileWriter);
//		} 
//		catch (IOException e) 
//		{
//			e.printStackTrace();
//		}
//	}
}