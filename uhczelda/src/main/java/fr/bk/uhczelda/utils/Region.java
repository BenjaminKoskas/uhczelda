package fr.bk.uhczelda.utils;

import java.util.UUID;

import org.bukkit.Location;

public class Region 
{
	private UUID worldUniqueId;

    private double maxX;
    private double maxY;
    private double maxZ;

    private double minX;
    private double minY;
    private double minZ;


    public Region(Location firstPoint, Location secondPoint) {
        worldUniqueId = firstPoint.getWorld().getUID();

        maxX = Math.max(firstPoint.getX(), secondPoint.getX());
        maxY = Math.max(firstPoint.getY(), secondPoint.getY());
        maxZ = Math.max(firstPoint.getZ(), secondPoint.getZ());

        minX = Math.min(firstPoint.getX(), secondPoint.getX());
        minY = Math.min(firstPoint.getY(), secondPoint.getY());
        minZ = Math.min(firstPoint.getZ(), secondPoint.getZ());
    }

    public boolean locationIsInRegion(Location loc) {
        return loc.getWorld().getUID().equals(worldUniqueId)
                && loc.getX() > minX && loc.getX() < maxX
                && loc.getY() > minY && loc.getY() < maxY
                && loc.getZ() > minZ && loc.getZ() < maxZ;
    }
}
