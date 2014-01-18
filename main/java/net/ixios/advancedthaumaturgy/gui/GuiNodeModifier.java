package net.ixios.advancedthaumaturgy.gui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemWispEssence;
import thaumcraft.common.tiles.TileJarNode;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.blocks.BlockNodeModifier;
import net.ixios.advancedthaumaturgy.tileentities.TileNodeModifier;
import net.ixios.advancedthaumaturgy.tileentities.TileNodeModifier.Operation;
import net.ixios.advancedthaumaturgy.tileentities.TileNodeModifier.Requirements;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.input.Mouse;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GuiNodeModifier extends GuiContainer
{
	private static RenderItem itemRenderer = new RenderItem();
	private ResourceLocation texture = new ResourceLocation("advthaum", "textures/gui/nodegui.png");
	private ResourceLocation buttonup = new ResourceLocation("advthaum", "textures/gui/button-up.png");
	//private ResourceLocation buttondown = new ResourceLocation("advthaum", "textures/gui/button-down.png");
	
	private static ItemStack node = null;
	
	public static final int id = 0;
	private EntityPlayer player = null;
	private World world;
	private int blockX, blockY, blockZ;
	private int selectedop = -1;
	private Rectangle buttonpos = new Rectangle(0, 0, 1, 1);
	private TileNodeModifier nm = null;
	
	public GuiNodeModifier(EntityPlayer plr, int x, int y, int z)
	{
		super(new ContainerNodeModifier(plr));
		this.player = plr;
		this.world = player.worldObj;
		this.blockX = x;
		this.blockY = y;
		this.blockZ = z;
		this.width = 250;
		this.height = 150;
		this.xSize = 250;
		this.ySize = 150;
		
		if (node == null)
			node = new ItemStack(AdvThaum.CreativeNode);
		
		nm = (TileNodeModifier)world.getBlockTileEntity(blockX, blockY, blockZ);
		
		((BlockNodeModifier)Block.blocksList[BlockNodeModifier.blockID]).refreshAvailableOperations(world, x, y, z);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
	{
		drawTexture(texture, guiLeft, guiTop, xSize, ySize, 1f, 0.75f);
		if (!(((TileNodeModifier)(world.getBlockTileEntity(blockX,  blockY,  blockZ))).isActive()))
			drawButton();
	}
 
	private void drawTexture(ResourceLocation tex, int x, int y, int w, int h, float u, float v)
	{
		mc.renderEngine.bindTexture(tex);
		
		Tessellator tessellator = Tessellator.instance;
	    tessellator.startDrawingQuads();
	    tessellator.addVertexWithUV(x, y, zLevel, 0, 0);
	    tessellator.addVertexWithUV(x, y + h, zLevel, 0, v);
	    tessellator.addVertexWithUV(x + w, y + h, zLevel, u, v);
	    tessellator.addVertexWithUV(x + w, y, zLevel, u, 0);
	    tessellator.draw();
	}
	
	private void drawButton()
	{
		buttonpos.setBounds((guiLeft + xSize) - 60, (guiTop + ySize) - 20, 50, 20);
		drawTexture(buttonup, buttonpos.getX(), buttonpos.getY(), 0, 0, 1, 1);
		drawString(mc.fontRenderer, "Start", buttonpos.getX() +  20, buttonpos.getY() + 4, (selectedop == -1 ? Color.gray.getRGB() : Color.white.getRGB()));
		
		itemRenderer.renderItemIntoGUI(super.mc.fontRenderer, super.mc.renderEngine, node, buttonpos.getX() + 5, buttonpos.getY());
		
	}
	
	@Override
	public void drawScreen(int x, int y, float f)
	{

		super.drawScreen(x,  y,  f);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y)
	{
		super.drawGuiContainerForegroundLayer(x, y);
		
		if (nm == null)
			return;
		
		TileEntity te = world.getBlockTileEntity(blockX, blockY + 1, blockZ);
		
		if (te == null)
		{
			fontRenderer.drawSplitString("Begin modification by placing a jarred node on the Node Modifier.", 5, 5, 150, Color.WHITE.getRGB());
			return;
		}
		else if (!(te instanceof TileJarNode))
		{
			fontRenderer.drawSplitString("Invalid modification target.  Begin modification by placing a jarred node on the Node Modifier.", 5, 5, 150, Color.WHITE.getRGB());
			return;
		}
		
		if (nm.availableOperations.size() > 0)
		{
			for (int i = 0; i < nm.availableOperations.size(); i++)
			{
				Operation op = nm.availableOperations.get(i);
				drawString(fontRenderer,  (i == selectedop ? "\u00a7n" : "") + op.toString(), 20, 15 + (i * 12), Color.WHITE.getRGB());
			}
		}
	
		if (selectedop != -1 && selectedop < nm.availableOperations.size())
		{
			Operation op = nm.availableOperations.get(selectedop);
			Requirements reqs = nm.getRequirements(op);
			
			String todraw = "";
			
			switch (op)
			{
				case Idle:
					break;
					
				case AddPure:
				{
					todraw = "Allows you to infuse a node with <something>, transforming it to a Pure node.";	
				}
				break;
				
				case AddAspect:
				{
					todraw = "Allows you to infuse a node with a brand new aspect.";
				}
				break;
				
				case AddBright:
				{
					todraw = "Allows you to boost a node's brightness, increasing it's rate of regeneration.";
				}
				break;
				
				case AddHungry:
				{
					todraw = "Allows you to corrupt a node, causing it to consume anything in it's vicinity.";
				}
				break;
			
				case AddSinister:
				{
					todraw = "Infuses a node with Exanimus, weaving darkness into it and causing it to become Sinister.";
				}
				break;
				
				case AddTainted:
				{
					todraw = "Allows you to corrupt a node with Vitium, causing it to become tainted.";
				}
				break;
				
				case RemovePale:
				{
					todraw = "Allows you to strengthen a node with Auram, causing it to become normal, and thus increasing it's regeneration rate.";
				}
				break;
				
				case IncreaseAspect:
				{
					todraw = "Allows you to strengthen a node's existing aspects with Auram, causing it to incrase the node's maximum capacity by one.";
				}
				break;
				
				case RemoveFading:
				{
					todraw = "Allows you to infuse a node with Auram, causing it to become stronger, and able to regenerate it's aspects.";
				}
				break;
				
				case RemoveHungry:
				{
					todraw = "Allows you to imbue a node with Granum, sating it's violent hunger and returning it to normal functionality.";
				}
				break;
				
				case RemoveTainted:
				{
					todraw = "Allows you to corrupt a node with Ordo, filtering the tainted essence from the node.";
				}
				break;
				
				case RemoveUnstable:
				{
					todraw = "Allows you to corrupt a node with Ordo, stabilizing the weak node..";
				}
				break;
				
				case RemoveSinister:
				{
					todraw = "Removes the dark energies from a node, canceling out it's Sinister quality.";
				}
				break;
			}
			
			// draw string
			fontRenderer.drawSplitString(todraw, 20, ySize - 45, xSize - 30, Color.WHITE.getRGB());
			
			// draw required wisp items from right to left
			int xpos = xSize - 30;
			
			for (Aspect a : reqs.getEssenceArray())
			{
				
				// draw required wisp(s)
				int amt = reqs.getEssenceAmount(a);
				boolean match = ((amt & 0xFF) != 0);
				amt = (amt & 0x0F);
				
				ItemStack essence = new ItemStack(ConfigItems.itemWispEssence, reqs.getEssenceAmount(a), (match ? 0 : 32767));
				
				if (match)
					((ItemWispEssence)essence.getItem()).setAspects(essence, new AspectList().add(a, amt));
				
				itemRenderer.renderItemIntoGUI(super.mc.fontRenderer, super.mc.renderEngine, essence, xpos, 15);
				
				GL11.glPushMatrix();
	            GL11.glScalef(0.5F, 0.5F, 0.5F);
	            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	            String am = String.valueOf(reqs.getEssenceAmount(reqs.getCurrentEssence()));
	            int sw = mc.fontRenderer.getStringWidth(am);
	            mc.fontRenderer.drawString(am, (32 - sw) + xpos * 2, (32 - mc.fontRenderer.FONT_HEIGHT) + 15 * 2, 0xffffff);
	            GL11.glPopMatrix();
				
				int MouseX = (Mouse.getEventX() * this.width / this.mc.displayWidth) - guiLeft;
				int MouseY = (this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1) - guiTop;
			        
				Rectangle rect = new Rectangle(xpos, 15, 16, 16);
	
				if (rect.contains(MouseX, MouseY))
					drawItemStackTooltip(essence, MouseX, MouseY);
				
				xpos -= 20;
			
			}
			// draw aspect cost
			for (Aspect a : reqs.getEssentiaArray())
			{
				UtilsFX.drawTag(xpos, 15, a, reqs.getEssentiaAmount(a), 0, 2F);
				xpos -= 20;
			}
         
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	protected void mouseClicked(int x, int y, int keycode)
	{
		if (((TileNodeModifier)(world.getBlockTileEntity(blockX,  blockY,  blockZ))).isActive())
			return;
		
		if (buttonpos.contains(x, y))
		{
			if (selectedop == -1)
				return;
			
			Operation op = nm.availableOperations.get(selectedop);
			AdvThaum.proxy.startModification(nm, op);
			return;
		}
		
		int possible = (int)((y - 15 - guiTop) / 12);
		if (possible >= 0 && possible <= nm.availableOperations.size())
			selectedop = possible;

	}
}
