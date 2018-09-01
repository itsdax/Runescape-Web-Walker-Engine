package scripts.dax_api.api_lib;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSPlayer;
import scripts.dax_api.api_lib.models.*;
import scripts.dax_api.api_lib.utils.DaxTrackerProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DaxTracker {

    public static void setCredentials(DaxCredentialsProvider daxCredentialsProvider) {
        DaxTrackerServerApi.getInstance().setDaxCredentialsProvider(daxCredentialsProvider);
    }

    /**
     *
     * @param propertyName
     * @param period
     * @return Accounts online belonging to the tribot user
     */
    public static ListSearch accountsOnline(String propertyName, Period period) {
        return DaxTrackerServerApi.getInstance().sourcesOnline(propertyName, General.getTRiBotUsername(), period);
    }

    /**
     *
     * @param period
     * @return
     */
    public static ListSearch usersOnline(Period period) {
        return DaxTrackerServerApi.getInstance().usersOnline(null, period);
    }

    public static PropertyStats getStats(String propertyName) {
        return getStats(null, propertyName);
    }

    /**
     * @param accountName
     * @param propertyName
     * @return Total accumulated stats for the tribot user.
     *         If accountName is supplied, only return stats from that runescape account.
     */
    public static PropertyStats getStats(String accountName, String propertyName) {
        return DaxTrackerServerApi.getInstance().getStats(General.getTRiBotUsername(), accountName, propertyName);
    }


    /**
     *
     * @param propertyName
     * @param period Only show results within time period.
     * @return High score for Accounts owned by the user for a tracked property.
     */
    public static SourceHighScore getAccountsHighScore(String propertyName, Period period) {
        return DaxTrackerServerApi.getInstance().topSources(General.getTRiBotUsername(), propertyName, period);
    }

    /**
     *
     * @param propertyName
     * @param period Only show results within time period.
     * @return High score of Tribot Usernames for a specific property.
     */
    public static UserHighScore getUsersHighScore(String propertyName, Period period) {
        return DaxTrackerServerApi.getInstance().topUsers(propertyName, period);
    }

    /**
     *
     * @param propertyName
     * @param period Filters by only stats within period.
     * @return Top users starting from highest to lowest stats for the property.
     */
    public static UserHighScore getUserHighscore(String propertyName, Period period) {
        return DaxTrackerServerApi.getInstance().topUsers(propertyName, period);
    }

    public static DataLog log(String propertyName, double value) {
        RSPlayer player = Player.getRSPlayer();
        String accountName = player != null ? player.getName().replaceAll("[^a-zA-Z0-9]", " ") : null;
        System.out.println(accountName + " @@@@");
        return DaxTrackerServerApi.getInstance().log(
                General.getTRiBotUsername(),
                accountName,
                propertyName,
                value
        );
    }

    private List<DaxTrackerProperty> trackerProperties;
    private long lastUpdated;

    private DaxTracker() {
        trackerProperties = new ArrayList<>();
        lastUpdated = System.currentTimeMillis();
    }

    public void add(DaxTrackerProperty daxTrackerProperty) {
        trackerProperties.add(daxTrackerProperty);
    }

    public void update() {

        if (Timing.timeFromMark(lastUpdated) < 60000) {
            return;
        }

        for (DaxTrackerProperty daxTrackerProperty : trackerProperties) {
            log(daxTrackerProperty.getName(), daxTrackerProperty.differenceSinceLastTracked());
            daxTrackerProperty.update();
        }

        lastUpdated = System.currentTimeMillis();
    }


}
