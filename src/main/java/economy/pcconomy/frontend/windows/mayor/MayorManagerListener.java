package economy.pcconomy.frontend.windows.mayor;

import economy.pcconomy.PcConomy;
import economy.pcconomy.frontend.windows.WindowListener;

import lombok.experimental.ExtensionMethod;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.persistence.PersistentDataType;
import org.j1sk1ss.itemmanager.manager.Manager;


@ExtensionMethod({Manager.class})
public class MayorManagerListener extends WindowListener {
    @Override
    public void onClick(InventoryClickEvent event) {
        var player = (Player)event.getWhoClicked();
        var inventory = event.getInventory();

        try {
            var traderId = Integer.parseInt(inventory.getItem(event.getSlot()).getName());
            player.openInventory(MayorManagerWindow.generateTradeControls(player, traderId));
        }
        catch (NumberFormatException ignored) {}
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        var player = (Player)event.getPlayer();
        var container = player.getPersistentDataContainer();
        var block = event.getClickedBlock();
        var key = new NamespacedKey(PcConomy.getPlugin(PcConomy.class), "trader-move");

        if (block == null) return;
        if (container.has(key, PersistentDataType.INTEGER)) {
            var trader = PcConomy.GlobalNPC.getNPC(container.get(key, PersistentDataType.INTEGER));
            trader.teleport(block.getLocation().add(0, 1, 0), PlayerTeleportEvent.TeleportCause.ENDER_PEARL);

            container.remove(key);
        }
    }
}
