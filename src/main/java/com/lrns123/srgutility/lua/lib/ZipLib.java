package com.lrns123.srgutility.lua.lib;

import java.io.File;
import java.util.zip.ZipFile;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

import com.lrns123.srgutility.lua.meta.ZipFileMeta;

public class ZipLib extends TwoArgFunction
{
	public ZipLib()
	{
	}

	public LuaValue call(LuaValue modname, LuaValue env)
	{
		LuaTable t = new LuaTable();
		
		bind(t, ZipLibV.class, new String[] {"open"});

		env.set("zip", t);
		env.get("package").get("loaded").set("zip", t);

		return t;
	}
	
	public static final class ZipLibV extends VarArgFunction
	{
		@Override
		public Varargs invoke(Varargs args)
		{
		    switch (opcode)
		    {
		    	case 0:	// zip.open(filename)
		    		return open(args.arg1());
		    	
		    }
		    return LuaValue.NIL;
		}
	}
	
	private static LuaValue open(LuaValue pathArg)
	{
		try
		{
			return new LuaUserdata(new ZipFile(new File(pathArg.checkjstring())), ZipFileMeta.getMetaTable());
		}
		catch (Exception e)
		{
			throw new LuaError(e);
		}
	}
}
