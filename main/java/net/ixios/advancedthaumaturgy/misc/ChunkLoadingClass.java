package net.ixios.advancedthaumaturgy.misc;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager.OrderedLoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.PlayerOrderedLoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

public class ChunkLoadingClass implements OrderedLoadingCallback, PlayerOrderedLoadingCallback
{

	@Override
    public void ticketsLoaded(List<Ticket> tickets, World world)
    {
	   
    }

	@Override
    public ListMultimap<String, Ticket> playerTicketsLoaded(ListMultimap<String, Ticket> tickets, World world)
    {
		return LinkedListMultimap.create();       
    }

	@Override
    public List<Ticket> ticketsLoaded(List<Ticket> tickets, World world, int maxTicketCount)
    {
		return new LinkedList<Ticket>();
    }
	 	
}
