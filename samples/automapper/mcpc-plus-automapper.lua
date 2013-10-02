--[[
**
**	Srg Utility - MCPC+ mapping generator (Using AutoMapper)
**
**  Target mappings:
**  - Packaged MCP -> Packaged Numeric (pkgmcp2numpkg.srg)
**  - Packaged MCP -> Obfuscated (pkgmcp2obf.srg)
**  - Bukkit -> Packaged Numeric (cb2numpkg.srg)
**
**  Target inheritance:
**  - Bukkit inheritance remapped to Packaged (nms.inheritmap)
**
]]--

AutoMapper = require("AutoMapper")

AutoMapper.setCacheDir("./cache/")
AutoMapper.setOutputDir("./1.6.2.mcpc/")
AutoMapper.setMinecraftVersion("1.6.2")
AutoMapper.setForgeVersion("9.10.1.871")

-- Add shortcuts for Mapping and Inheritance
local Mapping = AutoMapper.Mapping
local Inheritance = AutoMapper.Inheritance

local targetMappings = {
	{ Mapping.DESCRIPTIVE_PACKAGED, Mapping.NUMERIC_PACKAGED, "pkgmcp2numpkg.srg" },
	{ Mapping.DESCRIPTIVE_PACKAGED, Mapping.OBFUSCATED, "pkgmcp2obf.srg" },
	{ Mapping.BUKKIT, Mapping.NUMERIC_PACKAGED, "cb2numpkg.srg" }
}

local targetInheritance = {
	{ Inheritance.BUKKIT, Mapping.NUMERIC_PACKAGED, "nms.inheritmap" }
}

AutoMapper.generate(targetMappings, targetInheritance)
