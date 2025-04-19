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
import me.angeschossen.lands.api.flags.type.Flags;
import me.angeschossen.lands.api.land.Land;
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
                Land land = lands.getLandByChunk(chunk.getWorld(), chunk.getX(), chunk.getZ());
                List<LandOutpost> outposts = null;

                if(land == null) {
                    player.sendRichMessage(LandsOutposts.LANDSOUTPOSTS_BASE_MSG + LandsOutpostsLanguage.LANG.chunkNotInLand);
                    return Command.SINGLE_SUCCESS;
                }

                if(!land.getDefaultArea().hasRoleFlag(player.getUniqueId(), Flags.LAND_CLAIM)) {
                    Flags.LAND_CLAIM.sendDenied(lands.getLandPlayer(player.getUniqueId()), land.getDefaultArea());
                    return Command.SINGLE_SUCCESS;
                }

                try {
                    outposts = landsOutposts.getLandOutposts(land);
                } catch (Exception e) {
                    LandsOutposts.LOGGER.warning("Error while getting outposts for land " + land.getName() + " in " + player.getLocation() + ".");
                    player.sendRichMessage(LandsOutposts.UNEXPECTED_EXCEPTION_MSG);
                    e.printStackTrace();
                }
                
                for(LandOutpost outpost : outposts){
                    if(outpost.getSpawn().getChunk().equals(chunk)){
                        try {
                            landsOutposts.getDatabase().getOutpostsDao().delete(outpost);
                        } catch (SQLException e) {
                            LandsOutposts.LOGGER.warning(land.getName() + " " + player.getLocation() + " outpost deletion error.");
                            player.sendRichMessage(LandsOutposts.UNEXPECTED_EXCEPTION_MSG);
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
