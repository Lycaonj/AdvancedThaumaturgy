package net.ixios.advancedthaumaturgy.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.client.FMLClientHandler;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.wands.IWandFocus;
import thaumcraft.client.fx.FXScorch;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.tiles.TileJarFillable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Utilities
{
	// Stolen from Vazkii
	public static NBTTagCompound getCacheCompound(File cache)
	{
        if (cache == null)
        	throw new RuntimeException("No cache file!");

        try 
        {
        	NBTTagCompound cmp = CompressedStreamTools.readCompressed(new FileInputStream(cache));
        		return cmp;
        }
        catch (IOException e) 
        {
        	NBTTagCompound cmp = new NBTTagCompound();

        	writeCacheCompound(cmp, cache);
        	return getCacheCompound(cache);
        	
        }
	}
	
	public static void writeCacheCompound(NBTTagCompound tag, File cache)
	{
		try
		{
			CompressedStreamTools.writeCompressed(tag, new FileOutputStream(cache));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static TileJarFillable findEssentiaJar(World world, Aspect aspect, int srcx, int srcy, int srcz, int xrange, int yrange, int zrange)
    {
    	for (int cx = srcx - (xrange / 2); cx < srcx + (xrange / 2); cx++)
        {
            for (int cy = srcy - (yrange / 2); cy < srcy + (yrange / 2); cy++)
            {
                for (int cz = srcz - (zrange / 2); cz < srcz + (zrange / 2); cz++)
                {
                    TileEntity te = world.getBlockTileEntity(cx,  cy,  cz);
                    if ((te instanceof TileJarFillable))
                    {
                        TileJarFillable jar = (TileJarFillable)te;
                        
                        if (jar.amount == 0)
                            continue;
                        
                        if (jar.aspect == null)
                            continue;
                         
                        if (jar.doesContainerContainAmount(aspect, 1))
                        {
                            return (TileJarFillable)te;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public static TileJarFillable findEssentiaJar(World world, Aspect aspect, TileEntity src, int xrange, int yrange, int zrange)
    {
        return findEssentiaJar(world, aspect, src.xCoord, src.yCoord, src.zCoord, xrange, yrange, zrange);
    }
   

	public static IWandFocus getEquippedFocus(ItemStack stack)
	{
		 if ((stack == null) || !(stack.getItem() instanceof ItemWandCasting))
			 return null;
	 
		 ItemWandCasting wand = (ItemWandCasting)stack.getItem();
	 
		IWandFocus focus = wand.getFocus(stack);
	 
		return focus;
	  
	}
	
	public static boolean isOp(String name)
	{
		return MinecraftServer.getServer().getConfigurationManager().getOps().contains(name);
	}
	
	 @SuppressWarnings("unchecked")
	 public static void broadcastMessage(String text)
	 {
		 
	     List<EntityPlayer> players;
	     
	     if (Minecraft.getMinecraft().theWorld.isRemote)
	    	 players = Minecraft.getMinecraft().theWorld.playerEntities;
	     else
	    	 players = MinecraftServer.getServer().worldServers[0].playerEntities;
	     
	     for (int t = 0; t < players.size(); t++)
	     {
	         players.get(t).addChatMessage(text);
	     }
	 }
	 
	 public static void shootFireInDirection(World world, Vec3 direction)
	{
		Vec3 dir = direction.normalize();
	
		if (!world.isRemote)
			return;
		
        for(int q = 0; q < 3; q++)
        {
            FXScorch ef = new FXScorch(world, direction.xCoord, direction.yCoord, direction.zCoord, dir, 17);
            ef.posX += direction.xCoord * 0.30000001192092896D;
            ef.posY += direction.yCoord * 0.30000001192092896D;
            ef.posZ += direction.zCoord * 0.30000001192092896D;
            ef.prevPosX = ef.posX;
            ef.prevPosY = ef.posY;
            ef.prevPosZ = ef.posZ;
            ef.posX += direction.xCoord * 0.30000001192092896D;
            ef.posY += direction.yCoord * 0.30000001192092896D;
            ef.posZ += direction.zCoord * 0.30000001192092896D;
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(ef);
        }
	}
	 
	 public static boolean removeResearch(EntityPlayer player, String research)
	 {
		 ArrayList<String> list = (ArrayList<String>) ResearchManager.getResearchForPlayer(player.username);
		for (Iterator<String>it = list.iterator(); it.hasNext();)
		{
			String current = (String)it.next();
			
			if (current.equalsIgnoreCase(research))
			{
				it.remove();
				return true;
			}
		}
		return false;
	 }
}
