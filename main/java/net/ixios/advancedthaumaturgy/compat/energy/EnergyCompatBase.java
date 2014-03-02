package net.ixios.advancedthaumaturgy.compat.energy;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.blocks.BlockEssentiaEngine;
import net.ixios.advancedthaumaturgy.models.ModelEngine;
import net.ixios.advancedthaumaturgy.renderers.GenericRenderer;
import net.ixios.advancedthaumaturgy.tileentities.TileEssentiaEngine;
import net.minecraft.block.material.Material;
import net.minecraftforge.client.MinecraftForgeClient;

public class EnergyCompatBase 
{
	private boolean hasregistered = false;
	
	public void register()
	{
		AdvThaum.log("Energy mod not found, skipping integration.");
	}
	
	public void forceRegister(String modname)
	{
		int essentiaengineid = AdvThaum.config.getBlock("BlockIDs", "essentiaengine", 3436).getInt();
    	
    	if (!AdvThaum.config.get("Feature Control", "enable_engine", true).getBoolean(true))
    		return;

		AdvThaum.EssentiaEngine = new BlockEssentiaEngine(essentiaengineid, Material.rock);
		AdvThaum.EssentiaEngine.register();

		if (FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT)
			return;
		
		GenericRenderer renderer = new GenericRenderer(new ModelEngine());
		MinecraftForgeClient.registerItemRenderer(BlockEssentiaEngine.blockID, renderer);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEssentiaEngine.class, renderer);	
	}
	
}
