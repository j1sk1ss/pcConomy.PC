package economy.pcconomy.frontend.wallet;

import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.cash.Wallet;
import lombok.experimental.ExtensionMethod;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import org.j1sk1ss.itemmanager.manager.Item;
import org.j1sk1ss.itemmanager.manager.Manager;
import org.j1sk1ss.menuframework.objects.MenuSizes;
import org.j1sk1ss.menuframework.objects.MenuWindow;
import org.j1sk1ss.menuframework.objects.interactive.components.ClickArea;
import org.j1sk1ss.menuframework.objects.interactive.components.Panel;

import java.util.Arrays;


@ExtensionMethod({Manager.class, Cash.class})
public class WalletWindow {
    public static MenuWindow WalletWindow = new MenuWindow(
        Arrays.asList(
            new Panel(
                Arrays.asList(
                    new ClickArea(
                        0, 8,
                        (event) -> {
                            var player = (Player) event.getWhoClicked();
                            var currentItem = player.getInventory().getItemInMainHand();
                            if (currentItem.getAmount() > 1 && Wallet.isWallet(currentItem)) {
                                player.sendMessage("Выберите один кошелёк");
                                event.setCancelled(true);
                                return;
                            }

                            var wallet = Wallet.isWallet(currentItem) ? new Wallet(player.getInventory().getItemInMainHand()) : null;
                            if (wallet == null) return;

                            player.getInventory().setItemInMainHand(null);
                            var option = event.getCurrentItem();
                            if (option == null) return;

                            if (option.getLoreLines().size() < 2) return;
                            var amount = option.getDoubleFromContainer("item-wallet-value");

                            player.takeCashFromPlayer(Math.abs(amount), true);
                            wallet.changeCashInWallet(Math.abs(amount));

                            player.closeInventory();
                            wallet.giveWallet(player);
                        }
                    )
                ), "Кошелёк-Внесение", MenuSizes.OneLine
            ),
            new Panel(
                Arrays.asList(
                    new ClickArea(
                        0, 8,
                        (event) -> {
                            var player = (Player) event.getWhoClicked();
                            var currentItem = player.getInventory().getItemInMainHand();
                            if (currentItem.getAmount() > 1 && Wallet.isWallet(currentItem)) {
                                player.sendMessage("Выберите один кошелёк");
                                event.setCancelled(true);
                                return;
                            }

                            var wallet = Wallet.isWallet(currentItem) ? new Wallet(player.getInventory().getItemInMainHand()) : null;
                            if (wallet == null) return;

                            player.getInventory().setItemInMainHand(null);
                            var option = event.getCurrentItem();
                            if (option == null) return;

                            if (option.getLoreLines().size() < 2) return;
                            var amount = option.getDoubleFromContainer("item-wallet-value");

                            player.giveCashToPlayer(Math.abs(amount), true);
                            wallet.changeCashInWallet(-amount);

                            player.closeInventory();
                            wallet.giveWallet(player);
                        }
                    )
                ), "Кошелёк-Снятие", MenuSizes.OneLine
            )
        )
    );

    public static Inventory putWindow(Player player, Wallet wallet) {
        var window = Bukkit.createInventory(player, 9, Component.text("Кошелёк"));
        var cashInInventory = Math.min(player.amountOfCashInInventory(true), wallet.Capacity - wallet.Amount);

        var button = new Item("Положить все средства", "\n-" + cashInInventory + Cash.currencySigh, Material.PAPER, 1, 17000);
        button.setDouble2Container(Double.parseDouble("\n-" + cashInInventory), "item-wallet-value"); // TODO: DATA MODEL
        window.setItem(0, button);

        for (var i = 0; i < 8; i++)
            if (cashInInventory >= Cash.Denomination.get(i)) printButtons("\n-", window);

        return window;
    }

    public static Inventory withdrawWindow(Player player, Wallet wallet) {
        var window = Bukkit.createInventory(player, 9, Component.text("Кошелёк"));
        var cashInWallet = wallet.Amount;

        var button = new Item("Снять максимум", "\n" + Math.round(cashInWallet * 100) / 100 + Cash.currencySigh, Material.PAPER, 1, 17000);
        button.setDouble2Container(Math.round(cashInWallet * 100) / 100, "item-wallet-value"); // TODO: DATA MODEL
        window.setItem(0, button);

        for (var i = 0; i < 8; i++)
            if (cashInWallet >= Cash.Denomination.get(i)) printButtons("\n", window);

        return window;
    }

    private static void printButtons(String thing, Inventory window) {
        for (var j = 1; j < 8; j++) { // TODO: DATA MODEL
            var button = new Item("Действия", thing + Cash.Denomination.get(j) + Cash.currencySigh, Material.PAPER, 1, 17000);
            button.setDouble2Container(Double.parseDouble(thing + Cash.Denomination.get(j)), "item-wallet-value");
            window.setItem(j, button);
        }
    }
}
