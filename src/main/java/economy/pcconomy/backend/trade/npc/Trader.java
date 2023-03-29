package economy.pcconomy.backend.trade.npc;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.scripts.ItemWorker;
import economy.pcconomy.frontend.ui.windows.trade.TraderWindow;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
//import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.*;

@TraitName("Trader")
public class Trader extends Trait {
    public List<ItemStack> Storage = new ArrayList<>();
    public double Revenue;
    public double Margin;
    public double Cost;
    public boolean isRanted;
    public String Term = LocalDateTime.now().toString();
    public String homeTown = "";
    public UUID Owner;

    public Trader() {
        super("Trader");
    }

    @EventHandler
    public void onClick(NPCRightClickEvent event) {
        if (!event.getNPC().equals(this.getNPC())) return;
        if (homeTown.equals("")) homeTown = TownyAPI.getInstance().getTown(this.getNPC().getStoredLocation()).getName();

        if (LocalDateTime.now().isAfter(LocalDateTime.parse(Term)) && isRanted) {
            PcConomy.GlobalTownWorker.GetTownObject(homeTown).ChangeBudget(Revenue);

            isRanted = false;
            Owner    = null;
            Revenue  = 0;
            Storage.clear();

            return;
        }

        var player = event.getClicker();

        try {
            if (isRanted)
                if (Owner.equals(player.getUniqueId())) player.openInventory(TraderWindow.GetOwnerWindow(player, this));
                else player.openInventory(TraderWindow.GetWindow(player, this));
            else
                if (TownyAPI.getInstance().getTown(this.getNPC().getStoredLocation()).getMayor()
                        .getUUID().equals(player.getUniqueId())) player.openInventory(TraderWindow.GetMayorWindow(player, this));
                else player.openInventory(TraderWindow.GetRanterWindow(player, this));
        }
        catch (NullPointerException e) {
            player.sendMessage("Что-то пошло не так (1).");
        }
    }

    private final Dictionary<UUID, Integer> chat = new Hashtable<>();

    @EventHandler
    public void onInteraction(NPCLeftClickEvent event) {
        if (!event.getNPC().equals(this.getNPC())) return;
        var player = event.getClicker();
        var playerUUID = player.getUniqueId();

        if (isRanted)
            if (Owner.equals(playerUUID) && Storage.size() < 27) {
                player.sendMessage("Напишите свою цену. Учтите наценку города в " + Margin * 100 + "%");
                chat.put(playerUUID, event.getNPC().getId());
            }
            else if (Storage.size() >= 27) player.sendMessage("Склад торговца переполнен!");
        else
            if (TownyAPI.getInstance().getTown(homeTown).getMayor().getUUID().equals(playerUUID)) {
                player.sendMessage("Удалить торговца? (д/н)");
                chat.put(playerUUID, event.getNPC().getId());
            }
    }

    @EventHandler
    public void Chatting(AsyncChatEvent event) {
    	if (event.isAsynchronous()) {
    		Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("PcConomy"), () -> {
    			var player = event.getPlayer();
    	        var playerMessage = ((TextComponent) event.originalMessage()).content();
    	        event.setCancelled(true);
    	
    	        if (chat.get(player.getUniqueId()) != null) {
    	            var trader = CitizensAPI.getNPCRegistry().getById(chat.get(player.getUniqueId()));
    	
    	            if (StringUtils.containsAny(playerMessage, "днДН")) {
    	                if (playerMessage.toLowerCase().contains("д")) {
    	                    PcConomy.GlobalNPC.Traders.remove(trader.getId());
    	                    trader.destroy();
    	                }
    	                return;
    	            }
    	
    	            var sellingItem = player.getInventory().getItemInMainHand();
    	            if (sellingItem.getType().equals(Material.AIR)) {
    	                player.sendMessage("Воздух, пока что, нельзя продавать");
    	                return;
    	            }
    	
    	            try {
    	                var cost = Double.parseDouble(playerMessage);
    	                trader.getOrAddTrait(Trader.class).Storage.add(ItemWorker.SetLore(sellingItem,
    	                        cost + cost * Margin + CashWorker.currencySigh));
    	                player.getInventory().setItemInMainHand(null);
    	                chat.remove(player.getUniqueId());
    	
    	                player.sendMessage("Предмет " + ItemWorker.GetName(sellingItem) + " выставлен на продажу за "
    	                    + (cost + cost * Margin) + " " + CashWorker.currencySigh);
    	            }
    	            catch (NumberFormatException exception) {
    	                player.sendMessage("Напишите корректную цену");
    	            }
    	        }
    		});
    	}
    }
}
