package fr.bk.uhczelda.classes;

import java.util.ArrayList;

import org.bukkit.Bukkit;

import fr.bk.uhczelda.UZMain;
import fr.bk.uhczelda.events.UZPlayerJoinTeamEvent;
import fr.bk.uhczelda.events.UZPlayerQuitTeamEvent;
import lombok.Getter;
import lombok.Setter;

public class UZTeam
{
	public UZTeam(String _name, String _color) 
	{
		name = _name;
		color = _color;
	}
	
	@Getter private String name;
	@Getter private String color;
	@Getter private int maxPlayers;
	
	@Getter private ArrayList<UZPlayer> players = new ArrayList<UZPlayer>();
	
	@Getter @Setter private int SOFCooldown = 600;
	@Getter @Setter private long currentSOFCooldown;
	@Getter @Setter private long SOFTimeLeft;
	
	@Getter @Setter private int naviCooldown = 60;
	@Getter @Setter private long currentNaviCooldown;
	@Getter @Setter private long naviTimeLeft;
	
	@Getter @Setter private boolean ocarinaOfTimeUsed = false;
	@Getter @Setter private boolean faroreWindUsed = false;
	
	@Getter private UZChat teamChat = new UZChat((sender, message) -> {
		return color+sender.getName()+" " + color + "» §f"+message;
	});
	
	public void join(UZPlayer uzp) 
	{	
		if(players.contains(uzp)) {return;}
		players.add(uzp);
		
		Bukkit.getPluginManager().callEvent(new UZPlayerJoinTeamEvent(uzp.getGame(), uzp));
	}
	
	public void quit(UZPlayer uzp) 
	{
		if(!players.contains(uzp)) { return; }
		players.remove(uzp);
		
		Bukkit.getPluginManager().callEvent(new UZPlayerQuitTeamEvent(uzp.getGame(), uzp));
	}
	
	public static UZTeam getTeamByName(String name) 
	{
		for(UZTeam team : UZMain.getInstance().getGame().getTeams()) {
			if(team.getName().equalsIgnoreCase(name)) 
			{
				return team;
			}
		}
		return null;
	}
}
