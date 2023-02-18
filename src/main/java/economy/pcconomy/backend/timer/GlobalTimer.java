package economy.pcconomy.backend.timer;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.town.objects.TownObject;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class GlobalTimer {
    public GlobalTimer(Plugin plugin) {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this::Tick, 0L, 5000L);
    }

    private void Tick() {
        for (TownObject town :
                PcConomy.GlobalTownWorker.townObjects) {
            town.LifeCycle();
        }

        PcConomy.GlobalBank.TakePercentFromBorrowers();
        PcConomy.GlobalBank.LifeCycle();
    }
}
