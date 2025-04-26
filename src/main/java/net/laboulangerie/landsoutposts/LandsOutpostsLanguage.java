package net.laboulangerie.landsoutposts;

import org.bukkit.configuration.file.FileConfiguration;

import net.md_5.bungee.api.ChatColor;

import java.util.function.BiFunction;

public enum LandsOutpostsLanguage {
        LANG;

        public String outposts;
        public String clickToTeleport;
        public String outpostNotFound;
        public String landNotFound;
        public String teleportCooldown;
        public String notInALand;
        public String outpostDeleted;
        public String noOutpostInChunk;
        public String chunkNotInLand;
        public String maxChunks;
        public String maxOutposts;
        public String notEnoughMoney;
        public String alreadyClaimedChunk;
        public String outpostCreated;

        public void readLanguage(FileConfiguration savedLanguage) {
                BiFunction<String, String, String> translator = (path, def) -> ChatColor.translateAlternateColorCodes('&', savedLanguage.getString(path, def));

                LANG.outposts = translator.apply("outposts", "Outposts");
                LANG.clickToTeleport = translator.apply("click_to_teleport", "Click to teleport.");
                LANG.outpostNotFound = translator.apply("outpost_not_found", "Outpost not found.");
                LANG.landNotFound = translator.apply("land_not_found", "Land %name not found.");
                LANG.teleportCooldown = translator.apply("teleport_cooldown", "Please wait %wait more seconds before teleporting again.");
                LANG.notInALand = translator.apply("not_in_a_land", "You are not member of any land.");
                LANG.outpostDeleted = translator.apply("outpost_deleted", "Outpost deleted.");
                LANG.noOutpostInChunk = translator.apply("no_outpost_in_chunk", "No outpost detected in this chunk.");
                LANG.chunkNotInLand = translator.apply("chunk_not_in_land", "This chunk is not in any land.");
                LANG.maxChunks = translator.apply("max_chunks", "Maximum claimed chunks reached (max %max).");
                LANG.maxOutposts = translator.apply("max_outposts", "Maximum outposts reached (max %max).");
                LANG.notEnoughMoney = translator.apply("not_enough_money", "Not enough money (cost %cost).");
                LANG.alreadyClaimedChunk = translator.apply("already_claimed_chunk", "Chunk already claimed by %land.");
                LANG.outpostCreated = translator.apply("outpost_created", "Outpost successfully created.");

        }
}
