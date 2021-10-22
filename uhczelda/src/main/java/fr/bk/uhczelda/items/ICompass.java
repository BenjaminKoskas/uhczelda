package fr.bk.uhczelda.items;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import fr.bk.uhczelda.UZMain;
import fr.bk.uhczelda.classes.UZGame;
import fr.bk.uhczelda.classes.UZPlayer;
import fr.bk.uhczelda.classes.UZTriforce;

public class ICompass extends UZItem {

	public ICompass(UZGame game) {
		super(game);
		customRecipe();
	}

	@Override
	public String getName() {
		return "§6§lBoussole";
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("§7Permet de situer les Triforces");
	}

	@Override
	public ItemStack getItem() 
	{
		ItemStack compass = new ItemStack(Material.COMPASS);
		ItemMeta meta = compass.getItemMeta();
		meta.setDisplayName("§6§lBoussole");
		meta.setLore(Arrays.asList("§7Permet de trouver les triforces"));
		compass.setItemMeta(meta);
		return compass;
	}
	
	public void customRecipe() 
	{		
		NamespacedKey key = new NamespacedKey(UZMain.getInstance(), "compass");
		
		ShapedRecipe r = new ShapedRecipe(key, getItem());
		
		r.shape("#@#","^%!","#*#");
		r.setIngredient('#', Material.IRON_INGOT);
		r.setIngredient('@', Material.COAL);
		r.setIngredient('^', Material.GOLD_INGOT);
		r.setIngredient('!', Material.DIAMOND);
		r.setIngredient('%', Material.REDSTONE);
		r.setIngredient('*', Material.LAPIS_LAZULI);
		
		UZMain.getPlugin(UZMain.class).getServer().addRecipe(r);
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e)  
	{
		if(!getGame().isStarted()) {return;}
		if(e.getItem() == null) {return;}
		if(!e.getItem().equals(getItem())) {return;}
		if(!e.getHand().equals(EquipmentSlot.HAND)) {return;}
		if(!e.getAction().equals(Action.RIGHT_CLICK_AIR)) {return;}
		
		UZPlayer uzp = getGame().thePlayer(e.getPlayer());
		
		UZTriforce strenght = getGame().getStrenght();
		UZTriforce courage = getGame().getCourage();
		UZTriforce wisdom = getGame().getWisdom();
		
		if(uzp.isSneaking()) 
		{
			if(uzp.getTargetedTriforce().equals(courage)) 
			{
				uzp.setTargetedTriforce(strenght);
				uzp.sendMessage("§6§lZelda §7>> La boussole indique la positions de la Triforce " + strenght.getName());
			}
			else if(uzp.getTargetedTriforce().equals(strenght)) 
			{
				uzp.setTargetedTriforce(wisdom);
				uzp.sendMessage("§6§lZelda §7>> La boussole indique la positions de la Triforce " + wisdom.getName());
			}
			else if(uzp.getTargetedTriforce().equals(wisdom)) 
			{
				uzp.setTargetedTriforce(courage);
				uzp.sendMessage("§6§lZelda §7>> La boussole indique la positions de la Triforce " + courage.getName());
			}
		} 
		else 
		{
			uzp.getTriforceLocation();
		}
	}

}
