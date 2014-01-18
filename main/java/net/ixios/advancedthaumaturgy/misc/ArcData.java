package net.ixios.advancedthaumaturgy.misc;

import java.util.ArrayList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ArcData 
{
	public final EntityPlayer source;
	public final World world;
	public float amount;
	public EntityLivingBase last;
	public int jumps;
	public int tickstilnextjump = 10;
	public ArrayList<EntityLivingBase>targets = new ArrayList<EntityLivingBase>();
	
	public ArcData(World world, EntityPlayer src, float amount, int jumps)
	{
		this.world = world;
		this.source = src;
		this.jumps = jumps;
		this.last = src;
		this.amount = amount;
	}
	
}
