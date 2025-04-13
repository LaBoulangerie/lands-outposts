package net.laboulangerie.landsoutposts.command;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
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
        .then(Commands.argument("outpost", StringArgumentType.greedyString())
            .executes(ctx -> {
                Player player = (Player) ctx.getSource().getSender();
                long cooldown = System.currentTimeMillis() - tp.commandCooldown.getOrDefault(player.getUniqueId(), (long) 0);

                if (cooldown > (LandsOutpostsConfiguration.CONF.outpostsTeleportCooldown * 1000)) {
                    tp.commandCooldown.put(player.getUniqueId(), System.currentTimeMillis());

                    LandPlayer landPlayer = landsOutposts.getLands().getLandPlayer(player.getUniqueId());
                    String outpost = ctx.getArgument("outpost", String.class);
                    try {
                        LandOutpost landOutpost = landsOutposts.getPlayerLandsOutposts(landPlayer).get(outpost);
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
                    player.sendRichMessage(LandsOutposts.LANDSOUTPOSTS_BASE_MSG + LandsOutpostsLanguage.LANG.teleportCooldown.replace("%wait", String.valueOf(LandsOutpostsConfiguration.CONF.outpostsTeleportCooldown - (cooldown / 1000))));
                }

                return Command.SINGLE_SUCCESS;
            })
            .suggests((ctx, builder) -> CompletableFuture.supplyAsync(() -> {
                if (ctx.getSource().getSender() instanceof Player player) {
                    LandPlayer landPlayer = landsOutposts.getLands().getLandPlayer(player.getUniqueId());
                    try {
                        HashMap<String, LandOutpost> playerOutposts = landsOutposts.getPlayerLandsOutposts(landPlayer);
                        playerOutposts.forEach((name, outpost) -> {
                            if (builder.getRemaining().isEmpty() || name.startsWith(builder.getRemaining())) {
                                builder.suggest(name);
                            }
                        });
                        landPlayer.getLands().forEach(land -> {

                        });
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                return builder.build();
            }))
        );
    }
}
