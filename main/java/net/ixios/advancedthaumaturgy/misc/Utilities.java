package net.ixios.advancedthaumaturgy.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.wands.IWandFocus;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.TileJarFillable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
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
	
}
