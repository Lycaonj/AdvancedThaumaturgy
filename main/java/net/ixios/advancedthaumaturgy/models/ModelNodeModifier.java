package net.ixios.advancedthaumaturgy.models;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.obj.WavefrontObject;

public class ModelNodeModifier implements IModelContainer
{

	public static final ResourceLocation texture = new ResourceLocation("advthaum", "textures/models/thaum_node_modifier_tex.png");
	public static final WavefrontObject model = (WavefrontObject)AdvancedModelLoader.loadModel("/assets/advthaum/models/thaum_node_modifier_2.obj");
	
	@Override
	public void render(TileEntity te)
	{
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture);
		model.renderAll();
	}
	
}
