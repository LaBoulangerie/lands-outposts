package net.laboulangerie.landsoutposts;

import org.bukkit.configuration.file.FileConfiguration;

public enum LandsOutpostsConfiguration {
    CONF;

    /**
     * Language to be used for messages. Should be an ISO 639-1 (alpha-2) code.
     * If a language is not supported by Gringotts, use user-configured or default
     * (English) messages.
     */
    public String language = "custom";

    public int maxOutpostsPerLand = 1;
    public int outpostsCost = 4096;
    public int outpostsTeleportCooldown = 60000;

    public boolean debug = false;

    public void readConfig(FileConfiguration savedConfig) {
        CONF.language = savedConfig.getString("language", "custom");
        CONF.maxOutpostsPerLand = savedConfig.getInt("max_outposts_per_land", 1);
        CONF.outpostsCost = savedConfig.getInt("outposts_cost", 4096);
        CONF.outpostsCost = savedConfig.getInt("outposts_teleport_cooldown", 4096);
        CONF.debug = savedConfig.getBoolean("debug", false);
    }
}
