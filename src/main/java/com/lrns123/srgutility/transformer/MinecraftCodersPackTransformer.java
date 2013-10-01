package com.lrns123.srgutility.transformer;

import com.lrns123.srgutility.mcp.MinecraftCodersPackMapping;
import com.lrns123.srgutility.srg.SrgClass;
import com.lrns123.srgutility.srg.SrgField;
import com.lrns123.srgutility.srg.SrgMethod;
import com.lrns123.srgutility.srg.SrgTypeDescriptor;
import com.lrns123.srgutility.srg.SrgTypeDescriptor.Type;

/**
 * Uses MCP's csv files to transform mappings.
 */
public class MinecraftCodersPackTransformer implements MappingTransformer
{

	private final MinecraftCodersPackMapping mapping;
	
	public MinecraftCodersPackTransformer(MinecraftCodersPackMapping mapping)
	{
		this.mapping = mapping;
	}
	

	public SrgClass transform(SrgClass input)
	{
		if (mapping.getPackageMapping().containsKey(input.getClassName()))
		{
			input.setPackageName(mapping.getPackageMapping().get(input.getClassName()));
		}
		
		return input;
	}

	public SrgField transform(SrgField input)
	{
		if (input.getPackageName().equals("net/minecraft/src") && mapping.getPackageMapping().containsKey(input.getClassName()))
		{
			input.setPackageName(mapping.getPackageMapping().get(input.getClassName()));
		}
		
		if (mapping.getFieldMapping().containsKey(input.getFieldName()))
		{
			input.setFieldName(mapping.getFieldMapping().get(input.getFieldName()));
		}
		
		return input;
	}

	public SrgMethod transform(SrgMethod input)
	{
		if (input.getPackageName().equals("net/minecraft/src") && mapping.getPackageMapping().containsKey(input.getClassName()))
		{
			input.setPackageName(mapping.getPackageMapping().get(input.getClassName()));
		}
		
		if (mapping.getMethodMapping().containsKey(input.getMethodName()))
		{
			input.setMethodName(mapping.getMethodMapping().get(input.getMethodName()));
		}
		
		for (SrgTypeDescriptor descriptor : input.getArguments())
		{
			if (descriptor.getType() == Type.OBJECT)
			{
				SrgClass typeClass = descriptor.getClassType();
				if (typeClass.getPackageName().equals("net/minecraft/src") && mapping.getPackageMapping().containsKey(typeClass.getClassName()))
				{
					typeClass.setPackageName(mapping.getPackageMapping().get(typeClass.getClassName()));
				}
			}
		}
		
		if (input.getReturnType().getType() == Type.OBJECT)
		{
			SrgClass typeClass = input.getReturnType().getClassType();
			if (typeClass.getPackageName().equals("net/minecraft/src") && mapping.getPackageMapping().containsKey(typeClass.getClassName()))
			{
				typeClass.setPackageName(mapping.getPackageMapping().get(typeClass.getClassName()));
			}
		}
		
		return input;
	}

}
