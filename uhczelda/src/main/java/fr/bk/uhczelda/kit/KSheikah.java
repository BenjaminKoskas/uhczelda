package fr.bk.uhczelda.kit;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.bk.uhczelda.UZMain;
import fr.bk.uhczelda.classes.UZGame;
import fr.bk.uhczelda.classes.UZPlayer;

public class KSheikah extends Kit 
{	
	public KSheikah(UZGame game) {
		super(game);
	}

	@Override
	public String getName() {
		return "§1Sheikah";
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("§7En tant que Sheikah tu as", "§7Speed 1 quand la luminosite est", "§7plus petite que 7", "§7et tes fleches inflige famine", "§7mais tu n'as qu'coeur d'absorption", "§7et tu recois Famine quand tu", "§7prends des degats PVE");
	}
	
	@Override
	public ItemStack getItem() {
		return new ItemStack(Material.PHANTOM_MEMBRANE);
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) 
	{		
		if(!(e.getEntity() instanceof Player)) {return;}
		
		Player p = (Player) e.getEntity();
			
		if(e.getDamager() instanceof Player) {
			Player damager = (Player) e.getDamager();
			if(e.getCause() == DamageCause.PROJECTILE) 
			{
				if(getGame().thePlayer(damager).getKit() == null) {return;}
				if(getGame().thePlayer(damager).getKit() instanceof KSheikah) 
				{
					p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 150, 0, true, false));
				}
			}
		} 
		else 
		{
			p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 100, 0, true, false));
		}
		
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) 
	{
		Player p = e.getPlayer();
		UZPlayer uzp = getGame().thePlayer(p);
		if(uzp.getKit() instanceof KSheikah) {
			applySpeed(p);
		}		
	}
	
	public void applySpeed(Player p) 
	{
		new BukkitRunnable() {
			@Override
			public void run() {
				if(p.getLocation().getBlock().getLightLevel() <= 7) 
				{
					p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 0, true, false));
				}										
			}
		}.runTaskTimer(UZMain.getInstance(), 0, 20);
	}

	@EventHandler
	public void onEatGoldenApple(PlayerItemConsumeEvent e) 
	{
		Player p = e.getPlayer();
		UZPlayer uzp = getGame().thePlayer(p);
		if(!(uzp.getKit() instanceof KSheikah)) {return;}
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














