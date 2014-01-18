package net.ixios.advancedthaumaturgy.fx;

import org.lwjgl.opengl.GL11;

import thaumcraft.client.lib.UtilsFX;
import net.ixios.advancedthaumaturgy.misc.Vector3F;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class FloatyLineFX extends CustomParticleFX
{
	public final int color;
	private Vector3F src, dst;
	private float random = 1f;
	
	public FloatyLineFX(World world, Vector3F src, Vector3F dst, int color, boolean randomize)
	{
		super(world, src.x, src.y, src.z);
		this.src = src;
		this.dst = dst;
		this.color = color;
		this.particleAge = 0;
		this.particleMaxAge = 20;
		this.random = 1.0f + (float)(randomize ? world.rand.nextGaussian() : 0);
	}
	
	public FloatyLineFX(World world, Vector3F src, Vector3F dst, int color)
	{
		this(world, src, dst, color, false);
	}
	
	@Override
	public void onUpdate()
	{
		if (particleMaxAge-- <= 0)
			setDead();
	}
	
	@Override
	public void renderParticle(Tessellator tessellator, float partialTicks,	float rotationX, float rotationXZ, float rotationZ,
			float rotationYZ, float rotationXY)
	{
		boolean wasdrawing = tessellator.isDrawing;
		
		if (wasdrawing)
			tessellator.draw();
		
		float ticks = (float)(Minecraft.getMinecraft().renderViewEntity).ticksExisted + partialTicks;
        float h = (float) (Math.sin((((ticks * random) % 32767D) / 16D)) * 0.05D);
       
        GL11.glPushMatrix();
    	UtilsFX.drawFloatyLine(src.x, src.y - (h * 2.0D), src.z, dst.x, dst.y, dst.z, partialTicks, color, "textures/misc/wispy.png", -0.02F, Math.min(ticks, 10F) / 10F);
    	GL11.glPopMatrix();
        
        if (wasdrawing)
			tessellator.startDrawingQuads();
        
        Minecraft.getMinecraft().renderEngine.bindTexture(particleTexture);
        
	}
}
