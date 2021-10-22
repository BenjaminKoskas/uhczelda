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
import org.bukkit.potion.PotionEffectType;

import fr.bk.uhczelda.classes.UZGame;
import fr.bk.uhczelda.classes.UZPlayer;
import fr.bk.uhczelda.classes.UZTeam;

public class ISwordOfFour extends UZItem {

	public ISwordOfFour(UZGame game) {
		super(game);
	}

	@Override
	public String getName() {
		return "§6§lL'Épée de Quatre";
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("§7Vous donne 6 coeurs d'absorption toute les 10 minutes");
	}

	@Override
	public ItemStack getItem() 
	{
		ItemStack swordOf4 = new ItemStack(Material.PRISMARINE_SHARD);
		swordOf4.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
		ItemMeta meta = swordOf4.getItemMeta();
		meta.setDisplayName("§6§lL'Épée de Quatre");
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		swordOf4.setItemMeta(meta);
		return swordOf4;
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
		
		if(uzt.getCurrentSOFCooldown() > System.currentTimeMillis()) 
		{
			uzt.setSOFTimeLeft((uzt.getCurrentSOFCooldown() - System.currentTimeMillis()) / 1000);
			uzp.sendMessage("§6§lZelda §7>> Tu pourras utiliser L'Épée de Quatre dans §6§l" + uzt.getSOFTimeLeft() + " §7secondes");
		}
		else 
		{
			uzt.setCurrentSOFCooldown(System.currentTimeMillis() + (uzt.getSOFCooldown() * 1000));
			uzp.addPotionEffect(PotionEffectType.ABSORPTION, 99999, 2);
		}
	}
}














