package net.ixios.advancedthaumaturgy.models;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.obj.WavefrontObject;

public class ModelVulcanizer implements IModelContainer
{
	private ResourceLocation texturebase = new ResourceLocation("advthaum", "textures/models/thaum_melter2_base_tex.png");
	private ResourceLocation texturebowl = new ResourceLocation("advthaum", "textures/models/thaum_melter2_bowl_tex.png");
	private WavefrontObject model = (WavefrontObject)AdvancedModelLoader.loadModel("/assets/advthaum/models/thaum_melter2.obj");
		
	public ModelVulcanizer()
	{

	}
	
	@Override
	public void render(TileEntity te)
	{
		// bind textures
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(texturebase);
		model.renderOnly("Cube");
		
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(texturebowl);
		model.renderOnly("BezierCurve_Mesh");
	}
		
}
	
