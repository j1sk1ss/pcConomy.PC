package economy.pcconomy.backend.cash;

import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.cash.scripts.ChangeWorker;
import economy.pcconomy.backend.scripts.ItemWorker;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static economy.pcconomy.backend.cash.scripts.CashWorker.CreateCashObject;

public class Cash {

    public void GiveCashToPlayer(double amount, Player player) { // Дать игроку купюру существующим номиналом
        if (!ChangeWorker.Denomination.contains(amount)) { // Номинал не существует
            GiveSpecialAmountOfCashToPlayer(amount, player); // Выдача другими номиналами
            return;
        }
        if (ItemWorker.GetEmptySlots(player) < 1) return; // Есть место под банкноту

        ItemWorker.GiveItems(CreateCashObject(amount), player);
    }

    public void GiveSpecialAmountOfCashToPlayer(double amount, Player player) { // Выдача любой суммы купюрами
        var changeNumeric = ChangeWorker.getChange(amount); // Лист из кол-ва необходимых номиналов
        if (ItemWorker.GetEmptySlots(player) < changeNumeric.size()) return; // Если нет места для сдачи

        List<ItemStack> change = CashWorker.getChangeInCash(changeNumeric);
        ItemWorker.GiveItems(change, player);
    }

    public double AmountOfCashInHand(Player player) { // Колличество денег у игрока в руке
        return CashWorker.GetAmountFromCash(player.getInventory().getItemInMainHand());
    }

    public double TakeCashFromHand(Player player) { // Забрать деньги из рук игрока и получить их кол-во
        double amount = AmountOfCashInHand(player);
        if (amount == 0) return 0;

        player.getInventory().setItemInMainHand(null);
        return amount;
    }

    public double AmountOfCashInInventory(Player player) { // Колличество денег у игрока в инвенторе
        var playerCash = CashWorker.GetCashFromInventory(player.getInventory());
        return CashWorker.GetAmountFromCash(playerCash);
    }

    public void TakeCashFromInventory(double amount, Player player) { // Забрать необходимую сумму из инвентаря со сдачей
        var playerCash = CashWorker.GetCashFromInventory(player.getInventory());
        var playerCashAmount = AmountOfCashInInventory(player);

        if (playerCashAmount < amount) return; // Если у игрока ет таких денег
        if (ItemWorker.GetEmptySlots(player) < ChangeWorker.getChange(amount).size()) return; // Если нет места для сдачи

        ItemWorker.TakeItems(playerCash, player);
        GiveSpecialAmountOfCashToPlayer(playerCashAmount - amount, player);
    }
}
