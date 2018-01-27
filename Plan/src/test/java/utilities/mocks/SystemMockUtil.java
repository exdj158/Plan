/*
 * Licence is provided in the jar as license.yml also here:
 * https://github.com/Rsl1122/Plan-PlayerAnalytics/blob/master/Plan/src/main/resources/license.yml
 */
package utilities.mocks;

import com.djrapitops.plan.Plan;
import com.djrapitops.plan.api.exceptions.EnableException;
import com.djrapitops.plan.system.BukkitSystem;
import com.djrapitops.plan.system.database.DBSystem;
import com.djrapitops.plan.system.database.databases.sql.SQLDB;

import java.io.File;

/**
 * Utility for mocking only certain parts of systems.
 *
 * @author Rsl1122
 */
public class SystemMockUtil {

    private BukkitSystem bukkitSystem;

    public static SystemMockUtil setUp(File dataFolder) throws Exception {
        return new SystemMockUtil().initializeBukkitSystem(dataFolder);
    }

    public SystemMockUtil enableConfigSystem() throws Exception {
        bukkitSystem.getFileSystem().enable();
        bukkitSystem.getConfigSystem().enable();
        return this;
    }

    public SystemMockUtil enableCacheSystem() throws Exception {
        bukkitSystem.getCacheSystem().enable();
        return this;
    }

    private SystemMockUtil initializeBukkitSystem(File dataFolder) throws Exception {
        Plan planMock = BukkitMockUtil.setUp()
                .withDataFolder(dataFolder)
                .withLogging()
                .withResourceFetchingFromJar()
                .withPluginDescription()
                .withServer()
                .getPlanMock();
        bukkitSystem = new BukkitSystem(planMock);
        return this;
    }

    public SystemMockUtil enableProcessingQueue() {
        bukkitSystem.getProcessingQueue().enable();
        return this;
    }

    public SystemMockUtil enableDatabaseSystem() throws EnableException {
        bukkitSystem.getDatabaseSystem().enable();
        return this;
    }

    public SystemMockUtil enableServerInfoSystem() throws EnableException {
        bukkitSystem.getServerInfo().enable();
        return this;
    }

    public SystemMockUtil enableDatabaseSystem(SQLDB db) throws EnableException {
        DBSystem dbSystem = bukkitSystem.getDatabaseSystem();
        dbSystem.setActiveDatabase(db);
        dbSystem.enable();
        return this;
    }
}