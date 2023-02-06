package economy.pcconomy.listener;

import com.palmergames.bukkit.towny.event.actions.TownyBuildEvent;
import com.palmergames.bukkit.towny.event.actions.TownyDestroyEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import economy.pcconomy.town.Town;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TownyListener implements Listener {

    @EventHandler
    public void OnCreation(TownyBuildEvent event) throws NotRegisteredException {
        var mayor = event.getPlayer();
        var town = ((Resident) mayor).getTown();

        Town.CreateTownObject(town, false);
    }

    @EventHandler
    public void OnDestroy(TownyDestroyEvent event) throws NotRegisteredException {
        var mayor = event.getPlayer();
        var town = ((Resident) mayor).getTown();

        Town.DestroyTownObject(town);
    }
}
