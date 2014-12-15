package com.rapidminer.topicmodel.util;

import java.util.ArrayList;
import java.util.List;

import cc.mallet.types.Alphabet;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.ExampleTable;
import com.rapidminer.topicmodel.helper;


/**
 * 
 * @author maik
 *
 */
public class MalletInputTransformationHelper
{
	
//	public static void main(String[] args)
//	{
//		ExampleSet es = getSmallTestExampleSet();
//		
//		StringWriter writer = new StringWriter();
//		doSomeExampleSetStuff(es, writer);
//		System.out.println(writer.toString());
//		
//
//		Alphabet alph = getAlphabetFromExampleSet(es);
//
//		InstanceList featureVectorInstances = new InstanceList(alph, null);
//		
//		for(Example example : es)
//		{
//			double[] values = new double[es.getAttributes().size()];
//			int row = 0;
//			for(Attribute attr : es.getAttributes())
//			{
//				values[row++] = example.getNumericalValue(attr);
//			}
//			FeatureVector exampleVector = new FeatureVector(featureVectorInstances.getAlphabet(), values);
//			
//			System.out.println(exampleVector.toString());
//			featureVectorInstances.add(new Instance(exampleVector, "target", "name", "source"));
//		}
//		
//		
//		InstanceList featureSequenceInstances = new InstanceList(alph, null);
//		for(Example example : es)
//		{
//			int[] features = termFrequenceExampleToFeatures(example, featureSequenceInstances.getAlphabet());
//			FeatureSequence data = new FeatureSequence(featureSequenceInstances.getAlphabet(), features);
//			
//			System.out.println(data.toString());
//			featureSequenceInstances.add(new Instance(data, "target", "name", "source"));
//		}
//	}
	
	
	
	/**
	 * This determines the Alphabet from _example and will call<br>
	 * {@link #termFrequenceExampleToFeatures(Example, Alphabet)}.
	 * 
	 * @param _example _example Document in the form of a WordVector with term occurrences (total count).
	 * 
	 * @return An array where int[i] gives the index in _alphabet of the ith element of the sequence.<br>
	 * 	ATTENTION: The order is lexicographical.<br>
	 *  NOTE: We cant restore the initial order (scheduled by the outhor of the document),<br>
	 *  since we expect the Example as WordVector. 
	 */
	public static int[] termFrequenceExampleToFeatures(Example _example)
	{	
		List<String> termList = new ArrayList<String>();
		
		//TODO capsule!!
		for(Attribute attr : _example.getAttributes())
		{
			String alphabetObject = attr.getName();
			
			if(! termList.contains(alphabetObject) && attr.isNumerical())
			{
				termList.add(alphabetObject);
			}
		}
		
		Alphabet alphabet = new Alphabet(termList.toArray(new String[termList.size()]));
		
		return termFrequenceExampleToFeatures(_example, alphabet);
	}
	
	
	/**
	 * 
	 * Implementation Requirements:<br>
	 * 
	 * Term occurences in _example that are not contained in the _alphabet are not included.
	 * <br>
	 * 
	 * @param _example Document in the form of a WordVector with term occurrences (total count).
	 * 
	 * @param _alphabet A dictionary that maps objects in the _example to numeric indices.
	 * 
	 * @return An array where int[i] gives the index in _alphabet of the ith element of the sequence.<br>
	 * 	ATTENTION: The order is lexicographical.<br>
	 *  NOTE: We cant restore the initial order (scheduled by the outhor of the document),<br>
	 *  since we expect the Example as WordVector. 
	 */
	public static int[] termFrequenceExampleToFeatures(Example _example, Alphabet _alphabet)
	{
		List<Integer> features = new ArrayList<Integer>();
		
		int alphabetSize = _alphabet.size();
		
		for(int i=0; i< alphabetSize; i++)
		{
			Object alphabetKey = _alphabet.lookupObject(i);
			
			int alphabetKeyFrequency = 0;
			if(_example.get(alphabetKey) instanceof Number)
			{
				alphabetKeyFrequency = ((Number) _example.get(alphabetKey)).intValue();
			}

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
	
	
	/**
	 * 
	 * ATTENTION:<br>
	 * Only numerical Attributes will be added to the Alphabet.
	 * 
	 * @param _exampleSetList Amount of ExampleSets, where it is to determine the common Alphabet.
	 * @return Common Alphabet.<br>
	 * Each AlphabetObject is a numerical Attribute from a ExampleSet.
	 * 
	 */
	public static Alphabet getAlphabetFromExampleSet(List<ExampleSet> _exampleSetList)
	{
		
		
		List<String> globalAlphabet = new ArrayList<String>();
		
		for(ExampleSet exampleSet : _exampleSetList)
		{
			for(Attribute attr : exampleSet.getAttributes())
			{
				String alphabetObject = attr.getName();
				
				if(! globalAlphabet.contains(alphabetObject) && attr.isNumerical())
				{
					globalAlphabet.add(alphabetObject);
				}
			}
		}
		
		Alphabet ret = new Alphabet(globalAlphabet.toArray(new String[globalAlphabet.size()]));
		
		return ret;
	}
	
	
//	/**
//	 * 
//	 * ATTENTION:<br>
//	 * Only numerical Attributes will be added to the Alphabet.
//	 * 
//	 * @param ExampleSet, where it is to determine the common Alphabet.
//	 * @return Common Alphabet.<br>
//	 * Each AlphabetObject is a numerical Attribute from the ExampleSet.
//	 * 
//	 */
//	public static Alphabet getAlphabetFromExampleSet(ExampleSet _exampleSet)
//	{
//		List<ExampleSet> exampleSetList = new ArrayList<ExampleSet>();
//		exampleSetList.add(_exampleSet);
//		return getAlphabetFromExampleSet(exampleSetList);
//	}
	
	/**
	 * 
	 * ATTENTION:<br>
	 * Only numerical Attributes will be added to the Alphabet.
	 * 
	 * @param Amount of ExampleSeta, where it is to determine the common Alphabet.
	 * @return Common Alphabet.<br>
	 * Each AlphabetObject is a numerical Attribute from a ExampleSet.
	 * 
	 */
	public static Alphabet getAlphabetFromExampleSet(ExampleSet..._exampleSet)
	{
		List<ExampleSet> exampleSetList = new ArrayList<ExampleSet>();
		
		for(ExampleSet eSet : _exampleSet)
		{
			exampleSetList.add(eSet);
		}
		
		return getAlphabetFromExampleSet(exampleSetList);
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