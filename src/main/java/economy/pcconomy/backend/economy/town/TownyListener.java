package economy.pcconomy.backend.economy.town;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.*;
import com.palmergames.bukkit.towny.event.town.TownRuinedEvent;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.economy.Capitalist;
import lombok.experimental.ExtensionMethod;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


// TODO: Gorodki integration
@ExtensionMethod({TownManager.class})
public class TownyListener implements Listener {
    /**
     * Triggers when TownyAPI creates town
     * @param event Event
     */
    @EventHandler
    public void onCreation(NewTownEvent event) {
        var town = event.getTown();

        PcConomy.GlobalBank.getMainBank().BankBudget += 250d;
        town.createTownObject(false);
    }

    /**
     * Triggers when TownyAPI claim new terra
     * @param event Event
     */
    @EventHandler
    public void onClaim(TownClaimEvent event) {
        PcConomy.GlobalBank.getMainBank().BankBudget += event.getTownBlock().getPlotPrice();
    }

    /**
     * Triggers when TownyAPI delete town
     * @param event Event
     */
    @EventHandler
    public void onDestroy(DeleteTownEvent event) {
        var town = event.getTownUUID();
        PcConomy.GlobalShare.takeOffShares(town);
        town.destroyTown();
    }

    /**
     * Triggers when TownyAPI kill town
     * @param event Event
     */
    @EventHandler
    public void onDied(TownRuinedEvent event) {
        var town = event.getTown().getUUID();
        town.destroyTown();
    }

    /**
     * Triggers when TownyAPI run new day
     * @param event Event
     */
    @EventHandler
    public void newDay(NewDayEvent event) {
        TownyAPI.getInstance().getTowns().parallelStream().forEach((town) -> PcConomy.GlobalBank.getMainBank().BankBudget += town.getPlotTax());
        PcConomy.GlobalTown.Towns.parallelStream().forEach(Capitalist::newDay);

        PcConomy.GlobalShare.newDay();
        PcConomy.GlobalBank.getMainBank().newDay();
    }
}
