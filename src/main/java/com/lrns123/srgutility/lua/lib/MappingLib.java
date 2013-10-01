package com.lrns123.srgutility.lua.lib;

import java.io.File;
import java.io.IOException;
import net.md_5.specialsource.Jar;
import net.md_5.specialsource.JarComparer;
import net.md_5.specialsource.SpecialSource;
import net.md_5.specialsource.provider.JarProvider;
import net.md_5.specialsource.util.Pair;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.objectweb.asm.ClassReader;

import com.lrns123.srgutility.lua.meta.SrgInheritanceMapMeta;
import com.lrns123.srgutility.lua.meta.SrgMappingMeta;
import com.lrns123.srgutility.mcp.MinecraftCodersPackMapping;
import com.lrns123.srgutility.srg.SrgClass;
import com.lrns123.srgutility.srg.SrgField;
import com.lrns123.srgutility.srg.SrgInheritanceMap;
import com.lrns123.srgutility.srg.SrgMapping;
import com.lrns123.srgutility.srg.SrgMethod;

public class MappingLib extends TwoArgFunction
{	
	public MappingLib()
	{
	}

	public LuaValue call(LuaValue modname, LuaValue env)
	{
		LuaTable t = new LuaTable();
		
		bind(t, MappingLibV.class, new String[] {"loadSrg", "loadMCP", "compareJars", "makeInheritanceMap"});

		env.set("MappingFactory", t);
		env.get("package").get("loaded").set("MappingFactory", t);

		return t;
	}
	
	public static final class MappingLibV extends VarArgFunction
	{
		@Override
		public Varargs invoke(Varargs args)
		{
		    switch (opcode)
		    {
		    	case 0:	// MappingFactory.loadSrg(filename[, filename[, filename[, ...]]])
		    		return loadSrgMapping(args);
		    	case 1: // MappingFactory.loadMCP([fieldsFile], [methodsFile], [packagesFile])
		    		return loadMCPMapping(args.arg1(), args.arg(2), args.arg(3));
		    	case 2: // MappingFactory.compareJars(inputJarFile, outputJarFile)
		    		return compareJars(args.arg1(), args.arg(2));
		    	case 3:
		    		return makeInheritanceMap(args.arg1(),args.arg(2));
		    }
		    return LuaValue.NIL;
		}
	}

	/**
	 * Lua Closure for MappingFactory.loadSrg(filename, ...).
	 * Loads all specified srg files into a single srg mapping.
	 * 
	 * @param filenames The filenames to load
	 * @return The SrgMapping (as UserData)
	 */
	private static LuaValue loadSrgMapping(Varargs filenames)
	{
		
		SrgMapping mapping = new SrgMapping();
		
		int args = filenames.narg() + 1;
		for (int i = 1; i != args; ++i)
		{
			String filename = filenames.arg(i).checkjstring();

			File file = new File(filename);
			if (!file.exists())
				throw new LuaError("File " + filename + " does not exist.");

			try
			{
				mapping.loadMapping(file);
			}
			catch (Exception e)
			{
				throw new LuaError(e);
			}
		}
		
		return new LuaUserdata(mapping, SrgMappingMeta.getMetaTable());
	}

	/**
	 * Lua Closure for MappingFactory.loadMCP(fieldsFile, methodsFile, packagesFile).
	 * Loads the specified MCP csv files into a mapping. 
	 * @param fieldsFile The fields.csv file, or nil to ignore field mappings.
	 * @param methodsFile The methods.csv file, or nil to ignore method mappings.
	 * @param packagesFile The packages.csv file, or nil to ignore package mappings.
	 * @return The MinecraftCodersPackMapping (as UserData)
	 */
	private static final LuaValue loadMCPMapping(LuaValue fieldsFile, LuaValue methodsFile, LuaValue packagesFile)
	{
		File fields = fieldsFile.isnil() ? null : new File(fieldsFile.checkjstring());
		File methods = methodsFile.isnil() ? null : new File(methodsFile.checkjstring());
		File packages = packagesFile.isnil() ? null : new File(packagesFile.checkjstring());
		
		if (fields != null && fields.exists() == false)
			throw new LuaError("File " + fields.getAbsolutePath() + " does not exist.");
		
		if (methods != null && methods.exists() == false)
			throw new LuaError("File " + fields.getAbsolutePath() + " does not exist.");
		
		if (packages != null && packages.exists() == false)
			throw new LuaError("File " + fields.getAbsolutePath() + " does not exist.");
		

		try
		{
			MinecraftCodersPackMapping mapping = new MinecraftCodersPackMapping(fields, methods, packages);
			return new LuaUserdata(mapping);
		}
		catch (Exception e)
		{
			throw new LuaError(e);
		}
	}
	
