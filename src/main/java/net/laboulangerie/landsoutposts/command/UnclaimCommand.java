package net.laboulangerie.landsoutposts.command;

import java.sql.SQLException;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.angeschossen.lands.api.LandsIntegration;
import net.laboulangerie.landsoutposts.LandsOutposts;
import net.laboulangerie.landsoutposts.LandsOutpostsLanguage;
import net.laboulangerie.landsoutposts.database.LandOutpost;

public class UnclaimCommand {
    public static final LiteralArgumentBuilder<CommandSourceStack> command(LandsOutposts landsOutposts) {
        return Commands.literal("unclaim")
            .requires(sender -> sender.getSender() instanceof Player)
            .executes(ctx -> {
                LandsIntegration lands = LandsOutposts.instance.getLands();
                Player player = (Player) ctx.getSource().getSender();
                Chunk chunk = player.getWorld().getChunkAt(player.getLocation());
                List<LandOutpost> outposts = null;
                try {
                    outposts = landsOutposts.getLandOutposts(lands.getLandByChunk(chunk.getWorld(), chunk.getX(), chunk.getZ()));
                } catch (Exception e) {
                    LandsOutposts.LOGGER.warning("Error while getting outposts for land " + lands.getLandByChunk(chunk.getWorld(), chunk.getX(), chunk.getZ()).getName() + " in " + player.getLocation() + ".");
                    e.printStackTrace();
                }
                
                for(LandOutpost outpost : outposts){
                    if(outpost.getSpawn().getChunk().equals(chunk)){
                        try {
                            landsOutposts.getDatabase().getOutpostsDao().delete(outpost);
                        } catch (SQLException e) {
                            player.sendMessage(lands.getLandByChunk(chunk.getWorld(), chunk.getX(), chunk.getZ()).getName() + " " + player.getLocation() + " outpost deletion error.");
                            e.printStackTrace();
                        }
                        player.sendRichMessage(LandsOutposts.LANDSOUTPOSTS_BASE_MSG + LandsOutpostsLanguage.LANG.outpostDeleted);
                        return Command.SINGLE_SUCCESS;
                    }
                }
                
                player.sendRichMessage(LandsOutposts.LANDSOUTPOSTS_BASE_MSG + LandsOutpostsLanguage.LANG.noOutpostInChunk);
                return Command.SINGLE_SUCCESS;
            });
    }
}
