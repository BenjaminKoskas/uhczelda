package fr.bk.uhczelda.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.bk.uhczelda.UZMain;
import fr.bk.uhczelda.classes.UZPlayer;

public class ChatListener implements Listener
{
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		if(!e.isCancelled()) {
			UZPlayer player = UZMain.getInstance().getGame().thePlayer(e.getPlayer());
			player.onChat(e.getMessage());
			e.setCancelled(true);
		}
	}
}
