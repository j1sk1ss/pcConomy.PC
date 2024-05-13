package economy.pcconomy.backend.economy.share;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.db.Loadable;
import economy.pcconomy.backend.economy.share.objects.Share;
import economy.pcconomy.backend.economy.share.objects.ShareType;
import economy.pcconomy.backend.economy.town.TownManager;
import lombok.experimental.ExtensionMethod;

import org.bukkit.entity.Player;

import java.util.*;


@ExtensionMethod({Cash.class, TownManager.class})
public class ShareManager extends Loadable {
    public final List<UUID> InteractionList = new ArrayList<>();
    public final Map<UUID, List<Share>> Shares = new HashMap<>();

    /**
     * Expose shares
     * @param town Town who expose shares
     * @param price Price of share
     * @param count Count of shares
     * @param size Size of equality by one share
     * @param shareType Share type
     */
    public void exposeShares(UUID town, double price, int count, double size, ShareType shareType) {
        var shares = new ArrayList<Share>();
        var townShares = getTownShares(town);
        if (townShares != null)
            for (var share : townShares)
                if (share.IsSold) shares.add(share);

        for (var i = 0; i < count - shares.size(); i++)
            shares.add(new Share(town, shareType, price, size / (double) count));

        Shares.put(town, shares);
        InteractionList.add(town);
    }

    /**
     * Take off shares from market (Town will need to buy all shares from players by average price)
     * @param town Town who take off shares
     */
    public void takeOffShares(UUID town) {
        var shares = new ArrayList<Share>();
        var prevShares = getTownShares(town);

        if (prevShares != null)
            for (var share : prevShares)
                if (share.IsSold) shares.add(share);

        if (shares.size() > 0) Shares.put(town, shares);
        else Shares.remove(town);

        InteractionList.add(town);
    }

    /**
     * Get shares of town
     * @param town Town
     * @return Shares
     */
    public List<Share> getTownShares(UUID town) {
        return Shares.get(town);
    }

    /**
     * Sold first empty share of town
     * @param town Town
     * @return Share that was sold
     */
    public Share soldFirstEmptyShare(UUID town) {
        var shares = Shares.get(town);
        Share share = null;

        for (var body : shares)
            if (!body.IsSold) {
                body.IsSold = true;
                share = body;
                break;
            }

        Shares.put(town, shares);
        return share;
    }

    /**
     * Get share without owner
     * @param town Town
     * @return Share without owner
     */
    public List<Share> getEmptyTownShare(UUID town) {
        var list = new ArrayList<Share>();
        for (var share : Shares.get(town))
            if (!share.IsSold) list.add(share);

        return list;
    }

    /**
     * Get average price of share
     * @param town Town
     * @return Average price
     */
    public double getMedianSharePrice(UUID town) {
        var price = 0;
        var shares = Shares.get(town);
        for (var share : shares)
            if (!share.IsSold) price += share.Price;

        return price / ((double)shares.size() + 1);
    }

    /**
     * Daily pay
     */
    public void newDay() {
        Shares.keySet().parallelStream().forEach(this::payDividends);
        InteractionList.clear();
    }

    /**
     * Town pay for share owners
     * @param town Town that pay
     */
    public void payDividends(UUID town) {
        var townObject = town.getTown();
        for (var shares : Shares.get(town)) {
            if (shares.ShareType == ShareType.Equity) break;
            if (townObject.QuarterlyEarnings < 0) break;

            var pay = townObject.QuarterlyEarnings * shares.Equality;
            townObject.changeBudget(-pay);
            shares.Revenue += pay;
        }

        townObject.QuarterlyEarnings = 0;
    }

    /**
     * Give cash that earn this share
     * @param owner Current owner of share
     */
    public void cashOutShare(Player owner, Share share) {
        for (var townShares : Shares.get(share.TownUUID)) {
            if (townShares.ShareUUID.equals(share.ShareUUID)) {
                owner.giveCashToPlayer(PcConomy.GlobalBank.getMainBank().deleteVAT(townShares.Revenue), false);
                townShares.Revenue = 0;

                return;
            }
        }
    }

    @Override
    public String getName() {
        return "shares_data";
    }
}
