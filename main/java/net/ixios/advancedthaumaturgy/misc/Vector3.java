package net.ixios.advancedthaumaturgy.misc;

public class Vector3
{
	public int x, y, z;
	
	public Vector3(int x, int y, int z)
	{
		this.x = x; this.y = y; this.z = z;
	}
	
	public float distanceTo(Vector3F target)
	{
		float dx = (float)Math.pow(target.x - x, 2);
		float dy = (float)Math.pow(target.y - y, 2);
		float dz = (float)Math.pow(target.z - z, 2);
		return (float)Math.sqrt(dx + dy + dz);
	}

	@Override
	public String toString()
	{
	    return x + ";" + y + ";" + z;
	}
	
}
