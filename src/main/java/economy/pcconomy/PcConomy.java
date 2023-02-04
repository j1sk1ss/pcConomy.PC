package economy.pcconomy;

import me.yic.xconomy.api.XConomyAPI;
import org.bukkit.plugin.java.JavaPlugin;

public final class PcConomy extends JavaPlugin {
    public static XConomyAPI xConomyAPI;
    @Override
    public void onEnable() {
        xConomyAPI = new XConomyAPI();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
