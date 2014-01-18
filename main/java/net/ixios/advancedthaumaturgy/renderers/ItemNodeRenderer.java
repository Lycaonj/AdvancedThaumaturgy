package net.ixios.advancedthaumaturgy.renderers;

import net.ixios.advancedthaumaturgy.blocks.BlockCreativeNode;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.*;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.config.Config;
import thaumcraft.common.tiles.TileNode;

public class ItemNodeRenderer implements IItemRenderer
{
	AspectList aspects;
	static String tx1 = "textures/misc/node.png";
	static String tx_c_n = "textures/misc/node_core_normal.png";
	static String tx_c_d = "textures/misc/node_core_dark.png";
	static String tx_c_u = "textures/misc/node_core_unstable.png";
	static String tx_c_t = "textures/misc/node_core_taint.png";
	static String tx_c_p = "textures/misc/node_core_pure.png";
	static String tx_c_h = "textures/misc/node_core_hungry.png";

    public ItemNodeRenderer()
    {
        aspects = (new AspectList()).add(Aspect.AIR, 40).add(Aspect.FIRE, 40).add(Aspect.EARTH, 40).add(Aspect.WATER, 40);
    }

    public boolean handleRenderType(ItemStack item, net.minecraftforge.client.IItemRenderer.ItemRenderType type)
    {
        return item != null && item.getItem().itemID == BlockCreativeNode.blockID && (item.getItemDamage() == 0 || item.getItemDamage() == 5);
    }

    public boolean shouldUseRenderHelper(net.minecraftforge.client.IItemRenderer.ItemRenderType type, ItemStack item, net.minecraftforge.client.IItemRenderer.ItemRendererHelper helper)
    {
        return helper != net.minecraftforge.client.IItemRenderer.ItemRendererHelper.EQUIPPED_BLOCK;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        if(type == ItemRenderType.ENTITY)
            GL11.glTranslatef(-0.5F, -0.25F, -0.5F);
        else
        if(type == net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED && (data[1] instanceof EntityPlayer))
            GL11.glTranslatef(0.0F, 0.0F, -0.5F);
        TileNode tjf = new TileNode();
        tjf.setAspects(aspects);
        tjf.setNodeType(NodeType.NORMAL);
        tjf.blockType = Block.blocksList[Config.blockAiryId];
        tjf.blockMetadata = 0;
        GL11.glPushMatrix();
        GL11.glTranslated(0.5D, 0.5D, 0.5D);
        GL11.glScaled(2D, 2D, 2D);
        renderItemNode(tjf);
        GL11.glRotatef(90F, 0.0F, 1.0F, 0.0F);
        renderItemNode(tjf);
        GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        renderItemNode(tjf);
        GL11.glPopMatrix();
        GL11.glEnable(32826);
    }

    public static void renderItemNode(INode node)
    {
        if(node.getAspects().size() > 0)
        {
            EntityLivingBase viewer = Minecraft.getMinecraft().renderViewEntity;
            float alpha = 0.5F;

            if(node.getNodeModifier() != null)
            {
                switch(node.getNodeModifier())
                {
                case BRIGHT: // '\001'
                    alpha *= 1.5F;
                    break;

                case PALE: // '\002'
                    alpha *= 0.66F;
                    break;

                case FADING: // '\003'
                    alpha *= MathHelper.sin((float)((Entity) (viewer)).ticksExisted / 3F) * 0.25F + 0.33F;
                    break;
                }
        }
            GL11.glPushMatrix();
            GL11.glAlphaFunc(516, 0.003921569F);
            GL11.glDepthMask(false);
            GL11.glDisable(2884);
            long nt = System.nanoTime();
            float bscale = 0.25F;
            GL11.glPushMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
            UtilsFX.bindTexture(tx1);
            int frames = UtilsFX.getTextureAnimationSize(tx1);
            int i = (int)((nt / 0x2625a00L + 1L) % (long)frames);
            int count = 0;
            float scale = 0.0F;
            float average = 0.0F;
            Aspect arr$[] = node.getAspects().getAspects();
            int len$ = arr$.length;
            for(int i$ = 0; i$ < len$; i$++)
            {
                Aspect aspect = arr$[i$];
                if(aspect.getBlend() == 771)
                    alpha = (float)((double)alpha * 1.5D);
                average += node.getAspects().getAmount(aspect);
                GL11.glPushMatrix();
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, aspect.getBlend());
                scale = MathHelper.sin((float)((Entity) (viewer)).ticksExisted / (14F - (float)count)) * bscale + bscale * 2.0F;
                scale = 0.2F + scale * ((float)node.getAspects().getAmount(aspect) / 50F);
                UtilsFX.renderAnimatedQuad(scale, alpha / (float)node.getAspects().size(), frames, i, 0.0F, aspect.getColor());
                GL11.glDisable(3042);
                GL11.glPopMatrix();
                count++;
                if(aspect.getBlend() == 771)
                    alpha = (float)((double)alpha / 1.5D);
            }

            average /= node.getAspects().size();
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            i = (int)((nt / 0x2625a00L + 1L) % (long)frames);
            scale = 0.1F + average / 150F;
            switch(node.getNodeType())
            {
            case NORMAL: // '\001'
                GL11.glBlendFunc(770, 1);
                UtilsFX.bindTexture(tx_c_n);
                frames = UtilsFX.getTextureAnimationSize(tx_c_n);
                break;

            case UNSTABLE: // '\002'
                GL11.glBlendFunc(770, 1);
                UtilsFX.bindTexture(tx_c_u);
                frames = UtilsFX.getTextureAnimationSize(tx_c_u);
                break;

            case DARK: // '\003'
                GL11.glBlendFunc(770, 771);
                UtilsFX.bindTexture(tx_c_d);
                frames = UtilsFX.getTextureAnimationSize(tx_c_d);
                break;

            case TAINTED: // '\004'
                GL11.glBlendFunc(770, 771);
                UtilsFX.bindTexture(tx_c_t);
                frames = UtilsFX.getTextureAnimationSize(tx_c_t);
                break;

            case PURE: // '\005'
                GL11.glBlendFunc(770, 1);
                UtilsFX.bindTexture(tx_c_p);
                frames = UtilsFX.getTextureAnimationSize(tx_c_p);
                break;

            case HUNGRY: // '\006'
                scale *= 0.75F;
                GL11.glBlendFunc(770, 1);
                UtilsFX.bindTexture(tx_c_h);
                frames = UtilsFX.getTextureAnimationSize(tx_c_h);
                break;
            }
            
            GL11.glColor4f(1.0F, 0.0F, 1.0F, alpha);
            UtilsFX.renderAnimatedQuad(scale, alpha, frames, i, 0.0F, 0xffffff);
            GL11.glDisable(3042);
            GL11.glPopMatrix();
            GL11.glPopMatrix();
            GL11.glEnable(2884);
            GL11.glDepthMask(true);
            GL11.glAlphaFunc(516, 0.1F);
            GL11.glPopMatrix();
        }
    }

}
