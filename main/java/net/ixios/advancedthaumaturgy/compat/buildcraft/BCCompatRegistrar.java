package net.ixios.advancedthaumaturgy.compat.buildcraft;

import net.ixios.advancedthaumaturgy.AdvThaum;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.Optional.Method;

@Optional.InterfaceList({ })
public class BCCompatRegistrar extends BCCompatBase 
{
	@Method(modid = "BuildCraft|Energy")
	@Override
	public void register() 
	{
		forceRegister();
	}
	
	public void forceRegister()
	{
		AdvThaum.log("Buildcraft|Energy found, enabling BC integration.");
		AdvThaum.proxy.registerBuildcraftThings();
	}
}
