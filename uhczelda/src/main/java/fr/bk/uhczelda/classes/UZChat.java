package fr.bk.uhczelda.classes;

import java.util.HashMap;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UZChat 
{
	@Getter private final HashMap<UZPlayer, UZChatCallback> viewers = new HashMap<UZPlayer, UZChatCallback>();
	@Getter private final UZChatCallback defaultCallback;
	
	public static interface UZChatCallback{
		public String receive(UZPlayer sender, String message);
		public default String send(UZPlayer sender, String message) {return null;};
	}

	public void sendMessage(UZPlayer sender, String message) {
		String sendMessage = getViewers().get(sender).send(sender, message);
		for(Entry<UZPlayer, UZChatCallback> entry : viewers.entrySet())
			entry.getKey().sendMessage(sendMessage != null ? sendMessage : entry.getValue().receive(sender, message));
	}

	public void join(UZPlayer player, UZChatCallback callback) {
		if(getViewers().containsKey(player))
			getViewers().replace(player, callback);
		else
			getViewers().put(player, callback);
	}
	public void leave(UZPlayer player) {
		getViewers().remove(player);
	}
}
