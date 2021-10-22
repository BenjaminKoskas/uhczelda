package fr.bk.uhczelda.classes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;
import lombok.Setter;

public class UZTriforce 
{
	public enum UZTriforceEnum 
	{
		STRENGHT,
		COURAGE,
		WISDOM
	}
	
	@Getter private String name;
	@Getter private UZTriforceEnum type;
	@Setter private Location location;
	@Getter @Setter private UZPlayer holder;
	@Getter @Setter private UZTeam holders;
	
	@Getter ItemStack item;
	
	public UZTriforce(Location baseLocation, UZTriforceEnum type) 
	{
		this.item = getItemByType(type);
		this.name = this.item.getItemMeta().getDisplayName();
		this.location = baseLocation;
		this.type = type;
	}
	
	private ItemStack getItemByType(UZTriforceEnum type) 
	{
		ItemStack triforce = new ItemStack(Material.PINK_STAINED_GLASS);
		ItemMeta meta = triforce.getItemMeta();
		switch(type) 
		{
			case STRENGHT:
			{
				meta.setDisplayName("§c§lForce");
				break;
			}		
			case COURAGE:
			{
				meta.setDisplayName("§6§lCourage");
				break;
			}			
			case WISDOM:
			{
				meta.setDisplayName("§9§lSagesse");
				break;
			}		
		}
		triforce.setItemMeta(meta);
		
		return triforce;
	}
	
	public Location getLocation() 
	{
		if(holder != null)
			return holder.getLocation();
		else
			return location;
	}
}
