package net.laboulangerie.landsoutposts.database;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import me.angeschossen.lands.api.applicationframework.util.ULID;

@DatabaseTable(tableName = "lands_outposts")
public class LandOutpost {

    public LandOutpost() {}

    public LandOutpost(ULID ulid, Location spawn) {
        this.landId = ulid.toString();
        
        this.setSpawn(spawn);
    }

    @DatabaseField(columnName = "outpost_id", generatedId = true)
    private long id;

    @DatabaseField(columnName = "land_id", canBeNull = false)
    private String landId;

    @DatabaseField(columnName = "world", canBeNull = false)
    private String world;
    @DatabaseField(columnName = "spawn_x", canBeNull = false)
    private double spawnX;
    @DatabaseField(columnName = "spawn_y", canBeNull = false)
    private double spawnY;
    @DatabaseField(columnName = "spawn_z", canBeNull = false)
    private double spawnZ;

    public long getId() {
        return this.id;
    }

    public ULID getLandId() {
        return ULID.fromString(this.landId);
    }

    public Location getSpawn() {
        return new Location(Bukkit.getWorld(this.world), this.spawnX, this.spawnY, this.spawnZ);
    }

    public void setSpawn(Location spawn) {
        this.world = spawn.getWorld().getName();
        this.spawnX = spawn.getX();
        this.spawnY = spawn.getY();
        this.spawnZ = spawn.getZ();
    }

}
