package economy.pcconomy.backend.bank.npc;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.frontend.ui.windows.loan.LoanWindow;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.event.EventHandler;

@TraitName("Loaner")
public class Loaner extends Trait {
    public Loaner() {
        super("Loaner");
    }

    public String homeTown;

    @EventHandler
    public void onClick(NPCRightClickEvent event) {
        if (!event.getNPC().equals(this.getNPC())) return;

        homeTown = TownyAPI.getInstance().getTownName(this.getNPC().getStoredLocation());

        var player = event.getClicker();
        var town = TownyAPI.getInstance().getTown(homeTown);
        boolean canReadHistory =
                PcConomy.GlobalLicenseWorker.isOverdue(PcConomy.GlobalLicenseWorker
                        .GetLicense(town.getMayor().getPlayer(), LicenseType.LoanHistory));

        player.openInventory(LoanWindow.GetNPCLoanWindow(player, canReadHistory));
    }
}
