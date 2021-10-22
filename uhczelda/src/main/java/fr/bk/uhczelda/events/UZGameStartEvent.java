package fr.bk.uhczelda.events;

import fr.bk.uhczelda.classes.UZGame;
import lombok.Getter;

public class UZGameStartEvent extends UZEvent 
{
	public UZGameStartEvent(UZGame game) 
	{
		super(game);
		this.game = game;
	}
	
	@Getter UZGame game;
}
