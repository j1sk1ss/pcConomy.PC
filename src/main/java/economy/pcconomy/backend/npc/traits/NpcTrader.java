package economy.pcconomy.backend.npc.traits;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.frontend.NPCTraderWindow;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.potolotcraft.gorodki.GorodkiUniverse;
import org.bukkit.event.EventHandler;

import java.util.Objects;


@TraitName("NPCTrader")
public class NpcTrader extends Trait {
    public NpcTrader() {
        super("NPCTrader");
    }

    @EventHandler
    public void onClick(NPCRightClickEvent event) {
        var player = event.getClicker();
        if (!event.getNPC().equals(this.getNPC())) return;

        GorodkiUniverse.getInstance().getNPCGorod(TownyAPI.getInstance().getTownUUID(this.getNPC().getStoredLocation())).generateLocalPrices();
        NPCTraderWindow.generateWindow(player, this.getNPC());
    }
}
