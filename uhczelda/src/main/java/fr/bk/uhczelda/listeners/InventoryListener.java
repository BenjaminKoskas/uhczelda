package fr.bk.uhczelda.listeners;

import java.util.Arrays;
import java.util.Random;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import fr.bk.uhczelda.UZMain;
import fr.bk.uhczelda.classes.UZPlayer;
import fr.bk.uhczelda.classes.UZTeam;
import lombok.Getter;

@SuppressWarnings("deprecation")
public class InventoryListener implements Listener 
{
	@Getter static ItemStack teamSelector;
	@Getter static ItemStack kitSelector;
	
	static {
		teamSelector = new ItemStack(Material.WHITE_BANNER);
		
		BannerMeta meta = (BannerMeta)teamSelector.getItemMeta();
		meta.setDisplayName("§aSelection de Team");
		meta.setLore(Arrays.asList("Menu pour choisir ton equipe"));
		
		meta.setBaseColor(DyeColor.WHITE);
		
		meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_DOWNLEFT));;
		
		teamSelector.setItemMeta(meta);
		
		kitSelector = new ItemStack(Material.DIAMOND_AXE);
		
		ItemMeta kitMeta = kitSelector.getItemMeta();
		kitMeta.setDisplayName("§aSelection de Kit");
		kitMeta.setLore(Arrays.asList("Menu pour choisir ton kit"));
		
		kitSelector.setItemMeta(kitMeta);
		
	}
	
	@EventHandler
    public void OnInvClick(InventoryClickEvent e) {
		if(UZMain.getInstance().getGame().isStarted()) {return;}
		
		UZPlayer uzp = UZMain.getInstance().getGame().thePlayer((Player)e.getWhoClicked());
		
		if(e.getCurrentItem() != null && e.getView().getTitle().equalsIgnoreCase("§aSelection Equipe")) 
		{
			if(e.getCurrentItem().getType() == Material.YELLOW_BANNER) 
			{
				uzp.joinTeam(UZTeam.getTeamByName("Vent"));			
			}
			else if (e.getCurrentItem().getType() == Material.RED_BANNER) 
			{
				uzp.joinTeam(UZTeam.getTeamByName("Feu"));			
			}
			else if (e.getCurrentItem().getType() == Material.LIGHT_BLUE_BANNER) 
			{
				uzp.joinTeam(UZTeam.getTeamByName("Eau"));				
			}
			else if (e.getCurrentItem().getType() == Material.GREEN_BANNER)
			{
				uzp.joinTeam(UZTeam.getTeamByName("Terre"));			
			}
			else if (e.getCurrentItem().getType() == Material.PURPLE_BANNER)
			{
				uzp.joinTeam(UZTeam.getTeamByName("Ténèbre"));			
			}
			else if(e.getCurrentItem().getType() == Material.WHITE_BANNER) 
			{
				Random rand = new Random();
				UZTeam rTeam = UZMain.getInstance().getGame().getTeams().get(rand.nextInt(UZMain.getInstance().getGame().getTeams().size()));
				uzp.joinTeam(rTeam);
			}
			
			e.setCancelled(true);
		}
		
		if(e.getCurrentItem() != null && e.getView().getTitle().equalsIgnoreCase("§aSelection Kit")) {
			for(int i = 0; i < uzp.getGame().getKits().size(); i++) {
				if(e.getCurrentItem().getType() == uzp.getGame().getKits().get(i).getItem().getType()) {
					uzp.setKit(uzp.getGame().getKits().get(i));
				}
			}
			e.setCancelled(true);
		}
			
    }
	
	@EventHandler
    public void onDrop(PlayerDropItemEvent e) {
		if(!UZMain.getInstance().getGame().isStarted())
			e.setCancelled(true);
    }
	
	@EventHandler
	public void onRightClick(PlayerInteractEvent e) 
	{
		if(UZMain.getInstance().getGame().isStarted()) {return;}
		Player p = e.getPlayer();
		UZPlayer uzp = UZMain.getInstance().getGame().thePlayer(p);
		Action a = e.getAction();
		if(e.getItem() != null) 
		{
			if(a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) 
			{
				if(e.getItem().getType() == Material.WHITE_BANNER) 
				{
					uzp.openTeamSelectionInventory();
				} 
				else if (e.getItem().getType() == Material.DIAMOND_AXE) 
				{
					uzp.openKitSelectionInventory();
				}
			}
		}
		
	}
}
