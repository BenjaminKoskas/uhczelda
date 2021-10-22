package fr.bk.uhczelda.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import fr.bk.uhczelda.UZMain;
import fr.bk.uhczelda.classes.UZCustomSkin;
import fr.bk.uhczelda.events.UZSkinLoadEvent;

public class SkinListener implements Listener 
{
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSkinChange(UZSkinLoadEvent e) {
		if(e.getGame() == UZMain.getInstance().getGame()) 
		{
			e.getProfile().getProperties().removeAll("textures");
			e.getProfile().getProperties().put("textures", UZCustomSkin.LINK.getProperty());
		}
	}
}
