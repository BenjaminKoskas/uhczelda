package fr.bk.uhczelda.classes;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Difficulty;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.comphenix.packetwrapper.WrapperPlayServerChat;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.math.BlockVector3;

import fr.bk.uhczelda.UZMain;
import fr.bk.uhczelda.classes.UZTriforce.UZTriforceEnum;
import fr.bk.uhczelda.events.UZGameStartEvent;
import fr.bk.uhczelda.items.UZItem;
import fr.bk.uhczelda.kit.Kit;
import fr.bk.uhczelda.utils.RandomLocGenerator;
import fr.bk.uhczelda.utils.Region;
import lombok.Getter;
import lombok.Setter;

public class UZGame implements Listener 
{	
	@Getter private final int maxPlayers;
	@Getter private ArrayList<UZPlayer> inGame = new ArrayList<UZPlayer>();
	@Getter private ArrayList<UZPlayer> alive = new ArrayList<UZPlayer>();
	@Getter private ArrayList<UZPlayer> dead = new ArrayList<UZPlayer>();
	@Getter private ArrayList<UZPlayer> inWait = new ArrayList<UZPlayer>();
	private static HashMap<Player, UZPlayer> cachedPlayers = new HashMap<Player, UZPlayer>();	
	
	@Getter private ArrayList<UZTeam> teams = new ArrayList<UZTeam>();
	
	@Getter private ArrayList<Kit> kits = new ArrayList<Kit>();
	@Getter private ArrayList<UZItem> items = new ArrayList<UZItem>();

	@Getter private UZTriforce strenght;
	@Getter private UZTriforce courage;
	@Getter private UZTriforce wisdom;
	
	@Getter private Location ganondorfLoc;
	@Getter private Location midonaLoc;
	@Getter private Location girahimLoc;
	@Getter private Location impaLoc;
	@Getter private Location zeldaLoc;
	
	@Getter private ItemStack masterSword;
	@Getter private ItemStack heroesTunic;
	@Getter private ItemStack pegaseBoots;
	@Getter private ItemStack lightBow;
	@Getter private ItemStack ruby;
	
	@Getter boolean canStart = true;
	@Getter @Setter private boolean started = false;
	@Getter @Setter private boolean ended = false;
	@Getter private boolean pvp = false;
	
	@Getter private Region region;
	
	@Getter @Setter private int triforceCooldown = 60;
	@Getter @Setter private long currentTriforceCooldown;
	@Getter @Setter private long triforceTimeLeft;
	
	@Getter @Setter private boolean assemblyStarted = false;
	@Getter @Setter private boolean assemblyFinished = false;
	@Getter @Setter private UZTeam teamAssembling;
	
	private BukkitTask startingTask;
	private BukkitTask pvpTask;
	public int assemblingTask;
	private int endGameTask;
	
	@Getter private UZChat generalChat = new UZChat((sender, message) -> {
		return "§7"+sender.getName()+" §a» §f"+message;
	});	
	@Getter private UZChat spectatorChat = new UZChat((sender, message) -> {
		return "§7(Spectateur)"+sender.getName()+" §a» §f"+message;
	});
	
	@Getter private Location spawn = new Location(Bukkit.getWorld("world"),0.500,166,0.500);
	
	public UZPlayer thePlayer(Player player) 
	{
		UZPlayer uzp = cachedPlayers.get(player);
		if(uzp == null) {
			uzp = new UZPlayer(player, this);
			cachedPlayers.put(player, uzp);
		}
		return uzp;
	}	
	public void removePlayer(Player player) 
	{
		if(cachedPlayers.containsKey(player))
			cachedPlayers.remove(player);
	}
	
	public UZGame(int maxPlayers) 
	{
		this.maxPlayers = maxPlayers;
		
		Bukkit.getPluginManager().registerEvents(this, UZMain.getInstance());
		
		try 
		{
			for(Entry<String, Constructor<? extends Kit>> kit : UZMain.getInstance().getKits().entrySet())
				kits.add(kit.getValue().newInstance(this));
		}
		catch(Exception err) 
		{
			Bukkit.broadcastMessage("§4§lUne erreur est survenue lors de la création des kits... Regardez la console !");
			err.printStackTrace();
		}
		
		try 
		{
			for(Entry<String, Constructor<? extends UZItem>> item : UZMain.getInstance().getItems().entrySet())
				items.add(item.getValue().newInstance(this));
		}
		catch(Exception err) 
		{
			Bukkit.broadcastMessage("§4§lUne erreur est survenue lors de la création des items... Regardez la console !");
			err.printStackTrace();
		}
		
		initializeItems();
	}
	
