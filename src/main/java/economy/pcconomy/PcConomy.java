package economy.pcconomy;

import economy.pcconomy.link.Manager;
import me.yic.xconomy.api.XConomyAPI;
import org.bukkit.plugin.java.JavaPlugin;

public final class PcConomy extends JavaPlugin {
    public static XConomyAPI xConomyAPI;
    @Override
    public void onEnable() {
        xConomyAPI = new XConomyAPI();

        Manager manager = new Manager();
        getCommand("withdraw").setExecutor(manager);
        getCommand("change").setExecutor(manager);
        getCommand("put").setExecutor(manager);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
