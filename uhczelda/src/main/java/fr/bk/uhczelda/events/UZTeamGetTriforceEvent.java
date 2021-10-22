package fr.bk.uhczelda.events;

import fr.bk.uhczelda.classes.UZGame;
import fr.bk.uhczelda.classes.UZPlayer;
import fr.bk.uhczelda.classes.UZTeam;
import fr.bk.uhczelda.classes.UZTriforce;
import lombok.Getter;

public class UZTeamGetTriforceEvent extends UZEvent 
{
	public UZTeamGetTriforceEvent(UZGame game, UZPlayer player, UZTeam team, UZTriforce triforce) 
	{
		super(game);
		this.player = player;
		this.team = team;
		this.triforce = triforce;
	}
	
	@Getter UZTeam team;
	@Getter UZTriforce triforce;
	@Getter UZPlayer player;
}
