package economy.pcconomy.backend.economy.town.listener;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.*;
import com.palmergames.bukkit.towny.event.town.TownRuinedEvent;

import economy.pcconomy.PcConomy;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TownyListener implements Listener {
    @EventHandler
    public void OnCreation(NewTownEvent event) {
        var town = event.getTown();

        PcConomy.GlobalBank.BankBudget += 250.0d;
        PcConomy.GlobalTownManager.createTownObject(town, false);
    }

    @EventHandler
    public void onClaim(TownClaimEvent event) {
        PcConomy.GlobalBank.BankBudget += event.getTownBlock().getPlotPrice();
    }

    @EventHandler
    public void OnDestroy(DeleteTownEvent event) {
        var town = event.getTownName();
        PcConomy.GlobalTownManager.destroyTown(town);
    }

    @EventHandler
    public void onDied(TownRuinedEvent event) {
        var town = event.getTown().getName();
        PcConomy.GlobalTownManager.destroyTown(town);
    }

    @EventHandler
    public void NewDay(NewDayEvent event) {
        for (var town : TownyAPI.getInstance().getTowns()) {
            PcConomy.GlobalBank.BankBudget += town.getPlotTax();
        }

        for (var town : PcConomy.GlobalTownManager.towns) {
            town.lifeCycle();
        }

        PcConomy.GlobalBank.lifeCycle();
    }
}
