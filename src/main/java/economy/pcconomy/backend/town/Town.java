package economy.pcconomy.backend.town;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.scripts.ItemWorker;
import economy.pcconomy.backend.town.objects.TownObject;
import economy.pcconomy.backend.town.objects.scripts.StorageWorker;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Town {
    public static void BuyResourceFromStorage(TownObject townObject, ItemStack itemStack, Player buyer) {
        var itemAmount = 8;
        var price = ItemWorker.GetPriceFromLore(itemStack, 1) * itemAmount;

        if (townObject == null) return;
        if (!townObject.isNPC) return;

        if (StorageWorker.GetAmountOfResource(itemStack, townObject.Storage) * townObject.usefulStorage < itemAmount) {
            buyer.sendMessage("Извините, но данного товара и у нас самих не очень много.");
            return;
        }

        var cash = new Cash();
        if (cash.AmountOfCashInInventory(buyer) < price) return;

        cash.TakeCashFromInventory(price, buyer);
        townObject.ChangeBudget(price / PcConomy.GlobalBank.VAT);
        PcConomy.GlobalBank.BankBudget += (price - price / PcConomy.GlobalBank.VAT);
        ItemWorker.GiveItems(new ItemStack(itemStack.getType(), itemAmount), buyer);
        StorageWorker.SetAmountOfResource(itemStack, StorageWorker.GetAmountOfResource(itemStack,
                townObject.Storage) - itemAmount, townObject.Storage);

        PcConomy.GlobalTownWorker.GetTownObject(TownyAPI.getInstance()
                .getTownName(buyer.getLocation())).GenerateLocalPrices();
    }

    public static void SellResourceToStorage(TownObject townObject, ItemStack itemStack, Player seller) {
        var itemAmount = itemStack.getAmount();
        if (townObject == null) return;
        if (!townObject.isNPC) return;

        var resource = StorageWorker.GetResource(itemStack, townObject.Storage);
        if (resource == null) {
            seller.sendMessage("Такой товар мы не принимаем.");
            return;
        }

        var price = ItemWorker.GetPriceFromLore(resource, 1) * itemAmount;
        if (price > townObject.getBudget() * townObject.usefulBudget) {
            seller.sendMessage("Слишком дорого для нашего города.");
            return;
        }

        var cash  = new Cash();

        seller.getInventory().setItemInMainHand(null);
        StorageWorker.SetAmountOfResource(itemStack, StorageWorker.GetAmountOfResource(itemStack,
                townObject.Storage) + itemAmount, townObject.Storage);
        cash.GiveCashToPlayer(price / PcConomy.GlobalBank.VAT, seller);
        PcConomy.GlobalBank.BankBudget += (price - price / PcConomy.GlobalBank.VAT);
        townObject.ChangeBudget(-price);

        PcConomy.GlobalTownWorker.GetTownObject(TownyAPI.getInstance()
                .getTownName(seller.getLocation())).GenerateLocalPrices();
    }
}
