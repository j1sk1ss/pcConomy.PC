package economy.pcconomy.frontend.windows.mayor;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.backend.npc.NpcManager;
import economy.pcconomy.backend.npc.traits.Trader;
import economy.pcconomy.frontend.windows.Window;

import lombok.experimental.ExtensionMethod;
import net.citizensnpcs.api.CitizensAPI;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import org.bukkit.persistence.PersistentDataType;
import org.j1sk1ss.itemmanager.manager.Item;
import org.j1sk1ss.menuframework.objects.MenuWindow;
import org.j1sk1ss.menuframework.objects.interactive.components.Button;
import org.j1sk1ss.menuframework.objects.interactive.components.Panel;

import java.util.Arrays;
import java.util.List;

import static economy.pcconomy.frontend.windows.trade.TraderListener.getTraderFromTitle;


@ExtensionMethod({CashManager.class})
public class MayorManagerWindow extends Window {
    @SuppressWarnings("deprecation")
    public static MenuWindow TraderManager = new MenuWindow(Arrays.asList(
        new Panel(List.of(
            new Button(27, 35, "Купить торговца", "Купить нового торговца",
                (event) -> PcConomy.GlobalNPC.buyNPC((Player) event.getWhoClicked(),
                        LicenseType.Market, PcConomy.GlobalBank.addVAT(NpcManager.traderCost)))
        ), "Город-Торговцы"),

        new Panel(Arrays.asList(
            new Button(0, 20, "Уволить торговца", "Торговец будет уволен",
                (event) -> {
                    var title  = event.getView().getTitle();
                    var trader = getTraderFromTitle(title);
                    if (trader == null) return;
                    if (trader.IsRanted) return;

                    trader.destroy();
                }),

            new Button(3, 23, "Переместить торговца", "Торговец будет перемещён в место вашего клика",
                (event) -> {
                    var player = (Player)event.getWhoClicked();
                    var title  = event.getView().getTitle();
                    var trader = getTraderFromTitle(title);
                    if (trader == null) return;

                    var key = new NamespacedKey(PcConomy.getPlugin(PcConomy.class), "trader-move");
                    if (!player.getPersistentDataContainer().has(key, PersistentDataType.INTEGER))
                        player.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, trader.getNPC().getId());
                }),

            new Button(6, 26, "Улучшить торговца", "Торговец будет улучшен (+9 слотов)",
                (event) -> {
                    var player = (Player)event.getWhoClicked();
                    var title  = event.getView().getTitle();
                    var trader = getTraderFromTitle(title);
                    if (trader == null) return;
                    if (trader.Level >= 6) return;

                    var inventoryAmount = player.amountOfCashInInventory(false);
                    var price = NpcManager.traderCost * trader.Level;
                    if (PcConomy.GlobalBank.checkVat(price) > inventoryAmount) return;

                    trader.Level = Math.min(trader.Level + 1, 6);
                    player.takeCashFromPlayer(PcConomy.GlobalBank.addVAT(price), false);
                })
        ), "Город-Торговцы-Управление")
    ));

    @Override
    public Inventory generateWindow(Player player) {
        var window = Bukkit.createInventory(player, 36, Component.text("Город-Торговцы"));
        TraderManager.getPanel("Город-Торговцы").place(window);

        var town   = PcConomy.GlobalTownManager.getTown(TownyAPI.getInstance().getTown(player).getUUID());
        for (var i = 0; i < Math.min(27, town.traders.size()); i++) {
            var trader = CitizensAPI.getNPCRegistry().getById(town.traders.get(i)).getOrAddTrait(Trader.class);
            window.setItem(i, new Item(town.traders.get(i) + "",
                    "Ranted: " + trader.IsRanted + "\nMargin: " + trader.Margin + "\nRant price: " + trader.Cost));
        }

        return window;
    }

    public static Inventory generateTradeControls(Player player, int traderId) {
        var window = Bukkit.createInventory(player, 27, Component.text("Город-Торговцы-Управление " + traderId));
        TraderManager.getPanel("Город-Торговцы-Управление").place(window);
        return window;
    }
}
