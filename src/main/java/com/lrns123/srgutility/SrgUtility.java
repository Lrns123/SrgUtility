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
