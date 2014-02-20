package net.ixios.advancedthaumaturgy.items;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.IWandFocus;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.Utils;

public class ItemFocusVoidCage extends ItemFocusBasic
{

	private AspectList cost = null;
	
	public ItemFocusVoidCage(int id)
    {
	    super(id);
	    cost = new AspectList().add(Aspect.ORDER, 5000).add(Aspect.ENTROPY, 5000);
	    setUnlocalizedName("at.voidcage");
    }

	public void register()
	{
		GameRegistry.registerItem(this, "focusvoidcage");
		setCreativeTab(AdvThaum.tabAdvThaum);
		
		// research
		// if tt parent to dislocation and place off
		//else
		// put where dislocation is
		
		// bestia, vacuos, praecantatio
		
	}
	
	@Override
	public void registerIcons(IconRegister ir)
	{
        super.icon = ir.registerIcon("advthaum:voidcage");
    }
	
	@Override
    public ItemStack onFocusRightClick(ItemStack itemstack, World world, EntityPlayer player, MovingObjectPosition mop)
    {
		NBTTagCompound tag = itemstack.getTagCompound();
    	
    	if (tag == null || !tag.hasKey("classname"))
    	{
		
	 		Entity pointedEntity = Utils.getPointedEntity(world, player, 1D, 32D, 0f);
	 		ItemWandCasting wand = ((ItemWandCasting)itemstack.getItem());
	 		
			if (pointedEntity != null && wand.consumeAllVis(itemstack, player, cost, true))
			{
				if (!world.isRemote)
	    		{
					pointedEntity.writeToNBT(tag);
					tag.setString("classname", pointedEntity.getClass().getCanonicalName());
					world.removeEntity(pointedEntity);
					NBTTagList pos = tag.getTagList("Pos");
					float x = (float)((NBTTagDouble)pos.tagAt(0)).data;
					float y = (float)((NBTTagDouble)pos.tagAt(1)).data;
					float z = (float)((NBTTagDouble)pos.tagAt(2)).data;
					AdvThaum.proxy.createSparkleBurst(world, x, y + 1, z, 5, 0xFFFF00FF);
	    		}
				else
					player.swingItem();
			}
    			
    	}
		else if (tag.hasKey("classname"))
		{
			EntityLivingBase entity = null;
			String classname = tag.getString("classname");
			Class c;
			
			ForgeDirection fd = ForgeDirection.getOrientation(mop.sideHit);
			
            try
            {
                c = Class.forName(classname);
            } catch (Exception e)
            {
            	return itemstack;
            }
            
			try
            {
				Constructor<? extends EntityLivingBase> constructor = c.getConstructor(World.class);
				entity = constructor.newInstance(world);
				
				NBTTagList newpos = new NBTTagList();
				newpos.appendTag(new NBTTagDouble((String)null, mop.blockX + fd.offsetX));
				newpos.appendTag(new NBTTagDouble((String)null, mop.blockY + fd.offsetY));
				newpos.appendTag(new NBTTagDouble((String)null, mop.blockZ + fd.offsetZ));

				NBTTagList motion = new NBTTagList();
				motion.appendTag(new NBTTagDouble((String)null, 0D));
				motion.appendTag(new NBTTagDouble((String)null, 0D));
				motion.appendTag(new NBTTagDouble((String)null, 0D));
				
				tag.removeTag("Pos");
				tag.removeTag("Motion");
				
				tag.setTag("Pos", newpos);
				tag.setTag("Motion", motion);
				
				entity.readFromNBT(tag);
				tag.removeTag("classname");
			    
            } catch (Exception e) 
            { 
            	return itemstack;
            }
			if (entity != null)
			{
				
				AdvThaum.proxy.createSparkleBurst(world, mop.blockX + 0.5F, mop.blockY + 0.5F, mop.blockZ + 0.5F, 5, 0xFFFF00FF);
				if (!world.isRemote)
					world.spawnEntityInWorld(entity);
				else
					player.swingItem();
			}
		}
    	
        return itemstack; 
    }

    public boolean isVisCostPerTick()
    {
        return false;
    }

    @Override
    public WandFocusAnimation getAnimation()
    {
        return WandFocusAnimation.WAVE;
    }
    
    @Override
    public AspectList getVisCost()
    {
        return cost;
    }
    public boolean onFocusBlockStartBreak(ItemStack itemstack, int x, int y, int i, EntityPlayer entityplayer)
    {
        return false;
    }

    @Override
    public boolean acceptsEnchant(int id)
    {
        return id == ThaumcraftApi.enchantFrugal;
    }
    
    
}
