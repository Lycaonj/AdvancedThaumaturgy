package net.ixios.advancedthaumaturgy.fx;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class CustomParticleFX extends EntityFX
{
	
	protected static ResourceLocation particleTexture =
			(ResourceLocation)ReflectionHelper.getPrivateValue(EffectRenderer.class, null, new String[] { "particleTextures", "b", "field_110737_b" });
	protected static double deg2rad = 0.0174532925;
	
	public CustomParticleFX(World world, double x, double y, double z)
	{
		super(world, x, y, z);
	}

	public CustomParticleFX(World world, double x, double y, double z, double xc, double yc, double zc)
	{
		this(world, x, y, z);
		this.motionX = xc;
		this.motionY = yc;
		this.motionZ = zc;
	}

	public void setColor(int color)
	{
		Color c = new Color(color);
		this.particleRed = c.getRed() / 255F;
		this.particleGreen = c.getGreen() / 255F;
		this.particleBlue = c.getBlue() / 255F;
		this.particleAlpha = c.getAlpha() / 255F;
	}
	
	public void setScale(float scale)
	{
		this.particleScale = scale;
	}
	
	public void setAge(int age)
	{
		this.particleMaxAge = age;
	}
	
	@Override
	public void moveEntity(double xamt, double yamt, double zamt)
	{
		posX += motionX;
		posY += motionY;
		posZ += motionZ;
	}
	
	public void renderParticle(Tessellator tessellator, float partialTicks, float rotationX, float rotationXZ, 
			float rotationZ, float rotationYZ, float rotationXY)
	{
		boolean wasdrawing = tessellator.isDrawing;
		
		if (wasdrawing)
			tessellator.draw();
		
		GL11.glPushMatrix();
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
        tessellator.startDrawingQuads();
        
		Minecraft.getMinecraft().renderEngine.bindTexture(Particles.flare);
		
		float f = partialTicks;
		float f1 = rotationX;
		float f2 = rotationXZ;
		float f3 = rotationZ;
		float f4 = rotationYZ;
		float f5 = rotationXY;
		
        float var12 = super.particleScale * 0.1F;
        float var13 = (float)((super.prevPosX + (super.posX - super.prevPosX) * (double)f) - EntityFX.interpPosX);
        float var14 = (float)((super.prevPosY + (super.posY - super.prevPosY) * (double)f) - EntityFX.interpPosY);
        float var15 = (float)((super.prevPosZ + (super.posZ - super.prevPosZ) * (double)f) - EntityFX.interpPosZ);
        float var16 = 1.0F;

        tessellator.setBrightness(240);
        tessellator.setColorRGBA_F(super.particleRed * var16, super.particleGreen * var16, super.particleBlue * var16, 0.75F);
        tessellator.addVertexWithUV(var13 - f1 * var12 - f4 * var12, var14 - f2 * var12, var15 - f3 * var12 - f5 * var12, 0.0D, 1.0D);
        tessellator.addVertexWithUV((var13 - f1 * var12) + f4 * var12, var14 + f2 * var12, (var15 - f3 * var12) + f5 * var12, 1.0D, 1.0D);
        tessellator.addVertexWithUV(var13 + f1 * var12 + f4 * var12, var14 + f2 * var12, var15 + f3 * var12 + f5 * var12, 1.0D, 0.0D);
        tessellator.addVertexWithUV((var13 + f1 * var12) - f4 * var12, var14 - f2 * var12, (var15 + f3 * var12) - f5 * var12, 0.0D, 0.0D);
        
        tessellator.draw();
		
		if (wasdrawing)
			tessellator.startDrawingQuads();

		GL11.glDisable(3042);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    	Minecraft.getMinecraft().renderEngine.bindTexture(particleTexture);
		
	}
	
}
