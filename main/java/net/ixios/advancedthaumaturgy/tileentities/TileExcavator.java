package net.ixios.advancedthaumaturgy.tileentities;

import net.ixios.advancedthaumaturgy.blocks.BlockMicrolith;
import net.ixios.advancedthaumaturgy.misc.Vector3;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.util.Color;

import thaumcraft.common.items.wands.ItemWandCasting;

public class TileExcavator extends TileMicrolithBase
{

	private Vector3 BlockNE = null;
	private Vector3 BlockNW = null;
	private Vector3 BlockSE = null;
	private Vector3 BlockSW = null;
	
	public TileExcavator(Color color)
    {
	    super(color);
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
			for (ForgeDirection fd : ForgeDirection.VALID_DIRECTIONS)
			{
				if (fd == ForgeDirection.DOWN || fd == ForgeDirection.UP)
					continue;
				findMyPosition();
			}
		}
		
	    return false;
    }

	private void findMyPosition()
	{
		if (hasBlockInDirection(ForgeDirection.SOUTH) && hasBlockInDirection(ForgeDirection.EAST))
			BlockNW = new Vector3(xCoord, yCoord, zCoord);
		if (hasBlockInDirection(ForgeDirection.SOUTH) && hasBlockInDirection(ForgeDirection.WEST))
			BlockNE = new Vector3(xCoord, yCoord, zCoord);
		if (hasBlockInDirection(ForgeDirection.NORTH) && hasBlockInDirection(ForgeDirection.EAST))
			BlockSW = new Vector3(xCoord, yCoord, zCoord);
		if (hasBlockInDirection(ForgeDirection.NORTH) && hasBlockInDirection(ForgeDirection.WEST))
			BlockSE = new Vector3(xCoord, yCoord, zCoord);
	}
	
	private boolean hasBlockInDirection(ForgeDirection dir)
	{
		for (int offset = 0; offset < 64; offset++)
		{
			int id = worldObj.getBlockId(xCoord + (dir.offsetX * offset), yCoord, zCoord + (dir.offsetZ * offset));
			int metadata = worldObj.getBlockMetadata(xCoord + (dir.offsetX * offset), yCoord, zCoord + (dir.offsetZ * offset));
			if (id == BlockMicrolith.blockID && metadata == 10)
				return true;
		}
		return false;
	}
	
}
