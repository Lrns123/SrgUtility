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
package com.lrns123.srgutility;

import java.io.File;

import static java.util.Arrays.asList;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import com.lrns123.srgutility.lua.LuaVM;

public class SrgUtility
{
	public static void main(String args[])
	{
		OptionParser parser = new OptionParser()
		{
			{
				acceptsAll(asList("?", "help"), "Display help");
				acceptsAll(asList("v", "version"), "Display version");
				acceptsAll(asList("d", "debug"), "Enable debug mode");
				nonOptions("Lua script(s) to run");
			}
		};
		
		OptionSet options = parser.parse(args);
		if (options.has("?") || options.nonOptionArguments().size() == 0)
		{
			try
			{
				parser.printHelpOn(System.out);
			}
			catch (Exception e)
			{
			}
		}
		else if (options.has("v"))
		{
			// TODO
		}
		else
		{
			int count = options.nonOptionArguments().size();
			
			LuaVM vm = new LuaVM(options.has("d"));
			for (int i = 0; i != count; ++i)
			{
				String filename = (String)options.nonOptionArguments().get(i);
				vm.loadFile(new File(filename));
			}
		}

	}
}
