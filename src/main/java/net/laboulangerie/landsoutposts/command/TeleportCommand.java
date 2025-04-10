package net.laboulangerie.landsoutposts.command;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.angeschossen.lands.api.player.LandPlayer;
import net.laboulangerie.landsoutposts.LandsOutposts;
import net.laboulangerie.landsoutposts.LandsOutpostsConfiguration;
import net.laboulangerie.landsoutposts.LandsOutpostsLanguage;
import net.laboulangerie.landsoutposts.database.LandOutpost;

public class TeleportCommand {

    private HashMap<UUID,Long> commandCooldown;

    private TeleportCommand(LandsOutposts landsOutposts) {
        this.commandCooldown = new HashMap<>();
        landsOutposts.getServer().getScheduler().runTaskTimerAsynchronously(landsOutposts, () -> {
            LandsOutposts.debugMsg("Cooldown cleanup task.");
            for (Map.Entry<UUID,Long> entry : commandCooldown.entrySet()) {
                if ((System.currentTimeMillis() - entry.getValue()) > (LandsOutpostsConfiguration.CONF.outpostsTeleportCooldown * 1000)) {
                    commandCooldown.remove(entry.getKey());
                }
            } 
        }, 72000, 72000);
    }

    public static final LiteralArgumentBuilder<CommandSourceStack> command(LandsOutposts landsOutposts) {
        TeleportCommand tp = new TeleportCommand(landsOutposts);

        return Commands.literal("tp").requires(sender -> sender.getSender() instanceof Player)
        .then(Commands.argument("outpost", IntegerArgumentType.integer(1))
            .executes(ctx -> {
                Player player = (Player) ctx.getSource().getSender();
                long cooldown = System.currentTimeMillis() - tp.commandCooldown.getOrDefault(player.getUniqueId(), System.currentTimeMillis());

                if (cooldown > (LandsOutpostsConfiguration.CONF.outpostsTeleportCooldown * 1000)) {
                    tp.commandCooldown.put(player.getUniqueId(), System.currentTimeMillis());

                    LandPlayer landPlayer = landsOutposts.getLands().getLandPlayer(player.getUniqueId());
                    int outpost = ctx.getArgument("outpost", int.class);
                    try {
                        List<LandOutpost> outposts = landsOutposts.getLandPlayerOutposts(landPlayer);
                        LandOutpost landOutpost = outposts.get((outpost - 1));
                        if (landOutpost != null) {
                            player.teleport(landOutpost.getSpawn());
                        } else {
                            player.sendRichMessage(LandsOutposts.LANDSOUTPOSTS_BASE_MSG + LandsOutpostsLanguage.LANG.outpostNotFound);
                        }
                    } catch (SQLException e) {
                        player.sendRichMessage(LandsOutposts.UNEXPECTED_EXCEPTION_MSG);
                        e.printStackTrace();
                    }
                } else {
                    player.sendRichMessage(LandsOutposts.LANDSOUTPOSTS_BASE_MSG + LandsOutpostsLanguage.LANG.teleportCooldown.replace("%wait", String.valueOf(cooldown * 1000)));
                }

                return Command.SINGLE_SUCCESS;
            })
        );
    }
}
