package economy.pcconomy.frontend.ui.windows.trade;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.backend.scripts.items.ItemManager;
import economy.pcconomy.backend.npc.traits.Trader;
import economy.pcconomy.frontend.ui.objects.interactive.Slider;
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

        if (Window.isThisWindow(event, player, "Торговец")) {
            var title = event.getView().getTitle();
            var trader = getTraderFromTitle(title);
            if (trader == null) return;

            var choseItem = event.getCurrentItem();
            var inventory = event.getInventory();
            var option = event.getSlot();

            if (choseItem == null) return;

            event.setCancelled(true);

            if (title.contains("Торговец-Ассортимент"))
                if (!player.getInventory().contains(choseItem)) {
                    player.openInventory(TraderWindow.getAcceptWindow(player, choseItem, trader));
                    return;
                }

            if (title.contains("Торговец-Управление")) {
                switch (TraderWindow.OwnerPanel.click(option).getName()) {
                    case "Перейти в товары" -> player.openInventory(TraderWindow.getWindow(player, trader));
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
                            PcConomy.GlobalLicenseManager.getLicense(player.getUniqueId(), LicenseType.Trade);
                    if (playerTradeLicense == null) return;
                    if (!playerTradeLicense.isOverdue()) {
                        if (cash.amountOfCashInInventory(player) < trader.Cost) return;

                        cash.takeCashFromInventory(trader.Cost, player);
                        PcConomy.GlobalTownManager.getTown(trader.HomeTown).changeBudget(trader.Cost);

                        rantTrader(trader, player);
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
                                PcConomy.GlobalLicenseManager.getLicense(player.getUniqueId(), LicenseType.Trade);
                        if (playerTradeLicense == null) return;
                        if (!playerTradeLicense.isOverdue()) {
                            rantTrader(trader, player);
                            player.closeInventory();
                        }
                    }
                }

                return;
            }

            if (title.contains("Торговец-Цена")) {
                switch (TraderWindow.PricePanel.click(option).getName()) {
                    case "Slider" -> {
                        var slider = new Slider((Slider)TraderWindow.PricePanel.click(option));

                        slider.setChose(option);
                        slider.place(event.getInventory());
                    }
                    case "Установить" -> {
                        var slider = new Slider(TraderWindow.PricePanel.getSliders().get(0));

                        trader.Cost = Double.parseDouble(ItemManager.getName(slider.getChose()).replace(CashManager.currencySigh, ""));
                        player.sendMessage("Цена установлена!");
                    }
                    case "Отмена" -> player.closeInventory();
                }

                return;
            }

            if (title.contains("Торговец-Процент")) {
                switch (TraderWindow.MarginPanel.click(option).getName()) {
                    case "Slider" -> {
                        var slider = new Slider((Slider)TraderWindow.MarginPanel.click(option));

                        slider.setChose(option);
                        slider.place(event.getInventory());
                    }
                    case "Установить" -> {
                        var slider = new Slider(TraderWindow.MarginPanel.getSliders().get(0));

                        trader.Margin = Double.parseDouble(ItemManager.getName(slider.getChose()).replace("%", ""));
                        player.sendMessage("Процент установлен!");
                    }
                    case "Отмена" -> player.closeInventory();
                }

                return;
            }

            if (title.contains("Торговец-Покупка")) {
                switch (TraderWindow.AcceptPanel.click(option).getName()) {
                    case "Купить" -> {
                        var buyingItem = inventory.getItem(13);
                        var price = ItemManager.getPriceFromLore(buyingItem, 0);

                        if (CashManager.amountOfCashInInventory(player) >= price || trader.Owner.equals(player.getUniqueId())) {
                            if (trader.Storage.contains(buyingItem)) {
                                trader.Storage.remove(buyingItem);
                                ItemManager.giveItemsWithoutLore(buyingItem, player);

                                if (!trader.Owner.equals(player.getUniqueId())) {
                                    CashManager.takeCashFromInventory(price, player);

                                    var endPrice = price / (1 + trader.Margin);
                                    PcConomy.GlobalTownManager.getTown(trader.HomeTown).changeBudget(price - endPrice);
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

    private Trader getTraderFromTitle(String name) {
        try {
            if (Arrays.stream(name.split(" ")).toList().size() <= 1) return null;
            return PcConomy.GlobalNPC.getNPC(Integer.parseInt(name.split(" ")[1])).getOrAddTrait(Trader.class);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void rantTrader(Trader trader, Player ranter) {
        trader.Owner    = ranter.getUniqueId();
        trader.IsRanted = true;
        trader.Term     = LocalDateTime.now().plusDays(1).toString();
    }
}
