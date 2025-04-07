package net.laboulangerie.landsoutposts.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class TeleportCommand {

    // TODO: cooldown

    private TeleportCommand() {
        throw new IllegalStateException("Utility class");
    }

    public final static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("tp").executes(null);
        // TODO
    }
}
