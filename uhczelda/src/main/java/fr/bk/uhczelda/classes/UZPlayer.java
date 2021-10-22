package fr.bk.uhczelda.classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.packetwrapper.WrapperPlayServerChat;
import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import com.comphenix.packetwrapper.WrapperPlayServerTitle;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.EnumWrappers.TitleAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;

import fr.bk.uhczelda.UZMain;
import fr.bk.uhczelda.classes.UZChat.UZChatCallback;
import fr.bk.uhczelda.events.UZTeamLoseTriforceEvent;
import fr.bk.uhczelda.kit.Kit;
import fr.bk.uhczelda.listeners.InventoryListener;
import fr.bk.uhczelda.scoreboard.ScoreboardSign;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_15_R1.EntityPlayer;

public class UZPlayer 
{	
	@Getter private UZGame game;
	
	@Getter private UZTeam team;
	@Getter private Kit kit;
	
	@Getter private UZChat chat;
	@Getter @Setter private boolean talkInGeneral;
	
	@Getter @Setter private Player player;
	@Getter private String name;
	
	@Getter @Setter UZTriforce targetedTriforce;
	
	@Getter private ScoreboardSign scoreboard;
	
	@Getter private int mateDirectionTask;
	@Getter private int triforceDirectionTask;
	
	@Getter @Setter private boolean heldingMajoraMask = false;
	
	public UZPlayer(Player player, UZGame game) {
		this.player = player;
		this.game = game;
		this.name = player.getName();
	}
	
	public void removePlayer() {
		game.removePlayer(player);
	}
	
	public void joinGame() 
	{
		game.join(this);
		joinChat(game.getGeneralChat());
		talkInGeneral = true;
			
		setGamemode(GameMode.ADVENTURE);
		
		teleport(game.getSpawn());
		
		getInventory().clear();
		getInventory().setItem(3, InventoryListener.getTeamSelector());
		getInventory().setItem(5, InventoryListener.getKitSelector());
		getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
		setHealth(20);
		setFoodLevel(20);
		setExp(0);
		setLevel(0);
		
		for(PotionEffect po : getActivePotionEffects())
			removePotionEffect(po);
		
		createScoreboard();
		
		for(UZPlayer p : game.getInGame())
			p.updateScoreboard(2, "§8> §aJoueur(s) : §f" + game.getInGame().size() + "§7/§a" + game.getMaxPlayers());
		
		changePrefix("§7");
	}
	
	public void rejoinGame() 
	{
		Bukkit.getScheduler().cancelTask(mateDirectionTask);
		Bukkit.getScheduler().cancelTask(triforceDirectionTask);
		leaveTeam();
		leaveChat();
		setKit(null);
		joinGame();
		updatePrefix();
		heldingMajoraMask = false;
		targetedTriforce = null;
		getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
		setHealth(20);
		
		deleteScoreboard();
		createScoreboard();		
	}
	
