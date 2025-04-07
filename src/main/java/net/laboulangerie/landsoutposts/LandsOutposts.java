package net.laboulangerie.landsoutposts;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.palmergames.bukkit.towny.TownyUniverse;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.land.Land;
import me.angeschossen.lands.api.player.LandPlayer;
import net.laboulangerie.landsoutposts.command.LandsOutpostsCommand;
import net.laboulangerie.landsoutposts.database.LandOutpost;
import net.laboulangerie.landsoutposts.database.LandsOutpostsDatabase;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class LandsOutposts extends JavaPlugin {

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
            commands.registrar().register(LandsOutpostsCommand.build(this));
        });
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

    public List<LandOutpost> getLandPlayerOutposts(LandPlayer landPlayer) {

        return null;
    }

    public List<LandOutpost> getLandOutposts(Land land) {

        return null;
    }
}
