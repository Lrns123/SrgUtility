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
package com.lrns123.srgutility.lua.lib;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class FSLib extends TwoArgFunction
{
	public FSLib()
	{
	}

	public LuaValue call(LuaValue modname, LuaValue env)
	{
		LuaTable t = new LuaTable();
		
		bind(t, FSLibV.class, new String[] {"list", "exists", "isDir", "getName", "getSize", "makeDir", "move", "copy", "delete", "combine"});

		env.set("fs", t);
		env.get("package").get("loaded").set("fs", t);

		return t;
	}
	
	public static final class FSLibV extends VarArgFunction
	{
		@Override
		public Varargs invoke(Varargs args)
		{
		    switch (opcode)
		    {
		    	case 0:	// fs.list(path)
		    		return list(args.arg1());
		    	case 1: // fs.exists(path)
		    		return exists(args.arg1());
		    	case 2: // fs.isDir(path)
		    		return isDir(args.arg1());
		    	case 3: // fs.getName(path)
		    		return getName(args.arg1());
		    	case 4: // fs.getSize(path)
		    		return getSize(args.arg1());
		    	case 5: // fs.makeDir(path)
		    		return makeDir(args.arg1());
		    	case 6: // fs.move(source, destination)
		    		return move(args.arg1(), args.arg(2));
		    	case 7: // fs.copy(source, destination)
		    		return copy(args.arg1(), args.arg(2));
		    	case 8: // fs.delete(path)
		    		return delete(args.arg1());
		    	case 9:
		    		return combine(args.arg1(), args.arg(2));
		    }
		    return LuaValue.NIL;
		}
	}
	
	private static LuaValue list(LuaValue path)
	{
		File dir = new File(path.checkjstring());
		
		String[] fileList = dir.list();
		if (fileList == null)
			return LuaValue.NIL;
		
		LuaTable fileTable = new LuaTable();
		for (int i = 0; i != fileList.length; ++i)
		{
			fileTable.set(i + 1, fileList[i]);
		}
		
		return fileTable;
	}
	
	private static LuaValue exists(LuaValue path)
	{
		File file = new File(path.checkjstring());
		return LuaValue.valueOf(file.exists());
	}
	
	private static LuaValue isDir(LuaValue path)
	{
		File file = new File(path.checkjstring());
		return LuaValue.valueOf(file.isDirectory());
	}
	
	private static LuaValue getName(LuaValue path)
	{
		File file = new File(path.checkjstring());
		return LuaValue.valueOf(file.getName());
	}
	
	private static LuaValue getSize(LuaValue path)
	{
		File file = new File(path.checkjstring());
		return LuaValue.valueOf(file.length());
	}
	
	private static LuaValue makeDir(LuaValue path)
	{
		File file = new File(path.checkjstring());
		return LuaValue.valueOf(file.mkdirs());
	}
	
	private static LuaValue move(LuaValue source, LuaValue destination)
	{
		copy(source, destination);
		delete(source);		
		return LuaValue.NIL;
	}
	
	private static LuaValue copy(LuaValue source, LuaValue destination)
	{
		File srcFile = new File(source.checkjstring());
		File destFile = new File(destination.checkjstring());

		if (srcFile.exists())
			throw new LuaError("File does not exist: " + srcFile.getAbsolutePath());
		
		if (!srcFile.isDirectory())
			throw new LuaError("Copying of directories is currently unsupported.");

		BufferedInputStream inStream = null;
		BufferedOutputStream outStream = null;
		
		try
		{
			inStream = new BufferedInputStream(new FileInputStream(srcFile));
			outStream = new BufferedOutputStream(new FileOutputStream(destFile));
			
			byte buffer[] = new byte[1024];
			int bytes;
			
			while ((bytes = inStream.read(buffer)) != -1)
			{
				outStream.write(buffer, 0, bytes);
			}
			
			inStream.close();
			outStream.close();
		}
		catch (Exception e)
		{
			throw new LuaError(e);
		}
		finally
		{
			if (inStream != null)
			{
				try
				{
					inStream.close();
				}
				catch (Exception e)
				{
				}
			}
			
			if (outStream != null)
			{
				try
				{
					outStream.close();
				}
				catch (Exception e)
				{
				}
			}
		}
		
		return LuaValue.NIL;
	}
	
	private static LuaValue delete(LuaValue path)
	{
		File file = new File(path.checkjstring());
		return LuaValue.valueOf(file.delete());
	}
	
	private static LuaValue combine(LuaValue root, LuaValue path)
	{
		File file = new File(root.checkjstring(), path.checkjstring());
		return LuaValue.valueOf(file.getAbsolutePath());
	}
}
