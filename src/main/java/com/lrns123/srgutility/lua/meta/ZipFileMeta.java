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
package com.lrns123.srgutility.lua.meta;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class ZipFileMeta extends LibFunction
{
	private static LuaTable metatable;

	private static final int OP_CLOSE = 0;
	private static final int OP_ENTRIES = 1;
	private static final int OP_GETENTRY = 2;
	private static final int OP_GETNAME = 3;
	private static final int OP_SIZE = 4;
	private static final int OP_READALL = 5;
	private static final int OP_EXTRACT = 6;
	private static final int OP_GC = 7;
	
	public static LuaTable getMetaTable()
	{
		if (metatable == null)
			new ZipFileMeta();
		return metatable;
	}

	private ZipFileMeta()
	{
		metatable = new LuaTable();

		bind(metatable, ZipFileMetaV.class, new String[] { "close", "entries", "getEntry", "getName", "size", "readAll", "extract", "__gc" });
		metatable.set(INDEX, metatable);
		metatable.set(METATABLE, LuaValue.FALSE);
	}

	public static final class ZipFileMetaV extends VarArgFunction
	{
		@Override
		public Varargs invoke(Varargs args)
		{
			ZipFile instance = (ZipFile)args.arg1().checkuserdata(ZipFile.class);
			
			switch (opcode)
			{
				case OP_CLOSE:
					// zipfile:close()
					return close(instance);
				case OP_ENTRIES:
					// zipfile:entries()
					return entries(instance);
				case OP_GETENTRY:
					// zipfile:getEntry(path)
					return getEntry(instance, args.arg(2).checkjstring());
				case OP_GETNAME:
					// zipfile:getName()
					return getName(instance);
				case OP_SIZE:
					// zipfile:size()
					return size(instance);
				case OP_READALL:
					// zipfile:readAll(entry)
					return readAll(instance, (ZipEntry)args.arg(2).checkuserdata(ZipEntry.class));
				case OP_EXTRACT:
					// zipfile:extract(entry, destFile)
					return extract(instance, (ZipEntry)args.arg(2).checkuserdata(ZipEntry.class), args.arg(3).checkjstring());
				case OP_GC:
					// __gc
					return close(instance);
			}
			return LuaValue.NIL;
		}
	}

	private static LuaValue close(ZipFile instance)
	{		
		try
		{
			instance.close();
			return LuaValue.NIL;
		}
		catch (Exception e)
		{
			throw new LuaError(e);
		}
	}

	private static LuaValue entries(ZipFile instance)
	{		
		Enumeration<? extends ZipEntry> files = instance.entries();
		LuaTable fileTable = new LuaTable();		
		int i = 1;
		while (files.hasMoreElements())
		{
			ZipEntry entry = files.nextElement();
			
			fileTable.set(i++, new LuaUserdata(entry, ZipEntryMeta.getMetaTable()));
		}
		
		return fileTable;
	}

	private static LuaValue getEntry(ZipFile instance, String path)
	{		
		ZipEntry entry = instance.getEntry(path);
		
		if (entry == null)
			return LuaValue.NIL;
		else
			return new LuaUserdata(entry, ZipEntryMeta.getMetaTable());
	}
	
	private static LuaValue getName(ZipFile instance)
	{
		return LuaValue.valueOf(instance.getName());
	}

	private static LuaValue size(ZipFile instance)
	{
		return LuaValue.valueOf(instance.size());
	}
	
	private static LuaValue readAll(ZipFile instance, ZipEntry entry)
	{
		try
		{
			return LuaValue.valueOf(IOUtils.toString(instance.getInputStream(entry)));
		}
		catch (Exception e)
		{
			throw new LuaError(e);
		}
	}
	
	private static LuaValue extract(ZipFile instance, ZipEntry entry, String path)
	{
		File destFile = new File(path);
		
		try
		{
			InputStream inStream = instance.getInputStream(entry);
			FileOutputStream outStream = new FileOutputStream(destFile);
			
			
			final byte buffer[] = new byte[1024];
			int bytesRead;
			while ((bytesRead = inStream.read(buffer, 0, buffer.length)) != -1)
			{
				outStream.write(buffer, 0, bytesRead);
			}
			
			inStream.close();
			outStream.close();
			
			return LuaValue.NIL;
		}
		catch (Exception e)
		{
			throw new LuaError(e);
		}
	}
	
}
