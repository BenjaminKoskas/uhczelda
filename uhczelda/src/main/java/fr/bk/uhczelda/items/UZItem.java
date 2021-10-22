package fr.bk.uhczelda.items;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import fr.bk.uhczelda.UZMain;
import fr.bk.uhczelda.classes.UZGame;
import lombok.Getter;

public abstract class UZItem implements Listener
{
	@Getter private final UZGame game;
	
	public UZItem(UZGame game) 
	{
		this.game = game;
		Bukkit.getPluginManager().registerEvents(this, UZMain.getInstance());
	}
	
	public abstract String getName();
	public abstract List<String> getDescription();
	public abstract ItemStack getItem();
}
