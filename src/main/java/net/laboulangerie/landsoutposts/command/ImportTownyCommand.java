package net.laboulangerie.landsoutposts.command;

import java.sql.SQLException;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.palmergames.bukkit.towny.object.Town;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.angeschossen.lands.api.land.Land;
import net.laboulangerie.landsoutposts.LandsOutposts;
import net.laboulangerie.landsoutposts.database.LandOutpost;

public class ImportTownyCommand {

    private ImportTownyCommand() {
        throw new IllegalStateException("Utility class");
    }

    public static final LiteralArgumentBuilder<CommandSourceStack> command(LandsOutposts landsOutposts) {
        return Commands.literal("import-towny-outposts")
        .requires(sender -> !(sender.getExecutor() instanceof Player))
        .executes(ctx -> {
            CommandSender sender = ctx.getSource().getSender();

            for (Land land : landsOutposts.getLands().getLands()) {
            sender.sendMessage(land.getName() + " Is imported from Towny ?");
            Town town = landsOutposts.getTowny().getTown(land.getName());
            if (town == null) {
                sender.sendMessage(land.getName() + " not found in Towny.");
                continue;
            }
            sender.sendMessage(land.getName() + " found in Towny.");
            List<Location> outposts = town.getAllOutpostSpawns();
            sender.sendMessage(land.getName() + " " + outposts.size() + " found for towny.");
            for (Location location : outposts) {
                sender.sendMessage(land.getName() + " " + location + " create land outpost.");
                LandOutpost outpost = new LandOutpost(land.getULID(), location);
                try {
                    landsOutposts.getDatabase().getOutpostsDao().create(outpost);
                } catch (SQLException e) {
                    sender.sendMessage(land.getName() + " " + location + " outpost creation error.");
                    e.printStackTrace();
                }
            }

            sender.sendMessage("-------------");
        }
        sender.sendMessage("Migration done.");

            return Command.SINGLE_SUCCESS;
        });
    }
}
