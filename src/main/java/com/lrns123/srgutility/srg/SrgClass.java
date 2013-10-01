package com.lrns123.srgutility.srg;

import com.lrns123.srgutility.util.RegexUtil;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a class name for srg mappings.
 */
@Data
@AllArgsConstructor
public class SrgClass
{
	private String packageName;
	private String className;
	
	/**
	 * Instantiates a new class name representation from an fully qualified name.
	 * @param qualifiedName Fully qualified name of the class (e.g. 'package/of/the/Class')
	 */
	public SrgClass(String qualifiedName)
	{
		String[] parts = RegexUtil.parseJavaIdentifiers(qualifiedName);
		
		if (parts.length < 1)
		{
			throw new IllegalArgumentException("Invalid srg indentifier: " + qualifiedName);
		}
		className = parts[parts.length - 1];
		
		StringBuilder packageBuilder = new StringBuilder();
		
		for (int i = 0; i < parts.length - 1; i++)
		{
			if (i != 0)
				packageBuilder.append('/');
			packageBuilder.append(parts[i]);
		}
		
		
		packageName = packageBuilder.toString();
	}
	
	/**
	 * Returns the fully qualified name of the class represented by this object.
	 * @return
	 */
	public String getQualifiedName()
	{
		if (packageName.isEmpty())
			return className;
		
		return packageName + "/" + className;
	}
	
	/**
	 * Creates a new SrgClass instance representing the same class.
	 */
	@Override
	public SrgClass clone()
	{
		return new SrgClass(packageName, className);
	}
	
	@Override
	public String toString()
	{
		return getQualifiedName();
	}
}
