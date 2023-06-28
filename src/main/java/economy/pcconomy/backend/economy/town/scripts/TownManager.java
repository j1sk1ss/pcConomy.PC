package economy.pcconomy.backend.economy.town.scripts;

import com.google.gson.GsonBuilder;
import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.economy.town.objects.scripts.StorageManager;
import economy.pcconomy.backend.save.adaptors.ItemStackTypeAdaptor;
import economy.pcconomy.backend.economy.town.objects.TownObject;
import economy.pcconomy.backend.scripts.ItemManager;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TownManager {
    public final List<TownObject> townObjects = new ArrayList<>();

    /**
     * Reload and save all towns from server
     */
    public void reloadTownObjects() {
        townObjects.clear();
        for (com.palmergames.bukkit.towny.object.Town town : TownyAPI.getInstance().getTowns())
            createTownObject(town, false);
    }

    /**
     * Create new town in plugin
     * @param town Created town
     * @param isNPC Is this town belongs NPC
     */
    public void createTownObject(com.palmergames.bukkit.towny.object.Town town, boolean isNPC) {
        townObjects.add(new TownObject(town, isNPC));
    }

    /**
     * Destroys town from plugin
     * @param townName Name of town that was destroyed
     */
    public void destroyTownObject(String townName) {
        for (TownObject townObject : townObjects)
            if (townObject.TownName.equals(townName)) {
                townObjects.remove(townObject);
                break;
            }
    }

    /**
     * Changes town NPS status
     * @param townName Name of town that change status
     * @param isNPC New status
     */
    public void changeNPCStatus(String townName, boolean isNPC) {
        var townObject = getTownObject(townName);
        townObject.isNPC = isNPC;
        townObject.initializeNPC();
        setTownObject(townObject);
    }

    /**
     * Gets town from list of town in plugin
     * @param townName Name of town
     * @return TownObject
     */
    public TownObject getTownObject(String townName) {
        for (TownObject townObject : townObjects)
            if (townObject.TownName.equals(townName))
                return townObject;

        return null;
    }

    /**
     * Sets town to list of town in plugin
     * @param town New townObject
     */
    public void setTownObject(TownObject town) {
        for (TownObject townObject : townObjects)
            if (townObject.TownName.equals(town.TownName)) {
                townObjects.remove(townObject);
                townObjects.add(town);
            }
    }

    /**
     * Saves towns into .json file
     * @param fileName File name
     * @throws IOException If something goes wrong
     */
    public void saveTown(String fileName) throws IOException {
        FileWriter writer = new FileWriter(fileName + ".json", false);
        new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ItemStackTypeAdaptor())
                .create()
                .toJson(this, writer);
        writer.close();
    }

    /**
     * Buy resources from town storage
     * @param townObject Town that will sell resource
     * @param itemStack Item that was chosen
     * @param buyer  Player who want by this item
     */
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

    /**
     * Sell resource to town
     * @param townObject Town that will buy resource
     * @param itemStack Item that will be sold
     * @param seller Player who sell item
     */
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
