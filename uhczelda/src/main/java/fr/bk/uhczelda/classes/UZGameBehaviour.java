package fr.bk.uhczelda.classes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.bk.uhczelda.UZMain;
import fr.bk.uhczelda.events.UZRightClickNPCEvent;
import fr.bk.uhczelda.events.UZTeamGetTriforceEvent;
import fr.bk.uhczelda.events.UZTeamLoseTriforceEvent;
import fr.bk.uhczelda.utils.Region;
import net.minecraft.server.v1_15_R1.EntityPlayer;

public class UZGameBehaviour implements Listener 
{
	private UZGame game;
	
	public UZGameBehaviour(UZGame fkGame)
	{
		this.game = fkGame;	
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) 
	{
		if(!game.isStarted())
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onHealthLevelChange(EntityDamageEvent e) 
	{
		if(game.isStarted()) {return;}
		if(e.getEntity() instanceof Player)
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerDamageEntity(EntityDamageByEntityEvent e) 
	{
		if(e.getEntity() instanceof ItemFrame) 
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractAtEntityEvent e) 
	{
		if(game.isStarted()) {return;}
		if(e.getRightClicked() instanceof ArmorStand) 
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler 
	public void onCraft(CraftItemEvent e) 
	{
		if(!game.isStarted()) {return;}
		if(e.getCurrentItem() != null) 
		{
			if(e.getCurrentItem().equals(new ItemStack(Material.GOLDEN_SWORD))) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent e) {
		if(!game.isStarted()) {return;}
		if(e.getBlock() != null)
			if(e.getBlock().getType().equals(Material.PINK_STAINED_GLASS))
				e.setCancelled(true);
	}
	
	@EventHandler
    public void onDrop(PlayerDropItemEvent e) {
		if(!game.isStarted()) {return;}
		if(e.getItemDrop() == null) {return;}		
		if(e.getItemDrop().getItemStack().getType() == Material.PINK_STAINED_GLASS) 
			e.setCancelled(true);			
    }
	
	@EventHandler
	public void onRightClickNPC(UZRightClickNPCEvent e) 
	{
		Player player = e.getPlayer();
		UZPlayer uzp = game.thePlayer(player);
		EntityPlayer npc = e.getNpc();
		System.out.println(npc.getName());
		for(EntityPlayer _npc : UZNpc.getNPCs()) 
		{
			if(_npc.getId() == npc.getId()) 
			{
				uzp.openMerchant(player, _npc.getId());
			}
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) 
	{
		if(!game.isStarted() && !e.getWhoClicked().isOp()) 
		{
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) 
	{
		if(!game.isStarted()) {return;}
		if(!(e.getItem() == null)) {return;}
		if(e.getClickedBlock() == null) {return;}
		if(!e.getHand().equals(EquipmentSlot.HAND)) {return;}
		if(!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {return;}
		
		UZPlayer uzp = game.thePlayer(e.getPlayer());
		UZTeam uzt = uzp.getTeam();
		Block block = e.getClickedBlock();
		Location blockLoc = block.getLocation();
		
		UZTriforce strenght = game.getStrenght();
		UZTriforce courage = game.getCourage();
		UZTriforce wisdom = game.getWisdom();

		if(block.getType().equals(Material.PINK_STAINED_GLASS)) 
		{
			if(blockLoc.equals(strenght.getLocation())) 
			{
				uzp.getInventory().addItem(strenght.getItem());
				Bukkit.getPluginManager().callEvent(new UZTeamGetTriforceEvent(game, uzp, uzt, strenght));
				Bukkit.getWorld("world2").getBlockAt(blockLoc).setType(Material.AIR);
			}
			else if(blockLoc.equals(courage.getLocation())) 
			{
				uzp.getInventory().addItem(courage.getItem());
				Bukkit.getPluginManager().callEvent(new UZTeamGetTriforceEvent(game, uzp, uzt, courage));
				Bukkit.getWorld("world2").getBlockAt(blockLoc).setType(Material.AIR);
			}
			else if(blockLoc.equals(wisdom.getLocation())) 
			{
				uzp.getInventory().addItem(wisdom.getItem());
				Bukkit.getPluginManager().callEvent(new UZTeamGetTriforceEvent(game, uzp, uzt, wisdom));
				Bukkit.getWorld("world2").getBlockAt(blockLoc).setType(Material.AIR);
			}
		}
	}
	
	@EventHandler
	public void onGetTriforce(UZTeamGetTriforceEvent e) 
	{
		UZPlayer uzp = e.getPlayer();
		UZTeam uzt = e.getTeam();
		UZTriforce triforce = e.getTriforce();
		
		triforce.setHolder(uzp);
		triforce.setHolders(uzt);
		
		game.broadcastMessage("§6§lZelda §a>> L'équipe " + uzt.getColor() + uzt.getName() + " §aa récupéré la Triforce " + triforce.getName() + " §a!");
		
		for(UZPlayer uzp2 : game.getInGame()) {
			uzp2.playSound(Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 3f, 1f);
		}
			
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) 
	{
		if(!game.isStarted()) {return;}
		if(e.isCancelled()) {return;}
		if(e.getCause().equals(DamageCause.ENTITY_ATTACK)) {return;}
		if(!(e.getEntity() instanceof Player)) {return;}
		if(((Player)e.getEntity()).getHealth() - e.getFinalDamage() > 0) {return;}
		e.setCancelled(true);
		
		UZPlayer uzp = game.thePlayer((Player)e.getEntity());
		UZTeam uzt = uzp.getTeam();
		Location playerLoc = uzp.getLocation();
		
		UZTriforce strenght = game.getStrenght();
		UZTriforce courage = game.getCourage();
		UZTriforce wisdom = game.getWisdom();
		
		if(game.getAlive().contains(uzp)) 
		{
			game.killPlayer(uzp, null);
			
			if(uzp.getInventory().contains(strenght.getItem())) 
			{
				uzp.getInventory().remove(strenght.getItem());
				
				Block block = Bukkit.getWorld("world2").getBlockAt(playerLoc);
				block.setType(Material.PINK_STAINED_GLASS);
				
				strenght.setLocation(block.getLocation());
				
				Bukkit.getPluginManager().callEvent(new UZTeamLoseTriforceEvent(game, uzp, uzt, strenght));
			}
			if(uzp.getInventory().contains(courage.getItem())) 
			{
				uzp.getInventory().remove(courage.getItem());
				
				Block block = Bukkit.getWorld("world2").getBlockAt(playerLoc.add(1, 0, 0));
				block.setType(Material.PINK_STAINED_GLASS);
				
				courage.setLocation(block.getLocation());
				
				Bukkit.getPluginManager().callEvent(new UZTeamLoseTriforceEvent(game, uzp, uzt, courage));
			}
			if(uzp.getInventory().contains(wisdom.getItem())) 
			{
				uzp.getInventory().remove(wisdom.getItem());
				
				Block block = Bukkit.getWorld("world2").getBlockAt(playerLoc.add(2, 0, 0));
				block.setType(Material.PINK_STAINED_GLASS);
				
				wisdom.setLocation(block.getLocation());
				
				Bukkit.getPluginManager().callEvent(new UZTeamLoseTriforceEvent(game, uzp, uzt, wisdom));
			}
		}
		
		for(ItemStack item : uzp.getInventory().getContents()) 
		{
			if(item != null) 
			{
				playerLoc.getWorld().dropItem(playerLoc, item);
			}
		}
		
		uzp.getInventory().clear();
	}
	
	@EventHandler
	public void onPlayerKilledByEntity(EntityDamageByEntityEvent e) 
	{
		if(!game.isStarted()) {return;}
		if(!(e.getEntity() instanceof Player)) {return;}
		if((e.getDamager() instanceof Player)) {return;}
		if(((Player)e.getEntity()).getHealth() - e.getFinalDamage() > 0) {return;}
		e.setCancelled(true);
		
		UZPlayer uzp = game.thePlayer((Player)e.getEntity());
		UZTeam uzt = uzp.getTeam();
		Location playerLoc = uzp.getLocation();
		
		UZTriforce strenght = game.getStrenght();
		UZTriforce courage = game.getCourage();
		UZTriforce wisdom = game.getWisdom();
		
		if(game.getAlive().contains(uzp)) 
		{
			game.killPlayer(uzp, null);
			
			if(uzp.getInventory().contains(strenght.getItem())) 
			{
				uzp.getInventory().remove(strenght.getItem());
				
				Block block = Bukkit.getWorld("world2").getBlockAt(playerLoc);
				block.setType(Material.PINK_STAINED_GLASS);
				
				strenght.setLocation(block.getLocation());
				
				Bukkit.getPluginManager().callEvent(new UZTeamLoseTriforceEvent(game, uzp, uzt, strenght));
			}
			if(uzp.getInventory().contains(courage.getItem())) 
			{
				uzp.getInventory().remove(courage.getItem());
				
				Block block = Bukkit.getWorld("world2").getBlockAt(playerLoc.add(1, 0, 0));
				block.setType(Material.PINK_STAINED_GLASS);
				
				courage.setLocation(block.getLocation());
				
				Bukkit.getPluginManager().callEvent(new UZTeamLoseTriforceEvent(game, uzp, uzt, courage));
			}
			if(uzp.getInventory().contains(wisdom.getItem())) 
			{
				uzp.getInventory().remove(wisdom.getItem());
				
				Block block = Bukkit.getWorld("world2").getBlockAt(playerLoc.add(2, 0, 0));
				block.setType(Material.PINK_STAINED_GLASS);
				
				wisdom.setLocation(block.getLocation());
				
				Bukkit.getPluginManager().callEvent(new UZTeamLoseTriforceEvent(game, uzp, uzt, wisdom));
			}
		}
		
		for(ItemStack item : uzp.getInventory().getContents()) 
		{
			if(item != null) 
			{
				playerLoc.getWorld().dropItem(playerLoc, item);
			}
		}
		
		uzp.getInventory().clear();
	}
	
	@EventHandler
	public void onPlayerKilledByPlayer(EntityDamageByEntityEvent e) 
	{
		if(!game.isStarted()) {return;}
		if(!(e.getEntity() instanceof Player)) {return;}
		UZPlayer uzp = game.thePlayer((Player)e.getEntity());
		UZPlayer damager = null;
		if(e.getDamager() instanceof Player) 
		{
			damager = game.thePlayer((Player)e.getDamager());
			if(!game.isPvp()) 
			{
				e.setCancelled(true);
				return;
			}
			if(damager.getTeam().equals(uzp.getTeam())) 
			{
				e.setCancelled(true);
				return;
			}
		}
		if(((Player)e.getEntity()).getHealth() - e.getFinalDamage() > 0) {return;}
		e.setCancelled(true);
		
		
		if(e.getDamager() instanceof Player)
			damager = game.thePlayer((Player)e.getDamager());
		
		
		UZTeam uzt = uzp.getTeam();
		Location playerLoc = uzp.getLocation();
		
		UZTriforce strenght = game.getStrenght();
		UZTriforce courage = game.getCourage();
		UZTriforce wisdom = game.getWisdom();
		
		if(game.getAlive().contains(uzp)) 
		{
			game.killPlayer(uzp, damager);
			
			if(uzp.getInventory().contains(strenght.getItem())) 
			{
				uzp.getInventory().remove(strenght.getItem());
				
				Block block = Bukkit.getWorld("world2").getBlockAt(playerLoc);
				block.setType(Material.PINK_STAINED_GLASS);
				
				strenght.setLocation(block.getLocation());
				
				Bukkit.getPluginManager().callEvent(new UZTeamLoseTriforceEvent(game, uzp, uzt, strenght));
			}
			if(uzp.getInventory().contains(courage.getItem())) 
			{
				uzp.getInventory().remove(courage.getItem());
				
				Block block = Bukkit.getWorld("world2").getBlockAt(playerLoc.add(1, 0, 0));
				block.setType(Material.PINK_STAINED_GLASS);
				
				courage.setLocation(block.getLocation());
				
				Bukkit.getPluginManager().callEvent(new UZTeamLoseTriforceEvent(game, uzp, uzt, courage));
			}
			if(uzp.getInventory().contains(wisdom.getItem())) 
			{
				uzp.getInventory().remove(wisdom.getItem());
				
				Block block = Bukkit.getWorld("world2").getBlockAt(playerLoc.add(2, 0, 0));
				block.setType(Material.PINK_STAINED_GLASS);
				
				wisdom.setLocation(block.getLocation());
				
				Bukkit.getPluginManager().callEvent(new UZTeamLoseTriforceEvent(game, uzp, uzt, wisdom));
			}
		}
		
		for(ItemStack item : uzp.getInventory().getContents()) 
		{
			if(item != null) 
			{
				playerLoc.getWorld().dropItem(playerLoc, item);
			}
		}
		
		uzp.getInventory().clear();
	}
	
	@EventHandler
	public void onLoseTriforce(UZTeamLoseTriforceEvent e) 
	{
		UZTeam uzt = e.getTeam();
		UZTriforce triforce = e.getTriforce();
		
		triforce.setHolder(null);
		triforce.setHolders(null);
		
		game.broadcastMessage("§6§lZelda §a>> L'équipe " + uzt.getColor() + uzt.getName() + " §aa perdu la Triforce " + triforce.getName() + " §a!");
		
		for(UZPlayer uzp : game.getInGame()) {
			uzp.playSound(Sound.ITEM_TRIDENT_RETURN, 3f, 1f);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) 
	{
		if(!game.isStarted()) {return;}
		if(e.getBlock().equals(null)) {return;}
		
		UZPlayer uzp = game.thePlayer(e.getPlayer());
		Block block = e.getBlock();
		Material blockType = block.getType();
		Location blockLoc = block.getLocation();
		World world = blockLoc.getWorld();
		
		if(blockType.equals(Material.PINK_STAINED_GLASS)) {e.setCancelled(true);}
		if(blockType.equals(Material.GRASS) || blockType.equals(Material.TALL_GRASS)) 
		{
			game.getRuby().setAmount(1);
			world.dropItem(blockLoc, game.getRuby());
		}
		
		if(blockType.equals(Material.SUGAR_CANE)) 
		{
			e.setDropItems(false);
			world.dropItem(blockLoc, new ItemStack(Material.PAPER, 1));
		}
		
		if(uzp.getInventory().getItemInMainHand().equals(null)) {return;}
		
		Material itemInHand = uzp.getInventory().getItemInMainHand().getType();
		Material stonePickaxe = Material.STONE_PICKAXE;
		Material ironPickaxe = Material.IRON_PICKAXE;
		Material goldPickaxe = Material.GOLDEN_PICKAXE;
		Material diamondPickaxe = Material.DIAMOND_PICKAXE;
		
		if(blockType.equals(Material.IRON_ORE)) 
		{
			if(itemInHand.equals(stonePickaxe) || itemInHand.equals(ironPickaxe) || itemInHand.equals(goldPickaxe) || itemInHand.equals(diamondPickaxe)) 
			{
				e.setDropItems(false);
				
				world.dropItem(blockLoc, new ItemStack(Material.IRON_INGOT, 1));
				((ExperienceOrb)world.spawn(blockLoc, ExperienceOrb.class)).setExperience(1);
			}
		}
		else if(blockType.equals(Material.GOLD_ORE))
		{
			if(itemInHand.equals(ironPickaxe) || itemInHand.equals(goldPickaxe) || itemInHand.equals(diamondPickaxe)) 
			{
				e.setDropItems(false);
				
				world.dropItem(blockLoc, new ItemStack(Material.GOLD_INGOT, 1));
				((ExperienceOrb)world.spawn(blockLoc, ExperienceOrb.class)).setExperience(1);
			}
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) 
	{
		if(!game.isStarted()) {return;}
		game.applySpeed(e.getPlayer());
		
		UZPlayer uzp = game.thePlayer(e.getPlayer());
		UZTeam uzt = uzp.getTeam();
		
		UZTriforce strenght = game.getStrenght();
		UZTriforce courage = game.getCourage();
		UZTriforce wisdom = game.getWisdom();
		
		Region middle = game.getRegion();
		
		boolean allTriforceInMiddle = false;
		if(middle.locationIsInRegion(strenght.getLocation()) && middle.locationIsInRegion(courage.getLocation()) && middle.locationIsInRegion(wisdom.getLocation()))
			allTriforceInMiddle = true;
		else
			allTriforceInMiddle = false;
		
		boolean teamGotAllTriforce = false;
		if(strenght.getHolders() == courage.getHolders() && courage.getHolders() == wisdom.getHolders()) 
		{
			if(strenght.getHolders() == null || courage.getHolders() == null || wisdom.getHolders() == null) 
				teamGotAllTriforce = false;
			else 
				teamGotAllTriforce = true;
		}
		else 
		{
			teamGotAllTriforce = false;
		}
		
		if(teamGotAllTriforce) 
		{
			if(allTriforceInMiddle) 
			{
				if(!game.isAssemblyStarted() && !game.isAssemblyFinished()) 
				{
					game.setAssemblyStarted(true);
					game.setTeamAssembling(strenght.getHolders());
					
					game.setCurrentTriforceCooldown(System.currentTimeMillis() + (game.getTriforceCooldown() * 1000));
					
					game.broadcastMessage("§6§lZelda §a>> §f§lL'équipe " + game.getTeamAssembling().getColor() + "§l" + game.getTeamAssembling().getName() + " §f§là commencé l'assemblage des §6§lTriforces");
					
					game.assemblingTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(UZMain.getInstance(), new Runnable() 
					{
						@Override
						public void run() 
						{
							float x = 0.5f;
							if(game.getCurrentTriforceCooldown() > System.currentTimeMillis()) 
							{
								x += 0.0083;
								
								for(UZPlayer u : game.getInGame()) 
								{
									u.playSound(Sound.BLOCK_NOTE_BLOCK_CHIME, 3f, x);
								}
								
								game.setTriforceTimeLeft((game.getCurrentTriforceCooldown() - System.currentTimeMillis()) / 1000);
								
								if(game.getTriforceTimeLeft() == 60 || game.getTriforceTimeLeft() == 45 || game.getTriforceTimeLeft() == 30 || game.getTriforceTimeLeft() == 15) 
									game.broadcastMessage("§6§lZelda §a>> §7L'assemblage des Triforces est fini dans §a§l" + game.getTriforceTimeLeft() + " §7secondes");
								
								Location particleLoc = new Location(Bukkit.getWorld("world2"), -15.5, 66, 83);
								new BukkitRunnable() 
								{
									double phi = 0;
									public void run() 
									{
										phi += Math.PI/10;
										for(double theta = 0; theta <= 2*Math.PI; theta += Math.PI/40) 
										{
											double r = 4.5;
											double x = r*Math.cos(theta)*Math.sin(phi);
											double y = r*Math.cos(phi) + 1.5;
											double z = r*Math.sin(theta)*Math.sin(phi);
											particleLoc.add(x, y, z);
											particleLoc.getWorld().spawnParticle(Particle.FLAME, particleLoc, 0, 0, 0, 0, 1);
											particleLoc.subtract(x,y,z);
										}
										if(phi > 2*Math.PI) 
										{
											this.cancel();
										}
									}
								}.runTaskTimer(UZMain.getInstance(), 0, 1);
							}
							else if(game.getCurrentTriforceCooldown() <= System.currentTimeMillis()) 
							{
								game.setAssemblyFinished(true);
								
								for(UZPlayer u : game.getInGame()) 
								{
									u.playSound(Sound.UI_TOAST_CHALLENGE_COMPLETE, 3.0f, 0.5f);
								}
								
								World world = Bukkit.getWorld("world2");
								world.getBlockAt(-15, 66, 83).setType(Material.PINK_STAINED_GLASS);
								world.getBlockAt(-16, 67, 83).setType(Material.PINK_STAINED_GLASS);
								world.getBlockAt(-17, 66, 83).setType(Material.PINK_STAINED_GLASS);
								
								game.preEnd(game.getTeamAssembling());
								
								Bukkit.getScheduler().cancelTask(game.assemblingTask);
							}
						}		
					}, 1L, (long) 20);
				}
			}
			else if (!allTriforceInMiddle && game.isAssemblyStarted() && !game.isAssemblyFinished()) 
			{
				Bukkit.getScheduler().cancelTask(game.assemblingTask);
				
				game.broadcastMessage("§6§lZelda §a>> §7L'assemblage des Triforces est §a§lannulé §7!");
				
				game.setAssemblyStarted(false);
				game.setTeamAssembling(null);
			}
		}
		else if (!teamGotAllTriforce && game.isAssemblyStarted() && !game.isAssemblyFinished()) 
		{
			Bukkit.getScheduler().cancelTask(game.assemblingTask);
			
			game.broadcastMessage("§6§lZelda §a>> §7L'assemblage des Triforces est §a§lannulé §7!");
			
			game.setAssemblyStarted(false);
			game.setTeamAssembling(null);
		}
	}
}




























