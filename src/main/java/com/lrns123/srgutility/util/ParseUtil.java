/**
 * Copyright (c) 2013, Lourens "Lrns123" Elzinga
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the author nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.lrns123.srgutility.util;

public class ParseUtil
{
	/**
	 * Splits a fully qualified classname into its package and class.
	 * 
	 * Warning: Calling this function again invalidates previously returned values.
	 * 
	 * @param identifier The fully qualified (binary) name of the class.
	 * @return A string array containing the package and class
	 */
	public static String[] splitFQN(String identifier)
	{
		String[] ret = new String[2];
		
		int idx = identifier.lastIndexOf('/');
		
		if (idx == -1)
		{
			ret[0] = "";
			ret[1] = identifier;
		}
		else
		{
			ret[1] = identifier.substring(idx + 1);
			ret[0] = identifier.substring(0, idx);
		}
		
		return ret;
	}
	
	/**
	 * Splits a fully qualified member name into its package, class and member.
	 * 
	 * Warning: Calling this function again invalidates previously returned values.
	 * 
	 * @param identifier The fully qualified (binary) name of the class.
	 * @return A string array containing the package and class
	 */
	public static String[] splitFQMN(String identifier)
	{		
		int idx = identifier.lastIndexOf('/');
		if (idx == -1)
			throw new IllegalArgumentException("Malformed FQN");
		
		String[] ret = new String[3];
		
		ret[2] = identifier.substring(idx + 1);
	
		int idx2 = identifier.lastIndexOf('/', idx - 1);
		if (idx2 == -1)
		{
			ret[0] = "";
			ret[1] = identifier.substring(0, idx);
		}
		else
		{
			ret[0] = identifier.substring(0, idx2);
			ret[1] = identifier.substring(idx2 + 1, idx);
		}
		
		return ret;
	}
}
