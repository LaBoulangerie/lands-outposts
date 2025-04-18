package net.laboulangerie.landsoutposts.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.laboulangerie.landsoutposts.LandsOutposts;

public class LandsOutpostsCommand {

    private final LiteralArgumentBuilder<CommandSourceStack> cmd;
    private final LiteralArgumentBuilder<CommandSourceStack> cmdAlias;

    private LandsOutpostsCommand() {
        this.cmd = Commands.literal("lands-outposts");
        this.cmdAlias = Commands.literal("lo");
    }

    private void registerSubCommand(LiteralArgumentBuilder<CommandSourceStack> subCommand) {
        this.cmd.then(subCommand);
        this.cmdAlias.then(subCommand);
    }

    private void registar(Commands registrar) {
        registrar.register(this.cmd.build());
        registrar.register(this.cmdAlias.build());
    }

    public static final void build(LandsOutposts landsOutposts, Commands registrar) {
        LandsOutpostsCommand cmd = new LandsOutpostsCommand();

        cmd.registerSubCommand(TeleportCommand.command(landsOutposts));
        cmd.registerSubCommand(ListCommand.command(landsOutposts));

        cmd.registerSubCommand(ClaimCommand.command(landsOutposts));
        cmd.registerSubCommand(UnclaimCommand.command());

        if (landsOutposts.getTowny() != null) {
            cmd.registerSubCommand(ImportTownyCommand.command(landsOutposts));
        }

        cmd.registar(registrar);
    }
}
