package economy.pcconomy.frontend.ui.windows.loan;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.bank.scripts.LoanManager;
import economy.pcconomy.backend.cash.scripts.CashManager;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.backend.scripts.ItemWorker;
import economy.pcconomy.frontend.ui.windows.IWindow;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
// TODO: Cleanup code (Maybe rewrite)
public class LoanWindow implements IWindow {
    public LoanWindow(boolean isNpc) {
        this.isNpc = isNpc;
    }

    private final boolean isNpc;

    private final static int countOfAmountSteps = 9;
    private final static List<Integer> durationSteps = Arrays.asList(20, 30, 40, 50, 60, 70, 80, 90, 100);

    public Inventory generateWindow(Player player) {
        var window = Bukkit.createInventory(player, 27, Component.text(isNpc ? "Кредит" : "Кредит-Город"));

        for (var i = 0; i < countOfAmountSteps; i++) {
            if (isNpc) window.setItem(i, GetAmountButton(i, 18, player));
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

        return CreditDestroyButton(window, isNpc, player);
    }

    public static Inventory regenerateWindow(Inventory window, Player player, int option, boolean isNPC) {
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
            if (PcConomy.GlobalBank.Credit.contains(LoanManager.getLoan(player.getUniqueId(), PcConomy.GlobalBank))) {
                window.setItem(9, ItemWorker.SetLore(ItemWorker.SetName(new ItemStack(Material.BLACK_SHULKER_BOX),
                        "Выплатить кредит"), LoanManager.getLoan(player.getUniqueId(), PcConomy.GlobalBank).amount + CashManager.currencySigh));
            }
        } else {
            var townObject = PcConomy.GlobalTownWorker.GetTownObject(TownyAPI.getInstance().getTownName(player.getLocation()));
            if (LoanManager.getLoan(player.getUniqueId(), townObject) != null) {
                window.setItem(9, ItemWorker.SetLore(ItemWorker.SetName(new ItemStack(Material.BLACK_SHULKER_BOX),
                        "Выплатить кредит"), LoanManager.getLoan(player.getUniqueId(), townObject).amount + CashManager.currencySigh));
            }
        }
        return window;
    }

    private static boolean canReadHistory(Player player) {
        var town = TownyAPI.getInstance().getTown(player.getLocation());
        var licenseHistory = PcConomy.GlobalLicenseWorker
                .GetLicense(town.getMayor().getUUID(), LicenseType.LoanHistory);
        if (licenseHistory == null) return false;

        return !PcConomy.GlobalLicenseWorker.isOverdue(PcConomy.GlobalLicenseWorker
                        .GetLicense(town.getMayor().getUUID(), LicenseType.LoanHistory));
    }

    public static ItemStack GetAmountButton(int position, int chosen, Player player) {
        var maxLoanSize = PcConomy.GlobalBank.GetUsefulAmountOfBudget() * 2;
        boolean isSafe = LoanManager.isSafeLoan(maxLoanSize / (position + 1), durationSteps.get(chosen - 18), player);

        ItemStack tempItem = ItemWorker.SetLore(ItemWorker.SetName(new ItemStack(Material.RED_WOOL, 1),
                Math.round(maxLoanSize / (position + 1) * 100) / 100 + CashManager.currencySigh), "Банк не одобрит данный займ.");

        if (isSafe && !PcConomy.GlobalBank.Credit.contains(player.getUniqueId()))
            tempItem = CreditOptionButton(tempItem, maxLoanSize, chosen, position);

        return tempItem;
    }

    public static ItemStack GetAmountButton(int position, int chosen, String townName, Player player, boolean canReadHistory) {
        var townObject = PcConomy.GlobalTownWorker.GetTownObject(townName);
        var maxLoanSize = townObject.getBudget() * .2d;
        boolean isSafe = LoanManager.isSafeLoan(maxLoanSize / (position + 1), durationSteps.get(chosen - 18), player);

        ItemStack tempItem = ItemWorker.SetLore(ItemWorker.SetName(new ItemStack(Material.RED_WOOL, 1),
                Math.round(maxLoanSize / (position + 1) * 100) / 100 + CashManager.currencySigh), "Банк не одобрит данный займ.");

        if (((isSafe || !canReadHistory) && !PcConomy.GlobalBank.Credit.contains(player.getUniqueId()) && maxLoanSize > 0))
            tempItem = CreditOptionButton(tempItem, maxLoanSize, chosen, position);

        return tempItem;
    }

    private static ItemStack CreditOptionButton(ItemStack itemStack, double maxLoanSize, int chosen, int position) {
        return ItemWorker.SetMaterial(ItemWorker.SetLore(itemStack, "Банк одобрит данный займ.\nПроцент: " +
                (Math.round(LoanManager.getPercent(maxLoanSize / (position + 1),
                        durationSteps.get(chosen - 18)) * 100) * 100d) / 100d + "%"),  Material.GREEN_WOOL);
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
                return Double.parseDouble(ItemWorker.GetName(button).replace(CashManager.currencySigh, ""));
        }

        return 0;
    }
}
