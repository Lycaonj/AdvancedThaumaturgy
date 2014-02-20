package net.ixios.advancedthaumaturgy.models;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.obj.WavefrontObject;

public class ModelFertilizer implements IModelContainer
{

	private ResourceLocation texture = new ResourceLocation("advthaum", "textures/models/thaum_sprinkler_tex.png");
	private WavefrontObject model = (WavefrontObject)AdvancedModelLoader.loadModel("/assets/advthaum/models/thaum_sprinkler.obj");

	@Override
	public void render(TileEntity te)
	{
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture);
		model.renderAll();
	}
}
