package economy.pcconomy.frontend.bank;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.scripts.BalanceManager;

import lombok.experimental.ExtensionMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import org.j1sk1ss.itemmanager.manager.Manager;
import org.j1sk1ss.menuframework.objects.MenuSizes;
import org.j1sk1ss.menuframework.objects.MenuWindow;
import org.j1sk1ss.menuframework.objects.interactive.components.ClickArea;
import org.j1sk1ss.menuframework.objects.interactive.components.LittleButton;
import org.j1sk1ss.menuframework.objects.interactive.components.Panel;


@ExtensionMethod({Manager.class, CashManager.class})
public class BankerWindow {
    public static MenuWindow BankWindow = new MenuWindow(
        Arrays.asList(
            new Panel(
                Arrays.asList(
                    new ClickArea(36, 48,
                        (event) -> {
                            var player = (Player) event.getWhoClicked();
                            var option = event.getCurrentItem();
                            if (option == null) return;

                            if (option.getLoreLines().size() < 2) return;
                            var amount = option.getDoubleFromContainer("item-bank-value");

                            PcConomy.GlobalBank.takeCashFromPlayer(Math.abs(amount), player);
                            BankerWindow.regenerateWindow(player, event.getInventory());
                        }), // Put
                    new ClickArea(41, 53,
                        (event) -> {
                            var player = (Player) event.getWhoClicked();
                            var option = event.getCurrentItem();
                            if (option == null) return;

                            if (option.getLoreLines().size() < 2) return;
                            var amount = option.getDoubleFromContainer("item-bank-value");

                            PcConomy.GlobalBank.giveCashToPlayer(amount, player);
                            BankerWindow.regenerateWindow(player, event.getInventory());
                        }) // Withdraw
                ), "Банк", MenuSizes.SixLines
            )
        )
    );

    public static void generateWindow(Player player) {
        regenerateWindow(player, null);
    }

    public static void regenerateWindow(Player player, Inventory inventory) {
        var enableBalance   = PcConomy.GlobalBank.DayWithdrawBudget;
        var playerBalance   = BalanceManager.getBalance(player);
        var cashInInventory = player.amountOfCashInInventory(false);
        var textBalance = playerBalance + "";
        var charArray   = textBalance.toCharArray();

        var balance = printBalance(charArray, textBalance);
        var actions = printButtons(playerBalance, enableBalance, cashInInventory);

        var components = new ArrayList<org.j1sk1ss.menuframework.objects.interactive.Component>();
        components.addAll(balance);
        components.addAll(actions);
        
        if (inventory == null) BankWindow.getPanel("Банк").getViewWith(player, components);
        else BankWindow.getPanel("Банк").getViewWith(player, components, inventory);
    }

    // Print action buttons (Default + max-min actions)
    private static List<org.j1sk1ss.menuframework.objects.interactive.Component> printButtons(double playerBalance, double enableBalance, double cashInInventory) {
        var list = new ArrayList<org.j1sk1ss.menuframework.objects.interactive.Component>();

        if (playerBalance < enableBalance) {
            var withdrawMax = new LittleButton(41, "Снять максимум", "\n" + Math.round(playerBalance * 100) / 100 + CashManager.currencySigh);
            withdrawMax.setDouble2Container(Math.round(playerBalance * 100d) / 100d, "item-bank-value");
            list.add(withdrawMax);  
        } 

        var putMax = new LittleButton(36, "Положить все средства", "\n-" + cashInInventory + CashManager.currencySigh);
        putMax.setDouble2Container(Double.parseDouble("\n-" + cashInInventory), "item-bank-value");
        list.add(putMax);        
        
        for (var i = 0; i < 8; i++) {
            if (enableBalance >= CashManager.Denomination.get(i) && playerBalance >= CashManager.Denomination.get(i)) list.addAll(printButtons("\n", 41, i));
            if (cashInInventory >= CashManager.Denomination.get(i)) list.addAll(printButtons("\n-", 36, i));
        }

        return list;
    }

    // Print action buttons (default actions)
    private static List<org.j1sk1ss.menuframework.objects.interactive.Component> printButtons(String thing, int position, int enabled) {
        var list = new ArrayList<org.j1sk1ss.menuframework.objects.interactive.Component>();
        for (var j = enabled; j < 8; j++) {
            var button = new LittleButton(j + (position + 5 * (j / 4)), "Действие", thing + CashManager.Denomination.get(j) + CashManager.currencySigh);
            button.setDouble2Container(Double.parseDouble(thing + CashManager.Denomination.get(j)), "item-bank-value");
            list.add(button);
        }

        return list;
    }

    // Print user balance
    private static List<org.j1sk1ss.menuframework.objects.interactive.Component> printBalance(char[] charArray, String textBalance) {
        var list = new ArrayList<org.j1sk1ss.menuframework.objects.interactive.Component>();
        for (var i = 9; i < Math.min(charArray.length + 9, 27); i++) {
            var currentChar = charArray[i - 9];
            if (currentChar == 'E') list.add(new LittleButton(i, "Баланс", textBalance));
            else if (currentChar == '.') list.add(new LittleButton(i, "Баланс", textBalance));
            else  list.add(new LittleButton(i, "Баланс", textBalance)); // TODO: DATA MODEL
        }

        return list;
    }
}
