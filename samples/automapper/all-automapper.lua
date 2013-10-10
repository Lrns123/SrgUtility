--[[
**
**	Srg Utility - Generate all permutations (Using AutoMapper)
**
]]--

AutoMapper = require("AutoMapper")

-- Add shortcuts for Mapping and Inheritance
local Mapping = AutoMapper.Mapping
local Inheritance = AutoMapper.Inheritance

local mappingNames = {}
mappingNames[Mapping.OBFUSCATED] = "obf"
mappingNames[Mapping.NUMERIC] = "num"
mappingNames[Mapping.DESCRIPTIVE] = "mcp"
mappingNames[Mapping.NUMERIC_PACKAGED] = "pkgnum"
mappingNames[Mapping.DESCRIPTIVE_PACKAGED] = "pkgmcp"
mappingNames[Mapping.BUKKIT] = "cb"

local function generateMappingPermutations()
	local mappings = { Mapping.OBFUSCATED, Mapping.NUMERIC, Mapping.DESCRIPTIVE, Mapping.NUMERIC_PACKAGED, Mapping.DESCRIPTIVE_PACKAGED, Mapping.BUKKIT }

	local mappingTable = {}
	
	local _, v1, v2
	for _, v1 in pairs(mappings) do
		for _, v2 in pairs(mappings) do
			if (v1 ~= v2) then
				local entry = {v1, v2, mappingNames[v1] .. "2" .. mappingNames[v2] .. ".srg" }
				table.insert(mappingTable, entry)
			end
		end
	end
	
	return mappingTable
end

AutoMapper.setCacheDir("./cache/")
AutoMapper.setOutputDir("./1.6.2-all/")
AutoMapper.setMinecraftVersion("1.6.2")
AutoMapper.setForgeVersion("9.10.1.871")

local targetMappings = generateMappingPermutations()

local targetInheritance = {
	{ Inheritance.NMS, Mapping.OBFUSCATED, "obf.inheritmap" },
	{ Inheritance.NMS, Mapping.NUMERIC, "nms.inheritmap" },
	{ Inheritance.NMS, Mapping.NUMERIC_PACKAGED, "pkgnms.inheritmap" },
	{ Inheritance.BUKKIT, Mapping.OBFUSCATED, "cbobf.inheritmap" },
	{ Inheritance.BUKKIT, Mapping.NUMERIC, "cb.inheritmap" },
	{ Inheritance.BUKKIT, Mapping.NUMERIC_PACKAGED, "cbpkg.inheritmap" }
}

AutoMapper.generate(targetMappings, targetInheritance)
