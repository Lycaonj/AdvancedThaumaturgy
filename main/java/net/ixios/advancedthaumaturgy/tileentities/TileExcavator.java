package net.ixios.advancedthaumaturgy.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.lwjgl.util.Color;

import thaumcraft.common.items.wands.ItemWandCasting;

public class TileExcavator extends TileMicrolithBase
{

	public TileExcavator(Color color)
    {
	    super(color);
	    // TODO Auto-generated constructor stub
    }

	@Override
    public boolean onBlockActivated(World world, int x, int y, int z,
            EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
		if ((player.getHeldItem() != null) && (player.getHeldItem().getItem() instanceof ItemWandCasting))
		{
			// do a search for the 4 corners and activate if a square is formed, do maybe 64
		}
		else // toggle the finder beams ala landmarks
		{
			
		}
		
	    return false;
    }

}
