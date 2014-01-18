package net.ixios.advancedthaumaturgy.models;

import java.awt.Color;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.ReadableColor;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.obj.WavefrontObject;
import cpw.mods.fml.client.FMLClientHandler;

public class ModelMinilith implements IModelContainer
{
	private ResourceLocation texture = new ResourceLocation("minecraft", "textures/blocks/obsidian.png");
	private WavefrontObject model = (WavefrontObject)AdvancedModelLoader.loadModel("/assets/advthaum/models/minilith.obj");
	private float clrR, clrG, clrB;
	
	public ModelMinilith(ReadableColor color)
	{
		clrR = color.getRed() / 255F;
		clrG = color.getGreen() / 255F;
		clrB = color.getBlue() / 255F;
	}
	
	public ModelMinilith(int color)
	{
		clrR = ((color >> 16) & 0xFF) / 255F;
		clrG = (byte)((color >> 8) & 0xFF) / 255F;
		clrB = (byte)(color & 0xFF) / 255F;
	}
	
	public ModelMinilith(byte r, byte g, byte b)
	{
		clrR = r / 255F;
		clrG = g / 255F;
		clrB = b / 255F;
	}
	
	public ModelMinilith(Color color)
	{
		clrR = color.getRed() / 255F;
		clrG = color.getGreen() / 255F;
		clrB = color.getBlue() / 255F;
	}

	@Override
	public void render()
	{
		
		/*for (GroupObject go : model.groupObjects)
			AdvancedThaumaturgy.log(go.name);*/
		
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture);
		model.renderAllExcept("Sphere");

		GL11.glPushMatrix();
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(false);
        
		GL11.glColor4f(clrR, clrG, clrB, 0.4F);
		
		long time = Minecraft.getMinecraft().theWorld.getWorldTime();
		 
		double val = Math.sin(time / 5) / 40;
		
		GL11.glTranslated(0f, val, 0f);
		 
		model.renderOnly("Sphere");
		
		GL11.glDepthMask(true);
        
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	
		GL11.glPopMatrix();

	}
	
	
	
	
	
}