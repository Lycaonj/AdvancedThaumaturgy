package net.ixios.advancedthaumaturgy.tileentities;

import java.awt.Color;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemEssence;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.BiomeEvent.GetGrassColor;

public abstract class TileMicrolithBase extends TileEntity
{

	private Color color = null;
	
	public TileMicrolithBase(Color color)
	{
		this.color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public abstract boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
	        float hitY, float hitZ);
	
}
