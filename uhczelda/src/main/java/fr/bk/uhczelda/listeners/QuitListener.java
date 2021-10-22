package fr.bk.uhczelda.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.bk.uhczelda.UZMain;
import fr.bk.uhczelda.classes.UZPlayer;
import fr.bk.uhczelda.utils.PacketReader;

public class QuitListener implements Listener 
{
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {		
		Player p = e.getPlayer();
		UZPlayer uzp = UZMain.getInstance().getGame().thePlayer(p);
		e.setQuitMessage("");
		
		uzp.leaveGame();
		
		PacketReader reader = new PacketReader();
		reader.uninject(p);
	}
}
