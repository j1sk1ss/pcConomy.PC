package economy.pcconomy.frontend.ui.windows.trade;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.backend.scripts.items.ItemManager;
import economy.pcconomy.backend.npc.traits.Trader;
import economy.pcconomy.frontend.ui.objects.interactive.Slider;

import economy.pcconomy.frontend.ui.windows.IWindowListener;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;


public class TraderListener implements IWindowListener {
    public void onClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();
        var title = event.getView().getTitle();
        var trader = getTraderFromTitle(title);
        if (trader == null) return;

        var choseItem = event.getCurrentItem();
        var inventory = event.getInventory();
        var option = event.getSlot();
        if (choseItem == null) return;

        if (title.contains("Торговец-Ассортимент"))
            if (!player.getInventory().contains(choseItem))
                player.openInventory(TraderWindow.getAcceptWindow(player, choseItem, trader));

        else if (title.contains("Торговец-Управление")) {
            switch (TraderWindow.TraderMenu.getPanel("Торговец-Управление").click(option).getName()) {
                case "Перейти в товары" -> player.openInventory(TraderWindow.getWindow(player, trader));
                case "Забрать все товары" -> {
                    ItemManager.giveItemsWithoutLore(trader.Storage, player);
                    trader.Storage.clear();
                }
                case "Забрать прибыль" -> {
                    CashManager.giveCashToPlayer(trader.Revenue, player, false);
                    trader.Revenue = 0;
                }
            }
        }

        else if (title.contains("Торговец-Аренда-Время")) {
            var days = Integer.parseInt(ItemManager.getName(choseItem).split(" ")[0]);
            if (CashManager.amountOfCashInInventory(player, false) < trader.Cost * days) return;
            CashManager.takeCashFromPlayer(trader.Cost * days, player, false);
            PcConomy.GlobalTownManager.getTown(trader.HomeTown).changeBudget(trader.Cost * days);

            rantTrader(trader, days, player);
            player.closeInventory();
        }

        else if (title.contains("Торговец-Аренда")) {
            if (TraderWindow.TraderMenu.getPanel("Торговец-Аренда").click(option).getName().equals("Арендовать на один день")) {
                var playerTradeLicense =
                        PcConomy.GlobalLicenseManager.getLicense(player.getUniqueId(), LicenseType.Trade);
                if (playerTradeLicense == null) return;
                if (!playerTradeLicense.isOverdue())
                    player.openInventory(TraderWindow.getExtendedRantedWindow(player, trader));
            }
        }

        else if (title.contains("Торговец-Владелец")) {
            switch (TraderWindow.TraderMenu.getPanel("Торговец-Владелец").click(option).getName()) {
                case "Установить цену" -> player.openInventory(TraderWindow.getPricesWindow(player, trader));
                case "Установить процент" -> player.openInventory(TraderWindow.getMarginWindow(player, trader));
                case "Занять" -> {
                    var playerTradeLicense =
                            PcConomy.GlobalLicenseManager.getLicense(player.getUniqueId(), LicenseType.Trade);
                    if (playerTradeLicense == null) return;
                    if (!playerTradeLicense.isOverdue()) rantTrader(trader, 1, player);
                }
            }
        }

        else if (title.contains("Торговец-Цена")) {
            switch (TraderWindow.TraderMenu.getPanel("Торговец-Цена").click(option).getName()) {
                case "Slider" -> {
                    var slider = new Slider((Slider)TraderWindow.TraderMenu.getPanel("Торговец-Цена").click(option));

                    slider.setChose(option);
                    slider.place(event.getInventory());
                }

                case "Установить" -> {
                    var slider = new Slider(TraderWindow.TraderMenu.getPanel("Торговец-Цена").getSliders().get(0), event.getInventory());
                    if (slider.getChose() == null) return;

                    trader.Cost = Double.parseDouble(ItemManager.getName(slider.getChose()).replace(CashManager.currencySigh, ""));
                    player.sendMessage("Цена установлена!");
                }

                case "Отмена" -> player.closeInventory();
            }
        }

        else if (title.contains("Торговец-Процент")) {
            switch (TraderWindow.TraderMenu.getPanel("Торговец-Процент").click(option).getName()) {
                case "Slider" -> {
                    var slider = new Slider((Slider)TraderWindow.TraderMenu.getPanel("Торговец-Процент").click(option));

                    slider.setChose(option);
                    slider.place(event.getInventory());
                }

                case "Установить" -> {
                    var slider = new Slider(TraderWindow.TraderMenu.getPanel("Торговец-Процент").getSliders("Slider"), event.getInventory());
                    if (slider.getChose() == null) return;

                    trader.Margin = Double.parseDouble(ItemManager.getName(slider.getChose()).replace("%", ""));
                    player.sendMessage("Процент установлен!");
                }

                case "Отмена" -> player.closeInventory();
            }
        }

        else if (title.contains("Торговец-Покупка")) {
            switch (TraderWindow.TraderMenu.getPanel("Торговец-Покупка").click(option).getName()) {
                case "Купить" -> {
                    var buyingItem = inventory.getItem(13);
                    var price = ItemManager.getPriceFromLore(buyingItem, 0);

                    if (CashManager.amountOfCashInInventory(player, false) >= price || trader.Owner.equals(player.getUniqueId())) {
                        if (trader.Storage.contains(buyingItem)) {
                            trader.Storage.remove(buyingItem);
                            ItemManager.giveItemsWithoutLore(buyingItem, player);

                            if (!trader.Owner.equals(player.getUniqueId())) {
                                CashManager.takeCashFromPlayer(price, player, false);

                                var endPrice = price / (1 + trader.Margin);
                                PcConomy.GlobalTownManager.getTown(trader.HomeTown).changeBudget(price - endPrice);
                                trader.Revenue += endPrice;

                                if (TownyAPI.getInstance().getTown(player) != null)
                                    if (trader.SpecialList.contains(Objects.requireNonNull(TownyAPI.getInstance().getTown(player)).getUUID())) {
                                        CashManager.giveCashToPlayer(price - endPrice, player, false);
                                        PcConomy.GlobalTownManager.getTown(trader.HomeTown).changeBudget(-(price - endPrice));
                                        player.sendMessage("Так как вы состоите в торговом союзе, пошлина была " +
                                                "компенсированна городом");
                                    }
                            }
                        }
                    }

                    player.openInventory(TraderWindow.getWindow(player, trader));
                }

                case "Отмена" -> player.openInventory(TraderWindow.getWindow(player, trader));
            }
        }
    }

    private static Trader getTraderFromTitle(String name) {
        try {
            if (Arrays.stream(name.split(" ")).toList().size() <= 1) return null;
            return PcConomy.GlobalNPC.getNPC(Integer.parseInt(name.split(" ")[1])).getOrAddTrait(Trader.class);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static void rantTrader(Trader trader, int days, Player ranter) {
        trader.Owner    = ranter.getUniqueId();
        trader.IsRanted = true;
        trader.Term     = LocalDateTime.now().plusDays(days).toString();
    }
}
