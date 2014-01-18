package net.ixios.advancedthaumaturgy.tileentities;

import net.minecraft.tileentity.TileEntity;

public class TilePlaceholder extends TileEntity
{

	public int parentX, parentY, parentZ;
	private boolean broadcast = true;
	
	public TilePlaceholder(int x, int y, int z)
	{
		parentX = x; parentY = y; parentZ = z;
	}
	
	public void disableBroadcast()
	{
		broadcast = false;
	}
	
	public boolean broadcastDestroy()
	{
		return broadcast;
	}
}
