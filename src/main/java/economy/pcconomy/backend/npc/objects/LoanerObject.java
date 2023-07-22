package economy.pcconomy.backend.npc.objects;

import java.util.UUID;

public class LoanerObject implements INpcObject {
    public LoanerObject(double pull, UUID homeTown) {
        Pull     = pull;
        HomeTown = homeTown;
    }

    public final double Pull;
    public UUID HomeTown;
}
