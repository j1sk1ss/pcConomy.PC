package economy.pcconomy.frontend.ui.windows.loans.npcLoan;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.economy.credit.scripts.LoanManager;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.scripts.items.Item;
import economy.pcconomy.backend.scripts.items.ItemManager;
import economy.pcconomy.frontend.ui.objects.Panel;
import economy.pcconomy.frontend.ui.objects.interactive.Button;
import economy.pcconomy.frontend.ui.windows.loans.LoanBaseWindow;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class NPCLoanWindow extends LoanBaseWindow {
    private final static int countOfAmountSteps = 9;
    private final static List<Integer> durationSteps = Arrays.asList(20, 30, 40, 50, 60, 70, 80, 90, 100);

    public static final Panel Panel = new Panel(Arrays.asList(
            new Button(0, 21, "Взять кредит", ""),
            new Button(5, 26, "Погасить кредит", "")
    ), "Panel");

    public Inventory generateWindow(Player player) {
        return Panel.placeComponents(Bukkit.createInventory(player, 27, Component.text("Кредит-Банк")));
    }

    public Inventory takeWindow(Player player) {
        var window = Bukkit.createInventory(player, 27, Component.text("Кредит-Банк-Взятие"));

        for (var i = 0; i < countOfAmountSteps; i++) {
            window.setItem(i, getAmountButton(i, 18, player));

            if (i == 0) {
                window.setItem(i + 18, new Item(new ItemStack(Material.PURPLE_WOOL),
                        durationSteps.get(i) + "дней"));
                continue;
            }

            window.setItem(i + 18, new Item(new ItemStack(Material.GREEN_STAINED_GLASS),
                    durationSteps.get(i) + "дней"));
        }

        return window;
    }

    @Override
    public Inventory regenerateWindow(Inventory window, Player player, int option) {
        for (var i = 0; i < countOfAmountSteps; i++) {
            window.setItem(i, getAmountButton(i, option, player));

            if (i == option - 18) continue;
            window.setItem(i + 18, new Item(new ItemStack(Material.GREEN_STAINED_GLASS),
                    durationSteps.get(i) + "дней"));
        }

        return window;
    }

    public ItemStack getAmountButton(int position, int chosen, Player player) {
        var maxLoanSize = PcConomy.GlobalBank.getUsefulAmountOfBudget() * 2;
        boolean isSafe = LoanManager.isSafeLoan(maxLoanSize / (position + 1), durationSteps.get(chosen - 18), player);

        ItemStack tempItem = new Item(Math.round(maxLoanSize / (position + 1) * 100) / 100 + CashManager.currencySigh,
                "Банк не одобрит данный займ.", Material.RED_WOOL, 1, 17000); //TODO: DATA MODEL

        if (isSafe && !PcConomy.GlobalBank.getBorrowers().contains(player.getUniqueId()))
            tempItem = creditOptionButton(tempItem, maxLoanSize, chosen, position);

        return tempItem;
    }

    @Override
    public ItemStack creditOptionButton(ItemStack itemStack, double maxLoanSize, int chosen, int position) {
        //TODO: DATA MODEL
        return new Item(ItemManager.getName(itemStack), "Банк одобрит данный займ.\nПроцент: " +
                (Math.round(LoanManager.getPercent(maxLoanSize / (position + 1),
                        durationSteps.get(chosen - 18)) * 100) * 100d) / 100d + "%", Material.GREEN_WOOL, 1, 17000);
    }
}
