package economy.pcconomy.backend.town.listener;

import com.palmergames.bukkit.towny.event.*;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.town.scripts.TownWorker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TownyListener implements Listener {

    @EventHandler
    public void OnCreation(NewTownEvent event) {
        var town = event.getTown(); // Запоминаем город

        System.out.println("cathed creation");
        PcConomy.GlobalTownWorker.CreateTownObject(town, false); // Добавляем в лист установив что это игроковский город
    }

    @EventHandler
    public void OnDestroy(DeleteTownEvent event) {
        var town = event.getTownName(); // Удаление по имени

        System.out.println("cathed destroying");
        PcConomy.GlobalTownWorker.DestroyTownObject(town);
    }
}
