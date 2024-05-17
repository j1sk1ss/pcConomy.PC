package economy.pcconomy.frontend.mayor;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.economy.bank.Bank;
import economy.pcconomy.backend.economy.town.TownManager;
import economy.pcconomy.backend.npc.NpcManager;
import economy.pcconomy.backend.npc.traits.Trader;

import lombok.experimental.ExtensionMethod;
import net.citizensnpcs.api.CitizensAPI;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataType;

import org.j1sk1ss.itemmanager.manager.Manager;
import org.j1sk1ss.menuframework.objects.MenuSizes;
import org.j1sk1ss.menuframework.objects.MenuWindow;
import org.j1sk1ss.menuframework.objects.interactive.components.Button;
import org.j1sk1ss.menuframework.objects.interactive.components.ClickArea;
import org.j1sk1ss.menuframework.objects.interactive.components.Icon;
import org.j1sk1ss.menuframework.objects.interactive.components.Panel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static economy.pcconomy.frontend.trade.TraderWindow.getTraderFromTitle;


@ExtensionMethod({Cash.class, Manager.class, TownManager.class})
public class MayorManagerWindow {
    @SuppressWarnings("deprecation")
    public static MenuWindow TraderManager = new MenuWindow(Arrays.asList(
        new Panel(List.of(
            new ClickArea(0, 26, 
                (event) -> {
                    var player = (Player)event.getWhoClicked();
                    var inventory = event.getInventory();

                    var traderId = Integer.parseInt(Objects.requireNonNull(inventory.getItem(event.getSlot())).getName());
                    player.openInventory(MayorManagerWindow.generateTradeControls(player, traderId));
                }),

            new Button(27, 35, "Купить торговца", "Купить нового торговца",
                (event) -> new Trader().Buy((Player) event.getWhoClicked()))
        ), "დГород-Торговцы", MenuSizes.FourLines),

        new Panel(Arrays.asList(
            new Button(0, 20, "Уволить торговца", "Торговец будет уволен",
                (event) -> {
                    var title  = event.getView().getTitle();
                    var trader = getTraderFromTitle(title);
                    if (trader == null) return;
                    if (trader.IsRanted) return;

                    trader.destroy();
                }, Material.GOLD_INGOT, 7000),

            new Button(3, 23, "Переместить торговца", "Торговец будет перемещён в место вашего клика",
                (event) -> {
                    var player = (Player)event.getWhoClicked();
                    var title  = event.getView().getTitle();
                    var trader = getTraderFromTitle(title);
                    if (trader == null) return;

                    var key = new NamespacedKey(PcConomy.getPlugin(PcConomy.class), "trader-move");
                    if (!player.getPersistentDataContainer().has(key, PersistentDataType.INTEGER))
                        player.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, trader.getNPC().getId());
                }, Material.GOLD_INGOT, 7000),

            new Button(6, 26, "Улучшить торговца", "Торговец будет улучшен (+9 слотов)",
                (event) -> {
                    var player = (Player)event.getWhoClicked();
                    var title  = event.getView().getTitle();
                    var trader = getTraderFromTitle(title);
                    if (trader == null) return;
                    if (trader.Level >= 6) return;

                    var inventoryAmount = player.amountOfCashInInventory(false);
                    var price = NpcManager.traderCost * trader.Level;
                    if (Bank.checkVat(price) > inventoryAmount) return;

                    trader.Level = Math.min(trader.Level + 1, 6);
                    player.takeCashFromPlayer(PcConomy.GlobalBank.getBank().addVAT(price), false);
                }, Material.GOLD_INGOT, 7000)
        ), "ეГород-Торговцы-Управление", MenuSizes.ThreeLines)
    ));

    public static void generateWindow(Player player) {
        var components = new ArrayList<org.j1sk1ss.menuframework.objects.interactive.Component>();
        var town = TownyAPI.getInstance().getTown(player).getTown();
        for (var i = 0; i < Math.min(27, town.Traders.size()); i++) {
            var trader = CitizensAPI.getNPCRegistry().getById(town.Traders.get(i)).getOrAddTrait(Trader.class);
            components.add(new Icon(i, town.Traders.get(i) + "",
                "Ranted: " + trader.IsRanted + "\nMargin: " + trader.Margin + "\nRant price: " + trader.Cost, Material.GOLD_INGOT, 7000)); // TODO: Icons for traders
        }

        TraderManager.getPanel("დГород-Торговцы").getViewWith(player, components);
    }

    public static Inventory generateTradeControls(Player player, int traderId) {
        var window = Bukkit.createInventory(player, 27, Component.text("ეГород-Торговцы-Управление " + traderId));
        TraderManager.getPanel("ეГород-Торговцы-Управление").place(window);
        return window;
    }
}