	@SuppressWarnings("deprecation")
	public void sendActionBarMessage(String msg) {
		WrapperPlayServerChat chat = new WrapperPlayServerChat();
		chat.setPosition((byte)2);
		chat.setMessage(WrappedChatComponent.fromText(msg));
		for(UZPlayer lgp : inGame)
			chat.sendPacket(lgp.getPlayer());
	}	
	public void broadcastMessage(String msg) {
		for(UZPlayer uzp : inGame)
			uzp.sendMessage(msg);
	}
	
	public void join(UZPlayer uzp) 
	{
		if(ended) { return; }
		if(!started && inGame.size() < maxPlayers) 
		{	
			if(!inGame.contains(uzp)) 
			{
				inGame.add(uzp);
				
				sendActionBarMessage("§a"+uzp.getName()+"§7 a rejoint la partie ! (§f"+inGame.size()+"§7/§f"+maxPlayers+"§7)");
			}
		} 
		else
		{
			uzp.getPlayer().kickPlayer("Partie deja commence !");	
		}
	}
	
	public void updateStart() {
		if(!isStarted()) {
			canStart = true;
			for(UZPlayer uzp : inGame) {
				if(uzp.getKit() == null)
					canStart = false;
				else if (uzp.getTeam() == null)
					canStart = false;
			}
			
			if(!canStart) 
			{
				broadcastMessage("§6Zelda §a>> §c§lTous les joueurs n'ont pas choisi leur team/kit !");
				return;
			}
			if(startingTask == null) {
				startingTask = new BukkitRunnable() {
					int timeLeft = 5+1;
					@Override
					public void run() {
						if(--timeLeft == 0)//start
							start();
						else {
							sendActionBarMessage("§l§6Zelda §a>> §lLa partie se lance dans §6§l" + timeLeft + " §a§lsecondes");
							for(UZPlayer uzp : inGame) {
								uzp.getPlayer().playSound(uzp.getPlayer().getLocation(), Sound.BLOCK_ANVIL_LAND, 3.0f, 1f);
							}
						}
					}
				}.runTaskTimer(UZMain.getInstance(), 20, 20);
			}
		}
	}
	
