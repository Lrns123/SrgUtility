package com.lrns123.srgutility.srg;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Joiner;
import com.lrns123.srgutility.transformer.MappingTransformer;

import net.md_5.specialsource.provider.InheritanceProvider;

public class SrgInheritanceMap
{
	private Map<SrgClass, List<SrgClass>> inheritanceMap = new HashMap<SrgClass, List<SrgClass>>();

	public SrgInheritanceMap()
	{
	}
	
	/**
	 * Use SpecialSource to generate the inheritance map, mimics SpecialSource's behavior
	 */
	public SrgInheritanceMap(InheritanceProvider provider, SrgMapping mapping)
	{
		for (SrgClass clazz : mapping.getClassMappings().keySet())
		{
			Collection<String> parents = provider.getParents(clazz.getQualifiedName());
			
			if (parents == null)
				continue;
			
			List<SrgClass> filteredList = new ArrayList<SrgClass>();
			
			for (String candidate : parents)
			{
				if (mapping.getClassMapping(candidate) != null)
				{
					filteredList.add(new SrgClass(candidate));
				}
			}
			
			if (!filteredList.isEmpty())
			{
				inheritanceMap.put(clazz, filteredList);
			}
		}
	}
	
	public void setParent(SrgClass clazz, List<SrgClass> parents)
	{
		inheritanceMap.put(clazz, parents);
	}
	
	public List<SrgClass> getParent(SrgClass clazz)
	{
		return inheritanceMap.get(clazz);
	}
	
	public Map<SrgClass, List<SrgClass>> getInheritanceMap()
	{
		return inheritanceMap;
	}
	
	@Override
	public SrgInheritanceMap clone()
	{
		SrgInheritanceMap cloned = new SrgInheritanceMap();
		for (Entry<SrgClass, List<SrgClass>> entry : inheritanceMap.entrySet())
		{
			List<SrgClass> parents = new ArrayList<SrgClass>();
			
			for (SrgClass clz : entry.getValue())
			{
				parents.add(clz.clone());
			}
			
			cloned.setParent(entry.getKey().clone(), parents);
		}
		
		return cloned;
	}
	
	public SrgInheritanceMap transform(MappingTransformer transformer)
	{
		SrgInheritanceMap temp = new SrgInheritanceMap();
		for (Entry<SrgClass, List<SrgClass>> entry : inheritanceMap.entrySet())
		{
			List<SrgClass> parents = new ArrayList<SrgClass>();
			
			for (SrgClass clz : entry.getValue())
			{
				parents.add(transformer.transform(clz));
			}
			
			temp.setParent(transformer.transform(entry.getKey()), parents);
		}
		
		this.inheritanceMap = temp.inheritanceMap;
		
		return this;
	}
	
	public void write(File outFile) throws IOException
	{
		List<String> lines = new ArrayList<String>();
		
		for (Entry<SrgClass, List<SrgClass>> entry : inheritanceMap.entrySet())
		{
			lines.add(entry.getKey() + " " + Joiner.on(' ').join(entry.getValue()));
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
	
}
