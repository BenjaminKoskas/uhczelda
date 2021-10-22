package fr.bk.uhczelda.items;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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

public class INavi extends UZItem {

	public INavi(UZGame game) {
		super(game);
	}

	@Override
	public String getName() {
		return "§b§lNavi";
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("§7Navi dit les joueurs autour de toi");
	}

	@Override
	public ItemStack getItem() 
	{
		ItemStack navi = new ItemStack(Material.PRISMARINE_CRYSTALS);
		navi.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
		ItemMeta meta = navi.getItemMeta();
		meta.setDisplayName("§b§lNavi");
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		navi.setItemMeta(meta);
		return navi;
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
		
		if(uzt.getCurrentNaviCooldown() > System.currentTimeMillis()) 
		{
			uzt.setNaviTimeLeft((uzt.getCurrentNaviCooldown() - System.currentTimeMillis()) / 1000);
			uzp.sendMessage("§b§lNavi §7>> Tu pourras m'utiliser dans §b§l" + uzt.getNaviTimeLeft() + " §7secondes");
		}
		else 
		{
			uzt.setCurrentNaviCooldown(System.currentTimeMillis() + (uzt.getNaviCooldown() * 1000));
			
			String str = "§b§lNavi §7>> Autour de toi, il y'a : ";
			
			List<Entity> entities = uzp.getPlayer().getNearbyEntities(200, 200, 200);
			for(Entity entity : entities) 
			{
				if(entity instanceof Player) 
				{
					Player nearbyPlayer = (Player)entity;
					UZPlayer nearbyUZPlayer = getGame().thePlayer(nearbyPlayer);
					
					if(!nearbyUZPlayer.getTeam().equals(uzt)) 
					{
						UZTeam nteam = nearbyUZPlayer.getTeam();
						str += nteam.getColor() + "§l" + nearbyUZPlayer.getName() + " ";
					}
				}
			}
			
			if(str.equalsIgnoreCase("§b§lNavi §7>> Autour de toi, il y'a : ")) 
				uzp.sendMessage("§b§lNavi §7>> Il n'y a §b§lpersonne §7autour de toi !");
			else 
				uzp.sendMessage(str);
		}
	}
}













