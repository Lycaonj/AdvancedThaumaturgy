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
	
	@Override
	public AspectList getSuction(ForgeDirection loc)
	{
		AspectList list = super.getSuction(loc);
		for (Aspect a : list.getAspects())
			list.merge(a, 5);
	      return list;
	}
}
