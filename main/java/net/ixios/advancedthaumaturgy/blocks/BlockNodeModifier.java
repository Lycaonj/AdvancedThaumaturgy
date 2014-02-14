package net.ixios.advancedthaumaturgy.blocks;

import java.util.List;
import java.util.Arrays;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.blocks.BlockJar;
import thaumcraft.common.blocks.BlockJarItem;
import thaumcraft.common.blocks.ItemJarNode;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigResearch;
import thaumcraft.common.tiles.TileJarNode;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.items.ItemNodeModifier;
import net.ixios.advancedthaumaturgy.items.TCItems;
import net.ixios.advancedthaumaturgy.misc.ATResearchItem;
import net.ixios.advancedthaumaturgy.tileentities.TileNodeModifier;
import net.ixios.advancedthaumaturgy.tileentities.TileNodeModifier.Operation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockNodeModifier extends BlockContainer implements IWandable
{

	public final int renderID;
	public static int blockID;
	
	public BlockNodeModifier(int id, Material material)
	{
		super(id, material);
		blockID = id;
		this.setHardness(1.0F);
		this.setCreativeTab(AdvThaum.tabAdvThaum);
		this.setUnlocalizedName("at.modifier");
	    renderID = RenderingRegistry.getNextAvailableRenderId();
	}

	public void register()
	{
		GameRegistry.registerBlock(this, ItemNodeModifier.class, "blockNodeModifier");
		GameRegistry.registerTileEntity(TileNodeModifier.class, "tileentityNodeModifier");
		
		ItemStack gold = new ItemStack(Block.blockGold);
		ItemStack wood = new ItemStack(ConfigBlocks.blockMagicalLog, 1, 0);
		ItemStack essence = new ItemStack(ConfigItems.itemWispEssence, 1, 32767);
		ItemStack pedestal = new ItemStack(ConfigBlocks.blockStoneDevice, 2, 1);
			
		 InfusionRecipe recipe = ThaumcraftApi.addInfusionCraftingRecipe("NODEMODIFIER", new ItemStack(this), 5,
	                (new AspectList()).add(Aspect.GREED, 64).add(Aspect.AURA, 128).add(Aspect.MAGIC, 256).add(Aspect.TREE, 256),
	                pedestal,
	                new ItemStack[] { wood, essence, gold, essence, wood, essence, gold, essence });
	        
	        
		 ConfigResearch.recipes.put("NodeModifier", recipe);
	       
		 ItemStack empty = new ItemStack(ConfigBlocks.blockHole, 1, 15);
		 
		 ConfigResearch.recipes.put("NodeSetup", Arrays.asList(new Object[] {
		            new AspectList(),
		            Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(5),
		            Arrays.asList(new ItemStack[] {
		            empty, TCItems.arcanepedestal, TCItems.arcanepedestal, empty, empty, new ItemStack(this),
		            TCItems.arcanepedestal, empty, empty, TCItems.arcanepedestal, empty, empty }) }));
		                        
		 List list = (List)ConfigResearch.recipes.get("NodeSetup");
		 
		 ATResearchItem ri = new ATResearchItem("NODEMODIFIER", "BASICS",
					(new AspectList()).add(Aspect.AURA, 1).add(Aspect.SENSES, 1).add(Aspect.MAGIC, 1),
					-5, 6, 4,
					new ItemStack(this));
			ri.setTitle("at.research.nodemodifier.title");
			ri.setInfo("at.research.nodemodifier.desc");
			ri.setParents("NODEJAR", "INFUSION");
			ri.setPages(new ResearchPage("at.research.nodemodifier.pg1"),
					new ResearchPage("at.research.nodemodifier.pg2"), new ResearchPage(recipe), new ResearchPage(list));
			ri.setConcealed();
			ri.setSpecial();
			
			ri.registerResearchItem();
			
			
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockid)
	{
		super.onNeighborBlockChange(world, x, y, z, blockid);
		int id = world.getBlockId(x,  y + 1,  z);
		int jarid = ConfigBlocks.blockJar.blockID;
		
		if ((blockid == jarid) && id == 0)
			((TileNodeModifier)world.getBlockTileEntity(x,  y,  z)).cancel();
	}
	
	public static int refreshAvailableOperations(World world, int x, int y, int z)
	{
		TileEntity te = world.getBlockTileEntity(x,  y + 1,  z);
		if (!(te instanceof TileJarNode))
			return 0;
		TileJarNode jar = (TileJarNode)te;
		TileNodeModifier nm = (TileNodeModifier)world.getBlockTileEntity(x, y, z);
		
		nm.availableOperations.clear();
		
		int aspectcount = jar.getAspects().size();
		
		if (jar.getNodeModifier() != null)
		{
			switch (jar.getNodeModifier())
			{
				case PALE:
				{
					nm.availableOperations.add(Operation.RemovePale);
				}
				break;
				case FADING:
				{
					nm.availableOperations.add(Operation.RemoveFading);
				}
				break;
				default:
					break;
			}
		}
		else
		{
			if (jar.getNodeModifier() != NodeModifier.BRIGHT)
				nm.availableOperations.add(Operation.AddBright);
			if (jar.getNodeType() != NodeType.PURE)
				nm.availableOperations.add(Operation.AddPure);
		}
		
		if (jar.getNodeType() != null)
		{
			switch (jar.getNodeType())
			{
				case DARK:
				{
					nm.availableOperations.add(Operation.RemoveSinister);
				}
				break;
				
				case HUNGRY:
				{
					nm.availableOperations.add(Operation.RemoveHungry);
				}
				break;
				
				case NORMAL:
				{
					nm.availableOperations.add(Operation.AddHungry);
					nm.availableOperations.add(Operation.AddSinister);
					nm.availableOperations.add(Operation.AddTainted);
				}
				break;
				
				case TAINTED:
				{
					nm.availableOperations.add(Operation.RemoveTainted);
				}
				break;
				
				case UNSTABLE:
				{
					nm.availableOperations.add(Operation.RemoveUnstable);
				}
				break;
				case PURE:
					break;
			}
		}
		
		if (aspectcount < 6)
			nm.availableOperations.add(Operation.AddAspect);
		nm.availableOperations.add(Operation.IncreaseAspect);
		
		return nm.availableOperations.size();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		 if (!world.isRemote)
		 {
			 ItemStack helditem = player.getHeldItem();
			 int blockid = world.getBlockId(x, y + 1, z);
			 if (helditem != null && (helditem.getItem() instanceof ItemJarNode) && blockid == 0)
			 {
				return false;
			 }
			 else
			 {
				 player.openGui(AdvThaum.instance, 0, world, x, y, z);
			 }
			 return true;
		 }
		 return true;
	}
	
	@Override
    public TileEntity createTileEntity(World world, int metadata)
	{
		return new TileNodeModifier();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister ir)
	{
		blockIcon = ir.registerIcon("advthaum:node_modifier");
	}

	
	
    @Override
    public void onUsingWandTick(ItemStack arg0, EntityPlayer arg1, int arg2)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public ItemStack onWandRightClick(World world, ItemStack stack, EntityPlayer player)
    {
    	return stack;
    }

    @Override
    public int onWandRightClick(World world, ItemStack stack, EntityPlayer player,
            int x, int y, int z, int arg6, int arg7)
    {

        TileEntity target = world.getBlockTileEntity(x,  y + 1, z);
        TileNodeModifier nm = (TileNodeModifier)world.getBlockTileEntity(x, y, z);
        
        if (!(target instanceof TileJarNode))
        {
            if (nm.isActive())
                nm.cleanup();
            return 0;
        }
        
        TileNodeModifier ni = (TileNodeModifier)world.getBlockTileEntity(x, y, z);
        
        if (ni.isActive())
            return 0;
        
        world.scheduleBlockUpdate(x, y, z, BlockNodeModifier.blockID, 1);

        return 0;
    }

    @Override
    public void onWandStoppedUsing(ItemStack arg0, World arg1,
            EntityPlayer arg2, int arg3)
    {
        // TODO Auto-generated method stub
        
    }

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return null;
	}


	@Override
	public boolean isOpaqueCube()
	{
	   return false;
	}
	
	@Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType()
    {
    	return renderID;
    }
    
}
