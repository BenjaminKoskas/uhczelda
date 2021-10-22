package fr.bk.uhczelda.kit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.bk.uhczelda.UZMain;
import fr.bk.uhczelda.classes.UZGame;
import fr.bk.uhczelda.classes.UZPlayer;
import fr.bk.uhczelda.events.UZGameStartEvent;

public class KZora extends Kit 
{	
	public KZora(UZGame game) {
		super(game);
	}

	@Override
	public String getName() {
		return "§bZora";
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("§7En tant que Zora tu peux", "§7de respirer sous l'eau, tenir", "§7un poisson te donne l'effet speed", "§7et tu recois un livre Depth Rider 2", "§7mais tu prends double dégâts de feu", "§7et etre a 5 blocs de la lave", "§7te donne slowness");
	}
	
	@Override
	public ItemStack getItem() {
		return new ItemStack(Material.HEART_OF_THE_SEA);
	}
	
	@EventHandler
	public void onGameStart(UZGameStartEvent e) 
	{
		for(UZPlayer uzp : getPlayers() ) 
		{
			Player p = uzp.getPlayer();
			
			ItemStack speedFish = new ItemStack(Material.TROPICAL_FISH);
			ItemMeta meta = speedFish.getItemMeta();
			meta.setDisplayName("§b§lPoisson de la vitesse");
			meta.setLore(Arrays.asList("§7Le tenir en main te donne speed."));
			speedFish.setItemMeta(meta);
			
			p.getInventory().addItem(speedFish);
			
			ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
			meta = book.getItemMeta();
			meta.addEnchant(Enchantment.DEPTH_STRIDER, 2, true);
			book.setItemMeta(meta);
			
			p.getInventory().addItem(book);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMove(PlayerMoveEvent e) 
	{
		Player p = e.getPlayer();
		UZPlayer uzp = getGame().thePlayer(p);
		Location loc = p.getLocation();
		if(!(uzp.getKit() instanceof KZora)) {return;}
		for(Block b : getNearbyBlocks(loc, 5)) {
			if(b.getType() == Material.LAVA || b.getType() == Material.LEGACY_STATIONARY_LAVA) 
			{
				p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 0, true, false));
			}
		}
		if(loc.getBlock().getType() == Material.WATER || loc.getBlock().getType() == Material.LEGACY_STATIONARY_WATER) 
		{
			p.setRemainingAir(300);
		}
		applySpeed(p); 
	}
	
	@EventHandler
	public void onFireDamage(EntityDamageEvent e) 
	{
		if(!(e.getEntity() instanceof Player)) {return;}
		
		Player p = (Player) e.getEntity();
		UZPlayer uzp = getGame().thePlayer(p);
		
		if(!(uzp.getKit() instanceof KZora)) {return;}
		
		if(e.getCause().equals(DamageCause.FIRE_TICK)) {
			e.setDamage(e.getDamage() * 2);
		}
	}
	
	@EventHandler
	public void onPlayerEat(PlayerItemConsumeEvent e) 
	{
		if(e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("§b§lPoisson de la vitesse")) 
		{
			e.setCancelled(true);
		}
	}
	
	public void applySpeed(Player p) 
	{
		new BukkitRunnable() {
			@Override
			public void run() {
				if(p.getInventory().getItemInMainHand() != null) 
				{
					if(p.getInventory().getItemInMainHand().hasItemMeta())
						if(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase("§b§lPoisson de la vitesse"))
							p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 0, true, false));
				}								
			}
		}.runTaskTimer(UZMain.getInstance(), 0, 20);
	}
	
	public static List<Block> getNearbyBlocks(Location location, int radius) {
        List<Block> blocks = new ArrayList<Block>();
        for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                   blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }
}














