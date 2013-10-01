package com.lrns123.srgutility.transformer;

import com.lrns123.srgutility.srg.SrgClass;
import com.lrns123.srgutility.srg.SrgField;
import com.lrns123.srgutility.srg.SrgMethod;

/**
 * Interface for all mapping transformers.
 */
public interface MappingTransformer
{
	/**
	 * Transforms the input class.
	 * @param input The class to transform. Altering this instance is allowed.
	 * @return The transformed class. (May be the same instance as input)
	 */
	public SrgClass transform(SrgClass input);
	
	/**
	 * Transforms the input field.
	 * @param input The field to transform. Altering this instance is allowed.
	 * @return The transformed field. (May be the same instance as input)
	 */
	public SrgField transform(SrgField input);
	
	/**
	 * Transforms the input method.
	 * @param input The method to transform. Altering this instance is allowed.
	 * @return The transformed method. (May be the same instance as input)
	 */
	public SrgMethod transform(SrgMethod input);
}
