package economy.pcconomy.backend.npc.traits;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.npc.objects.TraderObject;
import economy.pcconomy.frontend.windows.trade.TraderWindow;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.j1sk1ss.itemmanager.manager.Manager;

import lombok.experimental.ExtensionMethod;
import java.time.LocalDateTime;
import java.util.*;


@TraitName("Trader")
@ExtensionMethod({Manager.class})
public class Trader extends Trait {
    public Trader() {
        super("Trader");

        Storage     = new ArrayList<>();
        SpecialList = new ArrayList<>();
        Term        = LocalDateTime.now().toString();
        HomeTown    = null;
        Level       = 1;
    }

    public Trader(TraderObject traderObject) {
        super("Trader");

        Owner    = traderObject.Owner;
        Storage  = traderObject.Storage;
        Revenue  = traderObject.Revenue;
        Cost     = traderObject.Cost;
        Margin   = traderObject.Margin;
        HomeTown = traderObject.HomeTown;
        IsRanted = traderObject.IsRanted;
        Term     = traderObject.Term;
        Level    = traderObject.Level;
    }

    public List<ItemStack> Storage;
    public List<UUID> SpecialList;
    public double Revenue;
    public double Margin;
    public double Cost;
    public boolean IsRanted;
    public String Term;
    public UUID HomeTown;
    public UUID Owner;
    public int Level;

    private final Dictionary<UUID, Integer> chat = new Hashtable<>();

    @EventHandler
    public void onClick(NPCRightClickEvent event) {
        if (!event.getNPC().equals(this.getNPC())) return;
        if (HomeTown == null) {
            var storedTown = TownyAPI.getInstance().getTown(this.getNPC().getStoredLocation());
            if (storedTown != null) HomeTown = storedTown.getUUID();
            else HomeTown = null; // TODO: Maybe delete NPC if town not exists?
        }

        // We stole all moneys to town and delete all resources if rant is over
        if (LocalDateTime.now().isAfter(LocalDateTime.parse(Term)) && IsRanted) {
            PcConomy.GlobalTownManager.getTown(HomeTown).changeBudget(Revenue);

            IsRanted = false;
            Owner    = null;
            Revenue  = 0;
            Storage.clear();

            return;
        }

        var player = event.getClicker();
        try {
            if (IsRanted)
                if (Owner.equals(player.getUniqueId())) player.openInventory(TraderWindow.getOwnerWindow(player, this));
                else player.openInventory(TraderWindow.getWindow(player, this));
            else
                if (Objects.requireNonNull(TownyAPI.getInstance().getTown(this.getNPC().getStoredLocation())).getMayor()
                        .getUUID().equals(player.getUniqueId())) player.openInventory(TraderWindow.getMayorWindow(player, this));
                else player.openInventory(TraderWindow.getRanterWindow(player, this));
        }
        catch (NullPointerException e) {
            player.sendMessage("Что-то пошло не так (1).");
        }
    }

    @EventHandler
    public void onInteraction(NPCLeftClickEvent event) {
        if (!event.getNPC().equals(this.getNPC())) return;

        var player = event.getClicker();
        var playerUUID = player.getUniqueId();

        try {
            if (IsRanted) {
                if (Owner.equals(playerUUID) && Storage.size() < event.getNPC().getOrAddTrait(Trader.class).Level) {
                    player.sendMessage("Напишите свою цену. Учтите наценку города в " + Margin * 100 + "%");
                    chat.put(playerUUID, event.getNPC().getId());
                } else if (Storage.size() >= event.getNPC().getOrAddTrait(Trader.class).Level)
                    player.sendMessage("Склад торговца переполнен!");

                return;
            }

            if (Objects.requireNonNull(TownyAPI.getInstance().getTown(HomeTown)).getMayor().getUUID().equals(playerUUID)) {
                player.sendMessage("Удалить торговца? (д/н)");
                chat.put(playerUUID, event.getNPC().getId());
            }
        } catch (NullPointerException exception) {
            player.sendMessage("Ошибка, мэр города не был найден! (2)");
        }
    }

    @EventHandler
    public void chatting(AsyncChatEvent event) {
    	if (event.isAsynchronous()) {
    		Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("PcConomy")), () -> {
    			var player = event.getPlayer();
    	        var playerMessage = ((TextComponent) event.originalMessage()).content();
    	        event.setCancelled(true);
    	
    	        if (chat.get(player.getUniqueId()) == null) return;

                var trader = CitizensAPI.getNPCRegistry().getById(chat.get(player.getUniqueId()));
//                if (StringUtils.containsAny(playerMessage.toLowerCase(), "дн")) {
//                    if (!Objects.requireNonNull(TownyAPI.getInstance().getTown(HomeTown)).getMayor().getUUID().equals(player.getUniqueId())) return;
//                    if (IsRanted) return;
//
//                    if (playerMessage.equalsIgnoreCase("д")) {
//                        PcConomy.GlobalNPC.Npc.remove(trader.getId());
//                        trader.destroy();
//                    }
//
//                    return;
//                }

                var sellingItem = player.getInventory().getItemInMainHand();
                if (sellingItem.getType().equals(Material.AIR)) {
                    player.sendMessage("Воздух, пока что, нельзя продавать.");
                    return;
                }

                try {
                    var cost = Double.parseDouble(playerMessage);
                    sellingItem.setDouble2Container(cost, "item-price");

                    trader.getOrAddTrait(Trader.class).Storage.add(sellingItem.setLore(cost + cost * Margin + CashManager.currencySigh + "\nБез пошлины: " + cost + CashManager.currencySigh));
                    player.getInventory().setItemInMainHand(null);
                    chat.remove(player.getUniqueId());

                    player.sendMessage("Предмет " + sellingItem.getName() + " выставлен на продажу за "
                        + (cost + cost * Margin) + " " + CashManager.currencySigh);
                }
                catch (NumberFormatException exception) {
                    player.sendMessage("Напишите корректную цену.");
                }

                chat.remove(player);
    		});
    	}
    }

    public void destroy() {
        this.getNPC().destroy();
    }
}
