package fr.bk.uhczelda.events;

import com.comphenix.protocol.wrappers.WrappedGameProfile;

import fr.bk.uhczelda.classes.UZGame;
import fr.bk.uhczelda.classes.UZPlayer;
import lombok.Getter;
import lombok.Setter;

public class UZSkinLoadEvent extends UZEvent 
{
	@Getter private final UZPlayer player, to;
	@Getter @Setter private WrappedGameProfile profile;
	public UZSkinLoadEvent(UZGame game, UZPlayer player, UZPlayer to, WrappedGameProfile profile) {
		super(game);
		this.player = player;
		this.to = to;
		this.profile = profile;
	}
}
