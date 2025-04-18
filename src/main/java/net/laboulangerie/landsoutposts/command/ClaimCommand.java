package net.laboulangerie.landsoutposts.command;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.angeschossen.lands.api.flags.type.Flags;
import me.angeschossen.lands.api.land.Land;
import me.angeschossen.lands.api.player.LandPlayer;
import net.laboulangerie.landsoutposts.LandsOutposts;
import net.laboulangerie.landsoutposts.LandsOutpostsLanguage;

public class ClaimCommand {

    private final LandsOutposts landsOutposts;
    
    private ClaimCommand(LandsOutposts landsOutposts) {
        this.landsOutposts = landsOutposts;
    }

    public static final LiteralArgumentBuilder<CommandSourceStack> command(LandsOutposts landsOutposts) {
        ClaimCommand cmd = new ClaimCommand(landsOutposts);

        return Commands.literal("list")
        .requires(sender -> sender.getSender() instanceof Player)
        .executes(ctx -> {
            Player player = (Player) ctx.getSource().getSender();
            LandPlayer landPlayer = landsOutposts.getLands().getLandPlayer(player.getUniqueId());
            Collection<? extends Land> lands = landPlayer.getLands();

            if (lands.isEmpty()) {
                player.sendRichMessage(LandsOutposts.LANDSOUTPOSTS_BASE_MSG + LandsOutpostsLanguage.LANG.notInALand);
            } else if (lands.size() == 1) {
                cmd.executes(landPlayer, lands.stream().findFirst().get());
            } else {
                player.sendRichMessage(LandsOutposts.LANDSOUTPOSTS_BASE_MSG + "/lands-outposts claim <aqua><land></aqua>");
            }

            return Command.SINGLE_SUCCESS;
        })
        .then(Commands.argument("land", StringArgumentType.greedyString()).executes(ctx -> {
            Player player = (Player) ctx.getSource().getSender();
            LandPlayer landPlayer = landsOutposts.getLands().getLandPlayer(player.getUniqueId());

            String landName = ctx.getArgument("land", String.class);
            Optional<? extends Land> landOptional = landPlayer.getLands().stream().filter(land -> land.getName().equals(landName)).findFirst();

            if (landOptional.isEmpty()) {
                player.sendRichMessage(LandsOutposts.LANDSOUTPOSTS_BASE_MSG + LandsOutpostsLanguage.LANG.landNotFound.replace("%name", landName));
            } else {
                cmd.executes(landPlayer, landOptional.get());
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

    private final void executes(LandPlayer landPlayer, Land land) {
        Player player = landPlayer.getPlayer();
        Chunk playerLocationChunk = player.getLocation().getChunk();

        Land claimedLand = this.landsOutposts.getLands().getLandByChunk(playerLocationChunk.getWorld(), playerLocationChunk.getX(), playerLocationChunk.getZ());
        if (claimedLand == null || land.equals(claimedLand)) {
            if (land.getDefaultArea().hasRoleFlag(player.getUniqueId(), Flags.LAND_CLAIM) && land.getChunksAmount() < land.getMaxChunks()){
                //TODO claim dispo et outpost dispo ?
            } else {
                Flags.LAND_CLAIM.sendDenied(landPlayer, land.getDefaultArea());
            }
        } else {
            //TODO this chunk is own by another land
        }
    }
}
