package fr.bk.uhczelda.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import fr.bk.uhczelda.UZMain;
import fr.bk.uhczelda.events.UZUpdatePrefixEvent;

public class PrefixListener implements Listener 
{
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onUpdatePrefix (UZUpdatePrefixEvent e) {
		if(e.getGame() == UZMain.getInstance().getGame())
			if(e.getPlayer().getTeam() != null) {
				e.setPrefix(e.getPlayer().getTeam().getColor());
			} else {
				e.setPrefix(e.getPrefix()+"§7");
			}
			
	}
}
