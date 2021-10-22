package fr.bk.uhczelda;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import fr.bk.uhczelda.classes.UZGame;
import fr.bk.uhczelda.classes.UZGameBehaviour;
import fr.bk.uhczelda.classes.UZPlayer;
import fr.bk.uhczelda.classes.UZTeam;
import fr.bk.uhczelda.events.UZSkinLoadEvent;
import fr.bk.uhczelda.events.UZUpdatePrefixEvent;
import fr.bk.uhczelda.items.ICompass;
import fr.bk.uhczelda.items.IFaroreWind;
import fr.bk.uhczelda.items.IMajoraMask;
import fr.bk.uhczelda.items.INavi;
import fr.bk.uhczelda.items.IOcarinaOfTime;
import fr.bk.uhczelda.items.ISwordOfFour;
import fr.bk.uhczelda.items.UZItem;
import fr.bk.uhczelda.kit.KGerudo;
import fr.bk.uhczelda.kit.KGoron;
import fr.bk.uhczelda.kit.KHylien;
import fr.bk.uhczelda.kit.KKokiri;
import fr.bk.uhczelda.kit.KPiafs;
import fr.bk.uhczelda.kit.KSheikah;
import fr.bk.uhczelda.kit.KZora;
import fr.bk.uhczelda.kit.Kit;
import fr.bk.uhczelda.listeners.ChatListener;
import fr.bk.uhczelda.listeners.InventoryListener;
import fr.bk.uhczelda.listeners.JoinListener;
import fr.bk.uhczelda.listeners.PrefixListener;
import fr.bk.uhczelda.listeners.QuitListener;
import fr.bk.uhczelda.listeners.SkinListener;
import lombok.Getter;
import lombok.Setter;

public class UZMain extends JavaPlugin 
{
	private static UZMain instance;	
	public static UZMain getInstance() 
	{
		return instance;
	}
	
	@Getter @Setter private UZGame game;
	
	@Getter private HashMap<String, Constructor<? extends Kit>> kits = new HashMap<String, Constructor<? extends Kit>>();
	@Getter private HashMap<String, Constructor<? extends UZItem>> items = new HashMap<String, Constructor<? extends UZItem>>();
	
	@Getter private static String prefix = "";
	
