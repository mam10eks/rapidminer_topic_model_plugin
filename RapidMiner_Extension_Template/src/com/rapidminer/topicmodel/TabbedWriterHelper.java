package com.rapidminer.topicmodel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

public class TabbedWriterHelper 
{
	public static void write(String[][] _text) throws IOException
	{
		File file = new File("/home/maik/Schreibtisch/wordVector");
		
		FileWriter fileWriter = new FileWriter(file);
		BufferedWriter writer = new BufferedWriter(fileWriter);
		
		int wordLength = getMaxStringLength(_text);
		for(String[] row : _text)
		{
			String line = "";
			for(String cell : row)
			{
//				for(int i=cell.length(); i<wordLength;i++)
//				{
				while(cell.length() <= wordLength)
				{
					cell += " ";
				}
				line += cell;
			}
			writer.write(line);
			writer.newLine();
		}
		
		
		writer.close();
		fileWriter.close();
		
		
		
	}

	
	
	@Test
	public void testWriter() throws IOException
	{
		write(new String[][]{new String[]{"","hhhhh", "dsfdsf","dfsf"}, new String[]{"Row1", "sfa", "dssd", "fsafdsaf"}, new String[]{"Row 2", "fasf","fsa", "fsa"}});
	}
	
	
	public static int getMaxStringLength(String[][] _input)
	{
		int maxLength = 0;
		for(String[]row : _input)
		{	
			for(String word : row)
			{
				maxLength = word.length() > maxLength ? word.length() : maxLength;
			}
		}
		
		return maxLength;
	}
}