package net.ixios.advancedthaumaturgy.tileentities;

import org.lwjgl.util.Color;

import net.ixios.advancedthaumaturgy.AdvThaum;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;

public class TileWatchfulMicrolith extends TileMicrolithBase
{

	Ticket ticket = null;
	
	public TileWatchfulMicrolith()
    {
	    super(new Color(0, 0, 128));
    }

	@Override
	public boolean canUpdate()
	{
		return true;
	}
	
	@Override
	public void updateEntity()
	{
	    super.updateEntity();
	    if (ticket == null)
	    	return;
	    
	    if (worldObj.rand.nextBoolean())
	    	AdvThaum.proxy.createCustomParticle(worldObj, xCoord + worldObj.rand.nextFloat(), yCoord, zCoord + worldObj.rand.nextFloat(), 0, worldObj.rand.nextGaussian() / 10, 0, 0xFF000099);
	}
	
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z,
            EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
		ChunkCoordIntPair coords = new ChunkCoordIntPair(xCoord >> 4, zCoord >> 4);
		
		if (ticket == null)
		{
			ticket = ForgeChunkManager.requestTicket(AdvThaum.instance, worldObj, Type.NORMAL);
		    if (ticket == null)
		    	return true;
		    ForgeChunkManager.forceChunk(ticket, coords);
		}
		else
		{
			ForgeChunkManager.unforceChunk(ticket, coords);
			ForgeChunkManager.releaseTicket(ticket);
			ticket = null;
		}
	    return true;
    }

	
}
