package net.laboulangerie.landsoutposts.command;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.angeschossen.lands.api.land.Land;
import me.angeschossen.lands.api.player.LandPlayer;
import net.laboulangerie.landsoutposts.LandsOutposts;
import net.laboulangerie.landsoutposts.LandsOutpostsLanguage;
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
            try {
                int index = 1;
                for (Land land : landPlayer.getLands()) {
                    player.sendRichMessage("<dark_gray>____.[</dark_gray> <dark_green>" + LandsOutpostsLanguage.LANG.outposts + ":</dark_green> " + land.getColorName() + "<dark_gray>].___</dark_gray>");
                    List<LandOutpost> outposts = landsOutposts.getLandOutposts(land);
                    for (Iterator<LandOutpost> it = outposts.iterator(); it.hasNext(); index++) {
                        LandOutpost landOutpost = it.next();
                        String outpostName = landsOutposts.getLandOutpostName(landOutpost);
                        Location outpostLocation = landOutpost.getSpawn();
                        String msg = "<hover:show_text:'" + LandsOutpostsLanguage.LANG.clickToTeleport + "'><click:run_command:'/lands-outposts tp " + index + "'><dark_green>" + index + "</dark_green> ";
                        if (outpostName != null) {
                            player.sendRichMessage("<dark_gray>-</dark_gray> name ");
                        }
                        player.sendRichMessage(msg + "<dark_gray>-</dark_gray> <blue>"
                            + outpostLocation.getWorld().getName()
                            + "</blue> <dark_gray>-</dark_gray> <blue>(" 
                            + (int) outpostLocation.getX() + "," + (int) outpostLocation.getY() + "," + (int) outpostLocation.getZ() 
                            + ")</blue></click></hover>");
                    }
                }
            } catch (Exception e) {
                player.sendRichMessage(LandsOutposts.UNEXPECTED_EXCEPTION_MSG);
                e.printStackTrace();
            }

            return Command.SINGLE_SUCCESS;
        });
    }
}
