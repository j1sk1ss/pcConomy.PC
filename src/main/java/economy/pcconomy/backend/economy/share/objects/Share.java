package economy.pcconomy.backend.economy.share.objects;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.economy.bank.Bank;

import lombok.Getter;
import lombok.Setter;

import net.potolotcraft.gorodki.GorodkiUniverse;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.j1sk1ss.itemmanager.manager.Item;
import org.j1sk1ss.itemmanager.manager.Manager;

import lombok.experimental.ExtensionMethod;
import java.util.UUID;


@Getter
@ExtensionMethod({Manager.class, Cash.class})
public class Share {
    public Share(UUID townUUID, ShareType shareType, double price, double equality) {
        this.townUUID  = townUUID;
        this.shareType = shareType;
        this.price     = price;
        this.equality  = equality;

        shareUUID = new UUID(100, 10000000);

        revenue = 0;
        isSold  = false;
    }

    public Share(ItemStack shareBody) {
        var loreLine = shareBody.getLoreLines();

        townUUID  = UUID.fromString(loreLine.get(0));
        shareUUID = UUID.fromString(loreLine.get(1));
        shareType = economy.pcconomy.backend.economy.share.objects.ShareType.Equity;
        price     = Double.parseDouble(loreLine.get(2));
        equality  = 0;
    }

    private final UUID townUUID;
    private final UUID shareUUID;
    private final ShareType shareType;
    @Setter private boolean isSold;
    private final double price;
    private final double equality;
    @Setter private double revenue;

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

            @SuppressWarnings("unused")
            var first  = UUID.fromString(loreLine.get(0));

            @SuppressWarnings("unused")
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
        if (isSold) return;
        if (Cash.amountOfCashInInventory(buyer, false) >= Bank.getValueWithVat(price)) {
            buyer.takeCashFromPlayer(PcConomy.GlobalBank.getBank().addVAT(price), false);
            GorodkiUniverse.getInstance().getGorod(townUUID).changeBudget(price);

            isSold = true;
            new Item("Акция", townUUID + "\n" + shareUUID + "\n" + price).giveItems(buyer);
        }
    }

    /**
     * Player sell share
     * @param seller  Player who buy share
     * @param shareItem Share item in inventory
     */
    public void sellShare(Player seller, ItemStack shareItem) {
        var currentTown = GorodkiUniverse.getInstance().getGorod(townUUID);
        if (currentTown == null) {
            seller.sendMessage("Город-владелец прекратил своё существование");
            return;
        }

        if (currentTown.getBudget() >= price) {
            seller.giveCashToPlayer(PcConomy.GlobalBank.getBank().deleteVAT(price), false);
            currentTown.changeBudget(-price);

            isSold = false;
            shareItem.takeItems(seller);
        }
    }
}
