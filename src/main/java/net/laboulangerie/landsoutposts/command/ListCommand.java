package net.laboulangerie.landsoutposts.command;

import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.angeschossen.lands.api.land.Land;
import me.angeschossen.lands.api.player.LandPlayer;
import net.laboulangerie.landsoutposts.LandsOutposts;
import net.laboulangerie.landsoutposts.database.LandOutpost;

public class ListCommand {

    private ListCommand() {
        throw new IllegalStateException("Utility class");
    }

    public static final LiteralArgumentBuilder<CommandSourceStack> command(LandsOutposts landsOutposts) {
        return Commands.literal("list")
        .requires(sender -> sender.getSender() instanceof Player)
        .executes(ctx -> {
            Player player = (Player) ctx.getSource().getSender();
            LandPlayer landPlayer = landsOutposts.getLands().getLandPlayer(player.getUniqueId());
            
            int index = 0;
            for (Land land : landPlayer.getLands()) {
                List<LandOutpost> outposts = landsOutposts.getLandOutposts(land);
                for (Iterator<LandOutpost> it = outposts.iterator(); it.hasNext(); index++) {
                    // TODO
                }                
            }

            return Command.SINGLE_SUCCESS;
        });
    }
}