	/**
	 * Lua Closure for MappingFactory.compareJars(inJar, outJar, [full])
	 * @param inputJarFile Jar file with input mappings
	 * @param outputJarFile Jar file with output mappings
	 * @param fullArg (Optional) Include duplicates, defaults to false.
	 * @return
	 */
	private static final LuaValue compareJars(LuaValue inputJarFile, LuaValue outputJarFile)
	{
		File inputJar = new File(inputJarFile.checkjstring());
		File outputJar = new File(outputJarFile.checkjstring());

		try
		{
			Jar inJar = Jar.init(inputJar);
			Jar outJar = Jar.init(outputJar);
			
			JarComparer inVisitor = new JarComparer(inJar);
			JarComparer outVisitor = new JarComparer(outJar);
			
			visit(new Pair<Jar>(inJar, outJar), new Pair<JarComparer>(inVisitor, outVisitor), new Pair<String>(inJar.getMain(), outJar.getMain()));
			
			SrgMapping mapping = new SrgMapping();
			
			int classCount = inVisitor.classes.size();
			for (int i = 0; i != classCount; ++i)
			{
				SrgClass inClass = new SrgClass(inVisitor.classes.get(i));
				SrgClass outClass = new SrgClass(outVisitor.classes.get(i));
				
				mapping.addClassMapping(inClass, outClass);
			}
			
			int fieldCount = inVisitor.fields.size();
			for (int i = 0; i != fieldCount; ++i)
			{
				SrgField inField = new SrgField(inVisitor.fields.get(i).owner + "/" + inVisitor.fields.get(i).name);
				SrgField outField = new SrgField(outVisitor.fields.get(i).owner + "/" + outVisitor.fields.get(i).name);
				
				mapping.addFieldMapping(inField, outField);
			}
			
			int methodCount = inVisitor.methods.size();
			for (int i = 0; i != methodCount; ++i)
			{
				SrgMethod inMethod = new SrgMethod(inVisitor.methods.get(i).owner + "/" + inVisitor.methods.get(i).name, inVisitor.methods.get(i).descriptor);
				SrgMethod outMethod = new SrgMethod(outVisitor.methods.get(i).owner + "/" + outVisitor.methods.get(i).name, outVisitor.methods.get(i).descriptor);
							
				mapping.addMethodMapping(inMethod, outMethod);
			}
		
			return new LuaUserdata(mapping, SrgMappingMeta.getMetaTable());
		}
		catch (Exception e)
		{
			throw new LuaError(e);
		}
	}
	
	private static void visit(Pair<Jar> jars, Pair<JarComparer> visitors, Pair<String> classes) throws IOException {
        JarComparer visitor1 = visitors.first;
        JarComparer visitor2 = visitors.second;

        ClassReader clazz1 = new ClassReader(jars.first.getClass(classes.first));
        ClassReader clazz2 = new ClassReader(jars.second.getClass(classes.second));
        clazz1.accept(visitor1, 0);
        clazz2.accept(visitor2, 0);

        SpecialSource.validate(visitor1, visitor2);

        while (visitor1.iterDepth < visitor1.classes.size()) {
            String className1 = visitor1.classes.get(visitor1.iterDepth);
            String className2 = visitor2.classes.get(visitor1.iterDepth);
            Pair<String> pair = new Pair<String>(className1, className2);
            visitor1.iterDepth++;
            visit(jars, visitors, pair);
        }
    }
	
	
	/**
	 * Lua Closure for MappingFactory.compareJars(inJar, outJar, [full])
	 * @param inputJarFile Jar file with input mappings
	 * @param outputJarFile Jar file with output mappings
	 * @param fullArg (Optional) Include duplicates, defaults to false.
	 * @return
	 */
	private static final LuaValue makeInheritanceMap(LuaValue jarFile, LuaValue mappingArg)
	{
		File inputJar = new File(jarFile.checkjstring());
		SrgMapping mapping = (SrgMapping) mappingArg.checkuserdata(SrgMapping.class);

		try
		{
			Jar inJar = Jar.init(inputJar);
			
			SrgInheritanceMap inheritMap = new SrgInheritanceMap(new JarProvider(inJar), mapping);
			
			return new LuaUserdata(inheritMap, SrgInheritanceMapMeta.getMetaTable());
		}
		catch (Exception e)
		{
			throw new LuaError(e);
		}
	}
	
}
