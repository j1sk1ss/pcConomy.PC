package economy.pcconomy.backend.economy;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.*;
import com.palmergames.bukkit.towny.event.town.TownRuinedEvent;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.economy.town.manager.TownManager;
import lombok.experimental.ExtensionMethod;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


@ExtensionMethod({TownManager.class})
public class TownyListener implements Listener {
    /**
     * Triggers when TownyAPI creates town
     * @param event Event
     */
    @EventHandler
    public void onCreation(NewTownEvent event) {
        var town = event.getTown();

        PcConomy.GlobalBank.BankBudget += 250d; // TODO: Towny API connect correct price
        town.createTownObject(false);
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
        PcConomy.GlobalShareManager.takeOffShares(town);
        town.destroyTown();
    }

    /**
     * Triggers when TownyAPI kill town
     * @param event Event
     */
    @EventHandler
    public void onDied(TownRuinedEvent event) { // TODO: Make correct paying for shares (maybe?)
        var town = event.getTown().getUUID();
        town.destroyTown();
    }

    /**
     * Triggers when TownyAPI run new day
     * @param event Event
     */
    @EventHandler
    public void newDay(NewDayEvent event) {
        TownyAPI.getInstance().getTowns().parallelStream().forEach((town) -> PcConomy.GlobalBank.BankBudget += town.getPlotTax()); // TODO: Towny API connect correct price
        PcConomy.GlobalTownManager.Towns.parallelStream().forEach(Capitalist::newDay);

        PcConomy.GlobalShareManager.newDay();
        PcConomy.GlobalBank.newDay();
    }
}
