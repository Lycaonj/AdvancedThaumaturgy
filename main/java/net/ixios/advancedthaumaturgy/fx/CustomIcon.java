package net.ixios.advancedthaumaturgy.fx;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Resource;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;

public class CustomIcon implements Icon
{

	ResourceLocation location;
	private int width, height;
	private String name;
	
	public CustomIcon(String reslocation) throws IOException
	{
		name = reslocation;
		location = new ResourceLocation("advancedthaumaturgy", reslocation);
		
		Resource resource = Minecraft.getMinecraft().getResourceManager().getResource(location);
        InputStream inputstream = resource.getInputStream();
        
        BufferedImage bufferedimage;
		try 
		{
			bufferedimage = ImageIO.read(inputstream);
		}
		catch (IOException e) 
		{ 
			throw e;
		}
		
        this.height = bufferedimage.getHeight();
        this.width = bufferedimage.getWidth();
        int[] aint = new int[this.height * this.width];
        bufferedimage.getRGB(0, 0, this.width, this.height, aint, 0, this.width);
	}

	@Override
	public int getIconHeight() 
	{
		return height;
	}

	@Override
	public int getIconWidth() 
	{
		return width;
	}

	@Override
	public String getIconName() 
	{
		return name;
	}

	@Override
	public float getInterpolatedU(double d0)
	{
		return 1 * (float)d0 / 16.0F;
	}

	@Override
	public float getInterpolatedV(double d0) 
	{
		return 1 * (float)d0 / 16.0F;	}

	@Override
	public float getMaxU()
	{
		return 1;
	}

	@Override
	public float getMaxV() 
	{
		return 1;
	}

	@Override
	public float getMinU()
	{
		return 0;
	}

	@Override
	public float getMinV() 
	{
		return 0;
	}
}
