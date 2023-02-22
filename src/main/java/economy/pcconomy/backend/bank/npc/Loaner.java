package economy.pcconomy.backend.bank.npc;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.scripts.ItemWorker;
import economy.pcconomy.backend.trade.npc.Trader;
import economy.pcconomy.frontend.ui.Window;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.UUID;

@TraitName("Loaner")
public class Loaner extends Trait {
    public Loaner() {
        super("Loaner");
    }

    @EventHandler
    public void onClick(NPCRightClickEvent event) {
        if (!event.getNPC().equals(this.getNPC())) return;
        var player = event.getClicker();

        Window.OpenLoanWindow(player, false);
    }

    @EventHandler
    public void onInteraction(NPCLeftClickEvent event) {
        if (!event.getNPC().equals(this.getNPC())) return;
        var player = event.getClicker();
        var playerUUID = player.getUniqueId();
        var homeTown = TownyAPI.getInstance().getTownName(player.getLocation());

        if (TownyAPI.getInstance().getTown(homeTown).getMayor().getUUID().equals(playerUUID)) {
            player.sendMessage("Удалить кредитора? (y/n)");
            chat.put(playerUUID, event.getNPC().getId());
        }
    }

    private final Dictionary<UUID, Integer> chat = new Hashtable<>();

    @EventHandler
    public void Chatting(PlayerChatEvent event) {
        var player = event.getPlayer();
        var playerMessage = event.getMessage();
        event.setCancelled(true);

        if (chat.get(player.getUniqueId()) != null) {
            var loaner = CitizensAPI.getNPCRegistry().getById(chat.get(player.getUniqueId()));
            if (StringUtils.containsAny(playerMessage, "ynYN")) {
                if (playerMessage.toLowerCase().contains("y")) {
                    loaner.destroy();
                }
            }
        }
    }

}
