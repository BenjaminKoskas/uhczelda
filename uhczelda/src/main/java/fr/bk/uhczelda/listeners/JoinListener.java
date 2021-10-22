package fr.bk.uhczelda.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.bk.uhczelda.classes.UZGame;
import fr.bk.uhczelda.classes.UZPlayer;
import fr.bk.uhczelda.utils.PacketReader;

public class JoinListener implements Listener 
{	
	private UZGame game;
	
	public JoinListener(UZGame game)
	{
		this.game = game;		
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) 
	{
		if(game.isStarted()) 
		{
			e.getPlayer().kickPlayer("Partie deja commence");
			e.setJoinMessage("");
			return;
		}
		
		Player p = e.getPlayer();				
		UZPlayer uzp = game.thePlayer(p);
			
		uzp.joinGame();		
		
		PacketReader reader = new PacketReader();
		reader.inject(p, game);
		
		e.setJoinMessage("");
	}	
}
