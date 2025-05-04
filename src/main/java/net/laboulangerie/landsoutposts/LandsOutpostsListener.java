package net.laboulangerie.landsoutposts;

import java.sql.SQLException;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.angeschossen.lands.api.events.ChunkDeleteEvent;
import me.angeschossen.lands.api.events.LandDeleteEvent;
import me.angeschossen.lands.api.player.LandPlayer;
import net.laboulangerie.landsoutposts.database.LandOutpost;

public class LandsOutpostsListener implements Listener {

    private LandsOutposts landsOutposts;

    public LandsOutpostsListener(LandsOutposts landsOutposts) {
        this.landsOutposts = landsOutposts;
    }

    @EventHandler
    public void onLandDeleteEvent(LandDeleteEvent event) {
        List<LandOutpost> outposts;
        try {
            outposts = landsOutposts.getLandOutposts(event.getLand());
        } catch (SQLException e) {
            LandsOutposts.LOGGER.warning("Error while getting outposts for land " + event.getLand().getName() + ".");
            e.printStackTrace();
            return;
        }

        for(LandOutpost outpost : outposts) {
            try {
                if (landsOutposts.getDatabase().getOutpostsDao().delete(outpost) != 0) {
                    LandsOutposts.debugMsg(event.getLand().getName() + " " + outpost.getSpawn() + " outpost deleted.");
                }
            } catch (SQLException e) {
                LandsOutposts.LOGGER.warning(event.getLand().getName() + " " + outpost.getSpawn() + " outpost deletion error.");
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onUnclaimEvent(ChunkDeleteEvent event) {
        LandPlayer landPlayer = event.getLandPlayer();
        event.getWorld().getChunkAtAsync(event.getX(), event.getZ()).thenAccept(chunk -> {
            List<LandOutpost> outposts;
            try {
                outposts = landsOutposts.getLandOutposts(event.getLand());
            } catch (SQLException e) {
                LandsOutposts.LOGGER.warning("Error while getting outposts for land " + event.getLand().getName() + ".");
                e.printStackTrace();
                return;
            }

            for(LandOutpost outpost : outposts){
                if(outpost.getSpawn().getChunk().equals(chunk)){
                    try {
                        if (landsOutposts.getDatabase().getOutpostsDao().delete(outpost) != 0) {
                            LandsOutposts.debugMsg(event.getLand().getName() + " " + outpost.getSpawn() + " outpost deleted.");
                            if (landPlayer != null) {
                                landPlayer.getPlayer().sendRichMessage(LandsOutposts.LANDSOUTPOSTS_BASE_MSG + LandsOutpostsLanguage.LANG.outpostDeleted);
                            }
                        }
                    } catch (SQLException e) {
                        LandsOutposts.LOGGER.warning(event.getLandPlayer().getName() + " " + outpost.getSpawn() + " outpost deletion error.");
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
