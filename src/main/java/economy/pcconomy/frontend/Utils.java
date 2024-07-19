package economy.pcconomy.frontend;

import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class Utils {
    public static Inventory getTopInventory(InventoryEvent event) {
        try {
            Object view = event.getView();
            Method getTopInventory = view.getClass().getMethod("getTopInventory");
            getTopInventory.setAccessible(true);
            return (Inventory) getTopInventory.invoke(view);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getInventoryTitle(InventoryEvent event) {
        try {
            Object view = event.getView();
            Method getTopInventory = view.getClass().getMethod("getTitle");
            getTopInventory.setAccessible(true);
            return (String) getTopInventory.invoke(view);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
