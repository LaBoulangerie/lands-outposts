package net.laboulangerie.landsoutposts.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class ClaimCommand {

    private ClaimCommand() {
        throw new IllegalStateException("Utility class");
    }

    public static final LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("claim");
        // TODO
    }
}
