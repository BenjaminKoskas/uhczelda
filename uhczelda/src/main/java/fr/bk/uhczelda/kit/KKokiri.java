package fr.bk.uhczelda.kit;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.bk.uhczelda.classes.UZGame;
import fr.bk.uhczelda.classes.UZPlayer;
import fr.bk.uhczelda.events.UZGameStartEvent;


public class KKokiri extends Kit 
{	
	public KKokiri(UZGame game) {
		super(game);
	}

	@Override
	public String getName() {
		return "§2Kokiri";
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("§7En tant que Kokiri tu disposes", "§7de l'effet Haste 1,", "§7tu as 2 coeurs supplementaire", "§7et les feuilles ont plus de", "§7chance de drop une pomme mais tu n'as qu'un coeur", "§7d'absorption et tu subis double degats de feu");
	}
	
	@Override
	public ItemStack getItem() {
		return new ItemStack(Material.OAK_SAPLING);
	}
	
	@EventHandler
	public void onGameStart(UZGameStartEvent e) 
	{
		for(UZPlayer uzp : getPlayers() ) 
		{
			Player p = uzp.getPlayer();
			
			p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 0, true, false));
			p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(24);
			p.setHealth(24);
		}
	}
	
	
	@EventHandler
	public void onFireDamage(EntityDamageEvent e) 
	{
		if(!(e.getEntity() instanceof Player)) {return;}
		
		Player p = (Player) e.getEntity();
		UZPlayer uzp = getGame().thePlayer(p);
		
		if(!(uzp.getKit() instanceof KKokiri)) {return;}
		
		if(e.getCause().equals(DamageCause.FIRE_TICK)) 
		{
			e.setDamage(e.getDamage() * 2);
		}
	}
	
	@EventHandler
	public void breakTree(BlockBreakEvent e) 
	{
		Player p = e.getPlayer();
		UZPlayer uzp = getGame().thePlayer(p);
		
		Block b = e.getBlock();
		Location loc = b.getLocation();
		
		Random r = new Random();
		
		if(uzp.getKit() instanceof KKokiri) 
		{
			if(b.getType() == Material.OAK_LEAVES || b.getType() == Material.ACACIA_LEAVES || b.getType() == Material.DARK_OAK_LEAVES || b.getType() == Material.SPRUCE_LEAVES || b.getType() == Material.JUNGLE_LEAVES || b.getType() == Material.BIRCH_LEAVES) 
			{
				if(r.nextInt() <= 0.40f) 
				{
					loc.getWorld().dropItem(loc, new ItemStack(Material.APPLE, 1));
				}
			}
		}
	}
	
	@EventHandler
	public void onEatGoldenApple(PlayerItemConsumeEvent e) 
	{
		Player p = e.getPlayer();
		UZPlayer uzp = getGame().thePlayer(p);
		if(!(uzp.getKit() instanceof KKokiri)) {return;}
		if(e.getItem() != null) {
			if(e.getItem().getType() == Material.GOLDEN_APPLE) 
			{
				e.setCancelled(true);
				p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
				p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
				p.setAbsorptionAmount(2);
				
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
				p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 3));
				p.setAbsorptionAmount(2);
				
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














