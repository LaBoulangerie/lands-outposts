package net.laboulangerie.landsoutposts.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.laboulangerie.landsoutposts.LandsOutposts;

public class LandsOutpostsCommand {

    private LandsOutpostsCommand() {
        throw new IllegalStateException("Utility class");
    }

    public static final LiteralCommandNode<CommandSourceStack> build(LandsOutposts landsOutposts) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("lands-outposts");
        root.then(TeleportCommand.command());
        root.then(ListCommand.command(landsOutposts));

        root.then(ClaimCommand.command());
        root.then(UnclaimCommand.command());

        if (landsOutposts.getTowny() != null) {
            root.then(ImportTownyCommand.command(landsOutposts));
        }

        return root.build();

    }
}
