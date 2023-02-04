package economy.pcconomy.scripts;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CashWorker {

    public static void GiveCash(double amount, Player player) { // Town -> Create cash -> Player
        BalanceWorker balanceWorker = new BalanceWorker();
        if (!balanceWorker.isSolvent(amount, player)) return;

        balanceWorker.TakeMoney(amount, player);
        for (ItemStack money: CreateCashObject(amount)) {
            player.getInventory().addItem(money);
        }
    }

    public static List<ItemStack> CreateCashObject(double amount) {
        List<ItemStack> moneys = new ArrayList<>();

        for (int money:ChangeWorker.getDenomination(amount)) {
            moneys.add(ItemWorker.SetName(ItemWorker.SetLore(new ItemStack(Material.PAPER, 1),
                    "" + money + "$"), "Доллар США"));
        }

        return moneys;
    }

    public void TakeCash(Player player) { // Player -> Town
        ItemStack money = player.getInventory().getItemInMainHand();
        int amount = 0;
        if (ItemWorker.GetName(money).equals("Доллар США")) {
            if (!Objects.equals(ItemWorker.GetLore(money).get(0), "")) {
                amount = Integer.parseInt(ItemWorker.GetLore(money).get(0)) * money.getAmount();
                new BalanceWorker().GiveMoney(amount, player);
                player.getInventory().setItemInMainHand(null);
            }
        }
    }

}
