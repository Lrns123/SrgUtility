--[[
**
** Srg Utility - Deobfuscate Forge Mods
**
** This script will automatically deobfuscate Forge mods.
** It will look for .jar and .zip files in the ./obfuscated/ directory 
**  and saves the deobfuscated version in the ./deobfuscated/ directory. (configurable below)
**
** If the necessary mappings are missing, they will automatically be generated for the version provided.
**
]]--

local obfDir = "./obfuscated/"
local deobfDir = "./deobfuscated/"

local minecraftVersion = "1.6.2"
local forgeVersion = "9.10.1.871"

local srgFile = string.format("./mappings/%s/merged.srg", forgeVersion)
local inheritFile = string.format("./mappings/%s/merged.inheritmap", forgeVersion)

function main()
	if not (fs.exists(srgFile) and fs.exists(inheritFile)) then
		generateMappings()
	end
	
	local mapping = MappingFactory.loadSrg(srgFile)
	local inheritance = MappingFactory.loadInheritanceMap(inheritFile)

	local files = fs.list(obfDir)
	local i, name
	for i, name in ipairs(files) do
		if (name:sub(-4) == ".jar" or name:sub(-4) == ".zip") then
			print("Deobfuscating " .. name .. "...")
			Remapper.remapJar(obfDir .. name, deobfDir .. name, mapping, inheritance)
		end
	end
end

-- Generate the required mappings
--
-- This will generate mappings that can convert both obf and num mappings to mcp in one go.
-- These mappings are NOT compatible with SpecialSource if provided on the command-line!
function generateMappings()
	AutoMapper = require("AutoMapper")

	local outDir = string.format("./mappings/%s/", forgeVersion)
	
	AutoMapper.setCacheDir("./cache/")
	AutoMapper.setOutputDir(outDir)
	AutoMapper.setMinecraftVersion(minecraftVersion)
	AutoMapper.setForgeVersion(forgeVersion)
	
	-- Add shortcuts for Mapping and Inheritance
	local Mapping = AutoMapper.Mapping
	local Inheritance = AutoMapper.Inheritance

	local targetMappings = {
		{ Mapping.OBFUSCATED, Mapping.DESCRIPTIVE_PACKAGED, "obf2pkgmcp.srg" },
		{ Mapping.NUMERIC_PACKAGED, Mapping.DESCRIPTIVE_PACKAGED, "pkgnum2pkgmcp.srg" }
	}

	local targetInheritance = {
		{ Inheritance.NMS, Mapping.NUMERIC_PACKAGED, "pkg.inheritmap" },
		{ Inheritance.NMS, Mapping.OBFUSCATED, "obf.inheritmap" }
	}

	AutoMapper.generate(targetMappings, targetInheritance)
	
	-- Merge the mappings into merged.srg
	local mergedSrg = MappingFactory.loadSrg(outDir .. "obf2pkgmcp.srg", outDir .. "pkgnum2pkgmcp.srg")
	mergedSrg:saveToFile(outDir .. "merged.srg")
	
	-- Delete the original files
	fs.delete(outDir .. "obf2pkgmcp.srg")
	fs.delete(outDir .. "pkgnum2pkgmcp.srg")
	
	-- Merge the inheritance maps into merged.inheritmap
	local mergedInherit = MappingFactory.loadInheritanceMap(outDir .. "pkg.inheritmap", outDir .. "obf.inheritmap")
	mergedInherit:saveToFile(outDir .. "merged.inheritmap")	
	
	-- Delete the original files
	fs.delete(outDir .. "pkg.inheritmap")
	fs.delete(outDir .. "obf.inheritmap")
end


main()

