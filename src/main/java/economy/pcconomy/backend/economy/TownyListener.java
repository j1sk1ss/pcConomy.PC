package economy.pcconomy.backend.economy;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.*;
import com.palmergames.bukkit.towny.event.town.TownRuinedEvent;

import economy.pcconomy.PcConomy;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class TownyListener implements Listener {
    /**
     * Triggers when TownyAPI creates town
     * @param event Event
     */
    @EventHandler
    public void onCreation(NewTownEvent event) {
        var town = event.getTown();

        PcConomy.GlobalBank.BankBudget += 250d; // TODO: Towny API connect correct price
        PcConomy.GlobalTownManager.createTownObject(town, false);
    }

    /**
     * Triggers when TownyAPI claim new terra
     * @param event Event
     */
    @EventHandler
    public void onClaim(TownClaimEvent event) {
        PcConomy.GlobalBank.BankBudget += event.getTownBlock().getPlotPrice(); // TODO: Towny API connect correct price
    }

    /**
     * Triggers when TownyAPI delete town
     * @param event Event
     */
    @EventHandler
    public void onDestroy(DeleteTownEvent event) { // TODO: Make correct paying for shares (maybe?)
        var town = event.getTownUUID();
        PcConomy.GlobalTownManager.destroyTown(town);
    }

    /**
     * Triggers when TownyAPI kill town
     * @param event Event
     */
    @EventHandler
    public void onDied(TownRuinedEvent event) { // TODO: Make correct paying for shares (maybe?)
        var town = event.getTown().getUUID();
        PcConomy.GlobalTownManager.destroyTown(town);
    }

    /**
     * Triggers when TownyAPI run new day
     * @param event Event
     */
    @EventHandler
    public void newDay(NewDayEvent event) {
        for (var town : TownyAPI.getInstance().getTowns()) PcConomy.GlobalBank.BankBudget += town.getPlotTax(); // TODO: Towny API connect correct price
        for (var town : PcConomy.GlobalTownManager.Towns) town.newDay();

        PcConomy.GlobalShareManager.newDay();
        PcConomy.GlobalBank.newDay();
    }
}
