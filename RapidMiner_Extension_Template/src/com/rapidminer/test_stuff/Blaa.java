package com.rapidminer.test_stuff;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.ExampleTable;
import com.rapidminer.topicmodel.helper;

import static com.rapidminer.operator.preprocessing.transformation.Numerical2DateOperator.doSomeExampleSetStuff;

public class Blaa 
{
	
	public static void main(String[] args)
	{
		ExampleSet es = getSmallTestExampleSet();
		
		StringWriter writer = new StringWriter();
		doSomeExampleSetStuff(es, writer);
		System.out.println(writer.toString());
		
		
//		String[] alphabet = new String[es.getAttributes().size()];
//		
//		int i=0;
//		for(Attribute attr : es.getAttributes())
//		{
//			alphabet[i++] = attr.getName();
//		}

		Alphabet alph = getAlphabetFromExampleSet(es);

		InstanceList featureVectorInstances = new InstanceList(alph, null);
		
		for(Example example : es)
		{
			double[] values = new double[es.getAttributes().size()];
			int row = 0;
			for(Attribute attr : es.getAttributes())
			{
				values[row++] = example.getNumericalValue(attr);
			}
			FeatureVector exampleVector = new FeatureVector(featureVectorInstances.getAlphabet(), values);
			
			System.out.println(exampleVector.toString());
			featureVectorInstances.add(new Instance(exampleVector, "target", "name", "source"));
		}
		
		
		InstanceList featureSequenceInstances = new InstanceList(alph, null);
		for(Example example : es)
		{
			int[] features = termFrequenceExampleToFeatures(example, featureSequenceInstances.getAlphabet());
			FeatureSequence data = new FeatureSequence(featureSequenceInstances.getAlphabet(), features);
			
			System.out.println(data.toString());
			featureSequenceInstances.add(new Instance(data, "target", "name", "source"));
		}
	}
	
	
	public static int[] termFrequenceExampleToFeatures(Example _example)
	{
		List<String> allAttributes = new ArrayList<String>();
		for(Attribute attr : _example.getAttributes())
		{
			allAttributes.add(attr.getName());
		}
		
		Alphabet alphabet =  new Alphabet(allAttributes.toArray(new String[allAttributes.size()]));
		return termFrequenceExampleToFeatures(_example, alphabet);
	}
	
	public static int[] termFrequenceExampleToFeatures(Example _example, Alphabet _alphabet)
	{
		List<Integer> features = new ArrayList<Integer>();
		
		int alphabetSize = _alphabet.size();
		
		for(int i=0; i< alphabetSize; i++)
		{
			Object alphabetKey = _alphabet.lookupObject(i);
			int alphabetKeyFrequency = ((Number) _example.get(alphabetKey)).intValue();
			for(int j=0; j<alphabetKeyFrequency; j++)
			{
				features.add(i);
			}
		}
		int[] ret = new int[features.size()];
		
		for(int i=0; i<ret.length;i++)
		{
			ret[i] = features.get(i);
		}
		
		return ret;
	}
	
	
	public static Alphabet getAlphabetFromExampleSet(ExampleSet _exampleSet)
	{
		String[] alphabetObjects = new String[_exampleSet.getAttributes().size()];
		
		int i=0;
		for(Attribute attr : _exampleSet.getAttributes())
		{
			alphabetObjects[i++] = attr.getName();
		}

		return new Alphabet(alphabetObjects);
	}
	
	/**
	 * Creates a small ExampleSet for test purposes.<br>
	 * This ExampleSet is similar to a sample text-processing input for this processor.<br>
	 * <br>
	 * ATTENTION: only term frequency vectors are supported. NOT tf-idf ore such stuff.
	 * <br>
	 * <br>
	 * The ExampleSet contains the following examples(i.e. "documents"):<br>
	 * example 1: "Dies ist ein hello world ExampleSet."<br>
	 * example 2: "Zum testen reicht es aus."<br>
	 * example 3: "Oder doch nicht?"
	 * @return
	 */
	public static ExampleSet getSmallTestExampleSet()
	{
		List<Attribute> attributes = helper.createAttributeList();
		
		List<List<Number>> documents = helper.createExampleTebleRows();
		
		ExampleTable exampleTable = helper.createExampleTable(attributes, documents);
		
		return exampleTable.createExampleSet();
	}
	
}