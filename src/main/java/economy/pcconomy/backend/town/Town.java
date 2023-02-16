package economy.pcconomy.backend.town;

import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.scripts.ItemWorker;
import economy.pcconomy.backend.town.objects.TownObject;
import economy.pcconomy.backend.town.objects.scripts.StorageWorker;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Town {
    public static void BuyResourceFromStorage(TownObject townObject, ItemStack itemStack, Player buyer) {
        // Покупка в НПС городе за наличку
        var itemAmount = 8;
        var price = Double.parseDouble(ItemWorker.GetLore(itemStack).get(1)
                .replace(CashWorker.currencySigh, "")) * itemAmount;

        if (townObject == null) return;
        if (!townObject.isNPC) return;
        if (StorageWorker.getAmountOfResource(itemStack, townObject.Storage) * 2 < itemAmount) return;

        var cash = new Cash();
        if (cash.AmountOfCashInInventory(buyer) < price) return;

        cash.TakeCashFromInventory(price, buyer);
        townObject.setBudget(townObject.getBudget() + price);

        ItemWorker.giveItems(new ItemStack(itemStack.getType(), itemAmount), buyer);
        StorageWorker.setAmountOfResource(itemStack, StorageWorker.getAmountOfResource(itemStack,
                townObject.Storage) - itemAmount, townObject.Storage);
    }

    public static void SellResourceToStorage(TownObject townObject, ItemStack itemStack, Player seller) {
        // Продажа в НПС городе за наличку
        var itemAmount = itemStack.getAmount();
        if (townObject == null) return;
        if (!townObject.isNPC) return;

        var resource = StorageWorker.getResource(itemStack, townObject.Storage);
        if (resource == null) return;

        var price = Double.parseDouble(ItemWorker.GetLore(resource)
                .get(1).replace(CashWorker.currencySigh, "")) * itemAmount;
        var cash = new Cash();

        if (price > townObject.getBudget()) return;

        seller.getInventory().setItemInMainHand(null);
        StorageWorker.setAmountOfResource(itemStack, StorageWorker.getAmountOfResource(itemStack,
                townObject.Storage) + itemAmount, townObject.Storage);

        cash.GiveCashToPlayer(price, seller);
        townObject.setBudget(townObject.getBudget() - price);
    }
}
