package com.lrns123.srgutility.lua;

import java.io.File;
import java.io.FileInputStream;

import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;

import com.lrns123.srgutility.lua.lib.FSLib;
import com.lrns123.srgutility.lua.lib.HTTPLib;
import com.lrns123.srgutility.lua.lib.MappingLib;
import com.lrns123.srgutility.lua.lib.ZipLib;


/**
 * Lua Virtual Machine
 */
public class LuaVM
{
	private Globals _G;
	
	public LuaVM()
	{		
		this(false);
	}
	
	public LuaVM(boolean debug)
	{	
		if (debug)
		{
			_G = JsePlatform.debugGlobals();
		}
		else
		{
			_G = JsePlatform.standardGlobals();
		}
		
		_G.load(new MappingLib());
		_G.load(new FSLib());
		_G.load(new HTTPLib());
		_G.load(new ZipLib());
	}
	
	public void loadFile(File file)
	{
		try
		{
			_G.compiler.load(new FileInputStream(file), file.getName(), _G).call();
		}
		catch (Exception e)
		{
			System.out.println("Could not execute file " + file.getAbsolutePath() + ": " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
}
