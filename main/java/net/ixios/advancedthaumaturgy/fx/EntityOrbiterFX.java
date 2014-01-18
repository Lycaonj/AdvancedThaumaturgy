package net.ixios.advancedthaumaturgy.fx;

import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntityOrbiterFX extends CustomParticleFX
{

	public double angleH, angleV;
	public double angleHchg, angleVchg;
	private double basex, basey, basez;
	
	private double distance = 0;
	
	public EntityOrbiterFX(World world, double x, double y, double z, double dist, int lifeticks)
	{
		this(world, x, y, z, dist, lifeticks, 0xFFFFFFFF);
	}
	
	public EntityOrbiterFX(World world, double x, double y, double z, double dist, int lifeticks, int color)
	{
		super(world, x, y, z);
		
		this.distance = dist;
		this.particleMaxAge = lifeticks;
   	
		setColor(color);
	    
	    particleScale = 0.5F;// world.rand.nextFloat() * 1.2F;
	    	    
		basex = x; basey = y; basez = z;
	
		updatePosition();
		
	}
	
	@Override
	public void onUpdate()
	{
		if (particleAge++ >= particleMaxAge)
		{
			setDead();
			return;
		}
		
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
	
		angleH += angleHchg;
		angleV += angleVchg;
      	      
		angleH %= 360;
		angleV %= 360;

		updatePosition();
   
		//AdvancedThaumaturgy.log("Base: " + basex + ";" + basey + ";" + basez + " | New: " + posX + ";" + posY + ";" + posZ);
    
	}
	
	private void updatePosition()
	{
		/*posX = basex + Math.cos(angleH * deg2rad) * Math.sin(angleV * deg2rad) * distance;
		posY = basey + Math.sin(angleH * deg2rad) * Math.sin(angleV * deg2rad) * distance;
		posZ = basez + Math.cos(angleV * deg2rad) * distance;*/
		double sh = Math.sin(angleH * deg2rad);
		double ch = Math.cos(angleH * deg2rad);
		double sv = Math.sin(angleV * deg2rad);
		double cv = Math.cos(angleV * deg2rad);
		
		if (angleV != 0)
			angleH = angleH*1;
		
		posX = basex + sh * cv * distance;
 		posY = basey + sh * sv * distance;
		posZ = basez + ch * cv * distance;
	}

}
