package economy.pcconomy.cash;

import economy.pcconomy.scripts.CashWorker;
import economy.pcconomy.scripts.ChangeWorker;
import economy.pcconomy.scripts.ItemWorker;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static economy.pcconomy.scripts.CashWorker.CreateCashObject;

public class Cash {

    public void GiveCashToPlayer(double amount, Player player) { // Дать игроку купюру существующим номиналом
        if (!ChangeWorker.Denomination.contains((int)amount)) return;
        ItemWorker.giveItems(CreateCashObject(amount), player);
    }

    public void GetSpecialCashToPlayer(double amount, Player player) { // Выдача любой суммы купюрами
        List<ItemStack> change = CashWorker.getChangeInCash(ChangeWorker.getChange(amount));
        ItemWorker.giveItems(change, player);
    }

    public double AmountOfCashInHand(Player player) { // Колличество денег у игрока в руке
        return new CashWorker().GetAmountFromCash(player.getInventory().getItemInMainHand());
    }

    public double TakeCashFromHand(Player player) { // Забрать деньги из рук игрока и получить их кол-во
        double amount = AmountOfCashInHand(player);
        if (amount == 0) return 0;

        player.getInventory().setItemInMainHand(null);
        return amount;
    }

    public double AmountOfCashInInventory(Player player) { // Колличество денег у игрока в инвенторе
        var playerCash = new CashWorker().GetCashFromInventory(player.getInventory());
        return new CashWorker().GetAmountFromCash(playerCash);
    }

    public void TakeCashFromInventory(double amount, Player player) { // Забрать необходимую сумму из инвентаря со сдачей
        var playerCash = new CashWorker().GetCashFromInventory(player.getInventory());
        var playerCashAmount = AmountOfCashInInventory(player);
        if (playerCashAmount < amount) return;

        ItemWorker.TakeItems(playerCash, player);
        GetSpecialCashToPlayer(playerCashAmount - amount, player);
    }
}
