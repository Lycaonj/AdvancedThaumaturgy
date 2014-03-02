package net.ixios.advancedthaumaturgy.compat.energy;

import javax.imageio.spi.RegisterableService;

import net.ixios.advancedthaumaturgy.AdvThaum;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.Optional.Method;

@Optional.InterfaceList({ })
public class BCCompatChecker extends EnergyCompatBase 
{
	
	@Method(modid = "BuildCraft|Energy")
	@Override
	public void register()
	{
		ispresent = true;
	}
	
}
