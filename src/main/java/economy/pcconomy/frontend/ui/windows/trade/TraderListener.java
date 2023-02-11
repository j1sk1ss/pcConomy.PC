package economy.pcconomy.frontend.ui.windows.trade;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.backend.license.scripts.LicenseWorker;
import economy.pcconomy.backend.npc.NPC;
import economy.pcconomy.backend.scripts.ItemWorker;
import economy.pcconomy.backend.town.scripts.TownWorker;
import economy.pcconomy.backend.trade.npc.Trader;
import economy.pcconomy.frontend.ui.windows.trade.TraderWindow;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;

public class TraderListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() != null)
            if (event.getInventory().getHolder() instanceof Player player1)
                if (player1.equals(player)) {

                    var title = event.getView().getTitle();
                    if (title.contains("Магазин")) return;

                    var trader = GetTraderFromTitle(event.getView().getTitle());
                    var choseItem = event.getCurrentItem();
                    var inventory = event.getInventory();

                    if (trader == null) return;

                    event.setCancelled(true);

                    if (title.contains("Торговец-Покупка"))
                        if (!player.getInventory().contains(choseItem)) {
                            player.openInventory(TraderWindow.GetAcceptWindow(player, choseItem, trader));
                            return;
                        }

                    if (title.contains("Торговец-Управление")) {
                        switch (ItemWorker.GetName(choseItem)) {
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
                        return;
                    }

                    if (title.contains("Торговец-Аренда")) {
                        if (ItemWorker.GetName(choseItem).equals("Арендовать")) {
                            var cash = new Cash();
                            if (PcConomy.GlobalLicenseWorker.GetLicense(player, LicenseType.Trade) != null) {
                                if (!PcConomy.GlobalLicenseWorker.isOverdue(PcConomy.GlobalLicenseWorker
                                        .GetLicense(player, LicenseType.Trade))) {
                                    if (cash.AmountOfCashInInventory(player) < trader.Cost) return;
                                    cash.TakeCashFromInventory(trader.Cost, player);
                                    PcConomy.GlobalTownWorker.GetTownObject(trader.homeTown)
                                            .setBudget(PcConomy.GlobalTownWorker.GetTownObject(trader.homeTown)
                                                    .getBudget() + trader.Cost);

                                    trader.Owner    = player.getUniqueId();
                                    trader.isRanted = true;
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
                            if (PcConomy.GlobalLicenseWorker.GetLicense(player, LicenseType.Trade) != null) {
                                if (!PcConomy.GlobalLicenseWorker.isOverdue(PcConomy.GlobalLicenseWorker
                                        .GetLicense(player, LicenseType.Trade))) {
                                    trader.Owner = player.getUniqueId();
                                    trader.isRanted = true;
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

                            var price = Double.parseDouble(ItemWorker.
                                    GetLore(buyingItem).get(0).replace(CashWorker.currencySigh, ""));
                            if (cash.AmountOfCashInInventory(player) >= price || trader.Owner.equals(player)) {
                                if (trader.Storage.contains(buyingItem)) {
                                    trader.Storage.remove(buyingItem);
                                    ItemWorker.giveItemsWithoutLore(buyingItem, player);

                                    if (!trader.Owner.equals(player)) {
                                        cash.TakeCashFromInventory(price, player);


                                        var endPrice = price / (1 + trader.Margin);

                                        PcConomy.GlobalTownWorker.GetTownObject(trader.homeTown).setBudget((
                                                PcConomy.GlobalTownWorker.GetTownObject(trader.homeTown)).getBudget() + (price - endPrice));
                                        trader.Revenue += endPrice;
                                    }
                                }
                            }

                            player.openInventory(TraderWindow.GetTraderWindow(player, trader));
                        } else if (ItemWorker.GetName(choseItem).equals("ОТМЕНА")){
                            player.openInventory(TraderWindow.GetTraderWindow(player, trader));
                        }
                    }
                }
    }

    private Trader GetTraderFromTitle(String name) {
        if (Arrays.stream(name.split(" ")).toList().size() <= 1) return null;
        var id = Integer.parseInt(name.split(" ")[1]);

        return PcConomy.GlobalNPC.GetNPC(id).getTrait(Trader.class);
    }
 }
