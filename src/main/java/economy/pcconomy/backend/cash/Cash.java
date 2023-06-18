package economy.pcconomy.backend.cash;

import economy.pcconomy.backend.cash.scripts.CashManager;
import economy.pcconomy.backend.cash.scripts.ChangeWorker;
import economy.pcconomy.backend.scripts.ItemWorker;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static economy.pcconomy.backend.cash.scripts.CashManager.CreateCashObject;

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

        List<ItemStack> change = CashManager.getChangeInCash(changeNumeric);
        ItemWorker.GiveItems(change, player);
    }

    public double AmountOfCashInInventory(Player player) { // Колличество денег у игрока в инвенторе
        return CashManager.GetAmountFromCash(CashManager.GetCashFromInventory(player.getInventory()));
    }

    public void TakeCashFromInventory(double amount, Player player) { // Забрать необходимую сумму из инвентаря со сдачей
        var playerCashAmount = AmountOfCashInInventory(player);

        if (playerCashAmount < amount) return;
        if (ItemWorker.GetEmptySlots(player) < ChangeWorker.getChange(amount).size()) return; // Если нет места для сдачи

        ItemWorker.TakeItems(CashManager.GetCashFromInventory(player.getInventory()), player);
        GiveSpecialAmountOfCashToPlayer(playerCashAmount - amount, player);
    }
}
