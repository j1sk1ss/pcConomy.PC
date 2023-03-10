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

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChatEvent;
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
        if (homeTown.equals(""))
            homeTown = TownyAPI.getInstance().getTown(this.getNPC().getStoredLocation()).getName();

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
            if (isRanted) {
                if (Owner.equals(player.getUniqueId())) {
                    player.openInventory(TraderWindow.GetOwnerWindow(player, this));
                } else {
                    player.openInventory(TraderWindow.GetWindow(player, this));
                }
            } else {
                if (TownyAPI.getInstance().getTown(this.getNPC().getStoredLocation()).getMayor()
                        .getUUID().equals(player.getUniqueId())) {
                    player.openInventory(TraderWindow.GetMayorWindow(player, this));
                } else {
                    player.openInventory(TraderWindow.GetRanterWindow(player, this));
                }
            }
        } catch (NullPointerException e) {
            player.sendMessage("??????-???? ?????????? ???? ?????? (1).");
        }
    }

    private final Dictionary<UUID, Integer> chat = new Hashtable<>();

    @EventHandler
    public void onInteraction(NPCLeftClickEvent event) {
        if (!event.getNPC().equals(this.getNPC())) return;
        var player = event.getClicker();
        var playerUUID = player.getUniqueId();

        if (isRanted) {
            if (Owner.equals(playerUUID) && Storage.size() < 27) {
                player.sendMessage("???????????????? ???????? ????????. ???????????? ?????????????? ???????????? ?? " + Margin * 100 + "%");
                chat.put(playerUUID, event.getNPC().getId());
            } else if (Storage.size() >= 27) player.sendMessage("?????????? ???????????????? ????????????????????!");
        } else {
            if (TownyAPI.getInstance().getTown(homeTown).getMayor().getUUID().equals(playerUUID)) {
                player.sendMessage("?????????????? ????????????????? (y/n)");
                chat.put(playerUUID, event.getNPC().getId());
            }
        }
    }

    @EventHandler
    public void Chatting(PlayerChatEvent event) {
        var player = event.getPlayer();
        var playerMessage = event.getMessage();
        event.setCancelled(true);

        if (chat.get(player.getUniqueId()) != null) {
            var trader = CitizensAPI.getNPCRegistry().getById(chat.get(player.getUniqueId()));
            if (StringUtils.containsAny(playerMessage, "ynYN")) {
                if (playerMessage.toLowerCase().contains("y")) {
                    PcConomy.GlobalNPC.Traders.remove(trader.getId());
                    trader.destroy();
                }
                return;
            }

            var sellingItem = player.getInventory().getItemInMainHand();
            if (sellingItem.getType().equals(Material.AIR)) {
                player.sendMessage("????????????, ???????? ??????, ???????????? ??????????????????");
                return;
            }

            try {
                var cost = Double.parseDouble(playerMessage);
                trader.getTrait(Trader.class).Storage.add(ItemWorker.SetLore(sellingItem,
                        cost + cost * Margin + CashWorker.currencySigh));
                player.getInventory().setItemInMainHand(null);
                chat.remove(player.getUniqueId());

                player.sendMessage("?????????????? " + ItemWorker.GetName(sellingItem) + " ?????????????????? ???? ?????????????? ???? "
                    + (cost + cost * Margin) + " " + CashWorker.currencySigh);
            } catch (NumberFormatException exception) {
                player.sendMessage("???????????????? ???????????????????? ????????");
            }
        }
    }
}
