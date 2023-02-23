package economy.pcconomy.backend.town.listener;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.*;
import com.palmergames.bukkit.towny.event.town.TownRuinedEvent;
import com.palmergames.bukkit.towny.object.Town;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.town.objects.TownObject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TownyListener implements Listener {

    @EventHandler
    public void OnCreation(NewTownEvent event) {
        var town = event.getTown(); // Запоминаем город

        PcConomy.GlobalBank.BankBudget += 250.0d;
        PcConomy.GlobalTownWorker.CreateTownObject(town, false); // Добавляем в лист установив что это игроковский город
    }

    @EventHandler
    public void onClaim(TownClaimEvent event) {
        PcConomy.GlobalBank.BankBudget += event.getTownBlock().getPlotPrice();
    }

    @EventHandler
    public void OnDestroy(DeleteTownEvent event) {
        var town = event.getTownName(); // Удаление по имени

        PcConomy.GlobalTownWorker.DestroyTownObject(town);
    }

    @EventHandler
    public void onDied(TownRuinedEvent event) {
        var town = event.getTown().getName(); // Удаление по имени

        PcConomy.GlobalTownWorker.DestroyTownObject(town);
    }

    @EventHandler
    public void NewDay(NewDayEvent event) {
        for (Town town: // Передача налогов с города в банк
                TownyAPI.getInstance().getTowns()) {
            PcConomy.GlobalBank.BankBudget += town.getPlotTax();
        }

        for (TownObject town : // НПС города делают свои дела, а нон-НПС берут проценты
                PcConomy.GlobalTownWorker.townObjects) {
            town.LifeCycle();
        }

        PcConomy.GlobalBank.LifeCycle(); // Банк анализирует и меняет свои параметры
    }
}
