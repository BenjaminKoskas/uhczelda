package fr.bk.uhczelda.classes;

public class UZNoChat extends UZChat
{
	public UZNoChat() {
		super(null);
	}

	public void sendMessage(UZPlayer sender, String message) {}

	public void join(UZPlayer player, UZChatCallback callback) {
		
	}
	public void leave(UZPlayer player) {
		
	}
}
