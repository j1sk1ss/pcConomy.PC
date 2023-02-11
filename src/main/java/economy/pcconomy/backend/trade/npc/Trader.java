package economy.pcconomy.backend.trade.npc;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;

import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.scripts.ItemWorker;

import economy.pcconomy.frontend.ui.windows.trade.TraderWindow;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@TraitName("Trader")
public class Trader extends Trait {

    public List<ItemStack> Storage = new ArrayList<>();
    public double Revenue;
    public double Margin;
    public double Cost;
    public boolean isRanted;
    public Town homeTown;
    public Player Owner;

    public Trader() {
        super("Trader");
    }

    @EventHandler
    public void onClick(NPCRightClickEvent event) {
        var player = event.getClicker();

        if (!event.getNPC().equals(this.getNPC())) return;

        homeTown = TownyAPI.getInstance().getTown(this.getNPC().getStoredLocation());

        if (isRanted) {
            if (Owner.equals(player)) {
                player.openInventory(TraderWindow.GetOwnerTraderWindow(player, this));
            } else {
                player.openInventory(TraderWindow.GetTraderWindow(player, this));
            }
        } else {
            if (TownyAPI.getInstance().getTown(this.getNPC().getStoredLocation()).getMayor().getPlayer().equals(player)) {
                player.openInventory(TraderWindow.GetMayorWindow(player, this));
            } else {
                player.openInventory(TraderWindow.GetRanterWindow(player, this));
            }
        }
    }

    private Dictionary<Player, NPC> chat = new Hashtable<>();

    @EventHandler
    public void CreateLot(NPCLeftClickEvent event) {
        var player = event.getClicker();

        if (!event.getNPC().equals(this.getNPC())) return;
        if (isRanted) {
            if (Owner.equals(player)) {
                player.sendMessage("Напишите свою цену. Учтите наценку города в " + Margin * 100 + "%");
                chat.put(player, event.getNPC());
            }
        }
    }

    @EventHandler
    public void Chatting(PlayerChatEvent event) {
        var player = event.getPlayer();
        if (chat.get(player) != null) {
            var sellingItem = player.getInventory().getItemInMainHand();
            if (sellingItem.getType().equals(Material.AIR)) return;

            var cost = Double.parseDouble(event.getMessage());
            chat.get(player).getTrait(Trader.class).Storage.add(ItemWorker.SetLore(sellingItem,
                    cost + cost * Margin + CashWorker.currencySigh));
            player.getInventory().setItemInMainHand(null);
            chat.remove(player);
        }
    }
}
