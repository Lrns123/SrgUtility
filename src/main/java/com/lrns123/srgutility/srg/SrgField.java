package com.lrns123.srgutility.srg;

import com.lrns123.srgutility.util.RegexUtil;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a field within a class.
 */
@Data
@AllArgsConstructor
public class SrgField
{
	private String packageName;
	private String className;
	private String fieldName;
	
	public SrgField(String qualifiedName)
	{
		String[] parts = RegexUtil.parseJavaIdentifiers(qualifiedName);
		
		if (parts.length < 2)
		{
			throw new IllegalArgumentException("Invalid qualified name");
		}
		fieldName = parts[parts.length - 1];
		className = parts[parts.length - 2];
		
		StringBuilder packageBuilder = new StringBuilder();
		
		for (int i = 0; i < parts.length - 2; i++)
		{
			if (i != 0)
				packageBuilder.append('/');
			packageBuilder.append(parts[i]);
		}
		
		
		packageName = packageBuilder.toString();
	}
	
	public String getQualifiedName()
	{
		if (packageName.isEmpty())
			return className + "/" + fieldName;
		return packageName + "/" + className + "/" + fieldName;
	}
	
	@Override
	public SrgField clone()
	{
		return new SrgField(packageName, className, fieldName);
	}
	
	@Override
	public String toString()
	{
		return getQualifiedName();
	}
}
