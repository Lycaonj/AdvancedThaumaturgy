package net.ixios.advancedthaumaturgy.fx;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.UtilsFX;


public class ColorableSparkleFX extends EntityFX
{
	public int multiplier;
    public boolean shrink;
    public int particle;
    public boolean tinkle;
    public int blendmode;
    public boolean slowdown;
    public int currentColor;
    
    public ColorableSparkleFX(World world, double x, double y, double z, 
            float scale, float r, float g, float b, int multiplier)
    {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        multiplier = 2;
        shrink = true;
        particle = 16;
        tinkle = false;
        blendmode = 1;
        slowdown = true;
        currentColor = 0;
        super.particleRed = r;
        super.particleGreen = g;
        super.particleBlue = b;
        super.particleGravity = 0.0F;
        super.motionX = super.motionY = super.motionZ = 0.0D;
        super.particleScale *= scale;
        super.particleMaxAge = 3 * multiplier;
        this.multiplier = multiplier;
        super.noClip = false;
        setSize(0.01F, 0.01F);
        super.prevPosX = super.posX;
        super.prevPosY = super.posY;
        super.prevPosZ = super.posZ;
    }

    public void renderParticle(Tessellator tessellator, float f, float f1, float f2, float f3, float f4, float f5)
    {
    	boolean wasdrawing = tessellator.isDrawing;
    	
    	if (wasdrawing)
    	{
    		tessellator.draw();
    		tessellator.startDrawingQuads();
    	}
    	
        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, blendmode);
        UtilsFX.bindTexture("textures/misc/particles.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.75F);
        int part = particle + super.particleAge / multiplier;
        float var8 = (float)(part % 8) / 8F;
        float var9 = var8 + 0.124875F;
        float var10 = (float)(part / 8) / 8F;
        float var11 = var10 + 0.124875F;
        float var12 = 0.1F * super.particleScale;
        
        if (shrink)
            var12 *= (float)((super.particleMaxAge - super.particleAge) + 1) / (float)super.particleMaxAge;
        
        float var13 = (float)((super.prevPosX + (super.posX - super.prevPosX) * (double)f) - EntityFX.interpPosX);
        float var14 = (float)((super.prevPosY + (super.posY - super.prevPosY) * (double)f) - EntityFX.interpPosY);
        float var15 = (float)((super.prevPosZ + (super.posZ - super.prevPosZ) * (double)f) - EntityFX.interpPosZ);
        
        tessellator.setBrightness(240);
        tessellator.setColorRGBA_F(super.particleRed, super.particleGreen, super.particleBlue, 1.0F);
        tessellator.addVertexWithUV(var13 - f1 * var12 - f4 * var12, var14 - f2 * var12, var15 - f3 * var12 - f5 * var12, var9, var11);
        tessellator.addVertexWithUV((var13 - f1 * var12) + f4 * var12, var14 + f2 * var12, (var15 - f3 * var12) + f5 * var12, var9, var10);
        tessellator.addVertexWithUV(var13 + f1 * var12 + f4 * var12, var14 + f2 * var12, var15 + f3 * var12 + f5 * var12, var8, var10);
        tessellator.addVertexWithUV((var13 + f1 * var12) - f4 * var12, var14 - f2 * var12, (var15 + f3 * var12) - f5 * var12, var8, var11);
        tessellator.draw();
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(UtilsFX.getParticleTexture());
        tessellator.startDrawingQuads();
    }

    public void onUpdate()
    {
        super.prevPosX = super.posX;
        super.prevPosY = super.posY;
        super.prevPosZ = super.posZ;
        if(super.particleAge == 0 && tinkle && super.worldObj.rand.nextInt(10) == 0)
            super.worldObj.playSoundAtEntity(this, "random.orb", 0.02F, 0.7F * ((super.worldObj.rand.nextFloat() - super.worldObj.rand.nextFloat()) * 0.6F + 2.0F));
        if(super.particleAge++ >= super.particleMaxAge)
            setDead();
        super.motionY -= 0.040000000000000001D * (double)super.particleGravity;
        if(!super.noClip)
            pushOutOfBlocks(super.posX, (super.boundingBox.minY + super.boundingBox.maxY) / 2D, super.posZ);
        super.posX += super.motionX;
        super.posY += super.motionY;
        super.posZ += super.motionZ;
        if(slowdown)
        {
            super.motionX *= 0.90800000190734864D;
            super.motionY *= 0.90800000190734864D;
            super.motionZ *= 0.90800000190734864D;
            if(super.onGround)
            {
                super.motionX *= 0.69999998807907104D;
                super.motionZ *= 0.69999998807907104D;
            }
        }
    
    }

    public void setGravity(float value)
    {
        super.particleGravity = value;
    }

    protected boolean pushOutOfBlocks(double par1, double par3, double par5)
    {
        int var7 = MathHelper.floor_double(par1);
        int var8 = MathHelper.floor_double(par3);
        int var9 = MathHelper.floor_double(par5);
        double var10 = par1 - (double)var7;
        double var12 = par3 - (double)var8;
        double var14 = par5 - (double)var9;
        if(!super.worldObj.isAirBlock(var7, var8, var9))
        {
            boolean var16 = !super.worldObj.isBlockNormalCube(var7 - 1, var8, var9);
            boolean var17 = !super.worldObj.isBlockNormalCube(var7 + 1, var8, var9);
            boolean var18 = !super.worldObj.isBlockNormalCube(var7, var8 - 1, var9);
            boolean var19 = !super.worldObj.isBlockNormalCube(var7, var8 + 1, var9);
            boolean var20 = !super.worldObj.isBlockNormalCube(var7, var8, var9 - 1);
            boolean var21 = !super.worldObj.isBlockNormalCube(var7, var8, var9 + 1);
            byte var22 = -1;
            double var23 = 9999D;
            if(var16 && var10 < var23)
            {
                var23 = var10;
                var22 = 0;
            }
            if(var17 && 1.0D - var10 < var23)
            {
                var23 = 1.0D - var10;
                var22 = 1;
            }
            if(var18 && var12 < var23)
            {
                var23 = var12;
                var22 = 2;
            }
            if(var19 && 1.0D - var12 < var23)
            {
                var23 = 1.0D - var12;
                var22 = 3;
            }
            if(var20 && var14 < var23)
            {
                var23 = var14;
                var22 = 4;
            }
            if(var21 && 1.0D - var14 < var23)
            {
                var23 = 1.0D - var14;
                var22 = 5;
            }
            float var25 = super.rand.nextFloat() * 0.05F + 0.025F;
            float var26 = (super.rand.nextFloat() - super.rand.nextFloat()) * 0.1F;
            if(var22 == 0)
            {
                super.motionX = -var25;
                super.motionY = super.motionZ = var26;
            }
            if(var22 == 1)
            {
                super.motionX = var25;
                super.motionY = super.motionZ = var26;
            }
            if(var22 == 2)
            {
                super.motionY = -var25;
                super.motionX = super.motionZ = var26;
            }
            if(var22 == 3)
            {
                super.motionY = var25;
                super.motionX = super.motionZ = var26;
            }
            if(var22 == 4)
            {
                super.motionZ = -var25;
                super.motionY = super.motionX = var26;
            }
            if(var22 == 5)
            {
                super.motionZ = var25;
                super.motionY = super.motionX = var26;
            }
            return true;
        } else
        {
            return false;
        }
    }

}

