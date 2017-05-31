package main.java.com.djrapitops.plan.data.cache;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import main.java.com.djrapitops.plan.Log;
import main.java.com.djrapitops.plan.Plan;
import main.java.com.djrapitops.plan.data.UserData;
import main.java.com.djrapitops.plan.database.Database;
import main.java.com.djrapitops.plan.utilities.MiscUtils;

/**
 * This class stores UserData objects used for displaying the Html pages.
 *
 * @author Rsl1122
 * @since 2.0.0
 */
public class InspectCacheHandler {

    private DataCacheHandler handler;
    private Plan plugin;
    private Map<UUID, UserData> cache;
    private Map<UUID, Long> cacheTimes;

    /**
     * Class constructor.
     *
     * @param plugin Current instance of Plan.class
     */
    public InspectCacheHandler(Plan plugin) {
        this.handler = plugin.getHandler();
        this.plugin = plugin;
        this.cache = new HashMap<>();
        cacheTimes = new HashMap<>();
    }

    /**
     * Caches the UserData object to InspectCache.
     *
     * If the Userdata is cached in DataCache it will be used. Otherwise the Get
     * Queue will handle the DBCallableProcessor.
     *
     * @param uuid UUID of the player.
     */
    public void cache(UUID uuid) {
        DBCallableProcessor cacher = new DBCallableProcessor() {
            @Override
            public void process(UserData data) {
                cache.put(uuid, new UserData(data));
                cacheTimes.put(uuid, MiscUtils.getTime());
            }
        };
        handler.getUserDataForProcessing(cacher, uuid, false);
    }

    /**
     * Used to cache all UserData to the InspectCache from the cache and
     * provided database.
     *
     * @param db Database to cache from if data is not in the cache.
     * @throws SQLException If Database is not properly enabled
     */
    public void cacheAllUserData(Database db) throws SQLException {
        Set<UUID> cachedUserData = handler.getDataCache().keySet();
        for (UUID uuid : cachedUserData) {
            cache(uuid);
        }
        Set<UUID> savedUUIDs = new HashSet<>();
        try {
            savedUUIDs = db.getUsersTable().getSavedUUIDs();
        } catch (SQLException ex) {
            Log.toLog(this.getClass().getName(), ex);
        }
        savedUUIDs.removeAll(cachedUserData);
        List<UserData> userDataForUUIDS = db.getUserDataForUUIDS(savedUUIDs);
        long time = MiscUtils.getTime();
        for (UserData uData : userDataForUUIDS) {
            UUID uuid = uData.getUuid();
            cache.put(uuid, uData);
            cacheTimes.put(uuid, time);
        }
    }

    /**
     * Checks the cache for UserData matching UUID.
     *
     * @param uuid UUID of the Player
     * @return UserData that matches the player, null if not cached.
     */
    public UserData getFromCache(UUID uuid) {
        return cache.get(uuid);
    }

    public long getCacheTime(UUID uuid) {
        if (cacheTimes.containsKey(uuid)) {
            return cacheTimes.get(uuid);
        }
        return -1;
    }

    /**
     * Check if the data of a player is in the inspect cache.
     *
     * @param uuid UUID of player.
     * @return true if cached.
     */
    public boolean isCached(UUID uuid) {
        return cache.containsKey(uuid);
    }

    /**
     * Used to get all cached userdata objects.
     *
     * @return List of cached userdata objects.
     */
    public List<UserData> getCachedUserData() {
        return new ArrayList<>(cache.values());
    }
}
