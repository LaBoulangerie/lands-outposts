package net.laboulangerie.landsoutposts;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;

public enum LandsOutpostsConfiguration {
    CONF;

    /**
     * Language to be used for messages. Should be an ISO 639-1 (alpha-2) code.
     * If a language is not supported by Gringotts, use user-configured or default
     * (English) messages.
     */
    public String language = "custom";

    public HashMap<Integer, Integer> landLevelsMaxOutposts = new HashMap<>();
    public HashMap<Integer, Integer> nationLevelsBonusOutposts = new HashMap<>();

    public int outpostsCost = 4096;
    public int outpostsTeleportCooldown = 60;

    public boolean debug = false;

    public void readConfig(FileConfiguration savedConfig) {
        CONF.language = savedConfig.getString("language", "custom");

        CONF.landLevelsMaxOutposts.clear();
        for (String key : savedConfig.getConfigurationSection("land_levels").getKeys(false)) {
            try {
                CONF.landLevelsMaxOutposts.put(Integer.parseInt(key), savedConfig.getInt("land_levels." + key + ".outposts", 1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        CONF.nationLevelsBonusOutposts.clear();
        for (String key : savedConfig.getConfigurationSection("nation_levels").getKeys(false)) {
            try {
                CONF.nationLevelsBonusOutposts.put(Integer.parseInt(key), savedConfig.getInt("nation_levels." + key + ".bonus_outposts", 1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        CONF.outpostsCost = savedConfig.getInt("outposts_cost", 4096);
        CONF.outpostsCost = savedConfig.getInt("outposts_teleport_cooldown", 60);
        CONF.debug = savedConfig.getBoolean("debug", false);
    }
}
