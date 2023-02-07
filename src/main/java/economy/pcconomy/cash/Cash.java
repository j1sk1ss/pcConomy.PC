package economy.pcconomy.cash;

import economy.pcconomy.cash.scripts.CashWorker;
import economy.pcconomy.cash.scripts.ChangeWorker;
import economy.pcconomy.scripts.ItemWorker;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static economy.pcconomy.cash.scripts.CashWorker.CreateCashObject;

public class Cash {

    public void GiveCashToPlayer(double amount, Player player) { // Дать игроку купюру существующим номиналом
        if (!ChangeWorker.Denomination.contains(amount)) { // Номинал не существует
            GiveSpecialAmountOfCashToPlayer(amount, player); // Выдача другими номиналами
            return;
        }
        if (ItemWorker.getEmptySlots(player) < 1) return; // Есть место под банкноту

        ItemWorker.giveItems(CreateCashObject(amount), player);
    }

    public void GiveSpecialAmountOfCashToPlayer(double amount, Player player) { // Выдача любой суммы купюрами
        var changeNumeric = ChangeWorker.getChange(amount); // Лист из кол-ва необходимых номиналов
        if (ItemWorker.getEmptySlots(player) < changeNumeric.size()) return; // Если нет места для сдачи

        List<ItemStack> change = CashWorker.getChangeInCash(changeNumeric);
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

        if (playerCashAmount < amount) return; // Если у игрока ет таких денег
        if (ItemWorker.getEmptySlots(player) < ChangeWorker.getChange(amount).size()) return; // Если нет места для сдачи

        ItemWorker.TakeItems(playerCash, player);
        GiveSpecialAmountOfCashToPlayer(playerCashAmount - amount, player);
    }
}
