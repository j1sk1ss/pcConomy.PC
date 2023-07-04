package economy.pcconomy.frontend.ui.windows.trade;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.backend.scripts.ItemManager;
import economy.pcconomy.backend.npc.traits.Trader;
import economy.pcconomy.frontend.ui.windows.Window;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.time.LocalDateTime;
import java.util.Arrays;

public class TraderListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();

        if (Window.isThisWindow(event, player)) {
            var title = event.getView().getTitle();
            var trader = GetTraderFromTitle(title);
            if (trader == null) return;

            var choseItem = event.getCurrentItem();
            var inventory = event.getInventory();
            var option = event.getSlot();

            if (choseItem == null) return;

            event.setCancelled(true);

            if (title.contains("Торговец-Покупка"))
                if (!player.getInventory().contains(choseItem)) {
                    player.openInventory(TraderWindow.getAcceptWindow(player, choseItem, trader));
                    return;
                }

            if (title.contains("Торговец-Управление")) {
                switch (TraderWindow.OwnerPanel.click(option).getName()) {
                    case "Перейти в товары" ->
                            player.openInventory(TraderWindow.getWindow(player, trader));
                    case "Забрать все товары" -> {
                        ItemManager.giveItemsWithoutLore(trader.Storage, player);
                        trader.Storage.clear();
                    }
                    case "Забрать прибыль" -> {
                        new CashManager().giveCashToPlayer(trader.Revenue, player);
                        trader.Revenue = 0;
                    }
                }

                return;
            }

            if (title.contains("Торговец-Аренда")) {
                if (TraderWindow.RantedPanel.click(option).getName().equals("Арендовать на один день")) {
                    var cash = new CashManager();
                    var playerTradeLicense =
                            PcConomy.GlobalLicenseWorker.getLicense(player.getUniqueId(), LicenseType.Trade);
                    if (playerTradeLicense == null) return;
                    if (!playerTradeLicense.isOverdue()) {
                        if (cash.amountOfCashInInventory(player) < trader.Cost) return;

                        cash.takeCashFromInventory(trader.Cost, player);
                        PcConomy.GlobalTownWorker.getTownObject(trader.homeTown).changeBudget(trader.Cost);

                        RantTrader(trader, player);
                        player.closeInventory();
                    }
                }

                return;
            }

            if (title.contains("Торговец-Владелец")) {
                switch (TraderWindow.MayorPanel.click(option).getName()) {
                    case "Установить цену" -> player.openInventory(TraderWindow.getPricesWindow(player, trader));
                    case "Установить процент" -> player.openInventory(TraderWindow.getMarginWindow(player, trader));
                    case "Занять" -> {
                        var playerTradeLicense =
                                PcConomy.GlobalLicenseWorker.getLicense(player.getUniqueId(), LicenseType.Trade);
                        if (playerTradeLicense == null) return;
                        if (!playerTradeLicense.isOverdue()) {
                            RantTrader(trader, player);
                            player.closeInventory();
                        }
                    }
                }

                return;
            }

            if (title.contains("Торговец-Цена")) {
                trader.Cost = Double.parseDouble(ItemManager.getName(choseItem).replace(CashManager.currencySigh, ""));
                return;
            }

            if (title.contains("Торговец-Процент")) {
                trader.Margin = Double.parseDouble(ItemManager.getName(choseItem).replace("%", "")) / 100d;
                return;
            }

            if (title.contains("Покупка")) {
                switch (TraderWindow.AcceptPanel.click(option).getName()) {
                    case "Купить" -> {
                        var cash = new CashManager();
                        var buyingItem = inventory.getItem(4);
                        var price = ItemManager.getPriceFromLore(buyingItem, 0);

                        if (cash.amountOfCashInInventory(player) >= price || trader.Owner.equals(player.getUniqueId())) {
                            if (trader.Storage.contains(buyingItem)) {
                                trader.Storage.remove(buyingItem);
                                ItemManager.giveItemsWithoutLore(buyingItem, player);

                                if (!trader.Owner.equals(player.getUniqueId())) {
                                    cash.takeCashFromInventory(price, player);

                                    var endPrice = price / (1 + trader.Margin);
                                    PcConomy.GlobalTownWorker.getTownObject(trader.homeTown).changeBudget(price - endPrice);
                                    trader.Revenue += endPrice;
                                }
                            }
                        }

                        player.openInventory(TraderWindow.getWindow(player, trader));
                    }
                    case "Отмена" -> player.openInventory(TraderWindow.getWindow(player, trader));
                }
            }
        }
    }

    private Trader GetTraderFromTitle(String name) {
        try {
            if (Arrays.stream(name.split(" ")).toList().size() <= 1) return null;
            return PcConomy.GlobalNPC.getNPC(Integer.parseInt(name.split(" ")[1])).getOrAddTrait(Trader.class);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void RantTrader(Trader trader, Player ranter) {
        trader.Owner    = ranter.getUniqueId();
        trader.isRanted = true;
        trader.Term     = LocalDateTime.now().plusDays(1).toString();
    }
}
