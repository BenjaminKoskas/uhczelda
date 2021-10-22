package fr.bk.uhczelda.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import fr.bk.uhczelda.classes.UZGame;
import lombok.Getter;
import net.minecraft.server.v1_15_R1.EntityPlayer;

public class UZRightClickNPCEvent extends UZEvent implements Cancellable
{
	@Getter private final Player player;
	@Getter private final EntityPlayer npc;
	
	private boolean isCancelled;
	
	public UZRightClickNPCEvent(UZGame game, Player player, EntityPlayer npc) 
	{
		super(game);
		this.player = player;
		this.npc = npc;
	}

	@Override
	public boolean isCancelled() 
	{
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean arg) 
	{	
		isCancelled = arg;
	}

}
