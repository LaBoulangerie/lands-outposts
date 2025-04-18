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

public class InfoCommand {
    public static final LiteralArgumentBuilder<CommandSourceStack> command(LandsOutposts landsOutposts) {
        return Commands.literal("info")
            .requires(sender -> sender.getSender() instanceof Player)
            .executes(ctx -> {
                Player player = (Player) ctx.getSource().getSender();
                List<LandOutpost> outposts = null;

                try {
                    outposts = landsOutposts.getDatabase().getOutpostsDao().queryForAll();
                } catch (Exception e) {
                    player.sendRichMessage(LandsOutposts.UNEXPECTED_EXCEPTION_MSG);
                    e.printStackTrace();
                }
                
                for(LandOutpost outpost : outposts){
                    player.sendRichMessage(outpost.getSpawn().getX() + " " + outpost.getSpawn().getZ());
                }

                return Command.SINGLE_SUCCESS;
            });
    }
}
