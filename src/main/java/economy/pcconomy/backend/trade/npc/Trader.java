package economy.pcconomy.backend.trade.npc;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.npc.NPC;
import economy.pcconomy.backend.scripts.ItemWorker;
import economy.pcconomy.backend.town.scripts.TownWorker;
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
            PcConomy.GlobalTownWorker.GetTownObject(homeTown)
                    .setBudget(PcConomy.GlobalTownWorker.GetTownObject(homeTown).getBudget() + Revenue);

            isRanted = false;
            Owner    = null;
            Revenue  = 0;
            Storage.clear();
            return;
        }

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
        } else {
            if (TownyAPI.getInstance().getTown(homeTown).getMayor().getPlayer().equals(player)) {
                player.sendMessage("Удалить торговца? (y/n)");
                chat.put(player.getUniqueId(), event.getNPC().getId());
            }
        }
    }

    @EventHandler
    public void Chatting(PlayerChatEvent event) {
        var player = event.getPlayer();

        if (chat.get(player.getUniqueId()) != null) {
            var trader = CitizensAPI.getNPCRegistry().getById(chat.get(player.getUniqueId()));
            if (StringUtils.containsAny(event.getMessage(), "ynYN")) {
                if (event.getMessage().toLowerCase().contains("y")) trader.destroy();
                return;
            }

            var sellingItem = player.getInventory().getItemInMainHand();
            if (sellingItem.getType().equals(Material.AIR)) {
                player.sendMessage("Воздух, пока что, нельзя продавать");
                return;
            }

            try {
                var cost = Double.parseDouble(event.getMessage());
                trader.getTrait(Trader.class).Storage.add(ItemWorker.SetLore(sellingItem,
                        cost + cost * Margin + CashWorker.currencySigh));
                player.getInventory().setItemInMainHand(null);
                chat.remove(player.getUniqueId());
            } catch (NumberFormatException exception) {
                player.sendMessage("Напишите корректную цену");
            }
        }
    }
}
