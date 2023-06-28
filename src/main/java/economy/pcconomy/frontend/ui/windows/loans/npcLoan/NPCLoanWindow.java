package economy.pcconomy.frontend.ui.windows.loans.npcLoan;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.bank.interfaces.IMoney;
import economy.pcconomy.backend.bank.scripts.LoanManager;
import economy.pcconomy.backend.cash.scripts.CashManager;
import economy.pcconomy.backend.scripts.ItemManager;
import economy.pcconomy.frontend.ui.windows.IWindow;
import economy.pcconomy.frontend.ui.windows.loans.LoanBaseWindow;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class NPCLoanWindow extends LoanBaseWindow implements IWindow {
    private final static int countOfAmountSteps = 9;
    private final static List<Integer> durationSteps = Arrays.asList(20, 30, 40, 50, 60, 70, 80, 90, 100);

    public Inventory generateWindow(Player player) {
        var window = Bukkit.createInventory(player, 27, Component.text("Кредит"));

        for (var i = 0; i < countOfAmountSteps; i++) {
            window.setItem(i, getAmountButton(i, 18, player));

            if (i == 0) {
                window.setItem(i + 18, ItemManager.setName(new ItemStack(Material.PURPLE_WOOL),
                        durationSteps.get(i) + "дней"));
                continue;
            }

            window.setItem(i + 18, ItemManager.setName(new ItemStack(Material.GREEN_STAINED_GLASS),
                    durationSteps.get(i) + "дней"));
        }

        return creditDestroyButton(window, player);
    }

    @Override
    protected IMoney getMoneyGiver(Player player) {
        return PcConomy.GlobalBank;
    }

    @Override
    public Inventory regenerateWindow(Inventory window, Player player, int option, boolean isNPC) {
        for (var i = 0; i < countOfAmountSteps; i++) {
            window.setItem(i, getAmountButton(i, option, player));

            if (i == option - 18) continue;
            window.setItem(i + 18, ItemManager.setName(new ItemStack(Material.GREEN_STAINED_GLASS),
                    durationSteps.get(i) + "дней"));
        }

        return creditDestroyButton(window, player);
    }

    public ItemStack getAmountButton(int position, int chosen, Player player) {
        var maxLoanSize = PcConomy.GlobalBank.getUsefulAmountOfBudget() * 2;
        boolean isSafe = LoanManager.isSafeLoan(maxLoanSize / (position + 1), durationSteps.get(chosen - 18), player);

        ItemStack tempItem = ItemManager.setLore(ItemManager.setName(new ItemStack(Material.RED_WOOL, 1),
                Math.round(maxLoanSize / (position + 1) * 100) / 100 + CashManager.currencySigh), "Банк не одобрит данный займ.");

        if (isSafe && !PcConomy.GlobalBank.Credit.contains(player.getUniqueId()))
            tempItem = creditOptionButton(tempItem, maxLoanSize, chosen, position);

        return tempItem;
    }

    @Override
    public ItemStack creditOptionButton(ItemStack itemStack, double maxLoanSize, int chosen, int position) {
        return ItemManager.setMaterial(ItemManager.setLore(itemStack, "Банк одобрит данный займ.\nПроцент: " +
                (Math.round(LoanManager.getPercent(maxLoanSize / (position + 1),
                        durationSteps.get(chosen - 18)) * 100) * 100d) / 100d + "%"),  Material.GREEN_WOOL);
    }
}
