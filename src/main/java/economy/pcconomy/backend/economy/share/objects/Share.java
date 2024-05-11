package economy.pcconomy.backend.economy.share.objects;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.economy.bank.Bank;
import economy.pcconomy.backend.economy.town.TownManager;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.j1sk1ss.itemmanager.manager.Item;
import org.j1sk1ss.itemmanager.manager.Manager;

import lombok.experimental.ExtensionMethod;
import java.util.UUID;


@ExtensionMethod({Manager.class, Cash.class, TownManager.class})
public class Share {
    public Share(UUID townUUID, ShareType shareType, double price, double equality) {
        TownUUID  = townUUID;
        ShareType = shareType;
        Price     = price;
        Equality  = equality;

        ShareUUID = new UUID(100, 10000000);

        Revenue = 0;
        IsSold  = false;
    }

    public Share(ItemStack shareBody) {
        var loreLine = shareBody.getLoreLines();

        TownUUID  = UUID.fromString(loreLine.get(0));
        ShareUUID = UUID.fromString(loreLine.get(1));

        ShareType = economy.pcconomy.backend.economy.share.objects.ShareType.Equity;

        Price    = Double.parseDouble(loreLine.get(2));
        Equality = 0;
    }

    public final UUID TownUUID;
    public final UUID ShareUUID;
    public final ShareType ShareType;
    public boolean IsSold;

    public final double Price;
    public final double Equality;

    public double Revenue;

    /**
     * Check if itemStack is shareBody
     * @param shareBody ItemStack
     * @return True or False
     */
    public static boolean isShare(ItemStack shareBody) {
        try {
            var loreLine = shareBody.getLoreLines();
            if (loreLine == null) return false;
            if (loreLine.size() < 3) return false;

            var first  = UUID.fromString(loreLine.get(0));
            var second = UUID.fromString(loreLine.get(1));
            Double.parseDouble(loreLine.get(2));
            return true;
        }
        catch (NumberFormatException exception) {
            return false;
        }
    }

    /**
     * Player buy share
     * @param buyer Player who buy share
     */
    public void buyShare(Player buyer) {
        if (IsSold) return;
        if (Cash.amountOfCashInInventory(buyer, false) >= Bank.checkVat(Price)) {
            buyer.takeCashFromPlayer(PcConomy.GlobalBank.getMainBank().addVAT(Price), false);
            TownUUID.getTown().changeBudget(Price);

            IsSold = true;
            new Item("Акция", TownUUID + "\n" + ShareUUID + "\n" + Price).giveItems(buyer);
        }
    }

    /**
     * Player sell share
     * @param seller  Player who buy share
     * @param shareItem Share item in inventory
     */
    public void sellShare(Player seller, ItemStack shareItem) {
        var currentTown = TownUUID.getTown();
        if (currentTown == null) {
            seller.sendMessage("Город-владелец прекратил своё существование");
            return;
        }

        if (currentTown.getBudget() >= Price) {
            seller.giveCashToPlayer(PcConomy.GlobalBank.getMainBank().deleteVAT(Price), false);
            currentTown.changeBudget(-Price);

            IsSold = false;
            shareItem.takeItems(seller);
        }
    }
}
