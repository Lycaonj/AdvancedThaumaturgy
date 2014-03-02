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
	public int getMinimumSuction()
	{
	    return super.getMinimumSuction() + 5;
	}
	
	@Override
	public int getSuctionAmount(ForgeDirection loc)
	{
	    return super.getSuctionAmount(loc) + 5;
	}
	
}
