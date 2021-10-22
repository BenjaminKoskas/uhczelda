package fr.bk.uhczelda.kit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import fr.bk.uhczelda.UZMain;
import fr.bk.uhczelda.classes.UZGame;
import fr.bk.uhczelda.classes.UZPlayer;
import lombok.Getter;

public abstract class Kit implements Listener
{
	@Getter private final UZGame game;
	@Getter private ArrayList<UZPlayer> players = new ArrayList<UZPlayer>();
	
	public Kit(UZGame game) {
		this.game = game;
		Bukkit.getPluginManager().registerEvents(this, UZMain.getInstance());
	}
	
	public abstract String getName();
	public abstract List<String> getDescription();
	public abstract ItemStack getItem();
	
	public void join(UZPlayer player) 
	{
		if(players.contains(player)) {return;}
		players.add(player);
	}
}
