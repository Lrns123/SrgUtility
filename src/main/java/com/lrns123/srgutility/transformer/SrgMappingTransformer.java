package com.lrns123.srgutility.transformer;

import com.lrns123.srgutility.srg.SrgClass;
import com.lrns123.srgutility.srg.SrgField;
import com.lrns123.srgutility.srg.SrgMapping;
import com.lrns123.srgutility.srg.SrgMethod;

public class SrgMappingTransformer implements MappingTransformer
{
	private final SrgMapping mapping;
	
	
	public SrgMappingTransformer(SrgMapping mapping)
	{
		this.mapping = mapping;
	}

	public SrgClass transform(SrgClass input)
	{
		SrgClass output = mapping.getClassMapping(input);
		if (output != null)
		{
			return output.clone();
		}
		
		return input;
	}

	public SrgField transform(SrgField input)
	{
		SrgField output = mapping.getFieldMapping(input);
		if (output != null)
		{
			return output.clone();
		}
		
		return input;
	}

	public SrgMethod transform(SrgMethod input)
	{
		SrgMethod output = mapping.getMethodMapping(input);
		if (output != null)
		{
			return output.clone();
		}
		
		return input;
	}

}
