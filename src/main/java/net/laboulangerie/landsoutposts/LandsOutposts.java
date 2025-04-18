package net.laboulangerie.landsoutposts;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.palmergames.bukkit.towny.TownyUniverse;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.land.Area;
import me.angeschossen.lands.api.land.Land;
import me.angeschossen.lands.api.player.LandPlayer;
import net.laboulangerie.landsoutposts.command.LandsOutpostsCommand;
import net.laboulangerie.landsoutposts.database.LandOutpost;
import net.laboulangerie.landsoutposts.database.LandsOutpostsDatabase;
import net.laboulangerie.landsoutposts.listener.onUnclaim;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class LandsOutposts extends JavaPlugin {

    public static final String LANDSOUTPOSTS_BASE_MSG = "<dark_gray>[</dark_gray><dark_green>Lands-Outposts</dark_green><dark_gray>]</dark_gray> ";
    public static final String UNEXPECTED_EXCEPTION_MSG = LANDSOUTPOSTS_BASE_MSG + "<red>Unhandled exception... contact server admin.</red>";

    public static LandsOutposts instance;
    public static Logger LOGGER;

    private static final String MESSAGES_YML = "messages.yml";

    private LandsIntegration lands;
    private LandsOutpostsDatabase database;
    private TownyUniverse towny;

    public LandsOutposts() {
        instance = this;
    }

    @Override
    public void onLoad() {
        // load and init configuration
        saveDefaultConfig(); // saves default configuration if no config.yml exists yet
        reloadConfig();

        LOGGER = this.getLogger();
        debugMsg("Plugin debug enabled.");
    }

    @Override
    public void onEnable() {
        this.lands = LandsIntegration.of(this);
        if (this.lands == null) {
            getLogger().warning("Looks like Lands plugin is not enabled.");
            this.onDisable();
            return;
        }

        try {
            Class.forName("com.palmergames.bukkit.towny.TownyUniverse");
            this.towny = TownyUniverse.getInstance();
            getLogger().info("Towny plugin found. Import command enabled.");
        } catch (ClassNotFoundException e) {
            getLogger().warning("Towny not found. Import command disabled.");
        }

        try {
            this.database = new LandsOutpostsDatabase(this);
        } catch (SQLException e) {
            getLogger().warning("Database error.");
            e.printStackTrace();
            this.onDisable();
            return;
        }

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(LandsOutpostsCommand.build(this, Commands.literal("lands-outposts")));
            commands.registrar().register(LandsOutpostsCommand.build(this, Commands.literal("lo")));
        });

        getServer().getPluginManager().registerEvents(new onUnclaim(instance), this);
    }

    @Override
    public void onDisable() {
        try {
            this.database.close();
        } catch (Exception e) {
            getLogger().warning("Database error.");
            e.printStackTrace();
        }
    }

    /**
     * Reload config.
     * <p>
     * override to handle custom config logic and language loading
     */
    @Override
    public void reloadConfig() {
        super.reloadConfig();
        LandsOutpostsConfiguration.CONF.readConfig(getConfig());
        LandsOutpostsLanguage.LANG.readLanguage(getMessages());
    }

    /**
     * Get the configured player interaction messages.
     *
     * @return the configured player interaction messages
     */
    public FileConfiguration getMessages() {
        String langPath = String.format("i18n/messages_%s.yml", LandsOutpostsConfiguration.CONF.language);

        // try configured language first
        InputStream langStream = getResource(langPath);
        FileConfiguration conf;

        if (langStream != null) {
            Reader langReader = new InputStreamReader(langStream, StandardCharsets.UTF_8);
            conf = YamlConfiguration.loadConfiguration(langReader);
        } else {
            // use custom/default
            File langFile = new File(getDataFolder(), MESSAGES_YML);
            conf = YamlConfiguration.loadConfiguration(langFile);
        }

        return conf;
    }

    public static final void debugMsg(String msg) {
        if (LandsOutpostsConfiguration.CONF.debug) {
            LOGGER.info("DEBUG " + msg);
        }        
    }

    public LandsIntegration getLands() {
        return this.lands;
    }

    public LandsOutpostsDatabase getDatabase() {
        return this.database;
    }

    public TownyUniverse getTowny() {
        return this.towny;
    }

    public HashMap<String, LandOutpost> getPlayerLandsOutposts(LandPlayer landPlayer) throws SQLException {
        return this.getPlayerLandsOutposts(landPlayer, true);
    }

    public HashMap<String, LandOutpost> getPlayerLandsOutposts(LandPlayer landPlayer, boolean withName) throws SQLException {
        HashMap<String, LandOutpost> outposts = new HashMap<>();
        
        int index = 1;
        for (Land land : landPlayer.getLands()) {
            for (Iterator<LandOutpost> it = this.getLandOutposts(land).iterator(); it.hasNext(); index++) {
                LandOutpost landOutpost = it.next();
                outposts.put(String.valueOf(index), landOutpost);
                if (withName) {
                    this.getLandOutpostName(landOutpost).ifPresent(name -> {
                        outposts.put(name, landOutpost);
                    });
                }
            }
        }

        return outposts;
    }

    public List<LandOutpost> getLandOutposts(Land land) throws SQLException {
        return this.database.getOutpostsDao().queryBuilder().where().eq("land_id", land.getULID().toString()).query();
    }

    public Optional<String> getLandOutpostName(LandOutpost landOutpost) {
        Area area = this.lands.getArea(landOutpost.getSpawn());
        if (area != null && !area.isDefault()) {
            return Optional.of(area.getName());
        }
        return Optional.empty();
    }

    public Optional<String> getLandOutpostColorName(LandOutpost landOutpost, CommandSender sender) {
        Area area = this.lands.getArea(landOutpost.getSpawn());
        if (area != null && !area.isDefault()) {
            return Optional.of(area.getColorName(sender));
        }
        return Optional.empty();
    }
}
