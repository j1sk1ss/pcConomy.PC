package economy.pcconomy.backend.npc.objects;

import economy.pcconomy.backend.npc.traits.Loaner;

import java.util.UUID;

public class LoanerObject extends NpcObject {
    public LoanerObject(double pull, UUID homeTown) {
        super(null, pull, 0, 0, false, homeTown, null, null);
    }

    public LoanerObject(Loaner loaner) {
        super(null, loaner.Pull, 0, 0, false, loaner.HomeTown, null, null);
    }
}
