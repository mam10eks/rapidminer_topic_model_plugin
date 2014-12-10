package com.rapidminer.operator.preprocessing.transformation;

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
public class Numerical2DateOperator extends Operator
{
	/**
	 * Unique name values for each Port.
	 */
	private static final String
		UNIQUE_INPUT_PORT_NAME = "example set input",
		UNIQUE_OUTPUT_PORT_NAME = "example set output";
		
	
	/**
	 * Unique port.
	 */
	private InputPort 
		exampleSetInput = getInputPorts().createPort(UNIQUE_INPUT_PORT_NAME);
	
	/**
	 * unique port.
	 */
	private OutputPort
		exampleSetOutput = getOutputPorts().createPort(UNIQUE_OUTPUT_PORT_NAME);
	
	
	/**
	 * 
	 * @param description
	 */
	public Numerical2DateOperator(OperatorDescription description)
	{
		super(description);
		this.exampleSetInput.addPrecondition(new SimplePrecondition(exampleSetInput, new MetaData(ExampleSet.class)));
//		this.exampleSetInput.addPrecondition(new ExampleSetPrecondition(exampleSetInput,new String[]{"relative time"},Ontology.ATTRIBUTE_VALUE));
	
		
		this.exampleSetOutput.deliverMD(new MetaData(ExampleSet.class));
		
	}

	
	/**
	 * We do some test work.
	 */
	@Override
	public void doWork() throws OperatorException
	{
		ExampleSet exampleSet = exampleSetInput.getData(ExampleSet.class);
	
		doSomeExampleSetStuff(exampleSet);

//		Attributes attributes = exampleSet.getAttributes();
//		Attribute sourceAttribute = attributes.get("relative time");
//		String newName = "date("+ sourceAttribute.getName() +")";
//		Attribute targetAttribute = AttributeFactory.createAttribute(newName, Ontology.DATE_TIME);
//		targetAttribute.setTableIndex(sourceAttribute.getTableIndex());
//		attributes.addRegular(targetAttribute);
//		attributes.remove(sourceAttribute);
//		
//		for(Example example : exampleSet)
//		{
//			double timeStampValue = example.getValue(targetAttribute);
//			example.setValue(targetAttribute, timeStampValue * 1000);
//		}
//		
		exampleSetOutput.deliver(exampleSet);
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