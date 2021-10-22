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

public class IOcarinaOfTime extends UZItem {

	public IOcarinaOfTime(UZGame game) {
		super(game);
	}

	@Override
	public String getName() {
		return "§3§lOcarina du Temps";
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("§7Permet de reanimer tous les membres de votre team");
	}

	@Override
	public ItemStack getItem() 
	{
		ItemStack ocarinaOfTime = new ItemStack(Material.QUARTZ);
		ocarinaOfTime.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
		ItemMeta meta = ocarinaOfTime.getItemMeta();
		meta.setDisplayName("§3§lOcarina du Temps");
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		ocarinaOfTime.setItemMeta(meta);
		return ocarinaOfTime;
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
		
		if(!uzt.isOcarinaOfTimeUsed()) 
		{
			uzt.setOcarinaOfTimeUsed(true);
			
			for(UZPlayer mate : uzt.getPlayers()) 
			{
				if(!mate.equals(uzp)) 
					if(getGame().getDead().contains(mate) && !getGame().getAlive().contains(mate)) 
					{
						getGame().revivePlayer(mate, uzp);
						mate.sendMessage("§6§lZelda §7>> Vous avez ete §3§lressucité par " + uzt.getColor() + "§l" + uzp.getName() + " !");
					}
			}
			
			uzp.sendMessage("§6§lZelda §7>> Vous avez §3§lressucité votre équipe !");
		}
	}
}












