package economy.pcconomy.backend.trade.npc;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import economy.pcconomy.backend.town.scripts.TownWorker;
import economy.pcconomy.frontend.ui.windows.TraderWindow;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

@TraitName("Trader")
public class Trader extends Trait {

    public List<ItemStack> Storage;
    public double Revenue;
    public double Cost;
    public boolean isRanted;
    public Town homeTown;
    public Player Owner;

    public Trader() {
        super("Trader");
    }

    public Trader(Player player, double cost) {
        super("Trader");

        homeTown = TownyAPI.getInstance().getTown(this.getNPC().getStoredLocation());
        Revenue  = TownWorker.GetTownObject(homeTown.getName()).Margin;
        Owner    = player;
        Cost     = cost;
    }

    @EventHandler
    public void onClick(NPCRightClickEvent event) {
        var player = event.getClicker();

        if (!event.getNPC().equals(this.getNPC())) return;

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
}
