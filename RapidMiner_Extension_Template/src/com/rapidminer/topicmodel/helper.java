package com.rapidminer.topicmodel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.table.ExampleTable;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.operator.preprocessing.filter.Numerical2Date;
import com.rapidminer.operator.preprocessing.transformation.Numerical2DateOperator;
import com.rapidminer.tools.Ontology;
import com.rapidminer.example.table.DataRow;
import com.rapidminer.example.table.DataRowFactory;

public class helper 
{
	public helper()
	{
	}
	
	@Test
	public void testCreateExampleSet()
	{
		List<Attribute> attributes = createAttributeList();
		
		ExampleTable table = createExampleTable(attributes, createExampleTebleRows());
		ExampleSet es = table.createExampleSet();
		
		System.out.println(es);
		
		Numerical2DateOperator.doSomeExampleSetStuff(es);
	}
	
	public static ExampleTable createExampleTable(List<Attribute> _attributes, List<List<Number>> _inputRows)
	{
		MemoryExampleTable table = new MemoryExampleTable(_attributes);
		MyDataRowFactory factory = MyDataRowFactory.getNewInstance(_attributes);
		
		for(List<Number> row : _inputRows)
		{
			DataRow dataRow = factory.createRow(row);
			table.addDataRow(dataRow);
		}
		
		return table; 
	}
	
	
	
	public static List<Attribute> createAttributeList()
	{
		List<Attribute> ret = new ArrayList<Attribute>();

		ret.add(AttributeFactory.createAttribute("hallo", Ontology.NUMERICAL));
		ret.add(AttributeFactory.createAttribute("mein",Ontology.NUMERICAL));
		ret.add(AttributeFactory.createAttribute("name",Ontology.NUMERICAL));
		ret.add(AttributeFactory.createAttribute("ist",Ontology.NUMERICAL));
		ret.add(AttributeFactory.createAttribute("maik",Ontology.NUMERICAL));
		ret.add(AttributeFactory.createAttribute("gib",Ontology.NUMERICAL));
		ret.add(AttributeFactory.createAttribute("mir",Ontology.NUMERICAL));
		ret.add(AttributeFactory.createAttribute("all",Ontology.NUMERICAL));
		ret.add(AttributeFactory.createAttribute("dein",Ontology.NUMERICAL));
		ret.add(AttributeFactory.createAttribute("geld",Ontology.NUMERICAL));
		
		return ret;
	}
	
	public static List<List<Number>> createExampleTebleRows()
	{
		List<List<Number>> ret = new ArrayList<List<Number>>();
		
		List<Number> row = new ArrayList<Number>();
		row.add(0.5);
		row.add(0.5);
		row.add(0.5);
		row.add(0.5);
		row.add(0.5);
		row.add(0);
		row.add(0);
		row.add(0);
		row.add(0);
		row.add(0);
		
		ret.add(row);
		
		
		row = new ArrayList<Number>();
		row.add(0);
		row.add(0);
		row.add(0);
		row.add(0);
		row.add(0);
		row.add(0.7);
		row.add(0.7);
		row.add(0.7);
		row.add(0.7);
		row.add(0.7);
		
		ret.add(row);
		
		row = new ArrayList<Number>();
		row.add(0.5);
		row.add(0.5);
		row.add(0.5);
		row.add(0.5);
		row.add(0.5);
		row.add(0);
		row.add(0);
		row.add(0);
		row.add(0);
		row.add(0);
		
		ret.add(row);
		
		return ret;
	}
}


abstract class MyDataRowFactory
{
	public abstract DataRow createRow(List<Number> _inputRow);
	
	public static MyDataRowFactory getNewInstance(List<Attribute> _attributes)
	{
		DataRowFactory factory = new DataRowFactory(DataRowFactory.TYPE_DOUBLE_ARRAY, '.');
		
		return new MyDataRowFactoryImpl(_attributes, factory);
	}
	
		
	private MyDataRowFactory(){}

	private static final class MyDataRowFactoryImpl extends MyDataRowFactory
	{
		 private final DataRowFactory factory;
	     private final Attribute[] attributes;
	     
	     private MyDataRowFactoryImpl(List<Attribute> _attributes, DataRowFactory _factory)
	     {
	    	 assert _factory != null;
	         assert _attributes != null;
	         this.attributes = _attributes.toArray(new Attribute[_attributes.size()]);
	         this.factory = _factory;
	     }

		@Override
		public DataRow createRow(List<Number> _inputRow) 
		{
			Number[] numbers = _inputRow.toArray(new Number[_inputRow.size()]);
		    return factory.create(numbers, attributes);
		}
	}
}