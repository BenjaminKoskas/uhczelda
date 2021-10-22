package fr.bk.uhczelda.kit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.bk.uhczelda.classes.UZGame;
import fr.bk.uhczelda.classes.UZPlayer;
import fr.bk.uhczelda.events.UZGameStartEvent;

public class KGoron extends Kit 
{	
	public KGoron(UZGame game) {
		super(game);
	}

	@Override
	public String getName() {
		return "§8Goron";
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("§7En tant que Goron tu disposes", "§7de l'effet Resistance 1, et", "§7tu peux te nourrir de cobblestone", "§7en courant et instantanément,", "§7quand tu touche l'eau tu recois l'effet slowness");
	}
	
	@Override
	public ItemStack getItem() {
		return new ItemStack(Material.COBBLESTONE, 1);
	}
	
	@EventHandler
	public void onGameStart(UZGameStartEvent e) 
	{
		for(UZPlayer uzp : getPlayers()) 
		{
			Player p = uzp.getPlayer();
			
			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, true, false));
		}
	}
	
	@EventHandler
	public void onRightClick(PlayerInteractEvent e)  
	{
		if(!(e.getAction().equals(Action.RIGHT_CLICK_AIR))) {return;}
		Player p = e.getPlayer();
		UZPlayer uzp = getGame().thePlayer(p);
		if(e.getItem() != null) 
		{
			if(e.getItem().getType().equals(Material.COBBLESTONE) && uzp.getKit() instanceof KGoron) {
				if(p.getFoodLevel() < 20) {
					p.setFoodLevel(p.getFoodLevel() + 1);
					p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 3.0f, 0.5f);
					uzp.consumeItem(1, Material.COBBLESTONE);
				}				
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onEnterWater(PlayerMoveEvent e) 
	{
		Player p = e.getPlayer();
		UZPlayer uzp = getGame().thePlayer(p);
		Location loc = p.getLocation();
		if(!(uzp.getKit() instanceof KGoron)) {return;}
		if(loc.getBlock().getType().equals(Material.WATER) || loc.getBlock().getType().equals(Material.LEGACY_STATIONARY_WATER)) 
		{
			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 0, true, false));
		}
	}
}














