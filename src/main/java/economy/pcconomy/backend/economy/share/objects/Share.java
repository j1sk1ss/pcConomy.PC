package economy.pcconomy.backend.economy.share.objects;

import java.util.UUID;

public class Share {
    public Share(UUID townUUID, ShareType shareType, UUID owner, double price, double equality) {
        TownUUID      = townUUID;
        ShareType = shareType;
        Owner     = owner;
        Price     = price;
        Equality  = equality;
    }

    public UUID TownUUID;
    public ShareType ShareType;
    public UUID Owner;

    public double Price;
    public double Equality;
}

