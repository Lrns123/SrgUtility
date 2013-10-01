package com.lrns123.srgutility.lua.meta;

import java.io.File;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;
import org.luaj.vm2.lib.VarArgFunction;

import com.lrns123.srgutility.srg.SrgInheritanceMap;
import com.lrns123.srgutility.transformer.MappingTransformer;

import static com.lrns123.srgutility.lua.util.LuaUtil.getTransformerFromArg;

public class SrgInheritanceMapMeta extends LibFunction
{
	private static LuaTable metatable;

	public static LuaTable getMetaTable()
	{
		if (metatable == null)
			new SrgInheritanceMapMeta();
		return metatable;
	}

	private SrgInheritanceMapMeta()
	{
		metatable = new LuaTable();

		bind(metatable, SrgMappingMetaV.class, new String[] { "saveToFile", "clone", "transform"});
		metatable.set(INDEX, metatable);
	}

	public static final class SrgMappingMetaV extends VarArgFunction
	{
		@Override
		public Varargs invoke(Varargs args)
		{
			switch (opcode)
			{
				case 0: // mapping:save(filename)
					return saveToFile(args.arg1(), args.arg(2));
				case 1: // mapping:clone()
					return SrgInheritanceMapMeta.clone(args.arg1());
				case 2: // mapping:transform(transformer)
					return transform(args.arg1(), args.arg(2));
			}
			return LuaValue.NIL;
		}
	}

	private static LuaValue saveToFile(LuaValue instance, LuaValue fileArg)
	{
		SrgInheritanceMap mapping = (SrgInheritanceMap) instance.checkuserdata(SrgInheritanceMap.class);
		String filename = fileArg.checkjstring();

		try
		{
			mapping.write(new File(filename));
			return LuaValue.NIL;
		}
		catch (Exception e)
		{
			throw new LuaError(e);
		}
	}

	private static LuaValue clone(LuaValue instance)
	{
		SrgInheritanceMap mapping = (SrgInheritanceMap) instance.checkuserdata(SrgInheritanceMap.class);
		return new LuaUserdata(mapping.clone(), SrgInheritanceMapMeta.getMetaTable());
	}
	
	private static LuaValue transform(LuaValue instance, LuaValue transformerArg)
	{
		SrgInheritanceMap mapping = (SrgInheritanceMap) instance.checkuserdata(SrgInheritanceMap.class);

		MappingTransformer inputTransformer = getTransformerFromArg(transformerArg);

		return new LuaUserdata(mapping.transform(inputTransformer), SrgInheritanceMapMeta.getMetaTable());
	}
}
