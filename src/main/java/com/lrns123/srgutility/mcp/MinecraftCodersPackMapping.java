package com.lrns123.srgutility.mcp;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import au.com.bytecode.opencsv.CSVReader;

@Data
public class MinecraftCodersPackMapping
{
	/**
	 * Field mappings (field_xxxx -> decorated name)
	 */
	private final Map<String, String> fieldMapping = new HashMap<String, String>();
	
	/**
	 * Method mappings (func_xxxx -> decorated name)
	 */
	private final Map<String, String> methodMapping = new HashMap<String, String>();
	
	/**
	 * Package mappings (class -> package)
	 */
	private final Map<String, String> packageMapping = new HashMap<String, String>();

	public MinecraftCodersPackMapping(File fieldsFile, File methodsFile, File packagesFile) throws IOException
	{
		if (fieldsFile != null && fieldsFile.exists())
		{
			readCSV(fieldMapping, fieldsFile);
		}
		
		if (methodsFile != null && methodsFile.exists())
		{
			readCSV(methodMapping, methodsFile);
		}
		
		if (packagesFile != null && packagesFile.exists())
		{
			readCSV(packageMapping, packagesFile);
		}
	}

	private void readCSV(Map<String, String> map, File file) throws IOException
	{
		FileReader fileReader = null;
		CSVReader csvReader = null;
		try
		{
			fileReader = new FileReader(file);
			csvReader = new CSVReader(fileReader);
			
			// Skip header
			csvReader.readNext();

			String[] line;
			while ((line = csvReader.readNext()) != null)
			{
				if (line.length == 0)
				{
					continue;
				}
				if (line.length >= 2)
				{
					map.put(line[0], line[1]);
				}
			}
		}
		finally
		{
			if (csvReader != null)
			{
				csvReader.close();
			}
			if (fileReader != null)
			{
				fileReader.close();
			}
		}
	}
}
