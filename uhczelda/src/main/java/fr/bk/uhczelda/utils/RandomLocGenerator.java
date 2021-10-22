package fr.bk.uhczelda.utils;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;

public class RandomLocGenerator 
{
	public static Location generateRandomLocation(World world, int y) 
	{
		Random r = new Random();
		
		int x = 0;
		int z = 0;
		
		if(r.nextInt() <= 0.50f)
			x = r.nextInt(700);
		else
			x = -r.nextInt(700);
		
		if(r.nextInt() <= 0.50f)
			z = r.nextInt(700);
		else
			z = -r.nextInt(700);
		
		return new Location(world, x, y, z);
	}
}
