package com.lrns123.srgutility.srg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.lrns123.srgutility.transformer.IdentityTransformer;
import com.lrns123.srgutility.transformer.MappingTransformer;

/**
 * Represents an srg mapping.
 * 
 */
public class SrgMapping
{
	/**
	 * Mapping tables. Contains input -> output mapping.
	 */
	private Map<SrgClass, SrgClass> classMapping = new HashMap<SrgClass, SrgClass>();
	private Map<SrgField, SrgField> fieldMapping = new HashMap<SrgField, SrgField>();
	private Map<SrgMethod, SrgMethod> methodMapping = new HashMap<SrgMethod, SrgMethod>();

	/**
	 * Mapping lookup table. Contains input signature -> input class. The input
	 * class can be used in the mapping tables to find the corresponding entry.
	 */
	private Map<String, SrgClass> classLookup = new HashMap<String, SrgClass>();
	private Map<String, SrgField> fieldLookup = new HashMap<String, SrgField>();
	private Map<String, SrgMethod> methodLookup = new HashMap<String, SrgMethod>();

	/**
	 * Adds a class mapping to the mapping tables.
	 * 
	 * @param input
	 * @param output
	 */
	public void addClassMapping(SrgClass input, SrgClass output)
	{
		if (classLookup.containsKey(input.getQualifiedName()))
		{
			System.out.println("Duplicate class entry for " + input.getQualifiedName() + "... Ignoring");
			return;
		}
		
		classMapping.put(input, output);
		classLookup.put(input.getQualifiedName(), input);
	}

	/**
	 * Adds a field mapping to the mapping tables.
	 * 
	 * @param input
	 * @param output
	 */
	public void addFieldMapping(SrgField input, SrgField output)
	{
		if (fieldLookup.containsKey(input.getQualifiedName()))
		{
			System.out.println("Duplicate field entry for " + input.getQualifiedName() + "... Ignoring");
			return;
		}
		
		fieldMapping.put(input, output);
		fieldLookup.put(input.getQualifiedName(), input);
	}

	/**
	 * Adds a method mapping to the mapping tables.
	 * 
	 * @param input
	 * @param output
	 */
	public void addMethodMapping(SrgMethod input, SrgMethod output)
	{
		if (methodLookup.containsKey(input.getQualifiedName()))
		{
			System.out.println("Duplicate method entry for " + input.getQualifiedName() + "... Ignoring");
			return;
		}
		
		methodMapping.put(input, output);
		methodLookup.put(input.getQualifiedName(), input);
	}

	/**
	 * Makes a deep copy of this mapping.
	 * 
	 * @return A new SrgMapping instance with identical mappings.
	 */
	@Override
	public SrgMapping clone()
	{
		SrgMapping temp = new SrgMapping();

		for (Entry<SrgClass, SrgClass> entry : classMapping.entrySet())
		{
			temp.addClassMapping(entry.getKey().clone(), entry.getValue().clone());
		}

		for (Entry<SrgField, SrgField> entry : fieldMapping.entrySet())
		{
			temp.addFieldMapping(entry.getKey().clone(), entry.getValue().clone());
		}

		for (Entry<SrgMethod, SrgMethod> entry : methodMapping.entrySet())
		{
			temp.addMethodMapping(entry.getKey().clone(), entry.getValue().clone());
		}

		return temp;
	}

	/**
	 * Reverses the mappings.
	 * 
	 * @return The current (now reversed) instance of SrgMapping.
	 */
	public SrgMapping reverse()
	{
		SrgMapping temp = new SrgMapping();

		for (Entry<SrgClass, SrgClass> entry : classMapping.entrySet())
		{
			temp.addClassMapping(entry.getValue(), entry.getKey());
		}

		for (Entry<SrgField, SrgField> entry : fieldMapping.entrySet())
		{
			temp.addFieldMapping(entry.getValue(), entry.getKey());
		}

		for (Entry<SrgMethod, SrgMethod> entry : methodMapping.entrySet())
		{
			temp.addMethodMapping(entry.getValue(), entry.getKey());
		}

		/**
		 * Swap data
		 */
		this.classMapping = temp.classMapping;
		this.fieldMapping = temp.fieldMapping;
		this.methodMapping = temp.methodMapping;

		this.classLookup = temp.classLookup;
		this.fieldLookup = temp.fieldLookup;
		this.methodLookup = temp.methodLookup;

		return this;
	}

