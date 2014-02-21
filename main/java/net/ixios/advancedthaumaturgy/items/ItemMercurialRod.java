package net.ixios.advancedthaumaturgy.items;

import java.util.List;

import org.lwjgl.input.Keyboard;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigResearch;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.misc.ATResearchItem;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class ItemMercurialRod extends WandRod
{

	ResourceLocation texture = new ResourceLocation("advthaum:wand_rod_quicksilver");
	
	public ItemMercurialRod()
	{
		super("mercurial", 500, new ItemStack(AdvThaum.MercurialRodBase), 20);
		AdvThaum.log("Adding mercurial wandrod");
		setGlowing(true);
		texture = new ResourceLocation("advthaum:textures/models/wand_rod_mercurial.png");
	}

    @Override
    public ResourceLocation getTexture()
    {
	    return texture;
    }
    
}
