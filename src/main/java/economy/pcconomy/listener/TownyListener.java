package economy.pcconomy.listener;

import com.palmergames.bukkit.towny.event.*;
import economy.pcconomy.town.Town;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TownyListener implements Listener {

    @EventHandler
    public void OnCreation(NewTownEvent event) {
        var town = event.getTown(); // Запоминаем город

        System.out.println("cathed creation");
        Town.CreateTownObject(town, false); // Добавляем в лист установив что это игроковский город
    }

    @EventHandler
    public void OnDestroy(DeleteTownEvent event) {
        var town = event.getTownName(); // Удаление по имени

        System.out.println("cathed destroying");
        Town.DestroyTownObject(town);
    }
}
