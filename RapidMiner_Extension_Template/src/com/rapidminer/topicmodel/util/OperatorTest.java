//package com.rapidminer.topicmodel.util;
//
//import java.util.LinkedList;
//import java.util.List;
//
//import org.junit.Test;
//
//import com.rapidminer.example.Attribute;
//import com.rapidminer.example.ExampleSet;
//import com.rapidminer.example.table.AttributeFactory;
//import com.rapidminer.example.table.DoubleArrayDataRow;
//import com.rapidminer.example.table.MemoryExampleTable;
//import com.rapidminer.tools.Ontology;
//
//import static com.rapidminer.topicmodel.util.ExampleSetFormatHelper.doSomeExampleSetStuff;
//
//
//public class OperatorTest 
//{
//	public OperatorTest()
//	{
//		
//	}
//	
//	
//	@Test
//	public void testSymmetricExample()
//	{
//		testDoWork(18, 18);
//	}
//	
//	@Test
//	public void testAsymmetricOne()
//	{
//		testDoWork(1111, 15);
//	}
//	
//	
//	@Test
//	public void testAsymmetricTwo()
//	{
//		testDoWork(6, 19);
//	}
//	
//	@Test
//	public void testAsymmetricTree()
//	{
//		testDoWork(99,20);
//	}
//	
//	private void testDoWork(int _rows, int attributCount)
//	{
//		// create attribute list
//		List <Attribute> attributes = new LinkedList<Attribute>();
//		
//		for ( int a = 0; a < attributCount; a++) 
//		{
//			attributes.add( AttributeFactory.createAttribute ("att" + a,Ontology.REAL));
//		}
//		
//		Attribute label = AttributeFactory.createAttribute ("label", Ontology.NOMINAL);
//		attributes.add(label);
//		
//		//create table
//		MemoryExampleTable table = new MemoryExampleTable(attributes);
//		// fill table (here : only real values )
//		for (int d = 0; d < _rows; d++) 
//		{
//			double[] data = new double[attributes.size()];
//			for (int a = 0; a < attributes.size(); a++) 
//			{
//				//fill with proper data here
//				data[a] = 121.4;
//			}
//				// maps the nominal classification to a double value
//				data[data.length - 1] = label.getMapping().mapString("hu");
//				//add data row
//				table.addDataRow(new DoubleArrayDataRow(data));
//		}
//		// create example set
//		ExampleSet exampleSet = table.createExampleSet(label);
//			
//		doSomeExampleSetStuff(exampleSet);
//	}
//}