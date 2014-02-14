package net.ixios.advancedthaumaturgy.compat.energy;

import net.ixios.advancedthaumaturgy.AdvThaum;

public class EnergyCompatBase 
{
	private boolean hasregistered = false;
	
	public void register()
	{
		AdvThaum.log("Energy mod not found, skipping integration.");
	}
	
	public void forceRegister(String modname)
	{
		AdvThaum.log(modname + " found");
		if (hasregistered)
			return;
		AdvThaum.proxy.registerBuildcraftThings();
		hasregistered = true;
		
	}
}
