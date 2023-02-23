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

    public static final double usefulStorage = .3d;

    public static void BuyResourceFromStorage(TownObject townObject, ItemStack itemStack, Player buyer) {
        // Покупка в НПС городе за наличку
        var itemAmount = 8;
        var price = ItemWorker.GetPriceFromLore(itemStack, 1) * itemAmount;

        if (townObject == null) return;
        if (!townObject.isNPC) return;
        if (StorageWorker.GetAmountOfResource(itemStack, townObject.Storage) * usefulStorage < itemAmount) return;

        var cash = new Cash();
        if (cash.AmountOfCashInInventory(buyer) < price) return;

        cash.TakeCashFromInventory(price, buyer);
        townObject.ChangeBudget(price / PcConomy.GlobalBank.VAT);
        PcConomy.GlobalBank.BankBudget += (price / (PcConomy.GlobalBank.VAT + 1) * PcConomy.GlobalBank.VAT);

        ItemWorker.GiveItems(new ItemStack(itemStack.getType(), itemAmount), buyer);
        StorageWorker.SetAmountOfResource(itemStack, StorageWorker.GetAmountOfResource(itemStack,
                townObject.Storage) - itemAmount, townObject.Storage);

        PcConomy.GlobalTownWorker.GetTownObject(TownyAPI.getInstance()
                .getTownName(buyer.getLocation())).GenerateLocalPrices();
    }

    public static final double usefulBudget = .35d;

    public static void SellResourceToStorage(TownObject townObject, ItemStack itemStack, Player seller) {
        // Продажа в НПС городе за наличку
        var itemAmount = itemStack.getAmount();
        if (townObject == null) return;
        if (!townObject.isNPC) return;

        var resource = StorageWorker.GetResource(itemStack, townObject.Storage);
        if (resource == null) return;

        var price = ItemWorker.GetPriceFromLore(itemStack, 1) * itemAmount;
        var cash  = new Cash();

        if (price > townObject.getBudget() * usefulBudget) return;

        seller.getInventory().setItemInMainHand(null);
        StorageWorker.SetAmountOfResource(itemStack, StorageWorker.GetAmountOfResource(itemStack,
                townObject.Storage) + itemAmount, townObject.Storage);

        cash.GiveCashToPlayer(price / PcConomy.GlobalBank.VAT, seller);
        PcConomy.GlobalBank.BankBudget += (price / (PcConomy.GlobalBank.VAT + 1) * PcConomy.GlobalBank.VAT);
        townObject.ChangeBudget(-price);
    }
}
