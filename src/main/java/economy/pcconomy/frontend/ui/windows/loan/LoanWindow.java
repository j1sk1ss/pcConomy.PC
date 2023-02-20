package economy.pcconomy.frontend.ui.windows.loan;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.bank.scripts.LoanWorker;
import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.backend.scripts.ItemWorker;
import economy.pcconomy.backend.town.scripts.TownWorker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class LoanWindow {
    private final static int countOfAmountSteps = 9;
    private final static List<Integer> durationSteps = Arrays.asList(20, 30, 40, 50, 60, 70, 80, 90, 100);
 // Много хардкодинга, кто нибудь почистите потом
    public static Inventory GetLoanWindow(Player player, boolean isNPC) {
        var window = Bukkit.createInventory(player, 27, isNPC ? "Кредит" : "Кредит-Город");

        for (var i = 0; i < countOfAmountSteps; i++) {
            if (isNPC) window.setItem(i, GetAmountButton(i, 18, player));
            else
                window.setItem(i, GetAmountButton(i, 18,
                        TownyAPI.getInstance().getTown(player.getLocation()).getName(), player, canReadHistory(player)));

            if (i == 0) {
                window.setItem(i + 18, ItemWorker.SetName(new ItemStack(Material.PURPLE_WOOL),
                        durationSteps.get(i) + "дней"));
                continue;
            }
            window.setItem(i + 18, ItemWorker.SetName(new ItemStack(Material.GREEN_STAINED_GLASS),
                    durationSteps.get(i) + "дней"));
        }

        return CreditDestroyButton(window, isNPC, player);
    }

    public static Inventory GetLoanWindow(Inventory window, Player player, int option, boolean isNPC) {
        for (var i = 0; i < countOfAmountSteps; i++) {
            if (isNPC) window.setItem(i, GetAmountButton(i, option, player));
            else
                window.setItem(i, GetAmountButton(i, option,
                    TownyAPI.getInstance().getTown(player.getLocation()).getName(), player, canReadHistory(player)));

            if (i == option - 18) continue;
            window.setItem(i + 18, ItemWorker.SetName(new ItemStack(Material.GREEN_STAINED_GLASS),
                    durationSteps.get(i) + "дней"));
        }

        return CreditDestroyButton(window, isNPC, player);
    }

    private static Inventory CreditDestroyButton(Inventory window, boolean isNPC, Player player) {
        if (isNPC) {
            if (PcConomy.GlobalBank.Credit.contains(PcConomy.GlobalBank.GetLoan(player.getUniqueId()))) {
                window.setItem(9, ItemWorker.SetLore(ItemWorker.SetName(new ItemStack(Material.BLACK_SHULKER_BOX),
                        "Выплатить кредит"), PcConomy.GlobalBank.GetLoan(player.getUniqueId()).amount + CashWorker.currencySigh));
            }
        } else {
            var townObject = PcConomy.GlobalTownWorker.GetTownObject(TownyAPI.getInstance().getTownName(player.getLocation()));
            if (townObject.GetLoan(player.getUniqueId()) != null) {
                window.setItem(9, ItemWorker.SetLore(ItemWorker.SetName(new ItemStack(Material.BLACK_SHULKER_BOX),
                        "Выплатить кредит"), townObject.GetLoan(player.getUniqueId()).amount + CashWorker.currencySigh));
            }
        }
        return window;
    }

    private static boolean canReadHistory(Player player) {
        var town = TownyAPI.getInstance().getTown(player.getLocation());
        var licenseHistory = PcConomy.GlobalLicenseWorker
                .GetLicense(town.getMayor().getPlayer(), LicenseType.LoanHistory);
        if (licenseHistory == null) return false;

        return !PcConomy.GlobalLicenseWorker.isOverdue(PcConomy.GlobalLicenseWorker
                        .GetLicense(town.getMayor().getPlayer(), LicenseType.LoanHistory));
    }

    public static ItemStack GetAmountButton(int i, int option, Player player) {
        var maxLoanSize = PcConomy.GlobalBank.GetUsefulAmountOfBudget() * 2;
        boolean isSafe = LoanWorker.isSafeLoan(maxLoanSize / (i + 1), durationSteps.get(option - 18), player);

        ItemStack tempItem = ItemWorker.SetLore(ItemWorker.SetName(new ItemStack(Material.RED_WOOL, 1),
                Math.round(maxLoanSize / (i + 1) * 100) / 100 + CashWorker.currencySigh), "Банк не одобрит данный займ.");

        if (isSafe && !PcConomy.GlobalBank.Credit.contains(player.getUniqueId())) {
            tempItem = ItemWorker.SetMaterial(ItemWorker.SetLore(tempItem, "Банк одобрит данный займ.\nПроцент: " +
                    (Math.round(LoanWorker.getPercent(maxLoanSize / (i + 1),
                            durationSteps.get(option - 18)) * 100) * 100d) / 100d + "%"),  Material.GREEN_WOOL);
        }

        return tempItem;
    }

    public static ItemStack GetAmountButton(int i, int option, String townName, Player player, boolean canReadHistory) {
        var townObject = PcConomy.GlobalTownWorker.GetTownObject(townName);
        var maxLoanSize = townObject.getBudget() * .2d;
        boolean isSafe = LoanWorker.isSafeLoan(maxLoanSize / (i + 1), durationSteps.get(option - 18), player);

        ItemStack tempItem = ItemWorker.SetLore(ItemWorker.SetName(new ItemStack(Material.RED_WOOL, 1),
                Math.round(maxLoanSize / (i + 1) * 100) / 100 + CashWorker.currencySigh), "Банк не одобрит данный займ.");

        if (((isSafe || !canReadHistory) && !PcConomy.GlobalBank.Credit.contains(player.getUniqueId()) && maxLoanSize > 0))
            tempItem = ItemWorker.SetMaterial(ItemWorker.SetLore(tempItem, "Банк одобрит данный займ.\nПроцент: " +
                    (Math.round(LoanWorker.getPercent(maxLoanSize / (i + 1),
                            durationSteps.get(option - 18)) * 100) * 100d) / 100d + "%"),  Material.GREEN_WOOL);

        return tempItem;
    }

    public static int GetSelectedDuration(Inventory window) {
        for (ItemStack button:
             window) {
            if (button == null) return 20;
            if (ItemWorker.GetMaterial(button).equals(Material.PURPLE_WOOL)) {
                System.out.println(Integer.parseInt(ItemWorker.GetName(button).replace("дней", "")));
                return Integer.parseInt(ItemWorker.GetName(button).replace("дней", ""));
            }
        }

        return 20;
    }

    public static double GetSelectedAmount(Inventory window) {
        for (ItemStack button:
                window) {
            if (button == null) return 0;
            if (ItemWorker.GetMaterial(button).equals(Material.LIGHT_BLUE_WOOL))
                return Double.parseDouble(ItemWorker.GetName(button).replace(CashWorker.currencySigh, ""));
        }

        return 0;
    }
}
