package fr.bk.uhczelda.classes;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.MinecraftServer;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_15_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_15_R1.PlayerConnection;
import net.minecraft.server.v1_15_R1.PlayerInteractManager;
import net.minecraft.server.v1_15_R1.WorldServer;

public class UZNpc 
{
	@Getter @Setter private static List<EntityPlayer> NPCs = new ArrayList<EntityPlayer>();
	
	public static void createNPC(Location npcLoc, String npcName, String skin) 
	{
		MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
		WorldServer world = ((CraftWorld) Bukkit.getWorld(npcLoc.getWorld().getName())).getHandle();
		GameProfile gameProfile = new GameProfile(UUID.randomUUID(), npcName);
		EntityPlayer npc = new EntityPlayer(server, world, gameProfile, new PlayerInteractManager(world));
		npc.setLocation(npcLoc.getX(), npcLoc.getY(), npcLoc.getZ(), npcLoc.getYaw(), npcLoc.getPitch());
		
		String[] name = getSkin(skin);
		gameProfile.getProperties().put("textures", new Property("textures", name[0], name[1]));
		
		addNPCPacket(npc);
		NPCs.add(npc);
	}
	
	private static String[] getSkin(String name) 
	{
		try 
		{
			URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
			InputStreamReader reader = new InputStreamReader(url.openStream());
			String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();
			
			URL url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
			InputStreamReader reader2 = new InputStreamReader(url2.openStream());
			JsonObject property = new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
			String texture = property.get("value").getAsString();
			String signature = property.get("signature").getAsString();
			
			return new String[] {texture, signature};
		} 
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			
			return new String[] {null};
		}
	}
	
	public static void addNPCPacket(EntityPlayer npc) 
	{
		for(Player player : Bukkit.getOnlinePlayers()) 
		{
			PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
			connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
			connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
			connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));
		}
	}
	
	public static void removeNPCsPacket()
	{
		for(Player player : Bukkit.getOnlinePlayers()) 
		{
			PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
			for(EntityPlayer npc : NPCs) 
			{
				connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
				connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
			}
		}
	}
	
	public static void clearNPCs() {
		NPCs.clear();
	}
	
	public static void addJoinPacket(Player player) 
	{
		for(EntityPlayer npc : NPCs) 
		{
			PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
			connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
			connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
			connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));
		}
	}
}
