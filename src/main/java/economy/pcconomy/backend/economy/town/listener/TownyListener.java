package economy.pcconomy.backend.economy.town.listener;

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
    public void OnCreation(NewTownEvent event) {
        var town = event.getTown();

        PcConomy.GlobalBank.BankBudget += 250d; //TODO: Towny API connect correct price
        PcConomy.GlobalTownManager.createTownObject(town, false);
    }

    /**
     * Triggers when TownyAPI claim new terra
     * @param event Event
     */
    @EventHandler
    public void onClaim(TownClaimEvent event) {
        PcConomy.GlobalBank.BankBudget += event.getTownBlock().getPlotPrice();
    }

    /**
     * Triggers when TownyAPI delete town
     * @param event Event
     */
    @EventHandler
    public void OnDestroy(DeleteTownEvent event) {
        var town = event.getTownUUID();
        PcConomy.GlobalTownManager.destroyTown(town);
    }

    /**
     * Triggers when TownyAPI kill town
     * @param event Event
     */
    @EventHandler
    public void onDied(TownRuinedEvent event) {
        var town = event.getTown().getUUID();
        PcConomy.GlobalTownManager.destroyTown(town);
    }

    /**
     * Triggers when TownyAPI run new day
     * @param event Event
     */
    @EventHandler
    public void NewDay(NewDayEvent event) {
        for (var town : TownyAPI.getInstance().getTowns())
            PcConomy.GlobalBank.BankBudget += town.getPlotTax();

        for (var town : PcConomy.GlobalTownManager.towns)
            town.newDay();

        PcConomy.GlobalShareManager.dailyPaying();
        PcConomy.GlobalBank.newDay();
        PcConomy.GlobalShareManager.InteractionList.clear();
    }
}
