package com.lrns123.srgutility.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil
{
	private static Pattern javaIdentifierPattern = Pattern.compile("(\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*)+");
	
	public static String[] parseJavaIdentifiers(String identifier)
	{
		ArrayList<String> components = new ArrayList<String>();
		
		Matcher matcher = javaIdentifierPattern.matcher(identifier);
		
		while (matcher.find())
		{
			components.add(matcher.group());
		}
		
		return components.toArray(new String[0]);
	}
}
