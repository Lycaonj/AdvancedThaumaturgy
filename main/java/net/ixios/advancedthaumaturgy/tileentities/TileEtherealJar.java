package net.ixios.advancedthaumaturgy.tileentities;

import net.minecraftforge.common.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.tiles.TileJarFillable;

public class TileEtherealJar extends TileJarFillable 
{
	public static int maxAmt = 256;
	
	public TileEtherealJar()
	{
		this.maxAmount = maxAmt;
	}
	
}
