package economy.pcconomy.backend.economy.town;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.economy.credit.Loan;
import economy.pcconomy.backend.economy.credit.scripts.LoanManager;
import economy.pcconomy.backend.economy.town.objects.Storage;

import org.apache.commons.math3.util.Precision;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.j1sk1ss.itemmanager.manager.Manager;

import lombok.experimental.ExtensionMethod;
import java.util.*;

import static economy.pcconomy.backend.cash.CashManager.*;


@ExtensionMethod({Manager.class, CashManager.class})
public class NpcTown extends Town {
    /**
     * Npc town
     * @param town TownyAPI town
     */
    public NpcTown(com.palmergames.bukkit.towny.object.Town town) {
        TownUUID = town.getUUID();
        Credit   = new ArrayList<>();

        Storage = new Storage(Arrays.asList(
                new ItemStack(Material.SPRUCE_WOOD, 1000),
                new ItemStack(Material.STONE, 2500),
                new ItemStack(Material.GLASS, 1700),
                new ItemStack(Material.CARROT, 5000),
                new ItemStack(Material.BEEF, 2000),
                new ItemStack(Material.IRON_INGOT, 1650),
                new ItemStack(Material.COBBLESTONE, 5000)
        ));

        StartStorageAmount = Storage.getAmountOfStorage();

        setBudget(previousBudget);
        newDay();
    }

    /**
     * Npc town
     * @param townUUID NPC town UID
     * @param credit NPC town credit list
     * @param storage NPC town storage
     * @param previousBudget NPC town previous budget
     */
    public NpcTown(UUID townUUID, List<Loan> credit, Storage storage, double previousBudget,
                   double usefulStorage, double usefulBudget, double townVAT) {
        TownUUID = townUUID;
        Credit   = credit;
        Storage  = storage;

        this.previousBudget = previousBudget;
        this.usefulStorage  = usefulStorage;
        this.usefulBudget   = usefulBudget;
        this.townVAT        = townVAT;
    }

    public double usefulStorage = PcConomy.Config.getDouble("town.start_useful_storage", .5);
    public double usefulBudget  = PcConomy.Config.getDouble("town.start_useful_budget", .5);
    public double townVAT       = PcConomy.Config.getDouble("town.start_vat", .05d);

    public final economy.pcconomy.backend.economy.town.objects.Storage Storage;
    public final List<Loan> Credit;
    public final UUID TownUUID;

    private final int purchaseSize = 8;
    private double previousBudget = 10000;
    private double dayBudget = previousBudget * usefulBudget;
    private int StartStorageAmount;
    private int dayStorage = (int)(StartStorageAmount * usefulStorage);

    /**
     * Buy resources from town storage
     * @param itemStack Item that was chosen
     * @param buyer  Player who want by this item
     */
    public void buyResourceFromStorage(ItemStack itemStack, Player buyer) {
        var price = itemStack.getDoubleFromContainer("item-price") * purchaseSize;
        if (price == -1) return;
        if (dayStorage < purchaseSize) {
            buyer.sendMessage("Извините, но данного товара и у нас самих не очень много.");
            return;
        }

        if (CashManager.amountOfCashInInventory(buyer, false) < price) {
            buyer.sendMessage("Приходи когда мммммммм, будешь немного по богаче.");
            return;
        }

        dayStorage -= purchaseSize;
        dayBudget  += price;

        CashManager.takeCashFromPlayer(price, buyer, false);

        changeBudget(PcConomy.GlobalBank.deleteVAT(price));
        new ItemStack(itemStack.getType(), purchaseSize).giveItems(buyer);
        Storage.setAmountOfResource(itemStack, Storage.getAmountOfResource(itemStack) - purchaseSize);

        generateLocalPrices();
    }

    /**
     * Sell resource to town
     * @param itemStack Item that will be sold
     * @param seller Player who sell item
     */
    public void sellResourceToStorage(ItemStack itemStack, Player seller) {
        var itemAmount = itemStack.getAmount();
        var resource   = Storage.getResource(itemStack);
        if (resource == null) {
            seller.sendMessage("Такой товар мы не принимаем.");
            return;
        }

        var price = resource.getDoubleFromContainer("item-price") * itemAmount;
        if (price == -1) return;
        if (price > dayBudget) {
            seller.sendMessage("Слишком дорого для нашего города.");
            return;
        }

        dayStorage += itemAmount;
        dayBudget  -= price;

        seller.getInventory().setItemInMainHand(null);
        Storage.setAmountOfResource(itemStack, Storage.getAmountOfResource(itemStack) + itemAmount);

        giveCashToPlayer(PcConomy.GlobalBank.deleteVAT(price), seller, false);
        changeBudget(-price);

        generateLocalPrices();
    }

    /**
     * Generate prices for stuff
     */
    public void generateLocalPrices() {
        var budget = getBudget();

        for (var i = 0; i < Storage.StorageBody.size(); i++) {
            var price  = Precision.round(Math.abs(budget / Storage.StorageBody.get(i).getAmount() + 1), 3);
            var margin = Precision.round((PcConomy.GlobalBank.VAT + townVAT), 3);
            var marginPercent = margin * 100d;
            var endPrive = Precision.round(price + (price * margin), 3);

            if (price > 0)
                Storage.StorageBody.set(i, 
                    Storage.StorageBody.get(i)
                        .setLore("Цена за " + purchaseSize + " шт.:\n" + purchaseSize * endPrive + CashManager.currencySigh + 
                                 "\nБез НДС в " + marginPercent + "%:\n" + purchaseSize * price + CashManager.currencySigh));
            else Storage.StorageBody.set(i, Storage.StorageBody.get(i).setLore("Не доступен для торговли"));

            Storage.StorageBody.get(i).setDouble2Container(price, "item-price");
        }
    }

    /**
     * Get town inflation
     * @return Local inflation
     */
    public double getLocalInflation() {
        return (getBudget() / previousBudget) - ((double) Storage.getAmountOfStorage() / StartStorageAmount);
    }

    /**
     * Take moneys from bank
     * @param amount Amount of taken moneys
     */
    public void getMoneyFromBank(double amount) {
        if (amount > PcConomy.GlobalBank.DayWithdrawBudget) return;

        PcConomy.GlobalBank.BankBudget -= amount;
        changeBudget(amount);
    }

    @Override
    public UUID getUUID() {
        return TownUUID;
    }

    @Override
    public void newDay() {
        LoanManager.takePercentFromBorrowers(this);

        var changePercent = (getBudget() - previousBudget) / previousBudget;
        var isRecession   = (changePercent <= 0 && getLocalInflation() > 0) ? 1 : -1;

        if (changePercent < 0 && isRecession == 1)
            getMoneyFromBank(1000 + Math.abs(changePercent) * 1000);

        townVAT       += (townVAT * Math.abs(changePercent) / 2) * isRecession;
        usefulBudget  -= (usefulBudget * Math.abs(changePercent)) * isRecession;
        usefulStorage += (usefulStorage * Math.abs(changePercent)) * isRecession;

        Storage.createResources(500);
        Storage.useResources(100);

        dayStorage = (int)(StartStorageAmount * usefulStorage);
        dayBudget  = previousBudget * usefulBudget;

        previousBudget = getBudget();
    }

    @Override
    public List<Loan> getCreditList() {
        return Credit;
    }
}
