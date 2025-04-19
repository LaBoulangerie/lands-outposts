package net.laboulangerie.landsoutposts.command;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.angeschossen.lands.api.flags.type.Flags;
import me.angeschossen.lands.api.land.Land;
import me.angeschossen.lands.api.player.LandPlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.laboulangerie.landsoutposts.LandsOutposts;
import net.laboulangerie.landsoutposts.LandsOutpostsConfiguration;
import net.laboulangerie.landsoutposts.LandsOutpostsLanguage;
import net.laboulangerie.landsoutposts.database.LandOutpost;

public class ClaimCommand {

    private final LandsOutposts landsOutposts;
    
    private ClaimCommand(LandsOutposts landsOutposts) {
        this.landsOutposts = landsOutposts;
    }

    public static final LiteralArgumentBuilder<CommandSourceStack> command(LandsOutposts landsOutposts) {
        ClaimCommand cmd = new ClaimCommand(landsOutposts);

        return Commands.literal("claim")
        .requires(sender -> sender.getSender() instanceof Player)
        .executes(ctx -> {
            Player player = (Player) ctx.getSource().getSender();
            LandPlayer landPlayer = landsOutposts.getLands().getLandPlayer(player.getUniqueId());
            Collection<? extends Land> lands = landPlayer.getLands();

            if (lands.isEmpty()) {
                player.sendRichMessage(LandsOutposts.LANDSOUTPOSTS_BASE_MSG + LandsOutpostsLanguage.LANG.notInALand);
            } else if (lands.size() == 1) {
                try {
                    cmd.executes(landPlayer, lands.stream().findFirst().get());
                } catch (SQLException e) {
                    player.sendRichMessage(LandsOutposts.UNEXPECTED_EXCEPTION_MSG);
                    e.printStackTrace();
                }
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
                try {
                    cmd.executes(landPlayer, landOptional.get());
                } catch (SQLException e) {
                    player.sendRichMessage(LandsOutposts.UNEXPECTED_EXCEPTION_MSG);
                    e.printStackTrace();
                }
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

    private final void executes(LandPlayer landPlayer, Land land) throws SQLException {
        Player player = landPlayer.getPlayer();
        Location playerLocation = player.getLocation();
        Chunk chunk = playerLocation.getChunk();

        Land claimedLand = this.landsOutposts.getLands().getLandByChunk(chunk.getWorld(), chunk.getX(), chunk.getZ());
        if (claimedLand == null || land.equals(claimedLand)) {
            if (land.getDefaultArea().hasRoleFlag(player.getUniqueId(), Flags.LAND_CLAIM)){
                int landMaxClaim = land.getMaxChunks();
                int landMaxOutposts = this.landsOutposts.getLandMaxOutposts(land);
                
                if (land.getChunksAmount() >= landMaxClaim) {
                    player.sendRichMessage(LandsOutposts.LANDSOUTPOSTS_BASE_MSG + LandsOutpostsLanguage.LANG.maxChunks.replace("%max", String.valueOf(landMaxClaim)));
                } else if (this.landsOutposts.getLandOutposts(land).size() >= landMaxOutposts) {
                    player.sendRichMessage(LandsOutposts.LANDSOUTPOSTS_BASE_MSG + LandsOutpostsLanguage.LANG.maxOutposts.replace("%max", String.valueOf(landMaxOutposts)));
                } else {
                    int outpostCost = LandsOutpostsConfiguration.CONF.outpostsCost;
                    if (land.modifyBalance(Math.negateExact(outpostCost))) {
                        LandOutpost outpost = new LandOutpost(land.getULID(), playerLocation);
                        this.landsOutposts.getDatabase().getOutpostsDao().create(outpost);
                        player.sendRichMessage(LandsOutposts.LANDSOUTPOSTS_BASE_MSG + LandsOutpostsLanguage.LANG.outpostCreated);
                        if (claimedLand != null) {
                            land.claimChunk(landPlayer, playerLocation.getWorld(), chunk.getX(), chunk.getZ()).thenAccept(claim -> {
                                if (claim) {
                                    land.calculateLevel(true);
                                }
                            });
                        }
                    } else {
                        player.sendRichMessage(LandsOutposts.LANDSOUTPOSTS_BASE_MSG + LandsOutpostsLanguage.LANG.notEnoughMoney.replace("%cost", String.valueOf(outpostCost)));
                    }
                }
            } else {
                Flags.LAND_CLAIM.sendDenied(landPlayer, land.getDefaultArea());
            }
        } else {
            String legacyLandColorName = MiniMessage.miniMessage().serialize(LegacyComponentSerializer.legacySection().deserialize(claimedLand.getColorName()));
            player.sendRichMessage(LandsOutposts.LANDSOUTPOSTS_BASE_MSG + LandsOutpostsLanguage.LANG.alreadyClaimedChunk.replace("%land", legacyLandColorName));
        }
    }
}
