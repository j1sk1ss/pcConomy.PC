package economy.pcconomy.backend.economy.town;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.scripts.ItemManager;
import economy.pcconomy.backend.economy.town.objects.TownObject;
import economy.pcconomy.backend.economy.town.objects.scripts.StorageManager;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Town {
    public static void buyResourceFromStorage(TownObject townObject, ItemStack itemStack, Player buyer) {
        var itemAmount = 8;
        var price = ItemManager.getPriceFromLore(itemStack, 1) * itemAmount;

        if (townObject == null) return;
        if (!townObject.isNPC) return;

        if (StorageManager.getAmountOfResource(itemStack, townObject.Storage) * townObject.usefulStorage < itemAmount) {
            buyer.sendMessage("Извините, но данного товара и у нас самих не очень много.");
            return;
        }

        var cash = new CashManager();
        if (cash.amountOfCashInInventory(buyer) < price) return;

        cash.takeCashFromInventory(price, buyer);
        townObject.changeBudget(price / PcConomy.GlobalBank.VAT);
        PcConomy.GlobalBank.BankBudget += (price - price / PcConomy.GlobalBank.VAT);
        ItemManager.giveItems(new ItemStack(itemStack.getType(), itemAmount), buyer);
        StorageManager.setAmountOfResource(itemStack, StorageManager.getAmountOfResource(itemStack, townObject.Storage) - itemAmount, townObject.Storage);

        PcConomy.GlobalTownWorker.getTownObject(TownyAPI.getInstance().getTownName(buyer.getLocation())).generateLocalPrices();
    }

    public static void sellResourceToStorage(TownObject townObject, ItemStack itemStack, Player seller) {
        var itemAmount = itemStack.getAmount();
        if (townObject == null) return;
        if (!townObject.isNPC) return;

        var resource = StorageManager.getResource(itemStack, townObject.Storage);
        if (resource == null) {
            seller.sendMessage("Такой товар мы не принимаем.");
            return;
        }

        var price = ItemManager.getPriceFromLore(resource, 1) * itemAmount;
        if (price > townObject.getBudget() * townObject.usefulBudget) {
            seller.sendMessage("Слишком дорого для нашего города.");
            return;
        }

        var cash  = new CashManager();

        seller.getInventory().setItemInMainHand(null);
        StorageManager.setAmountOfResource(itemStack, StorageManager.getAmountOfResource(itemStack, townObject.Storage) + itemAmount, townObject.Storage);
        cash.giveCashToPlayer(price / PcConomy.GlobalBank.VAT, seller);
        PcConomy.GlobalBank.BankBudget += (price - price / PcConomy.GlobalBank.VAT);
        townObject.changeBudget(-price);

        PcConomy.GlobalTownWorker.getTownObject(TownyAPI.getInstance().getTownName(seller.getLocation())).generateLocalPrices();
    }
}
