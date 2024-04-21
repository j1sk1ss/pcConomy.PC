package economy.pcconomy.frontend.ui.windows.shareholder;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.economy.share.objects.Share;
import economy.pcconomy.backend.economy.share.objects.ShareType;
import economy.pcconomy.backend.scripts.items.ItemManager;
import economy.pcconomy.frontend.ui.objects.interactive.Slider;
import economy.pcconomy.frontend.ui.windows.IWindowListener;

import lombok.experimental.ExtensionMethod;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;


@ExtensionMethod({ItemStack.class, ItemManager.class})
public class ShareholderListener implements IWindowListener {
    @SuppressWarnings("deprecation")
    public void onClick(InventoryClickEvent event) {
        var windowTitle = event.getView().getTitle();
        var player = (Player) event.getWhoClicked();
        var option = event.getSlot();

        if (windowTitle.contains("Акции-Меню")) {
            switch (ShareholderWindow.ShareHolderMenu.getPanel("Акции-Меню").click(option).getName()) {
                case "Покупка/продажа акций" -> player.openInventory(ShareholderWindow.sharesWindow(player, 0));
                case "Выставление акций" -> {
                    var town = TownyAPI.getInstance().getTown(player);
                    // if (town != null) TODO: Uncomment after test
                    //     if (PcConomy.GlobalShareManager.InteractionList.contains(town.getUUID())) {
                    //         player.sendMessage("Ваш город уже работал с акциями сегодня");
                    //         return;
                    //     }

                    if (player.equals(Objects.requireNonNull(town).getMayor().getPlayer()))
                        player.openInventory(ShareholderWindow.townSharesWindow(player, town.getUUID()));
                }
            }

            event.setCancelled(true);
        }

        if (windowTitle.contains("Акции-Список")) {
            var item = event.getCurrentItem();
            if (item == null) return;

            var townId = UUID.fromString(item.getLoreLines().get(3).split(" ")[1]);
            player.openInventory(ShareholderWindow.acceptWindow(player, townId));

            event.setCancelled(true);
        }

        if (windowTitle.contains("Акции-Города")) {
            var town = TownyAPI.getInstance().getTown(event.getView().getTitle().split(" ")[1]);
            if (town == null) return;

            switch (ShareholderWindow.ShareHolderMenu.getPanel("Акции-Города").click(option).getName()) {
                case "Продать одну акцию" -> {
                    var share = new Share(player.getInventory().getItemInMainHand());
                    if (share.Price > PcConomy.GlobalTownManager.getTown(town.getUUID()).getBudget()) return;
                    share.sellShare(player, player.getInventory().getItemInMainHand());
                }
                case "Купить одну акцию" -> {
                    var shares = PcConomy.GlobalShareManager.getEmptyTownShare(town.getUUID());
                    var share  = shares.get(0);

                    if (PcConomy.GlobalBank.checkVat(share.Price) > CashManager.amountOfCashInInventory(player, false)) return;
                    share.buyShare(player);
                }
                // TODO: Cash out operation
            }
        }

        if (windowTitle.contains("Акции-Выставление")) {
            var townSharesPanel = ShareholderWindow.ShareHolderMenu.getPanel("Акции-Выставление");
            if (townSharesPanel.click(option).getName().contains("Slider")) {
                var slider = new Slider((Slider)townSharesPanel.click(option));

                slider.setChose(option);
                slider.place(event.getInventory());
            }

            switch (townSharesPanel.click(option).getName()) {
                case "Выставить на продажу" -> {
                    var town = TownyAPI.getInstance().getTown(event.getView().getTitle().split(" ")[1]);
                    if (town == null) return;

                    var countSlider = new Slider(townSharesPanel.getSliders("SliderCount"), event.getInventory());
                    var percentSlider = new Slider(townSharesPanel.getSliders("SliderPercent"), event.getInventory());
                    var costSlider = new Slider(townSharesPanel.getSliders("SliderCost"), event.getInventory());
                    var typeSlider = new Slider(townSharesPanel.getSliders("SliderType"), event.getInventory());

                    if (costSlider.getChose() == null || countSlider.getChose() == null ||
                            percentSlider.getChose() == null || typeSlider.getChose() == null) return;
                    PcConomy.GlobalShareManager.exposeShares(
                            town.getUUID(),
                            Double.parseDouble(costSlider.getChose().getName().replace(CashManager.currencySigh, "")),
                            Integer.parseInt(costSlider.getChose().getName().replace("шт.", "")),
                            Double.parseDouble(percentSlider.getName().replace("%", "")),
                            (typeSlider.getName().equals("Дивиденты") ? ShareType.Dividends : ShareType.Equity));

                    player.sendMessage("Акции города выставлены на продажу");

                }
                case "Снять с продажи" -> {
                    var town = TownyAPI.getInstance().getTown(event.getView().getTitle().split(" ")[1]);
                    if (town == null) return;

                    PcConomy.GlobalShareManager.takeOffShares(town.getUUID());
                    player.sendMessage("Акции города сняты с продажы");
                }
            }

            event.setCancelled(true);
        }
    }
}