	public void leaveGame() 
	{
		if(game.isStarted()) 
		{			
			Bukkit.getScheduler().cancelTask(mateDirectionTask);
			Bukkit.getScheduler().cancelTask(triforceDirectionTask);
			
			UZTriforce strenght = game.getStrenght();
			UZTriforce courage = game.getCourage();
			UZTriforce wisdom = game.getWisdom();
			
			if(game.getAlive().contains(this)) 
			{
				if(getInventory().contains(strenght.getItem())) 
				{
					getInventory().remove(strenght.getItem());
					
					Block block = Bukkit.getWorld("world2").getBlockAt(getLocation());
					block.setType(Material.PINK_STAINED_GLASS);
					
					strenght.setLocation(block.getLocation());
					
					Bukkit.getPluginManager().callEvent(new UZTeamLoseTriforceEvent(game, this, team, strenght));
				}
				if(getInventory().contains(courage.getItem())) 
				{
					getInventory().remove(courage.getItem());
					
					Block block = Bukkit.getWorld("world2").getBlockAt(getLocation().add(1, 0, 0));
					block.setType(Material.PINK_STAINED_GLASS);
					
					courage.setLocation(block.getLocation());
					
					Bukkit.getPluginManager().callEvent(new UZTeamLoseTriforceEvent(game, this, team, courage));
				}
				if(getInventory().contains(wisdom.getItem())) 
				{
					getInventory().remove(wisdom.getItem());
					
					Block block = Bukkit.getWorld("world2").getBlockAt(getLocation().add(-1, 0, 0));
					block.setType(Material.PINK_STAINED_GLASS);
					
					wisdom.setLocation(block.getLocation());
					
					Bukkit.getPluginManager().callEvent(new UZTeamLoseTriforceEvent(game, this, team, wisdom));
				}
			}
			
			int numbersOfTeam = 0;
			for(UZTeam t : game.getTeams()) 
			{
				if(t.getPlayers().size() > 0) 
				{
					numbersOfTeam++;
					int numbersOfPlayersAlive = 0;
					for(UZPlayer u : t.getPlayers()) 
					{
						if(game.getAlive().contains(u))
							numbersOfPlayersAlive++;
					}
					if(numbersOfPlayersAlive == 0)
						numbersOfTeam--;
				}				
			}
			
			leaveTeam();
			leaveChat();
			
			game.removePlayer(player);
			
			if(game.getInGame().contains(this))
				game.getInGame().remove(this);
			if(game.getAlive().contains(this))
				game.getAlive().remove(this);
			if(game.getDead().contains(this))
				game.getDead().remove(this);
			if(game.getInWait().contains(this))
				game.getInWait().remove(this);
			
			for(UZPlayer player : game.getInGame()) 
			{
				player.playSound(Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 3f, 1f);
				player.updateScoreboard(3, "§8> §aTeam(s) : §f" + numbersOfTeam + " §7("+game.getAlive().size()+")");
			}
			
			game.sendActionBarMessage("§a"+getName()+"§7 a quitte la partie ! (§f"+(getGame().getInGame().size())+"§7/§f"+getGame().getMaxPlayers()+"§7)");
		} 
		else 
		{
			game.removePlayer(player);
			
			leaveTeam();
			leaveChat();
			
			if(game.getInGame().contains(this))
				game.getInGame().remove(this);
			if(game.getAlive().contains(this))
				game.getAlive().remove(this);
			if(game.getDead().contains(this))
				game.getDead().remove(this);
			if(game.getInWait().contains(this))
				game.getInWait().remove(this);
			
			for(UZPlayer uzp : game.getInGame()) 
			{
				uzp.updateScoreboard(2, "§8> §aJoueur(s) : §f" + game.getInGame().size() + "§7/§a" + game.getMaxPlayers());
			}
			
			game.sendActionBarMessage("§a"+getName()+"§7 a quitte la partie ! (§f"+(getGame().getInGame().size())+"§7/§f"+getGame().getMaxPlayers()+"§7)");
		}
	}
	
	public void sendIntoGame() 
	{
		getInventory().clear();
		sendTitle("§6Zelda §a: §lLa partie commence !", "§7Plugin créé par AISsBenji inspiré de Daxsun", 60);
		playSound(Sound.UI_TOAST_CHALLENGE_COMPLETE, 3.0f, 0.5f);
		setGamemode(GameMode.SURVIVAL);
		getTeamMateDirection();
		deleteScoreboard();
		createScoreboard();
		targetedTriforce = game.getCourage();
		updateTriforceLocation();
	}
	
	public void joinTeam(UZTeam _team) 
	{
		if(team != null)
			leaveTeam();
		
		_team.join(this);
		team = _team;
		
		joinChat(team.getTeamChat());
		changePrefix(team.getColor());
		
		updateScoreboard(3, "§8> §aTeam : §7" + getTeam().getColor() + getTeam().getName());
	}
	
	public void leaveTeam() 
	{
		if(team == null) {return;}
		team.quit(this);
		team = null;
		
		updateScoreboard(3, "§8> §aTeam : §7Aucune");
		changePrefix("§7");
	}
	
	public void kill() 
	{
		setGamemode(GameMode.SPECTATOR);
		
		for(UZPlayer player : game.getInGame()) 
		{
			player.playSound(Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 3f, 1f);
			player.updateScoreboard(3, "§8> §aTeam(s) : §f" + game.getNumberOfTeam() + " §7("+game.getAlive().size()+")");
		}
	}
	
