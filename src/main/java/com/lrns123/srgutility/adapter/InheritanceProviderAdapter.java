package com.lrns123.srgutility.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.lrns123.srgutility.srg.SrgClass;
import com.lrns123.srgutility.srg.SrgInheritanceMap;

import net.md_5.specialsource.provider.InheritanceProvider;

public class InheritanceProviderAdapter implements InheritanceProvider
{
	private SrgInheritanceMap inheritanceMap;
	public InheritanceProviderAdapter(SrgInheritanceMap inheritance)
	{
		this.inheritanceMap = inheritance;
	}
	
	@Override
	public Collection<String> getParents(String className)
	{
		List<SrgClass> parents = inheritanceMap.getParent(className);
		if (parents == null)
			return null;
		
		List<String> plainParents = new ArrayList<String>(parents.size());
		
		for (SrgClass parent : parents)
		{
			plainParents.add(parent.getQualifiedName());
		}
		
		return plainParents;
	}

}
