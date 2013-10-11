package com.lrns123.srgutility.adapter;

import java.util.Map.Entry;

import com.lrns123.srgutility.srg.SrgClass;
import com.lrns123.srgutility.srg.SrgField;
import com.lrns123.srgutility.srg.SrgMapping;
import com.lrns123.srgutility.srg.SrgMethod;

import net.md_5.specialsource.JarMapping;

public final class JarMappingAdapter
{
	/**
	 * Converts an SrgMapping into SpecialSource's JarMapping.
	 * @param mapping
	 * @return
	 */
	public static JarMapping convertSrgMapping(SrgMapping mapping)
	{
		JarMapping outMapping = new JarMapping();
		
		for (Entry<SrgClass, SrgClass> entry : mapping.getClassMappings().entrySet())
		{
			outMapping.classes.put(entry.getKey().getQualifiedName(), entry.getValue().getQualifiedName());
		}
		
		for (Entry<SrgField, SrgField> entry : mapping.getFieldMappings().entrySet())
		{
			outMapping.fields.put(entry.getKey().getQualifiedName(), entry.getValue().getFieldName());
		}
		
		for (Entry<SrgMethod, SrgMethod> entry : mapping.getMethodMappings().entrySet())
		{
			outMapping.methods.put(entry.getKey().getQualifiedName(), entry.getValue().getMethodName());
		}
		
		return outMapping;
	}
}
