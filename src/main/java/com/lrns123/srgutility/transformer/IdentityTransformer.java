package com.lrns123.srgutility.transformer;

import com.lrns123.srgutility.srg.SrgClass;
import com.lrns123.srgutility.srg.SrgField;
import com.lrns123.srgutility.srg.SrgMethod;

/**
 * A mapping transformer that keeps the mapping identical.
 */
public class IdentityTransformer implements MappingTransformer
{

	public SrgClass transform(SrgClass input)
	{
		return input;
	}

	public SrgField transform(SrgField input)
	{
		return input;
	}

	public SrgMethod transform(SrgMethod input)
	{
		return input;
	}

}
