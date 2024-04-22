package economy.pcconomy.frontend.ui.windows.shareholder;

import economy.pcconomy.backend.scripts.items.ItemManager;
import economy.pcconomy.frontend.ui.objects.interactive.Slider;
import economy.pcconomy.frontend.ui.windows.IWindowListener;

import lombok.experimental.ExtensionMethod;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.UUID;


@ExtensionMethod({ItemManager.class})
public class ShareholderListener implements IWindowListener {
    @SuppressWarnings("deprecation")
    public void onClick(InventoryClickEvent event) {
        var windowTitle = event.getView().getTitle();
        var player = (Player) event.getWhoClicked();
        var option = event.getSlot();

        if (windowTitle.contains("Акции-Меню")) {
            ShareholderWindow.ShareHolderMenu.getPanel("Акции-Меню").click(event);
        }

        if (windowTitle.contains("Акции-Список")) {
            var item = event.getCurrentItem();
            if (item == null) return;

            var townId = UUID.fromString(item.getLoreLines().get(3).split(" ")[1]);
            player.openInventory(ShareholderWindow.acceptWindow(player, townId));

            event.setCancelled(true);
        }

        if (windowTitle.contains("Акции-Города")) {
            ShareholderWindow.ShareHolderMenu.getPanel("Акции-Города").click(event);
        }

        if (windowTitle.contains("Акции-Выставление")) {
            var townSharesPanel = ShareholderWindow.ShareHolderMenu.getPanel("Акции-Выставление");
            if (townSharesPanel.click(option).getName().contains("Slider")) {
                var slider = new Slider((Slider)townSharesPanel.click(option));
                slider.setChose(option);
                slider.place(event.getInventory());
            }

            townSharesPanel.click(event);
        }
    }
}
