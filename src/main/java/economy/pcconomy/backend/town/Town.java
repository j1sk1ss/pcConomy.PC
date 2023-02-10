package economy.pcconomy.backend.town;

import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.scripts.ItemWorker;
import economy.pcconomy.backend.town.objects.TownObject;
import economy.pcconomy.backend.town.objects.scripts.StorageWorker;
import economy.pcconomy.backend.town.scripts.TownWorker;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Town {
    public static void BuyResourceFromStorage(TownObject townObject, ItemStack itemStack, Player buyer) {
        // Покупка в НПС городе за наличку
        var itemAmount = itemStack.getAmount();

        if (townObject == null) return;
        var storageAmount = StorageWorker.getAmountOfResource(itemStack, townObject.Storage);

        if (storageAmount < itemAmount) return;
        if (!townObject.isNPC) return;

        var price = townObject.Prices.get(StorageWorker.getResource(itemStack, townObject.Storage));
        var cash = new Cash();

        if (cash.AmountOfCashInInventory(buyer) < price) return;

        cash.TakeCashFromInventory(price, buyer);
        townObject.setBudget(townObject.getBudget() + price);

        ItemWorker.giveItems(itemStack, buyer);
        StorageWorker.setAmountOfResource(itemStack, StorageWorker.getAmountOfResource(itemStack,
                townObject.Storage) - itemAmount, townObject.Storage);
    }

    public static void SellResourceToStorage(TownObject townObject, ItemStack itemStack, Player seller) {
        // Продажа в НПС городе за наличку
        var itemAmount = itemStack.getAmount();

        if (townObject == null) return;
        if (!townObject.isNPC) return;

        var price = townObject.Prices.get(StorageWorker.getResource(itemStack, townObject.Storage));
        var cash = new Cash();

        if (price > townObject.getBudget()) return;

        ItemWorker.TakeItems(itemStack, seller);
        StorageWorker.setAmountOfResource(itemStack, StorageWorker.getAmountOfResource(itemStack,
                townObject.Storage) + itemAmount, townObject.Storage);

        cash.GiveCashToPlayer(price, seller);
        townObject.setBudget(townObject.getBudget() - price);
    }
}
