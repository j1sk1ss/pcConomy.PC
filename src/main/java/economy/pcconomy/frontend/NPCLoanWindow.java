package economy.pcconomy.frontend;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.economy.credit.Loan;
import economy.pcconomy.backend.cash.Cash;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import lombok.experimental.ExtensionMethod;

import org.j1sk1ss.itemmanager.manager.Manager;

import org.j1sk1ss.menuframework.objects.MenuSizes;
import org.j1sk1ss.menuframework.objects.MenuWindow;
import org.j1sk1ss.menuframework.objects.interactive.components.Bar;
import org.j1sk1ss.menuframework.objects.interactive.components.Button;
import org.j1sk1ss.menuframework.objects.interactive.components.Panel;
import org.j1sk1ss.menuframework.objects.interactive.components.Slider;
import org.j1sk1ss.menuframework.objects.nonInteractive.Direction;
import org.j1sk1ss.menuframework.objects.nonInteractive.Margin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;


@ExtensionMethod({Manager.class})
public class NPCLoanWindow {
    private static final MenuWindow LoanMenu = new MenuWindow(Arrays.asList(
        new Panel(Arrays.asList(
            new Button(new Margin(0, 0, 2, 3), "Взять кредит", "Взять кредит у банка",
                (event, menu) -> {
                    var player = (Player)event.getWhoClicked();
                    menu.getPanel("Кредит-Взятие").getView(player);
                }, Material.GOLD_INGOT, 7000),

            new Button(new Margin(0, 5, 2, 3), "Погасить кредит", "Погасить кредит банка",
                (event, menu) -> {
                    var player = (Player)event.getWhoClicked();
                    var code = Loan.payOffADebt(player, PcConomy.GlobalBank.getBank());
                    player.closeInventory();

                    if (code) player.sendMessage("Кредит был успешно погашен.");
                    else player.sendMessage("Что-то пошло не так. Может у вас нет денег?");
                }, Material.GOLD_INGOT, 7000)
        ), "Кредит-Банк", MenuSizes.ThreeLines, "\u10D2"),

        new Panel(Arrays.asList(
            new Slider(new Margin(2, 0, 8, Direction.Horizontal), Arrays.asList(
                "20 дн.", "30 дн.", "40 дн.", "50 дн.", "60 дн.", "70 дн.", "80 дн.", "90 дн.", "100 дн."
            ), "Размер", "Время выплаты",
                (event, menu) -> {
                    var durationSteps = Arrays.asList(20, 30, 40, 50, 60, 70, 80, 90, 100);
                    var countOfAmountSteps = 9;

                    var loanPanel = menu.getPanel("Кредит-Взятие");
                    var bar = loanPanel.getComponent("Размер кредита", Bar.class);
                    var player = (Player)event.getWhoClicked();
                    var value = 0;
                    var options = new ArrayList<String>();

                    for (var i = 0; i < countOfAmountSteps; i++) {
                        var maxLoanSize = PcConomy.GlobalBank.getBank().getDayWithdrawBudget() * 2;
                        var isSafe = Loan.isSafeLoan(maxLoanSize / (9 - i), durationSteps.get((8 - i)), PcConomy.GlobalBank.getBank(), player);
                        var val = Math.round(maxLoanSize / (countOfAmountSteps - i) * 100) / 100 + " " + Cash.currencySigh;
                        var opt = "Банк не одобрит данный займ";

                        if (isSafe && !PcConomy.GlobalBank.getBank().getBorrowers().contains(player.getUniqueId())) {
                            opt = "Банк одобрит данный займ\nПроцент: " +
                                    (Math.round(Loan.getPercent(maxLoanSize / (countOfAmountSteps - i), durationSteps.get((8 - i))) * 100) * 100d) / 100d + "%";
                            value = i;
                        }

                        options.add(val + "\n" + opt);
                    }

                    bar.place(event.getInventory(), options);
                    bar.setValue(event.getInventory(), -1, value);
                }, 17000, 7000, Material.GOLD_INGOT, Material.GOLD_INGOT),

            new Bar(new Margin(0, 0, 8, Direction.Horizontal),
                Direction.Right, "Размер кредита", "Размер кредита",
                Arrays.asList(
                "", "", "", "", "", "", "", "", ""
            ),
                (event, menu) -> {
                    var player    = (Player) event.getWhoClicked();
                    var loanPanel = menu.getPanel("Кредит-Взятие");
                    var durSlider = loanPanel.getComponent("Время выплаты", Slider.class).getChose(event);
                    var value     = Double.parseDouble(Objects.requireNonNull(event.getCurrentItem()).getLoreLines().get(0).split(" ")[0]);
                    var agreement = event.getCurrentItem().getLoreLines().get(1);

                    if (durSlider.equals("none")) return;
                    if (agreement.contains("Банк одобрит данный займ")) {
                        if (!PcConomy.GlobalBank.getBank().getCredit().contains(Loan.getLoan(player.getUniqueId(), PcConomy.GlobalBank.getBank()))) {
                            var loan = Loan.createLoan(value, Integer.parseInt(durSlider.split(" ")[0]), player);
                            loan.addLoan(PcConomy.GlobalBank.getBank());

                            player.closeInventory();
                            player.sendMessage("Вам был выдан кредит в размере: " + loan.getAmount() + Cash.currencySigh);
                        }
                    }
                }, 7001, 7002, Material.GOLD_INGOT, Material.GOLD_INGOT)
        ), "Кредит-Взятие", MenuSizes.ThreeLines, "\u10E1")
    ));

    public static void generateWindow(Player player) {
        LoanMenu.getPanel("Кредит-Банк").getView(player);
    }
}