	/**
	 * Transforms the mappings through the provided mapping transformers.
	 * 
	 * @param inputTransformer
	 *            The transformer to use on the input mappings.
	 * @param outputTransformer
	 *            The transformer to use on the output mappings.
	 * @return The current (now transformed) instance of SrgMapping.
	 */
	public SrgMapping transform(MappingTransformer inputTransformer, MappingTransformer outputTransformer)
	{

		if (inputTransformer == null)
		{
			inputTransformer = new IdentityTransformer();
		}

		if (outputTransformer == null)
		{
			outputTransformer = new IdentityTransformer();
		}

		SrgMapping temp = new SrgMapping();

		for (Entry<SrgClass, SrgClass> entry : classMapping.entrySet())
		{
			SrgClass input = inputTransformer.transform(entry.getKey());
			SrgClass output = outputTransformer.transform(entry.getValue());
			temp.addClassMapping(input, output);
		}

		for (Entry<SrgField, SrgField> entry : fieldMapping.entrySet())
		{
			SrgField input = inputTransformer.transform(entry.getKey());
			SrgField output = outputTransformer.transform(entry.getValue());
			temp.addFieldMapping(input, output);
		}

		for (Entry<SrgMethod, SrgMethod> entry : methodMapping.entrySet())
		{
			SrgMethod input = inputTransformer.transform(entry.getKey());
			SrgMethod output = outputTransformer.transform(entry.getValue());
			temp.addMethodMapping(input, output);
		}

		/**
		 * Swap data
		 */
		this.classMapping = temp.classMapping;
		this.fieldMapping = temp.fieldMapping;
		this.methodMapping = temp.methodMapping;

		this.classLookup = temp.classLookup;
		this.fieldLookup = temp.fieldLookup;
		this.methodLookup = temp.methodLookup;

		return this;
	}	
	
	/**
	 * Turns this mapping into an identity map (input == output) based on the current input mappings.
	 * @return 
	 */
	public SrgMapping identity()
	{
		SrgMapping temp = new SrgMapping();

		for (Entry<SrgClass, SrgClass> entry : classMapping.entrySet())
		{
			temp.addClassMapping(entry.getKey(), entry.getKey().clone());
		}

		for (Entry<SrgField, SrgField> entry : fieldMapping.entrySet())
		{
			temp.addFieldMapping(entry.getKey(), entry.getKey().clone());
		}

		for (Entry<SrgMethod, SrgMethod> entry : methodMapping.entrySet())
		{
			temp.addMethodMapping(entry.getKey(), entry.getKey().clone());
		}

		/**
		 * Swap data
		 */
		this.classMapping = temp.classMapping;
		this.fieldMapping = temp.fieldMapping;
		this.methodMapping = temp.methodMapping;

		this.classLookup = temp.classLookup;
		this.fieldLookup = temp.fieldLookup;
		this.methodLookup = temp.methodLookup;

		return this;
	}
	
	
	/**
	 * Filters this mapping, removing all entries that (in terms of input) are not present in the filter SrgMapping
	 * @param filter The mapping to use as filter
	 * @return 
	 */
	public SrgMapping filter(SrgMapping filter)
	{
		SrgMapping temp = new SrgMapping();

		for (Entry<SrgClass, SrgClass> entry : classMapping.entrySet())
		{
			if (filter.getClassMapping(entry.getKey().getQualifiedName()) != null)
			{
				temp.addClassMapping(entry.getKey(), entry.getValue());
			}
		}

		for (Entry<SrgField, SrgField> entry : fieldMapping.entrySet())
		{
			if (filter.getFieldMapping(entry.getKey().getQualifiedName()) != null)
			{
				temp.addFieldMapping(entry.getKey(), entry.getValue());
			}
		}

		for (Entry<SrgMethod, SrgMethod> entry : methodMapping.entrySet())
		{
			if (filter.getMethodMapping(entry.getKey().getQualifiedName()) != null)
			{
				temp.addMethodMapping(entry.getKey(), entry.getValue());
			}
		}

		/**
		 * Swap data
		 */
		this.classMapping = temp.classMapping;
		this.fieldMapping = temp.fieldMapping;
		this.methodMapping = temp.methodMapping;

		this.classLookup = temp.classLookup;
		this.fieldLookup = temp.fieldLookup;
		this.methodLookup = temp.methodLookup;

		return this;
	}
	
	
	public void loadMapping(File srgFile) throws IOException, IllegalArgumentException
	{
		FileReader freader = null;
		BufferedReader reader = null;

		try
		{
			freader = new FileReader(srgFile);
			reader = new BufferedReader(freader);
	
			String line;
			while ((line = reader.readLine()) != null)
			{
				int commentIndex = line.indexOf('#');
				if (commentIndex != -1)
				{
					line = line.substring(0, commentIndex);
				}
	
				String tokens[] = line.trim().split(" ");
				
				if (tokens.length < 1)
					continue;
				
				if (tokens[0].equals("CL:"))
				{
					if (tokens.length < 3)
						throw new IllegalArgumentException("Invalid CL entry found in srg: " + line);
					
					SrgClass input = new SrgClass(tokens[1]);
					SrgClass output = new SrgClass(tokens[2]);
					
					addClassMapping(input, output);				
				}
				else if (tokens[0].equals("FD:"))
				{
					if (tokens.length < 3)
						throw new IllegalArgumentException("Invalid FD entry found in srg: " + line);
					
					SrgField input = new SrgField(tokens[1]);
					SrgField output = new SrgField(tokens[2]);
					
					addFieldMapping(input, output);			
				}
				else if (tokens[0].equals("MD:"))
				{
					if (tokens.length < 5)
						throw new IllegalArgumentException("Invalid MD entry found in srg: " + line);
					
					SrgMethod input = new SrgMethod(tokens[1], tokens[2]);
					SrgMethod output = new SrgMethod(tokens[3], tokens[4]);
					
					addMethodMapping(input, output);		
				}
			}
		}
		finally
		{
			if (reader != null)
				reader.close();
			
			if (freader != null)
				freader.close();
		}
	}
	
