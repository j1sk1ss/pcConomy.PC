package economy.pcconomy.backend.npc.traits;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.scripts.ItemManager;
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
        if (homeTown.equals("")) homeTown = Objects.requireNonNull(TownyAPI.getInstance().getTown(this.getNPC().getStoredLocation())).getName();

        if (LocalDateTime.now().isAfter(LocalDateTime.parse(Term)) && isRanted) {
            PcConomy.GlobalTownWorker.getTownObject(homeTown).changeBudget(Revenue);

            isRanted = false;
            Owner    = null;
            Revenue  = 0;
            Storage.clear();

            return;
        }

        var player = event.getClicker();

        try {
            if (isRanted)
                if (Owner.equals(player.getUniqueId())) player.openInventory(TraderWindow.getOwnerWindow(player, this));
                else player.openInventory(TraderWindow.getWindow(player, this));
            else
                if (Objects.requireNonNull(TownyAPI.getInstance().getTown(this.getNPC().getStoredLocation())).getMayor()
                        .getUUID().equals(player.getUniqueId())) player.openInventory(TraderWindow.getMayorWindow(player, this));
                else player.openInventory(TraderWindow.getRanterWindow(player, this));
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

        try {
            if (isRanted) {
                if (Owner.equals(playerUUID) && Storage.size() < 27) {
                    player.sendMessage("Напишите свою цену. Учтите наценку города в " + Margin * 100 + "%");
                    chat.put(playerUUID, event.getNPC().getId());
                } else if (Storage.size() >= 27) player.sendMessage("Склад торговца переполнен!");

                return;
            }

            if (Objects.requireNonNull(TownyAPI.getInstance().getTown(homeTown)).getMayor().getUUID().equals(playerUUID)) {
                player.sendMessage("Удалить торговца? (д/н)");
                chat.put(playerUUID, event.getNPC().getId());
            }
        } catch (NullPointerException exception) {
            player.sendMessage("Ошибка, мэр города не был найден! (2)");
        }
    }

    @EventHandler
    public void chatting(AsyncChatEvent event) {
    	if (event.isAsynchronous()) {
    		Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("PcConomy")), () -> {
    			var player = event.getPlayer();
    	        var playerMessage = ((TextComponent) event.originalMessage()).content();
    	        event.setCancelled(true);
    	
    	        if (chat.get(player.getUniqueId()) == null) return;

                var trader = CitizensAPI.getNPCRegistry().getById(chat.get(player.getUniqueId()));
                if (StringUtils.containsAny(playerMessage.toLowerCase(), "дн")) {
                    if (!Objects.requireNonNull(TownyAPI.getInstance().getTown(homeTown)).getMayor().getUUID().equals(player.getUniqueId())) return;
                    if (isRanted) return;

                    if (playerMessage.equalsIgnoreCase("д")) {
                        PcConomy.GlobalNPC.Traders.remove(trader.getId());
                        trader.destroy();
                    }

                    return;
                }

                var sellingItem = player.getInventory().getItemInMainHand();
                if (sellingItem.getType().equals(Material.AIR)) {
                    player.sendMessage("Воздух, пока что, нельзя продавать.");
                    return;
                }

                try {
                    var cost = Double.parseDouble(playerMessage);
                    trader.getOrAddTrait(Trader.class).Storage.add(ItemManager.setLore(sellingItem,
                            cost + cost * Margin + CashManager.currencySigh));
                    player.getInventory().setItemInMainHand(null);
                    chat.remove(player.getUniqueId());

                    player.sendMessage("Предмет " + ItemManager.getName(sellingItem) + " выставлен на продажу за "
                        + (cost + cost * Margin) + " " + CashManager.currencySigh);
                }
                catch (NumberFormatException exception) {
                    player.sendMessage("Напишите корректную цену.");
                }
    		});
    	}
    }
}
