package net.ixios.advancedthaumaturgy.tileentities;

import org.lwjgl.util.Color;

import net.minecraft.tileentity.TileEntity;

public class TileMinilithBase extends TileEntity
{

	private Color color = null;
	
	public TileMinilithBase(Color color)
	{
		this.color = color;
	}
	
	public Color getColor()
	{
		return color;
	}
}
