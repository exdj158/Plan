/*
 * Licence is provided in the jar as license.yml also here:
 * https://github.com/Rsl1122/Plan-PlayerAnalytics/blob/master/Plan/src/main/resources/license.yml
 */
package main.java.com.djrapitops.plan.utilities.file.export;

import com.djrapitops.plugin.api.Check;
import com.djrapitops.plugin.api.utility.log.Log;
import com.djrapitops.plugin.task.AbsRunnable;
import main.java.com.djrapitops.plan.Plan;
import main.java.com.djrapitops.plan.settings.Settings;
import main.java.com.djrapitops.plan.systems.webserver.PageCache;
import main.java.com.djrapitops.plan.systems.webserver.response.Response;
import main.java.com.djrapitops.plan.utilities.MiscUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Abstract Html Export Task.
 *
 * @author Rsl1122
 */
public abstract class SpecificExport extends AbsRunnable {

    protected final File outputFolder;
    protected final boolean usingBungee;

    protected SpecificExport(String taskName) {
        super(taskName);
        outputFolder = getFolder();
        usingBungee = Check.isBungeeAvailable();
    }

    protected File getFolder() {
        String path = Settings.ANALYSIS_EXPORT_PATH.toString();

        Log.logDebug("Export", "Path: " + path);
        boolean isAbsolute = Paths.get(path).isAbsolute();
        Log.logDebug("Export", "Absolute: " + (isAbsolute ? "Yes" : "No"));
        if (isAbsolute) {
            File folder = new File(path);
            if (!folder.exists() || !folder.isDirectory()) {
                folder.mkdirs();
            }
            return folder;
        }
        File dataFolder = Plan.getInstance().getDataFolder();
        File folder = new File(dataFolder, path);
        folder.mkdirs();
        return folder;
    }

    protected void export(File to, List<String> lines) throws IOException {
        Files.write(to.toPath(), lines, Charset.forName("UTF-8"));
    }

    protected File getServerFolder() {
        File server = new File(outputFolder, "server");
        server.mkdirs();
        return server;
    }

    protected File getPlayerFolder() {
        File player = new File(outputFolder, "player");
        player.mkdirs();
        return player;
    }

    protected void exportAvailablePlayerPage(UUID uuid, String name) throws IOException {
        Response response = PageCache.loadPage("inspectPage: " + uuid);
        if (response == null) {
            return;
        }

        String html = response.getContent().replace("../", "../../");
        List<String> lines = Arrays.asList(html.split("\n"));

        File htmlLocation = new File(getPlayerFolder(), name.replace(" ", "%20").replace(".", "%2E"));
        htmlLocation.mkdirs();
        File exportFile = new File(htmlLocation, "index.html");

        export(exportFile, lines);
    }

    protected void exportAvailableServerPage(UUID serverUUID, String serverName) throws IOException {

        Response response = PageCache.loadPage("analysisPage:" + serverUUID);
        if (response == null) {
            return;
        }

        String html = response.getContent()
                .replace("href=\"plugins/", "href=\"../plugins/")
                .replace("href=\"css/", "href=\"../css/")
                .replace("src=\"plugins/", "src=\"../plugins/")
                .replace("src=\"js/", "src=\"../js/");

        File htmlLocation = null;
        if (usingBungee) {
            if (serverUUID.equals(MiscUtils.getIPlan().getServerUuid())) {
                htmlLocation = new File(outputFolder, "network");
            } else {
                htmlLocation = new File(getServerFolder(), serverName.replace(" ", "%20").replace(".", "%2E"));
                html = html.replace("../", "../../");
            }
        } else {
            htmlLocation = getServerFolder();
        }
        htmlLocation.mkdirs();
        File exportFile = new File(htmlLocation, "index.html");

        List<String> lines = Arrays.asList(html.split("\n"));

        export(exportFile, lines);
    }
}