package net.ixios.advancedthaumaturgy.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.research.ResearchManager;
import net.ixios.advancedthaumaturgy.tileentities.TileNodeModifier.Operation;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;

public class ATServerCommand implements ICommand
{
	
	@Override
	public int compareTo(Object arg0)
	{
		return 0;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] list)
	{
		ArrayList<String> options = new ArrayList<String>();
		options.add("research");
		return options;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return ((EntityPlayer)sender).capabilities.isCreativeMode || MinecraftServer.getServer().getConfigurationManager().getOps().contains(sender.getCommandSenderName());
	}

	@Override
	public List getCommandAliases()
	{
		return null;
	}

	@Override
	public String getCommandName()
	{
		return "at";
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "/at <option> <parameters>";
	}

	@Override
	public boolean isUsernameIndex(String[] arg0, int arg1)
	{
		return false;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params)
	{
		if (!(sender instanceof EntityPlayer))
		{
			sender.sendChatToPlayer(new ChatMessageComponent().addText("This command is only available to players."));
			return;
		}
		
		EntityPlayer player = (EntityPlayer)sender;
		
		if (params.length == 0)
		{
			showHelp(player);
			return;
		}
		
		String cmd = params[0].toLowerCase();
		
		if (cmd == "research")
		{
			if (params.length < 2)
			{
				showHelp(player);
				return;
			}

			String option = params[1].toLowerCase();
			String which = params[2].toLowerCase();
			
			if (option == "add")
			{
				Collection rc = ResearchCategories.researchCategories.values();
                
                for(Iterator i = rc.iterator(); i.hasNext();)
                {
                    ResearchCategoryList cat = (ResearchCategoryList)i.next();
                    Collection rl = cat.research.values();
                    Iterator res = rl.iterator();
                    while(res.hasNext()) 
                    {
                        ResearchItem ri = (ResearchItem)res.next();
                        if(!ResearchManager.isResearchComplete(player.username, ri.key) && ri.key.equalsIgnoreCase(which))
                        {
                            Thaumcraft.proxy.getResearchManager().completeResearch(player, ri.key);
                            player.addChatMessage("Added research: " + ri.getName());
                        }
                    }
                }
	                
			}
			else if (option == "remove")
			{
				ArrayList<String> list = (ArrayList<String>) ResearchManager.getResearchForPlayer(player.username);
				for (Iterator<String>it = list.iterator(); it.hasNext();)
				{
					String research = (String)it.next();
					
					if (research.equalsIgnoreCase(option))
					{
						player.addChatMessage("Removing research: " + research);
						it.remove();
					}
				}
				player.addChatMessage("Research removal complete.");
			}
			else
			{
				showHelp(player);
			}
		}
	}

	private void showHelp(EntityPlayer plr)
	{
		plr.addChatMessage("Usage:  /at <command> <option> <parameters>");
		plr.addChatMessage("     :  /at research add|remove ResearchKey");
	}
}
