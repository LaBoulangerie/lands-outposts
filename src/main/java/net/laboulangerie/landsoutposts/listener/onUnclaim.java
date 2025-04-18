package net.laboulangerie.landsoutposts.listener;

import java.sql.SQLException;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.angeschossen.lands.api.events.ChunkDeleteEvent;
import net.laboulangerie.landsoutposts.LandsOutposts;
import net.laboulangerie.landsoutposts.database.LandOutpost;

public class onUnclaim implements Listener {
    private LandsOutposts landsOutposts;

    public onUnclaim(LandsOutposts landsOutposts) {
        this.landsOutposts = landsOutposts;
    }

    @EventHandler
    public void onUnclaimEvent(ChunkDeleteEvent event) {
        Chunk chunk = event.getLandPlayer().getPlayer().getWorld().getChunkAt(event.getLandPlayer().getPlayer().getLocation());
        List<LandOutpost> outposts = null;

        try {
            outposts = landsOutposts.getLandOutposts(event.getLand());
        } catch (SQLException e) {
            LandsOutposts.LOGGER.warning("Error while getting outposts for land " + event.getLand().getName() + " in " + event.getLandPlayer().getPlayer().getLocation() + ".");
            e.printStackTrace();
        }

        for(LandOutpost outpost : outposts){
            if(outpost.getSpawn().getChunk().equals(chunk)){
                try {
                    landsOutposts.getDatabase().getOutpostsDao().delete(outpost);
                } catch (SQLException e) {
                    LandsOutposts.LOGGER.warning(event.getLandPlayer().getName() + " " + event.getLandPlayer().getPlayer().getLocation() + " outpost deletion error.");
                    e.printStackTrace();
                }
            }
        }
    }
}
