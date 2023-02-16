package economy.pcconomy.backend.trade.npc;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.scripts.ItemWorker;
import economy.pcconomy.frontend.ui.windows.trade.TraderWindow;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;

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
    public String Term;
    public String homeTown;
    public UUID Owner;

    public Trader() {
        super("Trader");
    }

    @EventHandler
    public void onClick(NPCRightClickEvent event) {
        if (!event.getNPC().equals(this.getNPC())) return;
        if (LocalDateTime.now().isAfter(LocalDateTime.parse(Term)) && isRanted) {
            Term     = "";
            isRanted = false;
            Owner    = null;
            Revenue  = 0;
            Storage.clear();
            return;
        }

        homeTown = TownyAPI.getInstance().getTown(this.getNPC().getStoredLocation()).getName();

        var player = event.getClicker();
        if (isRanted) {
            if (Owner.equals(player.getUniqueId())) {
                player.openInventory(TraderWindow.GetOwnerTraderWindow(player, this));
            } else {
                player.openInventory(TraderWindow.GetTraderWindow(player, this));
            }
        } else {
            if (TownyAPI.getInstance().getTown(this.getNPC().getStoredLocation()).getMayor()
                    .getPlayer().getUniqueId().equals(player.getUniqueId())) {
                player.openInventory(TraderWindow.GetMayorWindow(player, this));
            } else {
                player.openInventory(TraderWindow.GetRanterWindow(player, this));
            }
        }
    }

    private final Dictionary<UUID, Integer> chat = new Hashtable<>();

    @EventHandler
    public void CreateLot(NPCLeftClickEvent event) {
        var player = event.getClicker();

        if (!event.getNPC().equals(this.getNPC())) return;
        if (isRanted) {
            if (Owner.equals(player.getUniqueId())) {
                player.sendMessage("Напишите свою цену. Учтите наценку города в " + Margin * 100 + "%");
                chat.put(player.getUniqueId(), event.getNPC().getId());
            }
        }
    }

    @EventHandler
    public void Chatting(PlayerChatEvent event) {
        var player = event.getPlayer();
        if (chat.get(player.getUniqueId()) != null) {
            var sellingItem = player.getInventory().getItemInMainHand();
            if (sellingItem.getType().equals(Material.AIR)) return;

            var cost = Double.parseDouble(event.getMessage());
           CitizensAPI.getNPCRegistry().getById(chat.get(player.getUniqueId())).getTrait(Trader.class)
                   .Storage.add(ItemWorker.SetLore(sellingItem,
                    cost + cost * Margin + CashWorker.currencySigh));
            player.getInventory().setItemInMainHand(null);
            chat.remove(player.getUniqueId());
        }
    }
}
