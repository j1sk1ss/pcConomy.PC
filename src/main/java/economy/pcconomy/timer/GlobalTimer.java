package economy.pcconomy.timer;

import economy.pcconomy.PcConomy;
import economy.pcconomy.town.objects.TownObject;
import economy.pcconomy.town.scripts.TownWorker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class GlobalTimer {
    public GlobalTimer(Plugin plugin) {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this::Tick, 0L, 50L);
    }

    private void Tick() {
        for (TownObject town :
                TownWorker.townObjects) {
            town.LifeCycle();
        }

        PcConomy.GlobalBank.TakePercentFromBorrowers();
    }
}
