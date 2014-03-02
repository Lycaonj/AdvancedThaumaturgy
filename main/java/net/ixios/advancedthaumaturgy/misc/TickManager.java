package net.ixios.advancedthaumaturgy.misc;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;

import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.common.tiles.TileInfusionMatrix;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.blocks.BlockPlaceholder;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.relauncher.Side;

public class TickManager implements ITickHandler
{
	public ArrayList<TileInfusionMatrix> waiting = new ArrayList<TileInfusionMatrix>(); 
	public HashMap<TileInfusionMatrix, Integer> monitoring = new HashMap<TileInfusionMatrix, Integer>();
	public HashMap<String, AeroData> aeroblocks = new HashMap<String, AeroData>();
	
	public void loadData()
	{
		WorldServer world = MinecraftServer.getServer().worldServers[0];
		File location = world.getChunkSaveLocation();
		File cacheFile = new File(location, "advthaum_infusion.dat");
                  
		NBTTagCompound tag = Utilities.getCacheCompound(cacheFile);
          
		if (tag == null)
			return;
          
		int numwaiting = tag.getInteger("numwaiting");
		
        for (int i = 0; i < numwaiting; i++)
        {
        	int x = tag.getInteger("waitingx_" + i);
        	int y = tag.getInteger("waitingy_" + i);
        	int z = tag.getInteger("waitingz_" + i);
        	
        	TileEntity te = world.getBlockTileEntity(x, y, z);
        	if (!(te instanceof TileInfusionMatrix))
        		continue;
        	TileInfusionMatrix im = (TileInfusionMatrix)te;
        	
        	waiting.add(im);
        }
          
        int nummonitoring = tag.getInteger("nummonitoring");
        
        for (int i = 0; i < nummonitoring; i++)
        {
        	int x = tag.getInteger("monitoringx_" + i);
        	int y = tag.getInteger("monitoringy_" + i);
        	int z = tag.getInteger("monitoringx_" + i);
        	
        	TileEntity te = world.getBlockTileEntity(x, y, z);
        	if (!(te instanceof TileInfusionMatrix))
        		continue;
        	TileInfusionMatrix im = (TileInfusionMatrix)te;
        	
        	int base = tag.getInteger("monitoringi_" + i);

        	monitoring.put(im, base);		}
		
	}
	
	public void saveData()
	{
	    WorldServer world = MinecraftServer.getServer().worldServers[0];
		File location = world.getChunkSaveLocation();
		File cacheFile = new File(location, "advthaum_infusion.dat");
                  
		NBTTagCompound tag = Utilities.getCacheCompound(cacheFile);
          
		if (tag == null)
			return;
          
		tag.setInteger("numwaiting", waiting.size());
		int count = 0;
        for (Iterator<TileInfusionMatrix>i = waiting.iterator(); i.hasNext();)
        {
        	TileInfusionMatrix im = i.next();
        	tag.setInteger("waitingx_" + count, im.xCoord);
        	tag.setInteger("waitingy_" + count, im.xCoord);
        	tag.setInteger("waitingz_" + count, im.xCoord);
        }
          
        tag.setInteger("nummonitoring", monitoring.size());
        count = 0;
        for (Iterator<TileInfusionMatrix>i = monitoring.keySet().iterator(); i.hasNext();)
        {
        	TileInfusionMatrix im = i.next();
        	tag.setInteger("monitoringx_" + count, im.xCoord);
        	tag.setInteger("monitoringy_" + count, im.yCoord);
        	tag.setInteger("monitoringx_" + count, im.zCoord);
        	tag.setInteger("monitoringi_" + count, monitoring.get(im));
        }

	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	// OVERRIDES
	////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public String getLabel() 
	{
		return "AdvThaum RenderTickManager";
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
		checkMonitoredInfusionMatrices();
		checkMonitoredAeroBlocks();
		
	}

	private void checkMonitoredAeroBlocks()
	{
		for (Iterator<String>i = aeroblocks.keySet().iterator(); i.hasNext(); )
		{
			String key = i.next();
			AeroData data =  aeroblocks.get(key);
			
			Vector3 src = data.vector;
						
			Vector3F v = new Vector3F((float)data.player.posX, (float)data.player.posY, (float)data.player.posZ);
			float dist = src.distanceTo(v);
			if (dist > 6)
			{
				i.remove();
				data.player.worldObj.setBlock(src.x,  src.y,  src.z, data.blockid, data.blockmeta, 3);
			}
		}
	}
	
	private void checkMonitoredInfusionMatrices()
	{
		// check IMs we're waiting on to see if they're active, and if so, get their base instability
				// and move them to the monitoring hashmap
				for (Iterator<TileInfusionMatrix> i = waiting.iterator(); i.hasNext();)
				{
					TileInfusionMatrix im = i.next();
					if (im.active)
					{
						int instability = ReflectionHelper.getPrivateValue(TileInfusionMatrix.class, im, "instability");
						i.remove();
						monitoring.put(im, instability);
					}
				}
				
				for (Iterator<TileInfusionMatrix> i = monitoring.keySet().iterator(); i.hasNext();)
				{
					TileInfusionMatrix im = i.next();
					
					boolean isactive = im.active;
					
					if (!isactive)
					{
						i.remove();
						continue;
					}
					int baseinstability = monitoring.get(im);
					int currinstability = ReflectionHelper.getPrivateValue(TileInfusionMatrix.class, im, "instability");
			    	
					if (currinstability > (baseinstability * .75f))
					{
			    		ReflectionHelper.setPrivateValue(TileInfusionMatrix.class, im, (baseinstability * .75f), "instability");
					}
				}
	}
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) { }

	@Override
	public EnumSet<TickType> ticks() 
	{
		return EnumSet.of(TickType.SERVER);
	}
	
	public void beginMonitoring(TileInfusionMatrix im)
	{
		waiting.add(im);
	}
	
	public void beginMonitoring(EntityPlayer plr, Vector3 vec, Block block)
	{
		if (aeroblocks.containsKey(vec.toString()))
			return;
		int meta = plr.worldObj.getBlockMetadata(vec.x,  vec.y,  vec.z);
		plr.worldObj.setBlock(vec.x, vec.y, vec.z, BlockPlaceholder.blockID, 0, 2);
		aeroblocks.put(vec.toString(), new AeroData(plr, vec, block.blockID, meta));
		AdvThaum.log("Put block with ID " + block.blockID + " meta " + meta);
	}
	
}

