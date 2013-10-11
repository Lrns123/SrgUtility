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
package com.lrns123.srgutility.lua;

import java.io.File;
import java.io.FileInputStream;

import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;

import com.lrns123.srgutility.lua.lib.FSLib;
import com.lrns123.srgutility.lua.lib.HTTPLib;
import com.lrns123.srgutility.lua.lib.MappingLib;
import com.lrns123.srgutility.lua.lib.RemapperLib;
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
		_G.load(new RemapperLib());
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
