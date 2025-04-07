package net.laboulangerie.landsoutposts;

import org.bukkit.entity.Player;

/**
 * The Permissions.
 */
public enum LandsOutpostsPermissions {
    /**
     * Create vault land permissions.
     */
    LANDS_OUTPOSTS_ADMIN("landsoutposts.admin");

    /**
     * The Node.
     */
    public final String node;

    LandsOutpostsPermissions(String node) {
        this.node = node;
    }

    /**
     * Check if a player has this permission.
     *
     * @param player player to check
     * @return whether given player has this permission
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isAllowed(Player player) {
        return player.hasPermission(this.node);
    }
}
