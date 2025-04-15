package net.laboulangerie.landsoutposts.command;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.angeschossen.lands.api.land.Land;
import me.angeschossen.lands.api.player.LandPlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.laboulangerie.landsoutposts.LandsOutposts;
import net.laboulangerie.landsoutposts.LandsOutpostsLanguage;
import net.laboulangerie.landsoutposts.database.LandOutpost;

public class ListCommand {

    private final LandsOutposts landsOutposts;
    private ListCommand(LandsOutposts landsOutposts) {
        this.landsOutposts = landsOutposts;
    }

    public static final LiteralArgumentBuilder<CommandSourceStack> command(LandsOutposts landsOutposts) {
        ListCommand cmd = new ListCommand(landsOutposts);

        return Commands.literal("list")
        .requires(sender -> sender.getSender() instanceof Player)
        .executes(ctx -> {
            Player player = (Player) ctx.getSource().getSender();
            LandPlayer landPlayer = landsOutposts.getLands().getLandPlayer(player.getUniqueId());

            cmd.executes(landPlayer, landPlayer.getLands());

            return Command.SINGLE_SUCCESS;
        })
        .then(Commands.argument("land", StringArgumentType.greedyString()).executes(ctx -> {
            Player player = (Player) ctx.getSource().getSender();
            LandPlayer landPlayer = landsOutposts.getLands().getLandPlayer(player.getUniqueId());
            String landName = ctx.getArgument("land", String.class);

            List<? extends Land> lands = landPlayer.getLands().stream().filter(land -> {
                return land.getName().equals(landName);
            }).toList();

            if (lands.isEmpty()) {
                player.sendRichMessage(LandsOutposts.LANDSOUTPOSTS_BASE_MSG + LandsOutpostsLanguage.LANG.landNotFound.replace("%name", landName));
            } else {
                cmd.executes(landPlayer, lands);
            }           

            return Command.SINGLE_SUCCESS;
        }).suggests((ctx, builder) -> CompletableFuture.supplyAsync(() -> {
            if (ctx.getSource().getSender() instanceof Player player) {
                LandPlayer landPlayer = landsOutposts.getLands().getLandPlayer(player.getUniqueId());
                landPlayer.getLands().forEach(land -> {
                    if (builder.getRemaining().isEmpty() || land.getName().startsWith(builder.getRemaining())) {
                        builder.suggest(land.getName());
                    }
                });
            }

            return builder.build();
        })));
    }

    private final void executes(LandPlayer landPlayer, Collection<? extends Land> lands) {
        Player player = landPlayer.getPlayer();
        try {
            HashMap<String,LandOutpost> outposts = this.landsOutposts.getPlayerLandsOutposts(landPlayer, false);
            for (Land land : lands) {
                player.sendRichMessage("<dark_gray>____.[</dark_gray> <dark_green>" + LandsOutpostsLanguage.LANG.outposts + ":</dark_green> "
                + MiniMessage.miniMessage().serialize(LegacyComponentSerializer.legacySection().deserialize(land.getColorName()))
                + "<dark_gray>].____</dark_gray>");

                outposts.forEach((name, landOutpost) -> {
                    if (landOutpost.getLandId().equals(land.getULID())) {
                        Optional<String> outpostName = this.landsOutposts.getLandOutpostName(landOutpost);
                        Location outpostLocation = landOutpost.getSpawn();
                        String msg = "<hover:show_text:'" + LandsOutpostsLanguage.LANG.clickToTeleport + "'><click:run_command:'/lands-outposts tp " + name + "'><dark_green>" + name + "</dark_green> ";
                        if (outpostName.isPresent()) {
                            msg += "<dark_gray>-</dark_gray> " + MiniMessage.miniMessage().serialize(LegacyComponentSerializer.legacySection().deserialize(this.landsOutposts.getLandOutpostColorName(landOutpost, player).get()))
                            + " ";
                        }
                        player.sendRichMessage(msg + "<dark_gray>-</dark_gray> <blue>"
                            + outpostLocation.getWorld().getName()
                            + "</blue> <dark_gray>-</dark_gray> <blue>(" 
                            + (int) outpostLocation.getX() + "," + (int) outpostLocation.getY() + "," + (int) outpostLocation.getZ() 
                            + ")</blue></click></hover>");
                    }
                });
            }
        } catch (Exception e) {
            player.sendRichMessage(LandsOutposts.UNEXPECTED_EXCEPTION_MSG);
            e.printStackTrace();
        }
    }
}