	public void start() 
	{	
		if(startingTask != null) {
			startingTask.cancel();
			startingTask = null;
		}		
		
		Bukkit.createWorld(new WorldCreator("world2"));
		World world = Bukkit.getWorld("world2");
		world.setAutoSave(false);
					
		setGamerules(world);
		
		strenght = new UZTriforce(loadSchematic(world), UZTriforceEnum.STRENGHT);
		courage = new UZTriforce(loadSchematic(world), UZTriforceEnum.COURAGE);
		wisdom = new UZTriforce(loadSchematic(world), UZTriforceEnum.WISDOM);
		
		System.out.println(strenght.getName());
		System.out.println(courage.getName());
		System.out.println(wisdom.getName());
		
		started = true;
		sendPlayersIntoGame();
		
		Bukkit.getPluginManager().callEvent(new UZGameStartEvent(this));
				
		spawnMerchants();
		
		region = new Region(new Location(Bukkit.getWorld("world2"), -6.3, 63, 88.7), new Location(Bukkit.getWorld("world2"), -24.7, 68.2, 78.300));
		
		System.out.println(strenght.getLocation());
		System.out.println(courage.getLocation());
		System.out.println(wisdom.getLocation());
		
		pvpTask = new BukkitRunnable() 
		{
			int timeLeft = 60*20;
			@Override
			public void run() 
			{
				timeLeft--;
				if(timeLeft == 0) 
				{
					pvp = true;
					for(UZPlayer uzp : inGame)
					{
						uzp.playSound(Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 3.0f, 0.5f);
						uzp.updateScoreboard(2, "§8> §aPVP : §aActivé");
						uzp.setHealth(uzp.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
					}
					broadcastMessage("§6§lZelda §a>> §7Le §c§lPVP §7est activé !");
					broadcastMessage("§6§lZelda §a>> §7Les §c§ldonjons §7sont activés !");
					generateDungeonItem();
					this.cancel();
				}
				else 
				{
					LocalTime timeOfDay = LocalTime.ofSecondOfDay(timeLeft);
					for(UZPlayer uzp : inGame)
					{
						uzp.updateScoreboard(2, "§8> §aPVP : §f"+timeOfDay.getMinute()+"min"+timeOfDay.getSecond()+"s");
					}
				}
			}
			
		}.runTaskTimer(UZMain.getInstance(), 20, 20);
	}
	
	public void preEnd(UZTeam winningTeam)
	{
		for(UZPlayer uzp : inGame) 
		{
			if(uzp.getInventory().contains(strenght.getItem())) 
				uzp.getInventory().remove(strenght.getItem());
			if(uzp.getInventory().contains(courage.getItem()))
				uzp.getInventory().remove(courage.getItem());
			if(uzp.getInventory().contains(wisdom.getItem())) 
				uzp.getInventory().remove(wisdom.getItem());
			
			uzp.teleport(new Location(Bukkit.getWorld("world2"), -15.500, 64, 79));
			
			uzp.sendTitle("§6§lGagnant : " + winningTeam.getColor() + "§7" + winningTeam.getName(), "", 60);
			
			if(!uzp.getTeam().equals(winningTeam)) 
			{
				if(alive.contains(uzp) && !dead.contains(uzp)) 
				{
					alive.remove(uzp);
					dead.add(uzp);
				}	
				
				uzp.setGamemode(GameMode.SPECTATOR);
			}
			else 
			{
				uzp.setGamemode(GameMode.SURVIVAL);
				uzp.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
				uzp.setHealth(20);
				
				Firework firework = uzp.getLocation().getWorld().spawn(uzp.getLocation(), Firework.class);
				FireworkMeta data = (FireworkMeta)firework.getFireworkMeta();
				data.addEffects(FireworkEffect.builder().withColor(Color.YELLOW).withColor(Color.GREEN).with(Type.BALL_LARGE).withFlicker().build());
				data.setPower(1);
				firework.setFireworkMeta(data);
			}
		}
		endGameTask = Bukkit.getScheduler().scheduleSyncDelayedTask(UZMain.getInstance(), new Runnable() 
		{
			 @Override
			 public void run() 
			 {
				 end();
				 Bukkit.getScheduler().cancelTask(endGameTask);
			 }
		}, (long)(20*15));
	}
	
	public void end() 
	{
		started = false;
		Bukkit.getScheduler().cancelTask(assemblingTask);
		Bukkit.getScheduler().cancelTask(pvpTask.getTaskId());
	
		for(UZPlayer uzp : inGame) 
		{			
			uzp.rejoinGame();
		}
			
		for(UZPlayer uzp : inWait) 
		{
			uzp.joinGame();
		}
		
		inWait.removeAll(inWait);
		alive.removeAll(alive);
		dead.removeAll(dead);
		
		reset();
		
		UZNpc.removeNPCsPacket();
		UZNpc.clearNPCs();
		
		Bukkit.unloadWorld("world2", false);
	}
	
	public void reset() 
	{
		pvp = false;
		
		triforceCooldown = 60;
		currentTriforceCooldown = 0;
		triforceTimeLeft = 0;
		
		assemblyStarted = false;
		assemblyFinished = false;
		teamAssembling = null;
		
		for(UZTeam uzt : teams) 
		{
			uzt.setSOFCooldown(600);
			uzt.setCurrentSOFCooldown(0);
			uzt.setSOFTimeLeft(0);
			
			uzt.setNaviCooldown(60);
			uzt.setCurrentNaviCooldown(0);
			uzt.setNaviTimeLeft(0);
			
			uzt.setOcarinaOfTimeUsed(false);
			uzt.setFaroreWindUsed(false);
		}
		
		strenght = null;
		courage = null;
		wisdom = null;
	}
	
	public void sendPlayersIntoGame() 
	{
		for(UZTeam team : teams) {
			teleportTeam(team, Bukkit.getWorld("world2")); 
		}
				
		for(UZPlayer uzp : inGame) 
		{
			uzp.sendIntoGame();
			alive.add(uzp);
		}
		
		for(UZPlayer uzp : inGame) 
		{
			uzp.updateScoreboard(3, "§8> §aTeam(s) : §f" + getNumberOfTeam() + " §7("+getAlive().size()+")");
		}
	}
	
	private void setGamerules(World world) {
		world.setDifficulty(Difficulty.HARD);	
		world.setTime(1000);
		world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
		world.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false);
		world.setGameRule(GameRule.DISABLE_RAIDS, true);
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		world.setGameRule(GameRule.DO_ENTITY_DROPS, true);
		world.setGameRule(GameRule.DO_FIRE_TICK, true);
		world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
		world.setGameRule(GameRule.DO_INSOMNIA, false);
		world.setGameRule(GameRule.DO_LIMITED_CRAFTING, false);
		world.setGameRule(GameRule.DO_MOB_LOOT, true);
		world.setGameRule(GameRule.DO_MOB_SPAWNING, true);
		world.setGameRule(GameRule.DO_TILE_DROPS, true);
		world.setGameRule(GameRule.DO_WEATHER_CYCLE, true);
		world.setGameRule(GameRule.DROWNING_DAMAGE, true);
		world.setGameRule(GameRule.FALL_DAMAGE, true);
		world.setGameRule(GameRule.FIRE_DAMAGE, true);
		world.setGameRule(GameRule.KEEP_INVENTORY, false);
		world.setGameRule(GameRule.MOB_GRIEFING, false);
		world.setGameRule(GameRule.NATURAL_REGENERATION, true);
		world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
		world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, true);
		world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, true);
		world.setAutoSave(false);
	}
	
	private void teleportTeam(UZTeam team, World world) 
	{
		Location randomLoc = RandomLocGenerator.generateRandomLocation(world, 128);
		
		for(UZPlayer uzp : team.getPlayers()) 
		{
			uzp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 400, 0));
			uzp.getPlayer().teleport(randomLoc);
		}
	}
	
	private Location loadSchematic(World world) 
	{
		Location randomLoc = RandomLocGenerator.generateRandomLocation(world, 30);
		
		System.out.println(randomLoc.toString());
		
		File schematic = new File(UZMain.getInstance().getDataFolder() + File.separator + "/schematics/triforce.schem");
		
		@SuppressWarnings("unused")
		com.sk89q.worldedit.world.World _world = new BukkitWorld(world);
		
		try {
			EditSession editSession = ClipboardFormats.findByFile(schematic).load(schematic).paste(new BukkitWorld(randomLoc.getWorld()), BlockVector3.at(randomLoc.getBlockX(), randomLoc.getBlockY(), randomLoc.getBlockZ()));
			editSession.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Location blockLoc = new Location(world, randomLoc.getBlockX() + 12, randomLoc.getBlockY() + 4, randomLoc.getBlockZ() - 8);
		Block block = world.getBlockAt(blockLoc);
		block.setType(Material.PINK_STAINED_GLASS);
		
		return block.getLocation();
	}
	
	private void initializeItems() 
	{
		masterSword = new ItemStack(Material.GOLDEN_SWORD);
		heroesTunic = new ItemStack(Material.GOLDEN_CHESTPLATE);
		pegaseBoots = new ItemStack(Material.CHAINMAIL_BOOTS);
		lightBow = new ItemStack(Material.BOW);
		
		ItemMeta meta = masterSword.getItemMeta();		
		meta.setDisplayName("Master Sword");
		meta.setUnbreakable(true);
		meta.addEnchant(Enchantment.DAMAGE_ALL, 6, true);
		meta.addEnchant(Enchantment.DAMAGE_ARTHROPODS, 2, true);
		meta.addEnchant(Enchantment.DAMAGE_UNDEAD, 5, true);		
		masterSword.setItemMeta(meta);
		
		meta = heroesTunic.getItemMeta();		
		meta.setDisplayName("Tunique du heros");
		meta.setUnbreakable(true);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
		meta.addEnchant(Enchantment.PROTECTION_PROJECTILE, 3, true);
		meta.addEnchant(Enchantment.PROTECTION_FIRE, 3, true);		
		heroesTunic.setItemMeta(meta);
		
		meta = pegaseBoots.getItemMeta();		
		meta.setDisplayName("Bottes de Pegase");
		meta.setUnbreakable(true);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, true);		
		pegaseBoots.setItemMeta(meta);
		
		meta = lightBow.getItemMeta();	
		meta.setDisplayName("Arc de Lumiere");
		meta.setUnbreakable(true);
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 2, true);
		meta.addEnchant(Enchantment.ARROW_KNOCKBACK, 1, true);
		meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
		meta.addEnchant(Enchantment.ARROW_FIRE, 1, true);		
		lightBow.setItemMeta(meta);
		
		ruby = new ItemStack(Material.EMERALD);
		ruby.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
		meta = ruby.getItemMeta();
		meta.setDisplayName("§a§lRuby");
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		ruby.setItemMeta(meta);
	}
	
	private void generateDungeonItem() 
	{
		Location treeChestLoc = new Location(Bukkit.getWorld("world2"), -498, 110, -489);
		Location waterTempleChestLoc = new Location(Bukkit.getWorld("world2"), 478, 30, 464);
		Location volcanoChstLoc = new Location(Bukkit.getWorld("world2"), -491, 77, 522);
		Location fortressChestLoc = new Location(Bukkit.getWorld("world2"), 534, 73, -480);
		
		ArrayList<Location> locations = new ArrayList<Location>();
		locations.add(treeChestLoc);
		locations.add(waterTempleChestLoc);
		locations.add(volcanoChstLoc);
		locations.add(fortressChestLoc);
		
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		items.add(masterSword);
		items.add(heroesTunic);
		items.add(pegaseBoots);
		items.add(lightBow);
		
		for(int i = 0; i < locations.size(); i++) 
		{
			locations.get(i).getChunk().load();
			locations.get(i).getChunk().setForceLoaded(true);
			if(locations.get(i).getBlock().getState() instanceof Chest) 
			{
				Chest chest = (Chest) locations.get(i).getBlock().getState();
				chest.getInventory().clear();
				chest.getInventory().addItem(items.get(i));
			}
		}
	}
	
	public void spawnMerchants() 
	{
		ganondorfLoc = new Location(Bukkit.getWorld("world2"), 200.972d, 66d, -198.957d, -90.7f, 1.2f);
		midonaLoc = new Location(Bukkit.getWorld("world2"), 204.403d, 63d, 208.580d, 0.4f, 1.3f);
		girahimLoc = new Location(Bukkit.getWorld("world2"), -223.335d, 63d, -190.644d, -90.8f, 3.1f);
		impaLoc = new Location(Bukkit.getWorld("world2"), -194.506d, 73d, 218.700d, -179.4f, 1.9f);
		zeldaLoc = new Location(Bukkit.getWorld("world2"), -11.552d, 65d, 16.643d, -0.5f, 1.9f);
		
		UZNpc.createNPC(ganondorfLoc, "§4§lGanondorf", "Ganondorf1");
		UZNpc.createNPC(midonaLoc, "§3§lMidona", "Jannalein");
		UZNpc.createNPC(girahimLoc, "§7§lGirahim", "Apati");
		UZNpc.createNPC(impaLoc, "§b§lImpa", "AlexDld");
		UZNpc.createNPC(zeldaLoc, "§6§lZelda", "Zelda");
	}
	
	public void killPlayer(UZPlayer uzp, UZPlayer killer) 
	{
		alive.remove(uzp);
		dead.add(uzp);
		
		uzp.kill();
		
		if(killer != null)
			broadcastMessage("§6§lZelda §a>> §6§l" + uzp.getName() + "§c a été tué par " + killer.getTeam().getColor() + killer.getName() + " !");
		else
			broadcastMessage("§6§lZelda §a>> §6§l" + uzp.getName() + "§c est mort !");
	}
	
	public void revivePlayer(UZPlayer uzp, UZPlayer mate) 
	{
		Player p = uzp.getPlayer();
		dead.remove(uzp);
		alive.add(uzp);
		
		p.setGameMode(GameMode.SURVIVAL);
		p.teleport(mate.getPlayer());
		
		uzp.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
		uzp.getPlayer().setHealth(20);
		
		p.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
		p.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
		p.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
		p.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
		
		p.getInventory().setItemInMainHand(new ItemStack(Material.STONE_SWORD));
		
		p.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
		
		int numbersOfTeam = 0;
		for(UZTeam t : teams) 
		{
			if(t.getPlayers().size() > 0) 
			{
				numbersOfTeam++;
				int numbersOfPlayersAlive = 0;
				for(UZPlayer u : t.getPlayers()) 
				{
					if(alive.contains(u))
						numbersOfPlayersAlive++;
				}
				if(numbersOfPlayersAlive == 0)
					numbersOfTeam--;
			}				
		}
		
		for(UZPlayer player : inGame) 
		{
			player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 3f, 1f);
			player.updateScoreboard(3, "§8> §aTeam(s) : §f" + numbersOfTeam + " §7("+alive.size()+")");
		}
	}
	
	public void revivePlayer(UZPlayer uzp) 
	{
		Player p = uzp.getPlayer();
		dead.remove(uzp);
		alive.add(uzp);
		
		p.setGameMode(GameMode.SURVIVAL);
		
		uzp.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
		uzp.getPlayer().setHealth(20);
		
		p.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
		p.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
		p.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
		p.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
		
		p.getInventory().setItemInMainHand(new ItemStack(Material.STONE_SWORD));
		
		p.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
		
		int numbersOfTeam = 0;
		for(UZTeam t : teams) 
		{
			if(t.getPlayers().size() > 0) 
			{
				numbersOfTeam++;
				int numbersOfPlayersAlive = 0;
				for(UZPlayer u : t.getPlayers()) 
				{
					if(alive.contains(u))
						numbersOfPlayersAlive++;
				}
				if(numbersOfPlayersAlive == 0)
					numbersOfTeam--;
			}				
		}
		
		for(UZPlayer player : inGame) 
		{
			player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 3f, 1f);
			player.updateScoreboard(3, "§8> §aTeam(s) : §f" + numbersOfTeam + " §7("+alive.size()+")");
		}
		
		for(UZPlayer player : uzp.getTeam().getPlayers()) 
		{
			if(alive.contains(player)) 
				uzp.teleport(player.getLocation());
		}
	}
	
	public Map<Integer, MerchantRecipe> getMerchantRecipes() 
	{
		Map<Integer, MerchantRecipe> recipes = new HashMap<Integer, MerchantRecipe>();
		
		ruby.setAmount(64);

		MerchantRecipe ganondorfRecipe = createMerchantRecipe(getItem("§c§lMasque de Majora"), ruby, Material.ROTTEN_FLESH, 7);
		recipes.put(UZNpc.getNPCs().get(0).getId(), ganondorfRecipe);
		
		MerchantRecipe midonaRecipe = createMerchantRecipe(getItem("§b§lNavi"), ruby, Material.BOOK, 4);		
		recipes.put(UZNpc.getNPCs().get(1).getId(), midonaRecipe);
		
		MerchantRecipe girahimRecipe = createMerchantRecipe(getItem("§6§lL'Épée de Quatre"), ruby, Material.IRON_INGOT, 40);		
		recipes.put(UZNpc.getNPCs().get(2).getId(), girahimRecipe);
		
		MerchantRecipe impaRecipe = createMerchantRecipe(getItem("§a§lVent de Fafore"), ruby, Material.DIAMOND, 3);		
		recipes.put(UZNpc.getNPCs().get(3).getId(), impaRecipe);
		
		MerchantRecipe zeldaRecipe = createMerchantRecipe(getItem("§3§lOcarina du Temps"), ruby, Material.DIAMOND, 15);		
		recipes.put(UZNpc.getNPCs().get(4).getId(), zeldaRecipe);
		
		return recipes;
	}

	public MerchantRecipe createMerchantRecipe(ItemStack item, ItemStack ingredient1, Material ingredient2, int ingredient2Amount) 
	{
		MerchantRecipe recipe = new MerchantRecipe(item, 1);	
		recipe.addIngredient(ingredient1);
		recipe.addIngredient(new ItemStack(ingredient2, ingredient2Amount));
		recipe.setMaxUses(1);
		return recipe;
	}	
	
	public void applySpeed(Player p) 
	{
		new BukkitRunnable() {
			@Override
			public void run() {
				if(p.getInventory().getBoots() != null)
					if(p.getInventory().getBoots().equals(pegaseBoots)) 
						p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 1, true, false));						
			}
		}.runTaskTimer(UZMain.getInstance(), 0, 20);
	}
	
	public ItemStack getItem(String name) 
	{
		for(UZItem item : items) 
		{
			if(item.getName().equalsIgnoreCase(name))
				return item.getItem();
		}
		return null;
	}
	
	public int getNumberOfTeam() 
	{
		int numbersOfTeam = 0;
		for(UZTeam t : getTeams()) 
		{
			if(t.getPlayers().size() > 0) 
			{
				numbersOfTeam++;
				int numbersOfPlayersAlive = 0;
				for(UZPlayer u : t.getPlayers()) 
				{
					if(getAlive().contains(u))
						numbersOfPlayersAlive++;
				}
				if(numbersOfPlayersAlive == 0)
					numbersOfTeam--;
			}				
		}
		return numbersOfTeam;
	}
}





































