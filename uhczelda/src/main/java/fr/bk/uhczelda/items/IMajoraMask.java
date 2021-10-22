package fr.bk.uhczelda.items;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.bk.uhczelda.classes.UZGame;
import fr.bk.uhczelda.classes.UZPlayer;

public class IMajoraMask extends UZItem {

	public IMajoraMask(UZGame game) {
		super(game);
	}

	@Override
	public String getName() {
		return "§c§lMasque de Majora";
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("§7En portant ce masque les mobs sont passif envers vous");
	}

	@Override
	public ItemStack getItem() 
	{
		ItemStack majoraMask = new ItemStack(Material.SCUTE);
		majoraMask.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
		ItemMeta meta = majoraMask.getItemMeta();
		meta.setDisplayName("§c§lMasque de Majora");
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		majoraMask.setItemMeta(meta);
		return majoraMask;
	}
	
	@EventHandler
	public void onItemHeld(PlayerSwapHandItemsEvent e) 
	{
		if(!getGame().isStarted()) {return;}
		UZPlayer uzp = getGame().thePlayer(e.getPlayer());
		
		if(e.getOffHandItem() == null) 
		{
			if(uzp.isHeldingMajoraMask())
			{
				uzp.setHeldingMajoraMask(false);
				uzp.sendMessage("§6§lZelda §a>> §7Les §c§lmonstres §7peuvent §f§lvous attaquer !");
			}
			return;
		}
		
		if(!e.getOffHandItem().equals(getItem())) 
		{
			if(uzp.isHeldingMajoraMask())
			{
				uzp.setHeldingMajoraMask(false);
				uzp.sendMessage("§6§lZelda §a>> §7Les §c§lmonstres §7peuvent §f§lvous attaquer !");
			}
			return;
		}
			
		uzp.setHeldingMajoraMask(true);
		
		List<Entity> creatures = uzp.getPlayer().getNearbyEntities(200, 200, 200);
		for(Entity entity : creatures) 
		{
			if(entity instanceof Creature) 
			{
				Creature c = (Creature)entity;
				if(c.getTarget() == uzp.getPlayer()) 
					c.setTarget(null);
			}
		}
		
		uzp.sendMessage("§6§lZelda §a>> §7Les §c§lmonstres §7ne vous §f§lattaqueront plus !");
	}
	
	@EventHandler
	public void onEntityTarget(EntityTargetLivingEntityEvent e) 
	{
		if(!getGame().isStarted()) {return;}
		if(e.getTarget() instanceof Player) 
		{
			UZPlayer uzp = getGame().thePlayer((Player)e.getTarget());
			if(uzp.isHeldingMajoraMask())
				e.setCancelled(true);
		}
	}
}