	public void createScoreboard() 
	{
		ScoreboardSign sb = new ScoreboardSign(player, "§6Zelda");
		sb.create();
		
		if(!getGame().isStarted()) 
		{
			sb.setLine(7, "§6 ");
			sb.setLine(6, " §8§m                     §r ");
			sb.setLine(5, "§6  ");
			sb.setLine(4, "§8> §aKit : §7Aucun");
			sb.setLine(3, "§8> §aTeam : §7Aucune");			
			sb.setLine(2, "§8> §aJoueur(s) : §f" + game.getInGame().size() + "§7/§a" + game.getMaxPlayers());
			sb.setLine(1, "§6   ");
			sb.setLine(0, "§8§m                      §r");
		}
		else 
		{
			sb.setLine(6, "§6 ");
			sb.setLine(5, " §8§m                     §r ");
			sb.setLine(4, "§6  ");	
			sb.setLine(3, "§8> §aTeam(s) : §f" + game.getNumberOfTeam() + " §7("+getGame().getAlive().size()+")");
			sb.setLine(2, "§8> §aPVP : §f20min00s");	
			sb.setLine(1, "§6   ");
			sb.setLine(0, "§8§m                      §r");
		}
		
		setScoreboard(sb);
	}
		
	public void updateScoreboard(int line, String text) 
	{
		scoreboard.setLine(line, text);
	}
	
	public void setScoreboard(ScoreboardSign sb) 
	{
		scoreboard = sb;
	}
	
	public void deleteScoreboard() 
	{
		if(scoreboard != null) 
		{
			scoreboard.destroy();
			scoreboard = null;
		}	
	}
			
	public void getTeamMateDirection() 
	{
		if(team == null) {return;}
		if(team.getPlayers().size() <= 1) {return;}
		mateDirectionTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(UZMain.getInstance(), new Runnable() {
			 
		    @Override
		    public void run() {
		    	String str = "";
		    	for(UZPlayer uzp : team.getPlayers()) 
				{
		    		if(uzp.getPlayer() != player && game.getAlive().contains(uzp)) 
		    		{
		    			Player mate = uzp.getPlayer();
						Location mateLoc = mate.getLocation();
						Location playerLoc = player.getLocation();
						
						// distance between the players
						double x = Math.abs(mateLoc.getBlockX() - playerLoc.getBlockX());
						double z = Math.abs(mateLoc.getBlockZ() - playerLoc.getBlockZ());
						
						double xSquare = x * x;
						double zSquare = z * z;
						
						double squareDistance = xSquare + zSquare;
						double distance = Math.round(Math.sqrt(squareDistance));
		    			
		    			String orientation = getOrientation(player, mate.getName());
						str += uzp.getTeam().getColor() + uzp.getName() + " : §f§l" + distance + uzp.getTeam().getColor()+ " " + orientation + " ";
		    		}								
				}
		    	
		    	sendActionBarMessage(str);
		    }
		 
		}, 1L , (long) 20);
		
	}
	
