package com.rapidminer.topicmodel.mallet;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelSequence;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.table.ExampleTable;
import com.rapidminer.operator.IOObject;
import com.rapidminer.tools.Ontology;
import com.rapidminer.topicmodel.helper;
import com.rapidminer.topicmodel.inferencer.MalletTopicInferencerIOObject;

public class TopicTrainerPostProcessor
{
	private final ParallelTopicModel
		model;
	
	private final InstanceList
		instances;
	
	
	public TopicTrainerPostProcessor(ParallelTopicModel _model, InstanceList _instances)
	{
		this.model = _model;
		
		this.instances = _instances;
	}
	
	
	/**
	 * this method should return an ExampleSet as overview of all trained topics.<br>
	 * For each Topic there will be shown the _topWords most frequently words of the topic.
	 * 
	 * @return
	 */
	public ExampleSet getAllTopics(int _topWords)
	{
		List<Attribute> attributes = new ArrayList<Attribute>();

		List<List<Object>> rows = new ArrayList<List<Object>>();
		Object[][] topWords = model.getTopWords(_topWords);
		List<Object>[] row = new ArrayList[_topWords];
		
		for(int i = 0; i < _topWords; i++)
		{
			row[i] = new ArrayList<Object>();			
		}
		
		for(int i = 0; i < topWords.length; i++)
		{			
			attributes.add(AttributeFactory.createAttribute("Topic " + i, Ontology.NOMINAL));
			
			for(int f = 0; f < topWords[i].length; f++)
			{
				row[f].add(topWords[i][f]);
			}
		}	
		
		for(int i = 0; i < _topWords; i++)
		{
			rows.add(row[i]);
		}
		
		ExampleTable table = helper.createObjectExampleTable(attributes, rows);
		ExampleSet es = table.createExampleSet();	
		
		return es;
	}
	
	/**
	 * this method should return an ExampleSet with the probability that a topic is part of a document.<br>
	 * For each Topic there will be shown the probability that Topic is in document.
	 * 
	 * @return 
	 */
	public ExampleSet getTopicDistributionForAllInstances()
	{
		List<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(AttributeFactory.createAttribute("label", Ontology.NOMINAL));
		attributes.add(AttributeFactory.createAttribute("metadata_file", Ontology.NOMINAL));
		attributes.add(AttributeFactory.createAttribute("metadata_path", Ontology.NOMINAL));
		
		List<List<Object>> rows = new ArrayList<List<Object>>();
		
		for(int topic = 0; topic < model.getNumTopics(); topic++)
		{
			attributes.add(AttributeFactory.createAttribute("Topic " + topic, Ontology.NUMERICAL));
		}
		attributes.add(AttributeFactory.createAttribute("sum", Ontology.NUMERICAL));
		
		
		for(int doc = 0; doc < model.getData().size(); doc++)
		{
			List<Object> row = new ArrayList<Object>();
			
			row.add(model.getData().get(doc).instance.getName());
			row.add(model.getData().get(doc).instance.getTarget());
			row.add(model.getData().get(doc).instance.getSource());
						
			double[] topicDistribution = model.getTopicProbabilities(doc);
		
			float sum = 0.0f;
			
			for(int topic = 0; topic < model.numTopics; topic++) 
			{
				sum += topicDistribution[topic];
				row.add(topicDistribution[topic]);
			}
			row.add(sum);
			
			rows.add(row);
		}
		
//		System.out.println(rows);
		
		ExampleTable table = helper.createObjectExampleTable(attributes, rows);
		ExampleSet es = table.createExampleSet();	
		
		return es;
	}
	
	/**
	 * this method should return an ExampleSet with the topic that a type is in for each document.<br>
	 * 
	 * @return 
	 */
	public ExampleSet getTokenTopicAssignmentForAllInstances()
	{
		List<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(AttributeFactory.createAttribute("label", Ontology.NOMINAL));
		attributes.add(AttributeFactory.createAttribute("metadata_file", Ontology.NOMINAL));
		attributes.add(AttributeFactory.createAttribute("metadata_path", Ontology.NOMINAL));

		List<List<Object>> rows = new ArrayList<List<Object>>();
		
		Alphabet dataAlphabet = instances.getDataAlphabet();
		
		int longestDoc = -1;
		
		for(int doc =0; doc < model.getData().size(); doc++)
		{
			FeatureSequence tokens = (FeatureSequence) model.getData().get(doc).instance.getData();
			
			longestDoc = (longestDoc >= tokens.getLength()) ? longestDoc : tokens.getLength();
		}
		
		
		for(int i=0; i<longestDoc; i++)
		{
			attributes.add(AttributeFactory.createAttribute(i+ ". Wort", Ontology.STRING));
		}
		
		for(int doc = 0; doc < model.getData().size(); doc++)
		{
			List<Object> row = new ArrayList<Object>();
			
			row.add(model.getData().get(doc).instance.getName());
			row.add(model.getData().get(doc).instance.getTarget());
			row.add(model.getData().get(doc).instance.getSource());
			
			FeatureSequence tokens = (FeatureSequence) model.getData().get(doc).instance.getData();
			LabelSequence topics = model.getData().get(doc).topicSequence;			
			
			for(int index = 0; index < longestDoc; index++)
			{

				
//				if(longestDoc < index)
//				{ 
//					attributes.add(AttributeFactory.createAttribute("" + index, Ontology.NOMINAL));
//					longestDoc ++;
//				}
				
				if(tokens.getLength() > index)
				{
					Formatter out = new Formatter(new StringBuilder(), Locale.GERMAN);
					out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(index)), topics.getIndexAtPosition(index));
//					row.add("" + topics.getIndexAtPosition(index));
					row.add(out.toString());
				}
				else
				{
					row.add("?");
				}
			}
			
			rows.add(row);
		}
		
		ExampleTable table = helper.createObjectExampleTable(attributes, rows);
		ExampleSet es = table.createExampleSet();
		
		return es;
		
	}
	
	
	public IOObject getTrainedInferencer()
	{
		MalletTopicInferencerIOObject generatedInferencer = new MalletTopicInferencerIOObject(model.getInferencer());
		return generatedInferencer;
	}
}