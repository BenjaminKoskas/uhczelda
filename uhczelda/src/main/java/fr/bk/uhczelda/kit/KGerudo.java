package fr.bk.uhczelda.kit;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.bk.uhczelda.classes.UZGame;
import fr.bk.uhczelda.classes.UZPlayer;
import fr.bk.uhczelda.events.UZGameStartEvent;


public class KGerudo extends Kit 
{	
	public KGerudo(UZGame game) {
		super(game);
	}

	@Override
	public String getName() {
		return "§cGerudo";
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("§7En tant que Gerudo tu disposes", "§7de l'effet Force 1, et", "§7Fire Resistance, aller dans", "§7l'eau te donne Mining Fatigue,", "§7et tu n'as pas de coeurs d'absorptions");
	}
	
	@Override
	public ItemStack getItem() {
		return new ItemStack(Material.BLAZE_POWDER);
	}
	
	@EventHandler
	public void onGameStart(UZGameStartEvent e) 
	{
		for(UZPlayer uzp : getPlayers() ) 
		{
			Player p = uzp.getPlayer();
			
			p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, true, false));
			p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, true, false));
		}
	}
	
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onEnterWater(PlayerMoveEvent e) 
	{
		Player p = e.getPlayer();
		UZPlayer uzp = getGame().thePlayer(p);
		Location loc = p.getLocation();
		if(!(uzp.getKit() instanceof KGerudo)) {return;}
		if(loc.getBlock().getType() == Material.WATER || loc.getBlock().getType() == Material.LEGACY_STATIONARY_WATER) 
		{
			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 400, 0, true, false));
		}
	}
	
	@EventHandler
	public void onEatGoldenApple(PlayerItemConsumeEvent e) 
	{
		Player p = e.getPlayer();
		UZPlayer uzp = getGame().thePlayer(p);
		if(!(uzp.getKit() instanceof KGerudo)) {return;}
		if(e.getItem() != null) {
			if(e.getItem().getType() == Material.GOLDEN_APPLE) 
			{
				e.setCancelled(true);
				p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
				if(p.getFoodLevel() > 16)
					p.setFoodLevel(20);
				else
					p.setFoodLevel(p.getFoodLevel() + 4);
				
				if(p.getSaturation() > 11)
					p.setSaturation(20);
				else
					p.setSaturation(p.getSaturation() + 9);
				p.getInventory().removeItem(new ItemStack(Material.GOLDEN_APPLE, 1));
			} else if (e.getItem().getType() == Material.ENCHANTED_GOLDEN_APPLE) {
				e.setCancelled(true);
				p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 600, 1));
				p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 6000, 0));
				p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 6000, 0));
				if(p.getFoodLevel() > 16)
					p.setFoodLevel(20);
				else
					p.setFoodLevel(p.getFoodLevel() + 4);
				
				if(p.getSaturation() > 11)
					p.setSaturation(20);
				else
					p.setSaturation(p.getSaturation() + 9);
				p.getInventory().removeItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1));
			}
		}
	}
}














