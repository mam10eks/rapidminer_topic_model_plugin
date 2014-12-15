package com.rapidminer.topicmodel.util;

import static com.rapidminer.topicmodel.TabbedWriterHelper.write;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.ports.metadata.MetaData;
import com.rapidminer.operator.ports.metadata.SimplePrecondition;


/**
 * My first operator.
 * 
 * @author maik
 *
 */
public class ExampleSetFormatHelper
{

	private ExampleSetFormatHelper()
	{
	}
	
	
	public static void doSomeExampleSetStuff(ExampleSet _exampleSet, Writer _writer)
	{
		int 
			rowCount = _exampleSet.size() +1,
			columnCount = _exampleSet.getAttributes().size() +1;

		String[][] rows = new String[rowCount][/*columnCount*/];

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

	

	public static void doSomeExampleSetStuff(ExampleSet _exampleSet)
	{
		
		try 
		{
			File file = new File("/home/maik/Schreibtisch/wordVector");
			FileWriter fileWriter = new FileWriter(file);
			doSomeExampleSetStuff(_exampleSet, fileWriter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}