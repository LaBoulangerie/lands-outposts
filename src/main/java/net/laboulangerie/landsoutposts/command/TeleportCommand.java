package net.laboulangerie.landsoutposts.command;

import java.sql.SQLException;
import java.util.List;

import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.angeschossen.lands.api.player.LandPlayer;
import net.laboulangerie.landsoutposts.LandsOutposts;
import net.laboulangerie.landsoutposts.database.LandOutpost;

public class TeleportCommand {

    // TODO: cooldown

    private TeleportCommand() {
        throw new IllegalStateException("Utility class");
    }

    public static final LiteralArgumentBuilder<CommandSourceStack> command(LandsOutposts landsOutposts) {
        // not working ???
        //　　　　　 　 ____
        //　　　　　／＞　　 フ
        //　　　　　| 　_　 _l
        //　 　　　／` ミ＿xノ
        //　　 　 /　　　   |
        //　　　 /　 ヽ　　 ﾉ
        //　 　 │　　|　|　|
        //　／￣|　　 |　|　|
        //　| (￣ヽ＿ヽ)__) __)
        //  ＼二つ 
        return Commands.literal("tp").then(Commands.argument("outpost", IntegerArgumentType.integer(1))) 
        .requires(sender -> sender.getSender() instanceof Player)
        .executes(ctx -> {
            Player player = (Player) ctx.getSource().getSender();
            LandPlayer landPlayer = landsOutposts.getLands().getLandPlayer(player.getUniqueId());

            int outpost = ctx.getArgument("outpost", int.class);
            try {
                List<LandOutpost> outposts = landsOutposts.getLandPlayerOutposts(landPlayer);
                LandOutpost landOutpost = outposts.get(outpost);
                if (landOutpost != null) {
                    player.teleport(landOutpost.getSpawn());
                } else {
                    //TODO: not found
                }
            } catch (SQLException e) {
                player.sendRichMessage(LandsOutposts.UNEXPECTED_EXCEPTION_MSG);
                e.printStackTrace();
            }

            return Command.SINGLE_SUCCESS;
        });
    }
}
