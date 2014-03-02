package net.ixios.advancedthaumaturgy.models;

import java.awt.Color;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.ReadableColor;

import net.ixios.advancedthaumaturgy.tileentities.TileMicrolithBase;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.obj.WavefrontObject;
import cpw.mods.fml.client.FMLClientHandler;

public class ModelMinilith implements IModelContainer
{
	private ResourceLocation texture = new ResourceLocation("minecraft", "textures/blocks/obsidian.png");
	private WavefrontObject model = (WavefrontObject)AdvancedModelLoader.loadModel("/assets/advthaum/models/minilith.obj");
	
	@Override
	public void render(TileEntity te)
	{
	
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture);
		model.renderAllExcept("Sphere");

		GL11.glPushMatrix();
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
		long ticks = Minecraft.getMinecraft().renderViewEntity.ticksExisted;
		 
		float h = (float) (Math.sin(ticks % 32767.0F / 16.0F) * 0.05F);
		
		GL11.glTranslated(0f, h, 0f);
		
		if (te instanceof TileMicrolithBase)
		{
			TileMicrolithBase base = (TileMicrolithBase)te;
			Color clr = base.getColor();
			GL11.glColor4f(clr.getRed() / 255F, clr.getGreen() / 255F, clr.getBlue() / 255F, clr.getAlpha() / 255F);
			model.renderOnly("Sphere");
		}
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
	
		GL11.glPopMatrix();

	}
	
	
	
	
	
}