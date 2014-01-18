package net.ixios.advancedthaumaturgy.fx;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.relauncher.Side;

@SideOnly(Side.CLIENT)
public class StreamerFX extends EntityFX
{

	public double dstX, dstY, dstZ;
	public final int color;
	
	protected StreamerFX(World world, double srcx, double srcy, double srcz, double dstx, double dsty, double dstz, int color, int lifeticks)
	{
		super(world, srcx, srcy, srcz);
		dstX = dstx;
		dstY = dsty;
		dstZ = dstz;
		this.color = color;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
	}
	
}
