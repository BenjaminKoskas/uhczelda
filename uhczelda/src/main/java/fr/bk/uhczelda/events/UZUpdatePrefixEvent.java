package fr.bk.uhczelda.events;

import fr.bk.uhczelda.classes.UZGame;
import fr.bk.uhczelda.classes.UZPlayer;
import lombok.Getter;
import lombok.Setter;

public class UZUpdatePrefixEvent extends UZEvent
{
	@Getter @Setter private String prefix;
	@Getter private final UZPlayer player, to;
	public UZUpdatePrefixEvent(UZGame game, UZPlayer player, UZPlayer to, String prefix) {
		super(game);
		this.player = player;
		this.prefix = prefix;
		this.to = to;
	}
}
