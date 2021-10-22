package fr.bk.uhczelda.events;

import fr.bk.uhczelda.classes.UZGame;
import fr.bk.uhczelda.classes.UZPlayer;
import lombok.Getter;

public class UZPlayerQuitTeamEvent extends UZEvent 
{
	public UZPlayerQuitTeamEvent(UZGame game, UZPlayer player) 
	{
		super(game);
		this.player = player;
	}
	
	@Getter UZPlayer player;
}