	@Override
	public void onEnable() 
	{
		instance = this;
		
		loadKits();
		loadItems();
		loadConfig();
		
		Bukkit.getPluginManager().registerEvents(new JoinListener(game), this);
		Bukkit.getPluginManager().registerEvents(new QuitListener(), this);
		Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
		Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
		Bukkit.getPluginManager().registerEvents(new PrefixListener(), this);
		Bukkit.getPluginManager().registerEvents(new SkinListener(), this);
		Bukkit.getPluginManager().registerEvents(game, this);
		Bukkit.getPluginManager().registerEvents(new UZGameBehaviour(game), this);

		for(Player player : Bukkit.getOnlinePlayers())
			Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(player, "is connected"));
		
		 ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		 
		 protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO) {
				@Override
				public void onPacketSending(PacketEvent event) {
					UZPlayer player = game.thePlayer(event.getPlayer());
					
					WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo(event.getPacket());
					ArrayList<PlayerInfoData> datas = new ArrayList<PlayerInfoData>();
					
					for(PlayerInfoData data : info.getData()) {
						//UZPlayer uzp = game.thePlayer(Bukkit.getPlayer(data.getProfile().getUUID()));
						
						UZPlayer uzp = null;
						for(UZPlayer p : game.getInGame()) 
						{
							if(p.getPlayer().getUniqueId() == data.getProfile().getUUID()) 
							{
								uzp = p;
							}
						}
						
						if(uzp == null)
							return;
						
						if(player.getGame() != null && player.getGame() == uzp.getGame()) {
							UZUpdatePrefixEvent evt2 = new UZUpdatePrefixEvent(player.getGame(), uzp, player, "");
							WrappedChatComponent displayName = data.getDisplayName();
							Bukkit.getPluginManager().callEvent(evt2);
							
							if(evt2.getPrefix().length() > 0) {
									try {
									if(displayName != null) {
										JSONObject obj = (JSONObject) new JSONParser().parse(displayName.getJson());
										displayName = WrappedChatComponent.fromText(evt2.getPrefix()+obj.get("text"));
									} else
										displayName = WrappedChatComponent.fromText(evt2.getPrefix()+data.getProfile().getName());
								} catch (ParseException e) {
									e.printStackTrace();
								}
							}
							
							UZSkinLoadEvent evt = new UZSkinLoadEvent(uzp.getGame(), uzp, player, data.getProfile());
							Bukkit.getPluginManager().callEvent(evt);
							
							datas.add(new PlayerInfoData(evt.getProfile(), data.getLatency(), data.getGameMode(), displayName));
						}else
							datas.add(data);
					}
					info.setData(datas);
				}
			});
		 
		 WorldBorder border = Bukkit.getWorld("world").getWorldBorder();
		 border.reset();
		 /*border.setCenter(0,0);
		 border.setDamageAmount(3);
		 border.setDamageBuffer(10);
		 border.setWarningDistance(5);
		 border.setSize(1500);*/
		 
		 Bukkit.getWorld("world").setAutoSave(true);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
	{
		if(label.equalsIgnoreCase("uz")) {
			if(!sender.isOp()) {
				sender.sendMessage(prefix+"§4Erreur: Vous n'avez pas la permission...");
				return true;
			}
			
			if(args.length >= 1) {
				if(args[0].equalsIgnoreCase("start")) {
					if(game.isStarted()) {
						sender.sendMessage(prefix+"§4Erreur: La partie est deja lance...");
						return true;
					}
					game.updateStart();
					return true;
				} 
				if(args[0].equalsIgnoreCase("end")) 
				{
					if(!game.isStarted()) {
						sender.sendMessage(prefix+"§4Erreur: La partie n'est pas lance...");
						return true;
					}
					game.end();
					return true;
				}
			}
		} 
		
		if (label.equalsIgnoreCase("revive")) 
		{
			if(args.length >= 1) 
			{
				if(game.isStarted()) 
				{
					UZPlayer playerToRevive = null;
					for(UZPlayer uzp : game.getDead()) 
					{
						if(uzp.getPlayer().getName().equalsIgnoreCase(args[0])) 
						{
							playerToRevive = uzp;			
						}
					}
					if(playerToRevive != null)
						game.revivePlayer(playerToRevive);
				}
			}
		}
		
		if(label.equalsIgnoreCase("chat")) 
		{
			if(args.length >= 1) {
				UZPlayer uzp = game.thePlayer((Player)sender);
				if(args[0].equalsIgnoreCase("t")) 
				{
					if(uzp.getTeam() != null) 
					{
						uzp.setTalkInGeneral(false);
						uzp.joinChat(uzp.getTeam().getTeamChat());
						uzp.sendMessage("§6§lZelda §7» Tu as rejoint le chat d'équipe");
					}
					else 
					{
						uzp.sendMessage("§6§lZelda §7» Tu n'as pas d'équipe");
					}
					return true;
				}
				else if(args[0].equalsIgnoreCase("g")) 
				{
					uzp.setTalkInGeneral(true);
					uzp.sendMessage("§6§lZelda §7» Tu as rejoint le chat général");
					return true;
				}
			}		
		}
		return false;
	}
	
	@Override
	public void onDisable() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.getPlayer().kickPlayer("Reload");
		}
		game.setStarted(false);
		ProtocolLibrary.getProtocolManager().removePacketListeners(this);			
	}

	public void loadConfig() 
	{		
		game = new UZGame(16);
		
		UZTeam yellow = new UZTeam("Vent", "§e");
		game.getTeams().add(yellow);
		UZTeam red = new UZTeam("Feu", "§c");
		game.getTeams().add(red);
		UZTeam water = new UZTeam("Eau", "§b");
		game.getTeams().add(water);
		UZTeam brown = new UZTeam("Terre", "§2");
		game.getTeams().add(brown);
		UZTeam darkness = new UZTeam("Ténèbre", "§5");
		game.getTeams().add(darkness);
	}
	
	private void loadKits() 
	{
		try {
			kits.put("Hylien", KHylien.class.getConstructor(UZGame.class));
			kits.put("Goron", KGoron.class.getConstructor(UZGame.class));
			kits.put("Zora", KZora.class.getConstructor(UZGame.class));
			kits.put("Piafs", KPiafs.class.getConstructor(UZGame.class));
			kits.put("Gerudo", KGerudo.class.getConstructor(UZGame.class));
			kits.put("Sheikah", KSheikah.class.getConstructor(UZGame.class));
			kits.put("Kokiri", KKokiri.class.getConstructor(UZGame.class));
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	private void loadItems() 
	{
		try {
			items.put("Compass", ICompass.class.getConstructor(UZGame.class));
			items.put("FaroreWind", IFaroreWind.class.getConstructor(UZGame.class));
			items.put("MajoraMask", IMajoraMask.class.getConstructor(UZGame.class));
			items.put("Navi", INavi.class.getConstructor(UZGame.class));
			items.put("OcarinaOfTime", IOcarinaOfTime.class.getConstructor(UZGame.class));
			items.put("SwordOfFour", ISwordOfFour.class.getConstructor(UZGame.class));
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
}













