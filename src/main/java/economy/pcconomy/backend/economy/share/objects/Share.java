package economy.pcconomy.backend.economy.share.objects;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.scripts.items.Item;
import economy.pcconomy.backend.scripts.items.ItemManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;


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
        var loreLine = ItemManager.getLore(shareBody);

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
     * Player buy share
     * @param buyer Player who buy share
     */
    public boolean buyShare(Player buyer) {
        if (IsSold) return false;

        if (CashManager.amountOfCashInInventory(buyer, false) >= PcConomy.GlobalBank.checkVat(Price)) {
            CashManager.takeCashFromPlayer(PcConomy.GlobalBank.addVAT(Price), buyer, false);
            PcConomy.GlobalTownManager.getTown(TownUUID).changeBudget(Price);

            IsSold = true;
            ItemManager.giveItems(new Item("Акция", TownUUID + "\n" + ShareUUID + "\n" + Price), buyer);

            return true;
        }

        return false;
    }

    /**
     * Player sell share
     * @param seller  Player who buy share
     */
    public void sellShare(Player seller) {
        var currentTown = PcConomy.GlobalTownManager.getTown(TownUUID);
        if (currentTown == null) return;

        if (currentTown.getBudget() >= Price) {
            CashManager.giveCashToPlayer(PcConomy.GlobalBank.deleteVAT(Price), seller, false);
            currentTown.changeBudget(-Price);

            IsSold = false;
        }
    }

    /**
     * Give cash that earn this share
     * @param owner Current owner of share
     */
    public void cashOutShare(Player owner) {
        CashManager.giveCashToPlayer(PcConomy.GlobalBank.deleteVAT(Revenue), owner, false);
        Revenue = 0;
    }
}
