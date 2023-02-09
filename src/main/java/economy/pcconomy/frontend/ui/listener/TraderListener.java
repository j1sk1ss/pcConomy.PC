package economy.pcconomy.frontend.ui.listener;

import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.scripts.ItemWorker;
import economy.pcconomy.backend.town.scripts.TownWorker;
import economy.pcconomy.backend.trade.npc.Trader;
import economy.pcconomy.backend.trade.scripts.TraderWorker;
import economy.pcconomy.frontend.ui.windows.TraderWindow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TraderListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() != null)
            if (event.getInventory().getHolder() instanceof Player player1)
                if (player1.equals(player)) {
                    var trader = GetTraderFromTitle(event.getView().getTitle());
                    if (trader == null) return;

                    if (event.getView().getTitle().contains("Торговец-Покупка")) {
                        player.openInventory(TraderWindow.GetAcceptWindow(player, event.getCurrentItem(), trader));
                        event.setCancelled(true);
                    }

                    if (event.getView().getTitle().contains("Торговец-Управление")) {
                        switch (ItemWorker.GetName(event.getCurrentItem())) {
                            case "Перейти в товары":
                                player.openInventory(TraderWindow.GetTraderWindow(player, trader));
                            case "Забрать все товары":
                                ItemWorker.giveItems(trader.Storage, player);
                            case "Забрать прибыль":
                                new Cash().GiveCashToPlayer(trader.Revenue, player);
                        }
                        event.setCancelled(true);
                    }

                    if (event.getView().getTitle().contains("Торговец-Аренда")) {
                        if (ItemWorker.GetName(event.getCurrentItem()).equals("Арендовать")) {
                            var cash = new Cash();

                            if (cash.AmountOfCashInInventory(player) < trader.Cost) return;
                            cash.TakeCashFromInventory(trader.Cost, player);
                            TownWorker.GetTownObject(trader.homeTown.getName()).setBudget(
                                    TownWorker.GetTownObject(trader.homeTown.getName()).getBudget() + trader.Cost);

                            trader.Owner = player;
                            trader.isRanted = true;
                        }
                        event.setCancelled(true);
                    }

                    if (event.getView().getTitle().contains("Торговец-Владелец")) {
                        if (ItemWorker.GetName(event.getCurrentItem()).equals("Установить цену")) {
                            player.openInventory(TraderWindow.GetPricesWindow(player, trader));
                        }
                        event.setCancelled(true);
                    }

                    if (event.getView().getTitle().contains("Торговец-Цена")) {
                        trader.Cost = Double.parseDouble(ItemWorker.
                                GetName(event.getCurrentItem()).replace(CashWorker.currencySigh, ""));
                        event.setCancelled(true);
                    }

                    if (event.getView().getTitle().contains("Покупка")) {
                        if (ItemWorker.GetName(event.getCurrentItem()).equals("КУПИТЬ")) {
                            var cash = new Cash();

                            var price = Double.parseDouble(ItemWorker.
                                    GetLore(event.getCurrentItem()).get(0).replace(CashWorker.currencySigh, ""));
                            if (cash.AmountOfCashInInventory(player) < price) return;

                            ItemWorker.giveItems(event.getCurrentItem(), player);
                            trader.Storage.remove(event.getCurrentItem());

                            cash.TakeCashFromInventory(price, player);
                            trader.Revenue += price;
                        } else {
                            player.openInventory(TraderWindow.GetAcceptWindow(player, event.getCurrentItem(), trader));
                        }
                        event.setCancelled(true);
                    }

                    TraderWorker.SetTrader(trader);
                }
    }

    private Trader GetTraderFromTitle(String title) {
        var name = title.replace("Покупка ", "");
        return TraderWorker.GetTrader(name);
    }
 }
