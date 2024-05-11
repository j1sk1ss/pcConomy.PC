package economy.pcconomy.frontend.loans;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.economy.credit.Loan;
import economy.pcconomy.backend.economy.license.objects.LicenseType;
import economy.pcconomy.backend.cash.Cash;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.experimental.ExtensionMethod;

import org.j1sk1ss.itemmanager.manager.Manager;
import org.j1sk1ss.menuframework.objects.MenuSizes;
import org.j1sk1ss.menuframework.objects.MenuWindow;
import org.j1sk1ss.menuframework.objects.interactive.components.Bar;
import org.j1sk1ss.menuframework.objects.interactive.components.Button;
import org.j1sk1ss.menuframework.objects.interactive.components.Panel;
import org.j1sk1ss.menuframework.objects.interactive.components.Slider;
import org.j1sk1ss.menuframework.objects.nonInteractive.Direction;

import com.palmergames.bukkit.towny.TownyAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


@ExtensionMethod({Manager.class})
public class NPCLoanWindow {
    private final static int countOfAmountSteps = 9;
    private final static List<Integer> durationSteps = Arrays.asList(20, 30, 40, 50, 60, 70, 80, 90, 100);

    public static final MenuWindow LoanMenu = new MenuWindow(Arrays.asList(
        new Panel(Arrays.asList(
            new Button(0, 21, "Взять кредит", "Взять кредит у банка",
                (event) -> {
                    var player = (Player)event.getWhoClicked();
                    NPCLoanWindow.LoanMenu.getPanel("Кредит-Банк-Взятие").getView(player);
                }),

            new Button(5, 26, "Погасить кредит", "Погасить кредит банка",
                (event) -> {
                    var player = (Player)event.getWhoClicked();
                    Loan.payOffADebt(player, PcConomy.GlobalBank);
                    player.closeInventory();
                })
        ), "Кредит-Банк", MenuSizes.ThreeLines),

        new Panel(Arrays.asList(
            new Slider(Arrays.asList(
                18, 19, 20, 21, 22, 23, 24, 25, 26
            ), Arrays.asList(
                "20 дн.", "30 дн.", "40 дн.", "50 дн.", "60 дн.", "70 дн.", "80 дн.", "90 дн.", "100 дн."
            ), "Размер", "Время выплаты",
                (event) -> {
                    var loanPanel = NPCLoanWindow.LoanMenu.getPanel("Кредит-Банк-Взятие");
                    var bar = loanPanel.getBars("Размер кредита");
                    var player = (Player)event.getWhoClicked();
                    var value = 0;
                    var options = new ArrayList<String>();

                    for (var i = 0; i < countOfAmountSteps; i++) {
                        var maxLoanSize = PcConomy.GlobalBank.DayWithdrawBudget * 2;
                        var isSafe = Loan.isSafeLoan(maxLoanSize / (9 - i), durationSteps.get((8 - i)), PcConomy.GlobalBank, player);

                        var val = Math.round(maxLoanSize / (countOfAmountSteps - i) * 100) / 100 + " " + Cash.currencySigh;
                        var opt = "Банк не одобрит данный займ";

                        if (isSafe && !PcConomy.GlobalBank.getBorrowers().contains(player.getUniqueId())) { // TODO: Fix credits
                            opt = "Банк одобрит данный займ\nПроцент: " +
                                    (Math.round(Loan.getPercent(maxLoanSize / (countOfAmountSteps - i), durationSteps.get((8 - i))) * 100) * 100d) / 100d + "%";
                            value = i;
                        }

                        options.add(val + "\n" + opt);
                    }

                    bar.place(event.getInventory(), options);
                    bar.setValue(event.getInventory(), -1, value);
                }),

            new Bar(Arrays.asList(
                0, 1, 2, 3, 4, 5, 6, 7, 8
            ),
                Direction.Right, "Размер кредита", "Размер кредита",
                Arrays.asList(
                "", "", "", "", "", "", "", "", ""
            ),
                (event) -> {
                    var player    = (Player) event.getWhoClicked();
                    var loanPanel = NPCLoanWindow.LoanMenu.getPanel("Кредит-Банк-Взятие");
                    var durSlider = loanPanel.getSliders("Время выплаты").getChose(event);
                    var value     = Double.parseDouble(event.getCurrentItem().getLoreLines().get(0).split(" ")[0]);
                    var agreement = event.getCurrentItem().getLoreLines().get(1);

                    if (durSlider.equals("none")) return;
                    if (agreement.contains("Банк одобрит данный займ")) { // TODO: fix credits
                        if (!PcConomy.GlobalBank.Credit.contains(Loan.getLoan(player.getUniqueId(), PcConomy.GlobalBank))) {
                            var loan = Loan.createLoan(value, Integer.parseInt(durSlider.split(" ")[0]), player);
                            loan.addLoan(PcConomy.GlobalBank);

                            player.closeInventory();
                        }
                    }
                })
        ), "Кредит-Банк-Взятие", MenuSizes.ThreeLines)
    ));

    public static void generateWindow(Player player) {
        LoanMenu.getPanel("Кредит-Банк").getView(player);
    }

    /**
     * Checks license of reading credit history
     * @param player Player who wants to take credit
     * @return Status
     */
    protected static boolean canReadHistory(Player player) {
        var town = TownyAPI.getInstance().getTown(player.getLocation());
        var licenseHistory = PcConomy.GlobalLicense
                .getLicense(Objects.requireNonNull(town).getMayor().getUUID(), LicenseType.LoanHistory);
        if (licenseHistory == null) return false;

        return !licenseHistory.isOverdue();
    }

    /**
     * Get selected duration from window
     * @param window Window
     * @return Duration
     */
    public static int getSelectedDuration(Inventory window) {
        for (ItemStack button : window) {
            if (button == null) return 20;
            if (button.getMaterial().equals(Material.PURPLE_WOOL)) {
                System.out.println(Integer.parseInt(button.getName().replace("дней", "")));
                return Integer.parseInt(button.getName().replace("дней", ""));
            }
        }

        return 20;
    }

    /**
     * Get selected credit size from window
     * @param button Pressed button
     * @return Credit size
     */
    public static double getSelectedAmount(ItemStack button) {
        return Double.parseDouble(button.getName().replace(Cash.currencySigh, ""));
    }
}
