package net.laboulangerie.landsoutposts;

import org.bukkit.configuration.file.FileConfiguration;

import net.md_5.bungee.api.ChatColor;

import java.util.function.BiFunction;

public enum LandsOutpostsLanguage {
        LANG;

        public String outposts;
        public String clickToTeleport;
        public String outpostNotFound;
        public String teleportCooldown;

        public void readLanguage(FileConfiguration savedLanguage) {
                BiFunction<String, String, String> translator = (path, def) -> ChatColor.translateAlternateColorCodes('&', savedLanguage.getString(path, def));

                LANG.outposts = translator.apply("outposts", "Outposts");
                LANG.clickToTeleport = translator.apply("click_to_teleport", "Click to teleport.");
                LANG.outpostNotFound = translator.apply("outpost_not_found", "Outpost not found.");
                LANG.teleportCooldown = translator.apply("outpost_not_found", "Outpost not found.");
        }
}
