package economy.pcconomy.frontend.ui.windows.shareholder;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.economy.share.objects.ShareType;
import economy.pcconomy.backend.scripts.items.ItemManager;
import economy.pcconomy.frontend.ui.objects.interactive.Slider;
import economy.pcconomy.frontend.ui.windows.IWindowListener;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Objects;
import java.util.UUID;


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
                    if (town != null)
                        if (PcConomy.GlobalShareManager.InteractionList.contains(town.getUUID())) {
                            player.sendMessage("Ваш город уже работал с акциями сегодня");
                            return;
                        }

                    if (player.equals(Objects.requireNonNull(town).getMayor().getPlayer()))
                        player.openInventory(ShareholderWindow.townSharesWindow(player, town.getUUID()));
                }
            }

            event.setCancelled(true);
        }

        if (windowTitle.contains("Акции-Список")) {
            var item = event.getCurrentItem();
            if (item == null) return;

            var townId = UUID.fromString(ItemManager.getLore(item).get(3).split(" ")[1]);
            player.openInventory(ShareholderWindow.acceptWindow(player, townId));

            event.setCancelled(true);
        }

        if (windowTitle.contains("Акции-Города")) {
            var town = TownyAPI.getInstance().getTown(event.getView().getTitle().split(" ")[1]);
            if (town == null) return;

            var share = PcConomy.GlobalShareManager.getEmptyTownShare(town.getUUID());
            if (share.size() == 0) return;

            event.setCancelled(true);

            switch (ShareholderWindow.ShareHolderMenu.getPanel("Акции-Города").click(option).getName()) {
                case "Продать одну акцию" -> {
                    if (share.get(0).Price > PcConomy.GlobalTownManager.getTown(town.getUUID()).getBudget()) return;
                    PcConomy.GlobalShareManager.sellShare(town.getUUID(), player);
                }
                case "Купить одну акцию" -> {
                    if (share.get(0).Price + share.get(0).Price *
                            PcConomy.GlobalBank.VAT > CashManager.amountOfCashInInventory(player, false)) return;
                    PcConomy.GlobalShareManager.buyShare(town.getUUID(), player);
                }
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
                            Double.parseDouble(ItemManager.getName(costSlider.getChose()).replace(CashManager.currencySigh, "")),
                            Integer.parseInt(ItemManager.getName(countSlider.getChose()).replace("шт.", "")),
                            Double.parseDouble(ItemManager.getName(percentSlider.getChose()).replace("%", "")),
                            (ItemManager.getName(typeSlider.getChose()).equals("Дивиденты") ? ShareType.Dividends : ShareType.Equity));

                }
                case "Снять с продажи" -> {
                    var town = TownyAPI.getInstance().getTown(event.getView().getTitle().split(" ")[1]);
                    if (town == null) return;

                    PcConomy.GlobalShareManager.takeOffShares(town.getUUID());
                }
            }

            event.setCancelled(true);
        }
    }
}
