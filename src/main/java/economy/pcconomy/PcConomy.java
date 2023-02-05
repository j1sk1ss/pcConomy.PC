package economy.pcconomy;

import economy.pcconomy.link.Manager;
import me.yic.xconomy.api.XConomyAPI;
import org.bukkit.plugin.java.JavaPlugin;

public final class PcConomy extends JavaPlugin {
    public static XConomyAPI xConomyAPI;
    @Override
    public void onEnable() {
        xConomyAPI = new XConomyAPI(); // Общий API XConomy этого плагина. Брать только от сюда
        var manager = new Manager(); // Обработчик тестовых комманд
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
