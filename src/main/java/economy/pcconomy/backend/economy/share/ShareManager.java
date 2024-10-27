package economy.pcconomy.backend.economy.share;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.db.Loadable;
import economy.pcconomy.backend.economy.share.objects.Share;
import economy.pcconomy.backend.economy.share.objects.ShareType;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;

import net.potolotcraft.gorodki.GorodkiUniverse;
import org.bukkit.entity.Player;

import java.util.*;


@Getter
@ExtensionMethod({Cash.class})
public class ShareManager extends Loadable {
    private final List<UUID> InteractionList    = new ArrayList<>();
    private final Map<UUID, List<Share>> Shares = new HashMap<>();

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
                if (share.isSold()) shares.add(share);

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
                if (share.isSold()) shares.add(share);

        if (!shares.isEmpty()) Shares.put(town, shares);
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
            if (!body.isSold()) {
                body.setSold(true);
                share = body;
                break;
            }

        Shares.put(town, shares);
        return share;
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
        var townObject = GorodkiUniverse.getInstance().getGorod(town);
        Shares.get(town).parallelStream().forEach((shares) -> {
            if (shares.getShareType() != ShareType.Equity) {
                if (townObject.getQuarterlyEarnigns() >= 0) {
                    synchronized (townObject) {
                        var pay = townObject.getQuarterlyEarnigns() * shares.getEquality();
                        townObject.changeBudget(-pay);
                        shares.setRevenue(shares.getRevenue() + pay);
                    }
                }
            }
        });

        townObject.setQuarterlyEarnigns(0);
    }

    /**
     * Give cash that earn this share
     * @param owner Current owner of share
     */
    public void cashOutShare(Player owner, Share share) {
        for (var townShares : Shares.get(share.getTownUUID())) {
            if (townShares.getShareUUID().equals(share.getShareUUID())) {
                owner.giveCashToPlayer(PcConomy.GlobalBank.getBank().deleteVAT(townShares.getRevenue()), false);
                townShares.setRevenue(0);
                break;
            }
        }
    }

    @Override
    public String getName() {
        return "shares_data";
    }
}
