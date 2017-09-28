package scripts.webwalker_logic.teleport_logic;

/**
 * Check limits of a teleport, e.g. wilderness level under 20 or 30.
 */
public interface TeleportLimit {
    boolean canCast();
}
