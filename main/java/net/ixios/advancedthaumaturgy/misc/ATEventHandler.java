package net.ixios.advancedthaumaturgy.misc;

import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.enchants.EnchantArcing;
import net.ixios.advancedthaumaturgy.misc.ArcData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import thaumcraft.common.items.wands.foci.ItemFocusShock;

public class ATEventHandler 
{
	
	
	@ForgeSubscribe
	public void onItemTossEvent(ItemTossEvent event)
	{
		
	}
	 
	@ForgeSubscribe
	public void onServerChatEvent(ServerChatEvent event)
	{
		/*String args[] = event.message.split("\\s");
		if (args.length < 3)
			return;
		String activator = args[0].toLowerCase();
		String op = args[1].toLowerCase();
		String cmd = args[2].toLowerCase();
				
		if (!activator.equals("at"))
			return;

		int opcode = -1;
		if (op.equalsIgnoreCase("add"))
			opcode = 1;
		if (op.equalsIgnoreCase("remove"))
			opcode = 2;
		else if (op.equalsIgnoreCase("clear"))
			opcode = 3;
			
		switch (opcode)
		{
			case 1:
			{
				
			}
			break;
			
			case 2:
			{
				
			}
			break;
			
			case 3:
			{

				ArrayList<String> list = (ArrayList<String>) ResearchManager.getResearchForPlayer(event.player.username);
				for (Iterator<String>it = list.iterator(); it.hasNext();)
				{
					String research = (String)it.next();
					AdvancedThaumaturgy.log("Research: " + research);
					if (research.equalsIgnoreCase("ESSENTIAENGINE"))
					{
						event.player.addChatMessage("Removing research: " + research);
						it.remove();
					}
					break;
				}
				event.player.addChatMessage("Research removal complete.");
			}
		}*/
	}
	
	 @ForgeSubscribe
	 public void onLivingAttack(LivingAttackEvent event)
	 {
		 if (event.source.getDamageType() == "indirectMagic")
			 return;
		 
		 if (!(event.source.getEntity() instanceof EntityLivingBase))
			 return;
		 
		 EntityLivingBase entity = (EntityLivingBase)event.source.getEntity();
		 
		 if (!(entity instanceof EntityPlayer))
			 return;
		 
		 EntityPlayer player = (EntityPlayer)entity;
		 
		 World world = entity.worldObj;
				 
		 // is source entity holding a wand?
		 ItemStack helditem = entity.getHeldItem();
		
		if (!(Utilities.getEquippedFocus(helditem) instanceof ItemFocusShock))
			return;
		 
		 // does it have Arcing?
		 // get arcing level
		 int arclevel = EnchantmentHelper.getEnchantmentLevel(EnchantArcing.enchantID, helditem);
		 
		 if (arclevel == 0)
			 return;
		 
		ArcData data = new ArcData(world, player, event.ammount, arclevel);
		
		ArcingDamageManager.add(data);
		
	 }

}
