package economy.pcconomy.backend.cash.items;

import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.scripts.items.Item;
import economy.pcconomy.backend.scripts.items.ItemManager;
import economy.pcconomy.frontend.ui.windows.wallet.WalletWindow;

import org.apache.commons.lang.StringUtils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Wallet implements Listener {
    /**
     * Event for working with wallet
     * @param event Event
     */
    @EventHandler
    public void onWalletUse(PlayerInteractEvent event){
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.LEFT_CLICK_AIR &&
                event.getAction() != Action.RIGHT_CLICK_AIR) return;

        var player = event.getPlayer();
        var wallet = player.getInventory().getItemInMainHand();

        if (isWallet(wallet)) {
            switch (event.getAction()) {
                case LEFT_CLICK_AIR ->
                    player.openInventory(WalletWindow.putWindow(player, wallet));
                case RIGHT_CLICK_AIR ->
                    player.openInventory(WalletWindow.withdrawWindow(player, wallet));
            }

            event.setCancelled(true);
        }
    }

    public static final double WalletCapacity = 10000;
    private static final int walletDataModel = 17050; //TODO: DATA MODEL

    /**
     * Create new wallet
     * @param player Player that will take this wallet
     */
    public static void giveWallet(Player player) {
        ItemManager.giveItems(new Item("Кошелёк", "0.0 Алеф", Material.BOOK, 1, walletDataModel), player);
    }

    /**
     * Create wallet with special amount of cash
     * @param player Player that will take this wallet
     * @param amount Special amount
     */
    public static void giveWallet(Player player, double amount) {
        ItemManager.giveItems(new Item("Кошелёк", amount + " " + CashManager.getCurrencyNameByNum((int)amount),
                Material.BOOK, 1, walletDataModel), player);
    }

    /**
     * Get wallet from inventory
     * @param player Player
     * @return Wallet
     */
    public static List<ItemStack> getWallets(Player player) {
        var list = new ArrayList<ItemStack>();
        for (var item : player.getInventory())
            if (item != null)
                if (isWallet(item)) list.add(item);

        return list;
    }

    /**
     * Check item
     * @param itemStack Item
     * @return Wallet status
     */
    public static boolean isWallet(ItemStack itemStack) {
        return StringUtils.containsAny(ItemManager.getLore(itemStack).get(0).toLowerCase(), "алеф") &&
                ItemManager.getName(itemStack).contains("Кошелёк");
    }

    /**
     * Gets amount of wallet
     * @param wallet Wallet
     * @return Amount
     */
    public static double getWalletAmount(ItemStack wallet) {
        return Double.parseDouble(ItemManager.getLore(wallet).get(0).split(" ")[0]);
    }

    /**
     * Gets amount of wallet
     * @param wallets Wallets
     * @return Amount
     */
    public static double getWalletAmount(List<ItemStack> wallets) {
        var amount = 0;
        for (var wallet : wallets)
            amount += Double.parseDouble(ItemManager.getLore(wallet).get(0).split(" ")[0]);

        return amount;
    }

    /**
     * Put cash into players wallet
     * @param player Player
     * @param amount Amount
     */
    public static void changeCashInWallet(Player player, double amount) {
        var wallets = getWallets(player);
        ItemManager.takeItems(wallets, player);

        var count = 0;
        for (var wallet : wallets)
            count += wallet.getAmount();

        giveWallet(player, getWalletAmount(wallets) + amount);
        for (var i = 1; i < count; i++)
            giveWallet(player);
    }

    /**
     * Put cash into players wallet
     * @param wallet Wallet
     * @param player Player
     * @param amount Amount
     */
    public static void changeCashInWallet(ItemStack wallet, Player player, double amount) {
        ItemManager.takeItems(wallet, player);
        giveWallet(player, getWalletAmount(wallet) + amount);
    }
}