	public static String getOrientation(Player p, String name){
		String orientation = "";
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.getName().equals(name)){
				double oppose = player.getLocation().getZ()-p.getLocation().getZ();
				oppose = Math.sqrt(oppose*oppose);
				
				double adjacent = player.getLocation().getX()-p.getLocation().getX();
				adjacent = Math.sqrt(adjacent*adjacent);
				
				double angle = Math.atan(oppose/adjacent)*(180/Math.PI);
				
				int playerOrientation = 0;
				int seeOrientation = 0;
				
				if(player.getLocation().getX() >= p.getLocation().getX()){
					if(player.getLocation().getZ() >= p.getLocation().getZ()){
						if(angle <= 30.0){
							playerOrientation = 2;
						}else if(angle <= 60.0){
							playerOrientation = 3;
						}else{
							playerOrientation = 4;
						}
					}else{
						if(angle <= 30.0){
							playerOrientation = 2;
							
						}else if(angle <= 60.0){
							playerOrientation = 1;
						}else{
							playerOrientation = 0;
						}
					}
				}else if(player.getLocation().getX() < p.getLocation().getX()){
					if(player.getLocation().getZ() >= p.getLocation().getZ()){
						if(angle <= 30.0){
							playerOrientation = 6;
						}else if(angle <= 60.0){
							playerOrientation = 5;
						}else{
							playerOrientation = 4;
						}
					}else{
						if(angle <= 30.0){
							playerOrientation = 6;
						}else if(angle <= 60.0){
							playerOrientation = 7;
						}else{
							playerOrientation = 0;
						}
					}
				}
				
				// double ylaw = p.getEyeLocation().getYaw();
				double yaw = (p.getLocation().getYaw() - 90) % 360;
				if (yaw < 0) {
					yaw += 360.0;
		        }
				if((337.5 <= yaw && yaw < 360.0) || (0 <= yaw && yaw <=  22.5)){
					// OUEST
					seeOrientation = 6;
				}else if (22.5 <= yaw && yaw < 67.5) {
					// NORD OUEST
					seeOrientation = 7;
		        } else if (67.5 <= yaw && yaw < 112.5) {
		        	// NORD
		        	seeOrientation = 0;
		        } else if (112.5 <= yaw && yaw < 157.5) {
		        	// NORD EST
		        	seeOrientation = 1;
		        } else if (157.5 <= yaw && yaw < 202.5) {
		        	// EST
		        	seeOrientation = 2;
		        } else if (202.5 <= yaw && yaw < 247.5) {
		        	// SUD EST
		        	seeOrientation = 3;
		        } else if (247.5 <= yaw && yaw < 292.5) {
		        	// SUD
		        	seeOrientation = 4;
		        } else if (292.5 <= yaw && yaw < 337.5) {
		        	// SUD OUEST
		        	seeOrientation = 5;
		        }
				
				if(player.getWorld().getName().equals(p.getWorld().getName())){
					int pointOrientation = (playerOrientation - seeOrientation);
									
					switch (pointOrientation){
						case -7:
							orientation = orientation+"⬈ ";
							break;
						case -6:
							orientation = orientation+"➡ ";
							break;
						case -5:
							orientation = orientation+"⬊ ";
							break;
						case -4:
							orientation = orientation+"⬇ ";
							break;
						case -3:
							orientation = orientation+"⬋ ";
							break;
						case -2:
							orientation = orientation+"⬅ ";
							break;
						case -1:
							orientation = orientation+"⬉ ";
							break; 
						case 0:
							orientation = orientation+"⬆ ";
							break;
						case 1:
							orientation = orientation+"⬈ ";
							break;
						case 2:
							orientation = orientation+"➡ ";
							break;
						case 3:
							orientation = orientation+"⬊ ";
							break;
						case 4:
							orientation = orientation+"⬇ ";
							break;
						case 5:
							orientation = orientation+"⬋ ";
							break;
						case 6:
							orientation = orientation+"⬅ ";
							break;
						case 7:
							orientation = orientation+"⬉ ";
							break;                    
					}
				}
			}
		}
		return orientation;
	}
	
	public void updateTriforceLocation() 
	{
		triforceDirectionTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(UZMain.getInstance(), new Runnable() {
			 
		    @Override
		    public void run() 
		    {
		    	setCompassTarget(targetedTriforce.getLocation());
		    }
		 
		}, 1L , (long) 20);
	}
	
	public void getTriforceLocation() 
	{
		Location playerLoc = player.getLocation();
		Location triforceLoc = targetedTriforce.getLocation();   	
    	
    	double x = Math.abs(triforceLoc.getBlockX() - playerLoc.getBlockX());
		double z = Math.abs(triforceLoc.getBlockZ() - playerLoc.getBlockZ());
		
		double xSquare = x * x;
		double zSquare = z * z;
		
		double squareDistance = xSquare + zSquare;
		double distance = Math.round(Math.sqrt(squareDistance));
		
		sendMessage("§6§lZelda §7>> Vous êtes à §f§l" + distance + " §7blocs de la Triforce " + targetedTriforce.getName());
	}
	
	public void setKit(Kit kit) 
	{
		if(kit == null && this.kit != null) 
		{
			getKit().getPlayers().remove(this);
			return;
		}
		
		if(this.kit != null) 
		{
			getKit().getPlayers().remove(this);
		}
		
		kit.join(this);
		this.kit = kit;
		
		updateScoreboard(4, "§8> §aKit : " + kit.getName());
	}
	
	@SuppressWarnings("deprecation")
	public void openTeamSelectionInventory() {
		UZGame game = UZMain.getInstance().getGame();
		Inventory inv = Bukkit.createInventory(null, 54, "§aSelection Equipe");
		ItemStack[] items = new ItemStack[54];
		ItemStack random = new ItemStack(Material.WHITE_BANNER);
		
		BannerMeta bmeta = (BannerMeta)random.getItemMeta();
		bmeta.setDisplayName("§7Aleatoire");	
		bmeta.setBaseColor(DyeColor.WHITE);	
		bmeta.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_DOWNLEFT));;
		
		random.setItemMeta(bmeta);
		
		for(int i = 0; i < items.length; i++) {
			items[i] = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		}
		items[40] = random;
		
		for(int i = 0; i < game.getTeams().size(); i++) 
		{
			ItemStack teamFlag = new ItemStack(Material.WHITE_BANNER);
			ItemMeta meta = teamFlag.getItemMeta();
			List<String> players = new ArrayList<String>();
			
			if(game.getTeams().get(i).getName().equalsIgnoreCase("Vent")) 
			{				
				teamFlag = new ItemStack(Material.YELLOW_BANNER);
				if(!players.isEmpty())
					players.clear();
				if(meta.hasLore())
					if(!meta.getLore().isEmpty())
						meta.getLore().clear();
				for(UZPlayer p : game.getTeams().get(i).getPlayers()) 
				{
					players.add("§7> " + p.getName());
				}
			} 
			else if (game.getTeams().get(i).getName().equalsIgnoreCase("Feu")) 
			{
				teamFlag = new ItemStack(Material.RED_BANNER);
				if(!players.isEmpty())
					players.clear();
				if(meta.hasLore())
					if(!meta.getLore().isEmpty())
						meta.getLore().clear();
				for(UZPlayer p : game.getTeams().get(i).getPlayers()) 
				{
					players.add("§7> " + p.getName());
				}
			} 
			else if (game.getTeams().get(i).getName().equalsIgnoreCase("Eau")) 
			{
				teamFlag = new ItemStack(Material.LIGHT_BLUE_BANNER);
				if(!players.isEmpty())
					players.clear();
				if(meta.hasLore())
					if(!meta.getLore().isEmpty())
						meta.getLore().clear();
				for(UZPlayer p : game.getTeams().get(i).getPlayers()) 
				{
					players.add("§7> " + p.getName());
				}
			} 
			else if (game.getTeams().get(i).getName().equalsIgnoreCase("Terre")) 
			{
				teamFlag = new ItemStack(Material.GREEN_BANNER);
				if(!players.isEmpty())
					players.clear();
				if(meta.hasLore())
					if(!meta.getLore().isEmpty())
						meta.getLore().clear();
				for(UZPlayer p : game.getTeams().get(i).getPlayers()) 
				{
					players.add("§7> " + p.getName());
				}
			}
			else if (game.getTeams().get(i).getName().equalsIgnoreCase("Ténèbre")) 
			{
				teamFlag = new ItemStack(Material.PURPLE_BANNER);
				if(!players.isEmpty())
					players.clear();
				if(meta.hasLore())
					if(!meta.getLore().isEmpty())
						meta.getLore().clear();
				for(UZPlayer p : game.getTeams().get(i).getPlayers()) 
				{
					players.add("§7> " + p.getName());
				}
			}
						
			meta.setDisplayName(game.getTeams().get(i).getColor()+game.getTeams().get(i).getName());
			meta.setLore(players);
			
			teamFlag.setItemMeta(meta);
			
			/*if(i > 1) 
				items[11+i+1] = teamFlag;
			else 
				items[11+i] = teamFlag;*/
			items[11+i] = teamFlag;
		}
		ItemStack[] content = items.clone();
		inv.setContents(content);
		player.closeInventory();
		player.openInventory(inv);
	}
	
	@SuppressWarnings("deprecation")
	public void openKitSelectionInventory() 
	{
		UZGame game = UZMain.getInstance().getGame();
		Inventory inv = Bukkit.createInventory(null, 54, "§aSelection Kit");
		ItemStack[] items = new ItemStack[54];
		ItemStack random = new ItemStack(Material.WHITE_BANNER);
		
		BannerMeta bmeta = (BannerMeta)random.getItemMeta();
		bmeta.setDisplayName("§7Aleatoire");	
		bmeta.setBaseColor(DyeColor.WHITE);	
		bmeta.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_DOWNLEFT));;
		
		random.setItemMeta(bmeta);
		
		ItemStack kitItem = new ItemStack(Material.DIAMOND_AXE);
		ItemMeta meta = kitItem.getItemMeta();
		
		for(int i = 0; i < items.length; i++) {
			items[i] = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		}
		items[40] = random;
		
		for(int i = 0; i < game.getKits().size(); i++) 
		{
			kitItem = game.getKits().get(i).getItem();
			meta = kitItem.getItemMeta();
			if(meta.hasLore())
				if(!meta.getLore().isEmpty())
					meta.getLore().clear();
			meta.setDisplayName(game.getKits().get(i).getName());
			meta.setLore(game.getKits().get(i).getDescription());
			kitItem.setItemMeta(meta);
			items[10+i] = kitItem;
		}
		
		ItemStack[] content = items.clone();
		inv.setContents(content);
		player.closeInventory();
		player.openInventory(inv);
	}
	
	public void openMerchant(Player player, int merchantId) 
	{
		for(EntityPlayer npc : UZNpc.getNPCs()) 
		{
			if(npc.getId() == merchantId) 
			{
				Merchant merchant = Bukkit.createMerchant(npc.getName());
				List<MerchantRecipe> recipes = new ArrayList<>();
				recipes.add(game.getMerchantRecipes().get(merchantId));
				merchant.setRecipes(recipes);
				player.openMerchant(merchant, true);
			}
		}			
	}
	
	public void changePrefix(String color) 
	{
		WrapperPlayServerScoreboardTeam myTeam = new WrapperPlayServerScoreboardTeam();
		myTeam.setName(getName());
		myTeam.setPrefix(WrappedChatComponent.fromText(color));
		myTeam.setPlayers(Arrays.asList(getName()));
		myTeam.setMode(0);
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p != player) {
				WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam();
				team.setName(p.getName());
				team.setPrefix(WrappedChatComponent.fromText(color));
				team.setPlayers(Arrays.asList(p.getName()));
				team.setMode(0);
				
				team.sendPacket(player);
				myTeam.sendPacket(p);
			}
		}
		for(Player p : Bukkit.getOnlinePlayers())
			game.thePlayer(p).updatePrefix();
	}

	public void updatePrefix() {
		if(getGame() != null && player != null) {
			List<String> meList = Arrays.asList(getName());
			for(UZPlayer uzp : getGame().getInGame()) {
				WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo();
				ArrayList<PlayerInfoData> infos = new ArrayList<PlayerInfoData>();
				info.setAction(PlayerInfoAction.ADD_PLAYER);
				infos.add(new PlayerInfoData(new WrappedGameProfile(getPlayer().getUniqueId(), getName()), 0, NativeGameMode.ADVENTURE, WrappedChatComponent.fromText(getName())));
				info.setData(infos);
				info.sendPacket(uzp.getPlayer());

				WrapperPlayServerScoreboardTeam _team = new WrapperPlayServerScoreboardTeam();
				_team.setMode(2);
				_team.setName(getName());
				if(this.team != null)
					_team.setPrefix(WrappedChatComponent.fromText(this.team.getColor()));
				else 
					_team.setPrefix(WrappedChatComponent.fromText("§7"));
				_team.setPlayers(meList);
				_team.sendPacket(uzp.getPlayer());
			}
		}
	}
	
	public void remove() {
		this.player = null;
	}
	
	public void openInventory(Inventory inv) 
	{
		getPlayer().openInventory(inv);
	}	
	public void teleport(Location dest) 
	{
		player.teleport(dest);
	}	
	public void setGamemode(GameMode mode) 
	{
		player.setGameMode(mode);
	}	
	public void playSound(Sound sound, float volume, float pitch) 
	{
		player.playSound(getLocation(), sound, volume, pitch);
	}	
	public void addPotionEffect(PotionEffectType type, int duration, int amplifier) 
	{
		player.addPotionEffect(new PotionEffect(type, duration, amplifier));
	}	
	public void setHealth(double value) 
	{
		player.setHealth(value);
	}	
	public void setFoodLevel(int level) {
		getPlayer().setFoodLevel(level);
	}	
	public void setExp(float exp) {
		getPlayer().setExp(exp);
	}
	public void setLevel(int level) {
		getPlayer().setLevel(level);
	}
	public boolean consumeItem(int count, Material mat) {
	    Map<Integer, ? extends ItemStack> ammo = player.getInventory().all(mat);

	    int found = 0;
	    for (ItemStack stack : ammo.values())
	        found += stack.getAmount();
	    if (count > found)
	        return false;

	    for (Integer index : ammo.keySet()) {
	        ItemStack stack = ammo.get(index);

	        int removed = Math.min(count, stack.getAmount());
	        count -= removed;

	        if (stack.getAmount() == removed)
	            player.getInventory().setItem(index, null);
	        else
	            stack.setAmount(stack.getAmount() - removed);

	        if (count <= 0)
	            break;
	    }

	    player.updateInventory();
	    return true;
	}
	public void removePotionEffect(PotionEffect effect) {
		getPlayer().removePotionEffect(effect.getType());
	}
	public void setCompassTarget(Location location) {
		player.setCompassTarget(location);
	}
	
	public void sendMessage(String msg) {
		if(this.player != null)
			getPlayer().sendMessage(UZMain.getPrefix()+msg);
	}	
	@SuppressWarnings("deprecation")
	public void sendActionBarMessage(String msg) {
		WrapperPlayServerChat chat = new WrapperPlayServerChat();
		chat.setPosition((byte)2);
		chat.setMessage(WrappedChatComponent.fromText(msg));
		chat.sendPacket(player);
	}	
	public void sendTitle(String title, String subTitle, int stay) {
		if(this.player != null) {
			WrapperPlayServerTitle titlePacket = new WrapperPlayServerTitle();
			titlePacket.setAction(TitleAction.TIMES);
			titlePacket.setFadeIn(10);
			titlePacket.setStay(stay);
			titlePacket.setFadeOut(10);
			titlePacket.sendPacket(player);
			
			titlePacket = new WrapperPlayServerTitle();
			titlePacket.setAction(TitleAction.TITLE);
			titlePacket.setTitle(WrappedChatComponent.fromText(title));
			titlePacket.sendPacket(player);
			
			titlePacket = new WrapperPlayServerTitle();
			titlePacket.setAction(TitleAction.SUBTITLE);
			titlePacket.setTitle(WrappedChatComponent.fromText(subTitle));
			titlePacket.sendPacket(player);
		}
	}
	
	public void joinChat(UZChat chat, UZChatCallback callback) {
		joinChat(chat, callback, false);
	}
	public void joinChat(UZChat chat) {
		joinChat(chat, null, false);
	}
	public void joinChat(UZChat chat, boolean muted) {
		joinChat(chat, null, muted);
	}
	public void joinChat(UZChat chat, UZChatCallback callback, boolean muted) {
		if(this.chat != null && !muted)
			this.chat.leave(this);
		
		if(!muted)
			this.chat = chat;
		
		if(chat != null && player != null)
			chat.join(this, callback == null ? chat.getDefaultCallback() : callback);
	}
	
	public void onChat(String message) {
		if(chat != null && chat != game.getGeneralChat() && !talkInGeneral) {
			chat.sendMessage(this, message);
		} else if (talkInGeneral) {
			if(team != null)
				game.broadcastMessage(getTeam().getColor()+getName()+" §a» §f"+message);
			else
				game.broadcastMessage("§7"+getName()+" §a» §f"+message);
		}
	}
	
	public void leaveChat() {
		joinChat(new UZNoChat(), null);
	}
	
	public Collection<PotionEffect> getActivePotionEffects() {
		return getPlayer().getActivePotionEffects();
	}		
	public AttributeInstance getAttribute(Attribute attribute) {
		return getPlayer().getAttribute(attribute);
	}
	public Location getLocation() {
		return player.getLocation();
	}	
	public PlayerInventory getInventory() {
		return player.getInventory();
	}
	public boolean isSneaking() {
		return player.isSneaking();
	}
}
