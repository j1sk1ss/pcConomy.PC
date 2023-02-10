package economy.pcconomy.frontend.ui.listener;

import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.backend.license.scripts.LicenseWorker;
import economy.pcconomy.backend.npc.NPC;
import economy.pcconomy.backend.scripts.ItemWorker;
import economy.pcconomy.backend.town.scripts.TownWorker;
import economy.pcconomy.backend.trade.npc.Trader;
import economy.pcconomy.frontend.ui.windows.TraderWindow;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CraftingInventory;

import java.util.Arrays;

public class TraderListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() != null)
            if (event.getInventory().getHolder() instanceof Player player1)
                if (player1.equals(player)) {
                    var trader = GetTraderFromTitle(event.getView().getTitle());
                    if (trader == null) return;

                    if (event.getView().getTitle().contains("Торговец-Покупка"))
                        if (!player.getInventory().contains(event.getCurrentItem())) {
                            player.openInventory(TraderWindow.GetAcceptWindow(player, event.getCurrentItem(), trader));
                            event.setCancelled(true);
                        }

                    if (event.getView().getTitle().contains("Торговец-Управление")) {
                        switch (ItemWorker.GetName(event.getCurrentItem())) {
                            case "Перейти в товары" ->
                                    player.openInventory(TraderWindow.GetTraderWindow(player, trader));
                            case "Забрать все товары" -> {
                                ItemWorker.giveItemsWithoutLore(trader.Storage, player);
                                trader.Storage.clear();
                            }
                            case "Забрать прибыль" -> {
                                new Cash().GiveCashToPlayer(trader.Revenue, player);
                                trader.Revenue = 0;
                            }
                        }
                        event.setCancelled(true);
                    }

                    if (event.getView().getTitle().contains("Торговец-Аренда")) {
                        if (ItemWorker.GetName(event.getCurrentItem()).equals("Арендовать")) {
                            var cash = new Cash();
                            if (LicenseWorker.GetLicense(player, LicenseType.Trade) != null) {
                                if (!LicenseWorker.isOverdue(LicenseWorker.GetLicense(player, LicenseType.Trade))) {
                                    if (cash.AmountOfCashInInventory(player) < trader.Cost) return;
                                    cash.TakeCashFromInventory(trader.Cost, player);
                                    TownWorker.GetTownObject(trader.homeTown.getName()).setBudget(
                                            TownWorker.GetTownObject(trader.homeTown.getName()).getBudget() + trader.Cost);

                                    trader.Owner    = player;
                                    trader.isRanted = true;
                                }
                            }
                        }
                        event.setCancelled(true);
                    }

                    if (event.getView().getTitle().contains("Торговец-Владелец")) {
                        if (ItemWorker.GetName(event.getCurrentItem()).equals("Установить цену")) {
                            player.openInventory(TraderWindow.GetPricesWindow(player, trader));
                        }

                        if (ItemWorker.GetName(event.getCurrentItem()).equals("Установить процент")) {
                            player.openInventory(TraderWindow.GetMarginWindow(player, trader));
                        }

                        if (ItemWorker.GetName(event.getCurrentItem()).equals("Занять")) {
                            if (LicenseWorker.GetLicense(player, LicenseType.Trade) != null) {
                                if (!LicenseWorker.isOverdue(LicenseWorker.GetLicense(player, LicenseType.Trade))) {
                                    trader.Owner = player;
                                    trader.isRanted = true;
                                }
                            }
                        }
                        event.setCancelled(true);
                    }

                    if (event.getView().getTitle().contains("Торговец-Цена")) {
                        trader.Cost = Double.parseDouble(ItemWorker.
                                GetName(event.getCurrentItem()).replace(CashWorker.currencySigh, ""));
                        event.setCancelled(true);
                    }

                    if (event.getView().getTitle().contains("Торговец-Процент")) {
                        trader.Margin = Double.parseDouble(ItemWorker.
                                GetName(event.getCurrentItem()).replace("%", "")) / 100d;
                        event.setCancelled(true);
                    }

                    if (event.getView().getTitle().contains("Покупка")) {
                        if (ItemWorker.GetName(event.getCurrentItem()).equals("КУПИТЬ")) {
                            var cash = new Cash();

                            var price = Double.parseDouble(ItemWorker.
                                    GetLore(event.getInventory().getItem(4)).get(0).replace(CashWorker.currencySigh, ""));
                            if (cash.AmountOfCashInInventory(player) >= price || trader.Owner.equals(player)) {
                                if (trader.Storage.contains(event.getInventory().getItem(4))) {
                                    trader.Storage.remove(event.getInventory().getItem(4));
                                    ItemWorker.giveItemsWithoutLore(event.getInventory().getItem(4), player);

                                    if (!trader.Owner.equals(player)) {
                                        cash.TakeCashFromInventory(price, player);


                                        var endPrice = price / (1 + trader.Margin);

                                        TownWorker.GetTownObject(trader.homeTown.getName()).setBudget((
                                                TownWorker.GetTownObject(trader.homeTown.getName())).getBudget() + (price - endPrice));
                                        trader.Revenue += endPrice;
                                    }
                                }
                            }

                            player.openInventory(TraderWindow.GetTraderWindow(player, trader));
                        } else if (ItemWorker.GetName(event.getCurrentItem()).equals("ОТМЕНА")){
                            player.openInventory(TraderWindow.GetTraderWindow(player, trader));
                        }

                        event.setCancelled(true);
                    }
                }
    }

    private Trader GetTraderFromTitle(String name) {
        if (Arrays.stream(name.split(" ")).toList().size() <= 1) return null;
        var id = Integer.parseInt(name.split(" ")[1]);

        return NPC.GetNPC(id).getTrait(Trader.class);
    }
 }
