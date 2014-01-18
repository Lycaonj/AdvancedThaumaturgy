package net.ixios.advancedthaumaturgy.fx;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

@SideOnly(Side.CLIENT)
public class OrbitingParticlesFX 
{
	
	private EntityOrbiterFX[] particles;
	
	public OrbitingParticlesFX(World world, float x, float y, float z, ForgeDirection axis)
	{
		particles = new EntityOrbiterFX[20];
		
		for (int i = 0; i < 20; i++)
		{
			particles[i] = new EntityOrbiterFX(world, x, y, z, 0.2, Integer.MAX_VALUE);
			// set xyzChg here based on axis
					}
	}
}