	public void write(File outFile) throws IOException
	{
		List<String> lines = new ArrayList<String>();
		
		for (Entry<SrgClass, SrgClass> entry : classMapping.entrySet())
		{
			lines.add("CL: " + entry.getKey() + " " + entry.getValue());
		}

		for (Entry<SrgField, SrgField> entry : fieldMapping.entrySet())
		{
			lines.add("FD: " + entry.getKey() + " " + entry.getValue());
		}

		for (Entry<SrgMethod, SrgMethod> entry : methodMapping.entrySet())
		{
			lines.add("MD: " + entry.getKey() + " " + entry.getValue());
		}
		
		Collections.sort(lines);
		
		PrintWriter writer = null;
		
		if (!outFile.getParentFile().exists())
		{
			outFile.getParentFile().mkdirs();
		}
		
		try
		{
			writer = new PrintWriter(outFile);
			
			writer.println("# Mapping generated by Srg Utility (c) 2013 Lourens \"Lrns123\" Elzinga");
			writer.println("# Generated on " + new Date());
			
			for (String line : lines)
			{
				writer.println(line);
			}
		}
		finally
		{
			if (writer != null)
				writer.close();
		}
	}
	
	public SrgClass getClassMapping(SrgClass input)
	{
		return getClassMapping(input.getQualifiedName());
	}
	
	public SrgClass getClassMapping(String inputSignature)
	{
		SrgClass mapEntry = classLookup.get(inputSignature);
		if (mapEntry != null)
		{
			return classMapping.get(mapEntry);
		}
		
		return null;
	}
	
	public Map<SrgClass, SrgClass> getClassMappings()
	{
		return classMapping;
	}
	
	public SrgField getFieldMapping(SrgField input)
	{
		return getFieldMapping(input.getQualifiedName());
	}
	
	public SrgField getFieldMapping(String inputSignature)
	{
		SrgField mapEntry = fieldLookup.get(inputSignature);
		if (mapEntry != null)
		{
			return fieldMapping.get(mapEntry);
		}
		
		return null;
	}
	
	public Map<SrgField, SrgField> getFieldMappings()
	{
		return fieldMapping;
	}
	
	public SrgMethod getMethodMapping(SrgMethod input)
	{
		return getMethodMapping(input.getQualifiedName());
	}
	
	public SrgMethod getMethodMapping(String inputSignature)
	{
		SrgMethod mapEntry = methodLookup.get(inputSignature);
		if (mapEntry != null)
		{
			return methodMapping.get(mapEntry);
		}
		
		return null;
	}
	
	public Map<SrgMethod, SrgMethod> getMethodMappings()
	{
		return methodMapping;
	}
}
