package fr.bk.uhczelda.kit;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.bk.uhczelda.UZMain;
import fr.bk.uhczelda.classes.UZGame;
import fr.bk.uhczelda.classes.UZPlayer;
import fr.bk.uhczelda.events.UZGameStartEvent;
import lombok.Getter;


public class KHylien extends Kit 
{
	@Getter static ItemStack heartReceptacle;
	static {
		heartReceptacle = new ItemStack(Material.MAGMA_CREAM);
		ItemMeta meta = heartReceptacle.getItemMeta();
		meta.setDisplayName("§c§lRécéptacle à Coeurs");
		meta.setLore(Arrays.asList("§7Permet de régénérer 3 coeurs"));

		heartReceptacle.setItemMeta(meta);
	}
	
	public KHylien(UZGame game) {
		super(game);
		customRecipe();
	}

	@Override
	public String getName() {
		return "§dHylien";
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("§7En tant qu'Hylien tu débutes", "§7avec 11 coeurs plus la possibilité de", "§7crafter des récéptacles à coeurs", "§7qui régénerent 3 coeurs,", "§7tu reçois 3 pommes en début de partie");
	}
	
	@Override
	public ItemStack getItem() {
		return new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);
	}
	
	@EventHandler
	public void onGameStart(UZGameStartEvent e) 
	{
		for(UZPlayer uzp : getPlayers() ) 
		{
			Player p = uzp.getPlayer();
			
			p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(22);
			p.setHealth(22);
			
			p.getInventory().addItem(new ItemStack(Material.APPLE, 3));
		}
	}
	
	public void customRecipe() 
	{		
		NamespacedKey key = new NamespacedKey(UZMain.getInstance(), "magma_cream");
		
		ShapedRecipe r = new ShapedRecipe(key, heartReceptacle);
		
		r.shape("#@#","&%&","#@#");
		r.setIngredient('#', Material.GOLD_INGOT);
		r.setIngredient('@', Material.LAPIS_LAZULI);
		r.setIngredient('&', Material.APPLE);
		r.setIngredient('%', Material.DIAMOND);
		
		UZMain.getPlugin(UZMain.class).getServer().addRecipe(r);
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent e) 
	{
		Player p = (Player) e.getWhoClicked();
		if(e.getCurrentItem().getType() == Material.MAGMA_CREAM) 
		{
			if(!(getGame().thePlayer(p).getKit() instanceof KHylien)) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onRightClick(PlayerInteractEvent e)  
	{
		Player p = e.getPlayer();
		UZPlayer uzp = getGame().thePlayer(p);
		if(e.getItem() != null) 
		{
			if(e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("§c§lRécéptacle à Coeurs")) 
			{
				if(uzp.getKit() instanceof KHylien) 
				{
					if((p.getHealth() + 6) > p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()) 
					{
						p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(Math.max(0, Math.min(30, p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + 6 - (p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() - p.getHealth()))));
						p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
					} 
					else if ((p.getHealth() + 6) < p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()) 
					{
						p.setHealth(p.getHealth() + 6);
					}

					p.getInventory().removeItem(heartReceptacle);
					p.playSound(p.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 3.0f, 0.5f);
				}
			}
		}
	}
	
	@EventHandler
	public void onEatGoldenApple(PlayerItemConsumeEvent e) 
	{
		Player p = e.getPlayer();
		UZPlayer uzp = getGame().thePlayer(p);
		if(!(uzp.getKit() instanceof KHylien)) {return;}
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














