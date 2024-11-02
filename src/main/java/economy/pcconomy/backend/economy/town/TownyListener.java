package economy.pcconomy.backend.economy.town;

import com.palmergames.bukkit.towny.event.*;
import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import net.potolotcraft.gorodki.GorodkiUniverse;
import net.potolotcraft.gorodki.objects.goroda.NPCGorod;


public class TownyListener implements Listener {
    /**
     * Triggers when TownyAPI run new day
     * @param event Event
     */
    @EventHandler
    public void newDay(NewDayEvent event) {
        TownyAPI.getInstance().getTowns().parallelStream().forEach((town) -> PcConomy.GlobalBank.getBank().changeBudget(town.getPlotTax()));
        GorodkiUniverse.getInstance().getNPCGorods().parallelStream().forEach(NPCGorod::newDay);
        PcConomy.GlobalShare.newDay();
        PcConomy.GlobalBank.getBank().newDay();
    }
}
