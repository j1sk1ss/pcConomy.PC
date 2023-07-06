package economy.pcconomy.frontend.ui.windows.loans.loan;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.economy.bank.scripts.LoanManager;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.scripts.items.Item;
import economy.pcconomy.backend.scripts.items.ItemManager;
import economy.pcconomy.frontend.ui.objects.Panel;
import economy.pcconomy.frontend.ui.objects.interactive.Button;
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
import java.util.Objects;

public class LoanWindow extends LoanBaseWindow implements IWindow  {
    private final static int countOfAmountSteps = 9;
    private final static List<Integer> durationSteps = Arrays.asList(20, 30, 40, 50, 60, 70, 80, 90, 100);

    public static final economy.pcconomy.frontend.ui.objects.Panel Panel = new Panel(Arrays.asList(
            new Button(Arrays.asList(
                    0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21
            ), "Взять кредит", ""),
            new Button(Arrays.asList(
                    5, 6, 7, 8, 14, 15, 16, 17, 23, 24, 25, 26
            ), "Погасить кредит", "")
    ));

    public Inventory generateWindow(Player player) {
        return Panel.placeComponents(Bukkit.createInventory(player, 27, Component.text("Кредит-Город")));
    }

    public Inventory takeWindow(Player player) {
        var window = Bukkit.createInventory(player, 27, Component.text("Кредит-Город-Взятие"));

        for (var i = 0; i < countOfAmountSteps; i++) {
            window.setItem(i, getAmountButton(i, 18,
                    Objects.requireNonNull(TownyAPI.getInstance().getTown(player.getLocation())).getName(), player, canReadHistory(player)));

            if (i == 0) { //TODO: DATA MODEL
                window.setItem(i + 18, new Item(durationSteps.get(i) + "дней", "", Material.PURPLE_WOOL));
                continue;
            }
            window.setItem(i + 18, new Item(durationSteps.get(i) + "дней", "", Material.GREEN_STAINED_GLASS));
        }

        return window;
    }

    @Override
    public Inventory regenerateWindow(Inventory window, Player player, int option, boolean isNPC) {
        for (var i = 0; i < countOfAmountSteps; i++) {
            window.setItem(i, getAmountButton(i, option,
                Objects.requireNonNull(TownyAPI.getInstance().getTown(player.getLocation())).getName(), player, canReadHistory(player)));

            if (i == option - 18) continue;//TODO: DATA MODEL
            window.setItem(i + 18, new Item(durationSteps.get(i) + "дней", "", Material.GREEN_STAINED_GLASS));
        }

        return window;
    }

    public ItemStack getAmountButton(int position, int chosen, String townName, Player player, boolean canReadHistory) {
        var townObject = PcConomy.GlobalTownManager.getTown(townName);
        var maxLoanSize = townObject.getBudget() * .2d;
        boolean isSafe = LoanManager.isSafeLoan(maxLoanSize / (position + 1), durationSteps.get(chosen - 18), player);

        ItemStack tempItem = new Item(Math.round(maxLoanSize / (position + 1) * 100) / 100 + CashManager.currencySigh,
                "Город не одобрит данный займ.", Material.RED_WOOL, 1, 17000); //TODO: DATA MODEL

        if (((isSafe || !canReadHistory) && !townObject.getBorrowers().contains(player.getUniqueId()) && maxLoanSize > 0))
            tempItem = creditOptionButton(tempItem, maxLoanSize, chosen, position);

        return tempItem;
    }

    @Override
    public ItemStack creditOptionButton(ItemStack itemStack, double maxLoanSize, int chosen, int position) {
        //TODO: DATA MODEL
        return new Item(ItemManager.getName(itemStack), "Город одобрит данный займ.\nПроцент: " +
                (Math.round(LoanManager.getPercent(maxLoanSize / (position + 1),
                        durationSteps.get(chosen - 18)) * 100) * 100d) / 100d + "%", Material.GREEN_WOOL, 1, 17000);
    }
}
