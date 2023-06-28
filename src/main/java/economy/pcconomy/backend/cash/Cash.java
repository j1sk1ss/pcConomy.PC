package economy.pcconomy.backend.cash;

import economy.pcconomy.backend.cash.scripts.CashManager;
import economy.pcconomy.backend.cash.scripts.ChangeManager;
import economy.pcconomy.backend.scripts.ItemManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static economy.pcconomy.backend.cash.scripts.CashManager.createCashObject;

public class Cash {
    /***
     * Gives to player items of cash
     * @param amount Amount of cash
     * @param player Player that will take this cash
     */
    public void giveCashToPlayer(double amount, Player player) {
        if (!ChangeManager.Denomination.contains(amount)) {
            giveSpecialAmountOfCashToPlayer(amount, player);
            return;
        }

        if (ItemManager.getEmptySlots(player) < 1) return;

        ItemManager.giveItems(CashManager.createCashObject(amount), player);
    }

    /***
     * Gives to player items of cash with special amount
     * @param amount Amount of cash
     * @param player Player that will take this cash
     */
    public void giveSpecialAmountOfCashToPlayer(double amount, Player player) {
        var changeNumeric = ChangeManager.getChange(amount);
        if (ItemManager.getEmptySlots(player) < changeNumeric.size()) return;

        List<ItemStack> change = CashManager.getChangeInCash(changeNumeric);
        ItemManager.giveItems(change, player);
    }

    /***
     * Amount of cash in player inventory
     * @param player Player that will be checked
     * @return Amount of cah in player`s inventory
     */
    public double amountOfCashInInventory(Player player) {
        return CashManager.getAmountFromCash(CashManager.getCashFromInventory(player.getInventory()));
    }

    /***
     * Take cash from player
     * @param amount Amount that will be taken
     * @param player Player that will lose this amount
     */
    public void takeCashFromInventory(double amount, Player player) {
        var playerCashAmount = amountOfCashInInventory(player);

        if (playerCashAmount < amount) return;
        if (ItemManager.getEmptySlots(player) < ChangeManager.getChange(amount).size()) return;

        ItemManager.takeItems(CashManager.getCashFromInventory(player.getInventory()), player);
        giveSpecialAmountOfCashToPlayer(playerCashAmount - amount, player);
    }
}
