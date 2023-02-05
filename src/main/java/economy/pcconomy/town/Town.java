package economy.pcconomy.town;

import economy.pcconomy.cash.Cash;
import economy.pcconomy.scripts.BalanceWorker;
import economy.pcconomy.scripts.CashWorker;
import economy.pcconomy.scripts.ItemWorker;
import economy.pcconomy.town.objects.TownObject;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Town {
    public List<TownObject> townObjects = new ArrayList<>(); // все города сервера

    public void CreateTown(com.palmergames.bukkit.towny.object.Town town, boolean isNPC) {
        // метод который должен быть вызван вместе с созданием города игроком
        townObjects.add(new TownObject(town, isNPC));
    }

    public void ChangeNPCStatus(com.palmergames.bukkit.towny.object.Town town, boolean isNPC) {
        // Метод изменяющий город игрока на город NPC
        var townObject = GetTownObject(town);
        townObject.isNPC = isNPC;
        SetTownObject(townObject);
    }

    public void WithdrawCash(int amount, Player player, com.palmergames.bukkit.towny.object.Town town) {
        // Метод снятия денег из города игроком (если в городе есть на это бюджет)
        var townObject = GetTownObject(town);
        var balanceWorker = new BalanceWorker();
        var cash = new Cash();

        if (amount > townObject.GetUsefulAmountOfBudget()) return;
        if (balanceWorker.isSolvent(amount, player)) return;
        if (!townObject.isNPC) return;

        balanceWorker.TakeMoney(amount, player);
        townObject.setBudget(townObject.getBudget() - amount);
        cash.GiveCashToPlayer(amount, player);
    }

    public void PutCash(ItemStack money, Player player, com.palmergames.bukkit.towny.object.Town town) {
        // Метод внесения денег городу игроком (идут на счёт и городу и игроку)
        var townObject = GetTownObject(town);
        if (!CashWorker.isCash(money)) return;
        if (!townObject.isNPC) return;

        var amount = new CashWorker().GetAmountFromCash(money);
        ItemWorker.TakeItems(money, player);
        new BalanceWorker().GiveMoney(amount, player);
        townObject.setBudget(townObject.getBudget() + amount);
    }

    public TownObject GetTownObject(com.palmergames.bukkit.towny.object.Town town) {
        // Получение обьекта города
        for (TownObject townObject:
                townObjects) {
            if (townObject.Town.equals(town)) {
                return townObject;
            }
        }

        return null;
    }

    public void SetTownObject(TownObject town) {
        // Обновление обьекта города
        for (TownObject townObject:
                townObjects) {
            if (townObject.Town.equals(town.Town)) {
                townObjects.remove(townObject);
                townObjects.add(town);
            }
        }
    }

    public void DestroyTownObject(com.palmergames.bukkit.towny.object.Town town) {
        // метод который должен быть вызван вместе с удалением
        for (TownObject townObject:
                townObjects) {
            if (townObject.Town.equals(town)) {
                townObjects.remove(townObject);
                break;
            }
        }
    }
}
