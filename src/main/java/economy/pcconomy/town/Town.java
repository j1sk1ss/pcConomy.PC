package economy.pcconomy.town;

import economy.pcconomy.cash.Cash;
import economy.pcconomy.scripts.ItemWorker;
import economy.pcconomy.town.scripts.TownWorker;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Town {
    public void BuyResourceFromStorage(String townName, ItemStack itemStack, Player buyer) {
        // Покупка в НПС городе за наличку
        var townObject = TownWorker.GetTownObject(townName);
        var itemAmount = itemStack.getAmount();

        if (townObject == null) return;
        var storageAmount = townObject.getAmountOfResource(itemStack);

        if (storageAmount < itemAmount) return;
        if (!townObject.isNPC) return;

        var price = townObject.Prices.get(townObject.getResource(itemStack));
        var cash = new Cash();

        if (cash.AmountOfCashInInventory(buyer) < price) return;

        cash.TakeCashFromInventory(price, buyer);
        townObject.setBudget(townObject.getBudget() + price);

        ItemWorker.giveItems(itemStack, buyer);
        townObject.setAmountOfResource(itemStack, townObject.getAmountOfResource(itemStack) - itemAmount);
    }

    public void SellResourceToStorage(String townName, ItemStack itemStack, Player seller) {
        // Продажа в НПС городе за наличку
        var townObject = TownWorker.GetTownObject(townName);
        var itemAmount = itemStack.getAmount();

        if (townObject == null) return;
        if (!townObject.isNPC) return;

        var price = townObject.Prices.get(townObject.getResource(itemStack));
        var cash = new Cash();

        if (price > townObject.getBudget()) return;

        ItemWorker.TakeItems(itemStack, seller);
        townObject.setAmountOfResource(itemStack, townObject.getAmountOfResource(itemStack) + itemAmount);

        cash.GiveCashToPlayer(price, seller);
        townObject.setBudget(townObject.getBudget() - price);
    }
}
