package fr.bk.uhczelda.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.bk.uhczelda.classes.UZGame;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UZEvent extends Event
{
	@Getter final UZGame game;
	
    private static final HandlerList handlers = new HandlerList();
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
