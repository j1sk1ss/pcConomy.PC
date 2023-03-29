package economy.pcconomy.frontend.ui.windows.trade;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.backend.scripts.ItemWorker;
import economy.pcconomy.backend.trade.npc.Trader;

import economy.pcconomy.frontend.ui.Window;
import net.kyori.adventure.text.TextComponent;
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
            var title = ((TextComponent) event.getView().title()).content();
            var trader = GetTraderFromTitle(title);
            if (trader == null) return;

            var choseItem = event.getCurrentItem();
            var inventory = event.getInventory();

            event.setCancelled(true);

            if (title.contains("Торговец-Покупка"))
                if (!player.getInventory().contains(choseItem)) {
                    player.openInventory(TraderWindow.GetAcceptWindow(player, choseItem, trader));
                    return;
                }

            if (title.contains("Торговец-Управление")) {
                switch (ItemWorker.GetName(choseItem)) {
                    case "Перейти в товары" ->
                            player.openInventory(TraderWindow.GetWindow(player, trader));
                    case "Забрать все товары" -> {
                        ItemWorker.GiveItemsWithoutLore(trader.Storage, player);
                        trader.Storage.clear();
                    }
                    case "Забрать прибыль" -> {
                        new Cash().GiveCashToPlayer(trader.Revenue, player);
                        trader.Revenue = 0;
                    }
                }
                return;
            }

            if (title.contains("Торговец-Аренда")) {
                if (ItemWorker.GetName(choseItem).equals("Арендовать на один день")) {
                    var cash = new Cash();
                    var playerTradeLicense =
                            PcConomy.GlobalLicenseWorker.GetLicense(player.getUniqueId(), LicenseType.Trade);

                    if (playerTradeLicense != null) {
                        if (!PcConomy.GlobalLicenseWorker.isOverdue(playerTradeLicense)) {
                            if (cash.AmountOfCashInInventory(player) < trader.Cost) return;

                            cash.TakeCashFromInventory(trader.Cost, player);
                            PcConomy.GlobalTownWorker.GetTownObject(trader.homeTown).ChangeBudget(trader.Cost);

                            RantTrader(trader, player);
                            player.closeInventory();
                        }
                    }
                }
                return;
            }

            if (title.contains("Торговец-Владелец")) {
                if (ItemWorker.GetName(choseItem).equals("Установить цену")) {
                    player.openInventory(TraderWindow.GetPricesWindow(player, trader));
                }

                if (ItemWorker.GetName(choseItem).equals("Установить процент")) {
                    player.openInventory(TraderWindow.GetMarginWindow(player, trader));
                }

                if (ItemWorker.GetName(choseItem).equals("Занять")) {
                    var playerTradeLicense =
                            PcConomy.GlobalLicenseWorker.GetLicense(player.getUniqueId(), LicenseType.Trade);

                    if (playerTradeLicense != null) {
                        if (!PcConomy.GlobalLicenseWorker.isOverdue(playerTradeLicense)) {
                            RantTrader(trader, player);
                            player.closeInventory();
                        }
                    }
                }
                return;
            }

            if (title.contains("Торговец-Цена")) {
                trader.Cost = Double.parseDouble(ItemWorker.
                        GetName(choseItem).replace(CashWorker.currencySigh, ""));
                return;
            }

            if (title.contains("Торговец-Процент")) {
                trader.Margin = Double.parseDouble(ItemWorker.
                        GetName(choseItem).replace("%", "")) / 100d;
                return;
            }

            if (title.contains("Покупка")) {
                if (ItemWorker.GetName(choseItem).equals("КУПИТЬ")) {
                    var cash = new Cash();
                    var buyingItem = inventory.getItem(4);
                    var price = ItemWorker.GetPriceFromLore(buyingItem, 0);

                    if (cash.AmountOfCashInInventory(player) >= price || trader.Owner.equals(player.getUniqueId())) {
                        if (trader.Storage.contains(buyingItem)) {
                            trader.Storage.remove(buyingItem);
                            ItemWorker.GiveItemsWithoutLore(buyingItem, player);

                            if (!trader.Owner.equals(player.getUniqueId())) {
                                cash.TakeCashFromInventory(price, player);

                                var endPrice = price / (1 + trader.Margin);
                                PcConomy.GlobalTownWorker.GetTownObject(trader.homeTown)
                                        .ChangeBudget(price - endPrice);
                                trader.Revenue += endPrice;
                            }
                        }
                    }

                    player.openInventory(TraderWindow.GetWindow(player, trader));
                } else if (ItemWorker.GetName(choseItem).equals("ОТМЕНА")){
                    player.openInventory(TraderWindow.GetWindow(player, trader));
                }
            }
        }
    }

    private Trader GetTraderFromTitle(String name) {
        try {
            if (Arrays.stream(name.split(" ")).toList().size() <= 1) return null;

            var id = Integer.parseInt(name.split(" ")[1]);
            return PcConomy.GlobalNPC.GetNPC(id).getOrAddTrait(Trader.class);
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
