package net.laboulangerie.landsoutposts;

import org.bukkit.configuration.file.FileConfiguration;

import net.md_5.bungee.api.ChatColor;

import java.util.function.BiFunction;

public enum LandsOutpostsLanguage {
        LANG;

        public void readLanguage(FileConfiguration savedLanguage) {
                BiFunction<String, String, String> translator = (path, def) -> ChatColor.translateAlternateColorCodes('&', savedLanguage.getString(path, def));

                // TODO

        }
}
