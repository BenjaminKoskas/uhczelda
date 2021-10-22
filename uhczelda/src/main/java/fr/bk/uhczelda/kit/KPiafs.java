package fr.bk.uhczelda.kit;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.bk.uhczelda.UZMain;
import fr.bk.uhczelda.classes.UZGame;
import fr.bk.uhczelda.classes.UZPlayer;
import fr.bk.uhczelda.events.UZGameStartEvent;


public class KPiafs extends Kit 
{	
	public KPiafs(UZGame game) {
		super(game);
	}

	@Override
	public String getName() {
		return "§ePiafs";
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("§7En tant que Piafs tu n'as", "§7pas de degats de chute", "§7tu peux t'envoler 5 secondes avec une plume", "§7et tu infliges +15% de degats a l'arc", "§7mais tu as Jump Boost");
	}
	
	@Override
	public ItemStack getItem() {
		return new ItemStack(Material.FEATHER);
	}
	
	@EventHandler
	public void onGameStart(UZGameStartEvent e) 
	{
		for(UZPlayer uzp : getPlayers() ) 
		{
			Player p = uzp.getPlayer();
			
			p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 0, true, false));
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onDamage(EntityDamageEvent e) 
	{
		if(!(e.getEntity() instanceof Player)) {return;}

		Player p = (Player) e.getEntity();
		UZPlayer uzp = getGame().thePlayer(p);		
		
		if(uzp.getKit() instanceof KPiafs) 
			if(e.getCause().equals(DamageCause.FALL)) 
				e.setCancelled(true);		
	}
	
	@EventHandler
	public void onBow(EntityDamageByEntityEvent e) 
	{
		if(!(e.getDamager() instanceof Player)) {return;}

		Player damager = (Player) e.getDamager();
		if(e.getCause() == DamageCause.PROJECTILE) 
		{
			if(getGame().thePlayer(damager).getKit() == null) {return;}
			if(getGame().thePlayer(damager).getKit() instanceof KPiafs) 
			{
				e.setDamage(e.getDamage() + (e.getDamage() * 0.15));
			}
		}
	}
	
	boolean isFlying = false;
	boolean canFly = true;
	
	@EventHandler
	public void onRightClick(PlayerInteractEvent e)  
	{
		Player p = e.getPlayer();
		UZPlayer uzp = getGame().thePlayer(p);
		
		if(e.getItem() != null) 
		{
			if(e.getItem().getType() == Material.FEATHER) 
			{
				if(uzp.getKit() instanceof KPiafs) 
				{
					if(!isFlying && canFly) {
						canFly = false;
						p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100, 2, true, false));
						isFlying = true;
						
						Bukkit.getScheduler().scheduleSyncDelayedTask(UZMain.getInstance(), new Runnable() {

							@Override
							public void run() {
								canFly = true;							
							}
							
						}, 200L);
					} else if (isFlying) {
						p.removePotionEffect(PotionEffectType.LEVITATION);
						isFlying = false;
					} else if (!canFly) {
						uzp.sendMessage("§6§lZelda §a>> §7Tu dois attendre §a§l10 §7secondes avant de pouvoir voler !");
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onEatGoldenApple(PlayerItemConsumeEvent e) 
	{
		Player p = e.getPlayer();
		UZPlayer uzp = getGame().thePlayer(p);
		if(!(uzp.getKit() instanceof KPiafs)) {return;}
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














