package economy.pcconomy.frontend.mayor;

import economy.pcconomy.PcConomy;

import economy.pcconomy.backend.npc.NpcManager;
import lombok.experimental.ExtensionMethod;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.persistence.PersistentDataType;
import org.j1sk1ss.itemmanager.manager.Manager;


@ExtensionMethod({Manager.class})
public class MayorManagerListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        var player = (Player)event.getPlayer();
        var container = player.getPersistentDataContainer();
        var block = event.getClickedBlock();
        var key = new NamespacedKey(PcConomy.getPlugin(PcConomy.class), "trader-move");

        if (block == null) return;
        if (container.has(key, PersistentDataType.INTEGER)) {
            var id = container.get(key, PersistentDataType.INTEGER);
            if (id == null) return;

            var trader = NpcManager.getNPC(id);
            trader.teleport(block.getLocation().add(0, 1, 0), PlayerTeleportEvent.TeleportCause.ENDER_PEARL);

            container.remove(key);
        }
    }
}
