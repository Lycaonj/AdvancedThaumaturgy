package net.ixios.advancedthaumaturgy.items;

import java.util.List;

import org.lwjgl.input.Keyboard;

import net.ixios.advancedthaumaturgy.AdvThaum;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;

public class ItemCreativeNode extends ItemBlock
{

	public ItemCreativeNode(int id)
	{
		super(id);
		this.setCreativeTab(AdvThaum.tabAdvThaum);
	}

	@Override
	public void registerIcons(IconRegister ir)
	{
		itemIcon = ir.registerIcon("advthaum:node");
	}
	
	@Override
	public Icon getIcon(ItemStack stack, int pass) 
	{
		return itemIcon;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack stack,	EntityPlayer player, List list, boolean par4)
	{
		boolean shiftdown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
    	if (!shiftdown)
    		return;String desc = StatCollector.translateToLocal("tile.at.creativenode.desc");
		String[] lines = desc.split("\\|");
		for (String s : lines)
			list.add(s);
	}
}
