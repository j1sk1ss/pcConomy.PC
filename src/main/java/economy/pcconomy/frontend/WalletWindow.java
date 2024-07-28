package economy.pcconomy.frontend;

import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.cash.Wallet;

import lombok.experimental.ExtensionMethod;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import org.j1sk1ss.itemmanager.manager.Manager;

import org.j1sk1ss.menuframework.objects.MenuSizes;
import org.j1sk1ss.menuframework.objects.MenuWindow;
import org.j1sk1ss.menuframework.objects.interactive.components.ClickArea;
import org.j1sk1ss.menuframework.objects.interactive.components.Icon;
import org.j1sk1ss.menuframework.objects.interactive.components.Panel;
import org.j1sk1ss.menuframework.objects.nonInteractive.Direction;
import org.j1sk1ss.menuframework.objects.nonInteractive.Margin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@ExtensionMethod({Manager.class, Cash.class})
public class WalletWindow implements Listener {
    public static MenuWindow WalletWindow = new MenuWindow(
        Arrays.asList(
            new Panel(
                List.of(
                    new ClickArea(
                        new Margin(0, 0, 8, Direction.Horizontal),
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
                            var option = event.getInventory().getItem(event.getSlot());
                            if (option == null) return;

                            var amount = option.getDoubleFromContainer("item-wallet-value");

                            player.takeCashFromPlayer(Math.abs(amount), true);
                            wallet.changeCashInWallet(Math.abs(amount));

                            player.closeInventory();
                            wallet.giveWallet(player);
                        }
                    )
                ), "Кошелёк-Внесение", MenuSizes.OneLine, "\u10DB"
            ),
            new Panel(
                List.of(
                    new ClickArea(
                        new Margin(0, 0, 8, Direction.Horizontal),
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
                            var option = event.getInventory().getItem(event.getSlot());
                            if (option == null) return;

                            var amount = option.getDoubleFromContainer("item-wallet-value");

                            player.giveCashToPlayer(Math.abs(amount), true);
                            wallet.changeCashInWallet(-amount);

                            player.closeInventory();
                            wallet.giveWallet(player);
                        }
                    )
                ), "Кошелёк-Снятие", MenuSizes.OneLine, "\u10DB"
            )
        )
    );

    public static void putWindow(Player player, Wallet wallet) {
        var cashInInventory = Math.min(player.amountOfCashInInventory(true), wallet.getCapacity() - wallet.getAmount());
        var components = new ArrayList<org.j1sk1ss.menuframework.objects.interactive.Component>();
        var icon = new Icon(
            new Margin(0, 0,0),
            "Положить все средства", -cashInInventory + Cash.currencySigh, Material.GOLD_INGOT, 7002
        );

        icon.setDouble2Container(-cashInInventory, "item-wallet-value");
        for (var i = 0; i < 8; i++)
            if (cashInInventory >= Cash.Denomination.get(i)) components.add(printButtons(-1, i));

        components.add(icon);
        WalletWindow.getPanel("Кошелёк-Внесение").getViewWith(player, components);
    }

    public static void withdrawWindow(Player player, Wallet wallet) {
        var cashInWallet = wallet.getAmount();
        var components = new ArrayList<org.j1sk1ss.menuframework.objects.interactive.Component>();
        var icon = new Icon(
            new Margin(0, 0,0),
            "Снять максимум", "\n" + Math.round(cashInWallet) + Cash.currencySigh, Material.GOLD_INGOT, 7001
        );

        icon.setDouble2Container(cashInWallet, "item-wallet-value");
        for (var i = 0; i < 8; i++)
            if (cashInWallet >= Cash.Denomination.get(i)) components.add(printButtons(1, i));

        components.add(icon);
        WalletWindow.getPanel("Кошелёк-Снятие").getViewWith(player, components);
    }

    private static org.j1sk1ss.menuframework.objects.interactive.Component printButtons(int positive, int pos) {
        var icon = new Icon(
            new Margin(pos, 0,0),
            "Действия", (positive * Cash.Denomination.get(pos)) + Cash.currencySigh, Material.GOLD_INGOT, 7002
        );

        icon.setDouble2Container(positive * Cash.Denomination.get(pos), "item-wallet-value");
        return icon;
    }

    @EventHandler
    public void onWalletUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.LEFT_CLICK_AIR &&
            event.getAction() != Action.RIGHT_CLICK_AIR) return;

        var player = event.getPlayer();
        var item = player.getInventory().getItemInMainHand();
        if (item.getItemMeta() == null) return;

        var wallet = Wallet.isWallet(item) ? new Wallet(item) : null;
        if (wallet != null) {
            switch (event.getAction()) {
                case LEFT_CLICK_AIR ->putWindow(player, wallet);
                case RIGHT_CLICK_AIR -> withdrawWindow(player, wallet);
                default -> throw new IllegalArgumentException("Unexpected value: " + event.getAction());
            }

            event.setCancelled(true);
        }
    }
}
