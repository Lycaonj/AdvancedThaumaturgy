package net.ixios.advancedthaumaturgy.misc;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import thaumcraft.common.items.wands.foci.ItemFocusShock;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ArcingDamageManager implements ITickHandler
{

	private static ArrayList<ArcData> instances = new ArrayList<ArcData>();
	
	public static void add(ArcData ad)
	{
		instances.add(ad);
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) 
	{
		update();		
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public String getLabel() 
	{
		return "ArcDataManager";
	}
	
	private void update()
	{
		for(Iterator<ArcData> i = instances.iterator(); i.hasNext();)
		{
			ArcData ad = i.next();
			ad.tickstilnextjump--;
			
			if (ad.tickstilnextjump > 0)
				continue;
			
			 ArrayList<EntityLivingBase> targets = new ArrayList<EntityLivingBase>();
			 
			 for (Object o : ad.world.getLoadedEntityList())
			 {
				 if (!(o instanceof EntityLivingBase))
					 continue;
				 EntityLivingBase e = (EntityLivingBase)o;
				 
				 if ((e != ad.last) && ((e.isCreatureType(EnumCreatureType.monster, false)) || (e.isCreatureType(EnumCreatureType.creature, false))) && (e.getDistanceToEntity(ad.last) <= 10) && (!ad.targets.contains(e)))
					 targets.add(e);
				 
				 if ((e instanceof EntityPlayer) && (MinecraftServer.getServer().isPVPEnabled()) && (e != ad.source))
					 targets.add(e);
			 }
		
			 if (targets.size() != 0)
			 {
				// pick random entity to jump to
				EntityLivingBase e = targets.get(ad.world.rand.nextInt(targets.size()));
				
				// do lightning bolt from last entity to entity
				ItemFocusShock.shootLightning(ad.world, ad.last, e.posX, e.posY, e.posZ, false);
				ad.world.playSoundEffect(e.posX, e.posY, e.posZ, "thaumcraft:shock", 0.25F, 1.0F);
				  
				e.attackEntityFrom(DamageSource.causeIndirectMagicDamage(ad.source, e), ad.amount);
				
				// current entity = last entity
				ad.last = e;
				ad.amount = Math.max(ad.amount * 0.8F, 1);
				ad.jumps--;
				ad.tickstilnextjump = 10;
				ad.targets.add(e);

				if (ad.jumps == 0)
				{  
					ad.targets.clear();
					i.remove();
				}
			 }
			 else
			 {
				 ad.targets.clear();
				 i.remove();
		
			 }
				
		}
		 
	}
	
}
