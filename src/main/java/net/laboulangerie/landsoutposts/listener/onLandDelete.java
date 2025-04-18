package net.laboulangerie.landsoutposts.listener;

import java.sql.SQLException;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.angeschossen.lands.api.events.LandDeleteEvent;

import net.laboulangerie.landsoutposts.LandsOutposts;
import net.laboulangerie.landsoutposts.database.LandOutpost;

public class onLandDelete implements Listener {
    private LandsOutposts landsOutposts;

    public onLandDelete(LandsOutposts landsOutposts) {
        this.landsOutposts = landsOutposts;
    }

    @EventHandler
    public void onLandDeleteEvent(LandDeleteEvent event) {
        List<LandOutpost> outposts = null;

        try {
            outposts = landsOutposts.getLandOutposts(event.getLand());
        } catch (SQLException e) {
            LandsOutposts.LOGGER.warning("Error while getting outposts for land " + event.getLand().getName() + " in " + event.getLandPlayer().getPlayer().getLocation() + ".");
            e.printStackTrace();
        }

        for(LandOutpost outpost : outposts) {
            try {
                landsOutposts.getDatabase().getOutpostsDao().delete(outpost);
            } catch (SQLException e) {
                LandsOutposts.LOGGER.warning(event.getLand().getName() + " " + event.getLandPlayer().getPlayer().getLocation() + " outpost deletion error.");
                e.printStackTrace();
            }
        }
    }
}
