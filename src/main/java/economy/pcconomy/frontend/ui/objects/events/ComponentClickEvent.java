package economy.pcconomy.frontend.ui.objects.events;

import economy.pcconomy.frontend.ui.objects.interactive.IComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
 
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;


public class ComponentClickEvent extends Event implements Cancellable {
    public ComponentClickEvent(IComponent component, Player player, int slot) {
        this.slot = slot;
        clickedComponent = component;
        this.player = player;

        handlers = new HandlerList();
        isCancelled = false;
    }

    private int slot;
    private IComponent clickedComponent;
    private Player player;
    private boolean isCancelled;
    private HandlerList handlers;


    @Override
    public boolean isCancelled() {
        return isCancelled;
    }
    
    @Override
    public void setCancelled(boolean arg0) {
        isCancelled = arg0;
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public IComponent getClickedComponent() {
        return clickedComponent;
    }

    public int getClickedSlot() {
        return slot;
    }
}
