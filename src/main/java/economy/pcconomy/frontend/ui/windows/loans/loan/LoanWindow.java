package economy.pcconomy.frontend.ui.windows.loans.loan;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.economy.credit.scripts.LoanManager;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.npc.traits.Loaner;
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
import java.util.Objects;
import java.util.UUID;

public class LoanWindow extends LoanBaseWindow {
    public LoanWindow(Loaner loaner) {
        Loaner = loaner;
    }

    private final Loaner Loaner;

    private final static int countOfAmountSteps = 9;
    private final static List<Integer> durationSteps = Arrays.asList(20, 30, 40, 50, 60, 70, 80, 90, 100);

    public static final economy.pcconomy.frontend.ui.objects.Panel Panel = new Panel(Arrays.asList(
            new Button(0, 21, "Взять кредит", ""),
            new Button(5, 26, "Погасить кредит", "")
    ), "Panel");

    public Inventory generateWindow(Player player) {
        return Panel.placeComponents(Bukkit.createInventory(player, 27, Component.text("Кредит-Город")));
    }

    public Inventory takeWindow(Player player) {
        var window = Bukkit.createInventory(player, 27, Component.text("Кредит-Город-Взятие"));

        for (var i = 0; i < countOfAmountSteps; i++) {
            window.setItem(i, getAmountButton(i, 18,
                    Objects.requireNonNull(TownyAPI.getInstance().getTown(player.getLocation())).getUUID(), player, canReadHistory(player)));

            if (i == 0) { //TODO: DATA MODEL
                window.setItem(i + 18, new Item(durationSteps.get(i) + "дней", "", Material.PURPLE_WOOL));
                continue;
            }
            window.setItem(i + 18, new Item(durationSteps.get(i) + "дней", "", Material.GREEN_STAINED_GLASS));
        }

        return window;
    }

    @Override
    public Inventory regenerateWindow(Inventory window, Player player, int option) {
        for (var i = 0; i < countOfAmountSteps; i++) {
            window.setItem(i, getAmountButton(i, option,
                Objects.requireNonNull(TownyAPI.getInstance().getTown(player.getLocation())).getUUID(), player, canReadHistory(player)));

            if (i == option - 18) continue;//TODO: DATA MODEL
            window.setItem(i + 18, new Item(durationSteps.get(i) + "дней", "", Material.GREEN_STAINED_GLASS));
        }

        return window;
    }

    public ItemStack getAmountButton(int position, int chosen, UUID townName, Player player, boolean canReadHistory) {
        var townObject = PcConomy.GlobalTownManager.getTown(townName);
        var maxLoan = Math.min(Loaner.Pull, townObject.getBudget());

        boolean isSafe = LoanManager.isSafeLoan(maxLoan / (position + 1), durationSteps.get(chosen - 18), player);

        ItemStack tempItem = new Item(Math.round(maxLoan / (position + 1) * 100) / 100 + CashManager.currencySigh,
                "Город не одобрит данный займ.", Material.RED_WOOL, 1, 17000); //TODO: DATA MODEL

        if (((isSafe || !canReadHistory) && !townObject.getBorrowers().contains(player.getUniqueId()) && maxLoan > 0))
            tempItem = creditOptionButton(tempItem, maxLoan, chosen, position);

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
