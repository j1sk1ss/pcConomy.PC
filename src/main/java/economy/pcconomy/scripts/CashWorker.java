package economy.pcconomy.scripts;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class CashWorker {
    public static void WithdrawCash(double amount, Player player) { // Town -> Create cash -> Player
        BalanceWorker balanceWorker = new BalanceWorker();
        if (!balanceWorker.isSolvent(amount, player)) return;
        //if (!город может выдать эту сумму) return;

        balanceWorker.TakeMoney(amount, player);
        //город.TakeMoney(amount);

        player.getInventory().addItem(CreateCashObject(amount));
    }

    public static ItemStack CreateCashObject(double amount) {
        return ItemWorker.SetName(ItemWorker.SetLore(new ItemStack(Material.PAPER, 1),
                "" + amount + "$"), "Доллар США");
    }

    public void TakeCash(Player player) { // Player -> Town
        ItemStack moneyObject = player.getInventory().getItemInMainHand();

        new BalanceWorker().GiveMoney(GetAmountFromCash(moneyObject), player);
        player.getInventory().setItemInMainHand(null);
    }

    public int GetAmountFromCash(ItemStack money) {
        if (ItemWorker.GetName(money).equals("Доллар США")) {
            if (!Objects.equals(ItemWorker.GetLore(money).get(0), "")) {
                return Integer.parseInt(ItemWorker.GetLore(money).get(0)) * money.getAmount();
            } else System.out.println("Подделка.");
        } else System.out.println("Это не деньги.");

        return 0;
    }
}
