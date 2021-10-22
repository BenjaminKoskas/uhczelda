package fr.bk.uhczelda.items;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.bk.uhczelda.classes.UZGame;
import fr.bk.uhczelda.classes.UZPlayer;
import fr.bk.uhczelda.classes.UZTeam;

public class IFaroreWind extends UZItem {

	public IFaroreWind(UZGame game) {
		super(game);
	}

	@Override
	public String getName() {
		return "§a§lVent de Fafore";
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("§7Permet de teleporter son equipe a l'utilisateur");
	}

	@Override
	public ItemStack getItem() 
	{
		ItemStack faforeWind = new ItemStack(Material.RABBIT_HIDE);
		faforeWind.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
		ItemMeta meta = faforeWind.getItemMeta();
		meta.setDisplayName("§a§lVent de Fafore");
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		faforeWind.setItemMeta(meta);
		return faforeWind;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) 
	{
		if(!getGame().isStarted()) {return;}
		if(e.getItem() == null) {return;}
		if(!e.getItem().equals(getItem())) {return;}
		if(!e.getHand().equals(EquipmentSlot.HAND)) {return;}
		if(!e.getAction().equals(Action.RIGHT_CLICK_AIR) && !e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {return;}
		
		UZPlayer uzp = getGame().thePlayer(e.getPlayer());
		UZTeam uzt = uzp.getTeam();
		
		if(!uzt.isFaroreWindUsed()) 
		{
			uzt.setFaroreWindUsed(true);
			
			for(UZPlayer mate : uzt.getPlayers()) 
			{
				if(getGame().getAlive().contains(mate)) 
				{
					if(!mate.equals(uzp)) 
					{
						mate.getPlayer().teleport(uzp.getPlayer());
						mate.sendMessage("§6§lZelda §7>> Vous avez été §a§ltéléporté §7à " + uzt.getColor() + "§l" + uzp.getName());
					}
				}
			}
			
			uzp.sendMessage("§6§lZelda §7>> Vous avez §a§ltéléporté votre équipe §7à §a§lvotre position");
		}
		else 
		{
			uzp.sendMessage("§6§lZelda §7>> Vous avez §a§ldéjà utilisé §7le §a§lVent de Farore !");
		}
	}
}













