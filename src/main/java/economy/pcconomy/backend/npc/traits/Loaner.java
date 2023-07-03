package economy.pcconomy.backend.npc.traits;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.frontend.ui.windows.Window;
import economy.pcconomy.frontend.ui.windows.loans.loan.LoanWindow;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

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

        Window.OpenWindow(player, new LoanWindow());
    }

    @EventHandler
    public void onInteraction(NPCLeftClickEvent event) {
        if (!event.getNPC().equals(this.getNPC())) return;
        var player = event.getClicker();
        var playerUUID = player.getUniqueId();
        var homeTown = TownyAPI.getInstance().getTownName(player.getLocation());

        if (TownyAPI.getInstance().getTown(homeTown).getMayor().getUUID().equals(playerUUID)) {
            player.sendMessage("Удалить кредитора? (д/н)");
            chat.put(playerUUID, event.getNPC().getId());
        }
    }

    private final Dictionary<UUID, Integer> chat = new Hashtable<>();

    @EventHandler
    public void Chatting(AsyncChatEvent event) {
    	if (event.isAsynchronous()) {
    		Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("PcConomy"), () -> {
    			var player = event.getPlayer();
    	        var playerMessage = ((TextComponent) event.originalMessage()).content();
    	        event.setCancelled(true);
    	
    	        if (chat.get(player.getUniqueId()) != null) {
    	            var loaner = CitizensAPI.getNPCRegistry().getById(chat.get(player.getUniqueId()));
    	            if (StringUtils.containsAny(playerMessage.toLowerCase(), "дн")) {
    	                if (playerMessage.equalsIgnoreCase("д")) {
    	                    loaner.destroy();
    	                }
    	            }
    	        }
    		});
    	}
    }

}
