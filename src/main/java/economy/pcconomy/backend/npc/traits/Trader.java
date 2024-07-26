package economy.pcconomy.backend.npc.traits;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.economy.bank.Bank;
import economy.pcconomy.backend.economy.license.objects.LicenseType;
import economy.pcconomy.frontend.TraderWindow;

import lombok.Getter;
import lombok.Setter;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;

import io.papermc.paper.event.player.AsyncChatEvent;

import net.kyori.adventure.text.TextComponent;

import net.potolotcraft.gorodki.GorodkiUniverse;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.j1sk1ss.itemmanager.manager.Manager;

import lombok.experimental.ExtensionMethod;
import java.time.LocalDateTime;
import java.util.*;


@TraitName("Trader")
@ExtensionMethod({Manager.class, Cash.class})
public class Trader extends Trait {
    public Trader() {
        super("Trader");

        Storage     = new ArrayList<>();
        SpecialList = new ArrayList<>();
        Margin      = 0d;
        Cost        = 0d;
        Ranted      = false;
        HomeTown    = null;
        Owner       = null;
        Term        = LocalDateTime.now().toString();
        Level       = 1;
    }

    public Trader(TraderData data) {
        super("Trader");

        Storage  = data.getStorage();
        Revenue  = data.getRevenue();
        Margin   = data.getMargin();
        Cost     = data.getCost();
        Ranted   = data.isRanted();
        HomeTown = data.getHomeTown();
        Owner    = data.getOwner();
        Term     = data.getTerm();
        Level    = data.getLevel();
    }

    public Trader(List<ItemStack> storage, double revenue, double margin, double cost, boolean isRanted,
                     UUID homeTown, UUID owner, String term, int level) {
        super("Trader");

        Storage  = storage;
        Revenue  = revenue;
        Margin   = margin;
        Cost     = cost;
        Ranted   = isRanted;
        HomeTown = homeTown;
        Owner    = owner;
        Term     = term;
        Level    = level;
    }

    @Getter @Setter private List<ItemStack> Storage;
    @Getter @Setter private List<UUID> SpecialList;
    @Getter @Setter private double Revenue;
    @Getter @Setter private double Margin;
    @Getter @Setter private double Cost;
    @Getter @Setter private boolean Ranted;
    @Getter @Setter private String Term;
    @Getter @Setter private UUID HomeTown;
    @Getter @Setter private UUID Owner;
    @Getter @Setter private int Level;

    private transient final Dictionary<UUID, Integer> chat = new Hashtable<>();

    @EventHandler
    public void onClick(NPCRightClickEvent event) {
        if (!event.getNPC().equals(this.getNPC())) return;
        if (HomeTown == null) {
            var storedTown = TownyAPI.getInstance().getTown(this.getNPC().getStoredLocation());
            if (storedTown != null) HomeTown = storedTown.getUUID();
            else {
                event.getClicker().sendMessage("Что я здесь забыл?");
                HomeTown = null;
            }

            return;
        }

        // We stole all moneys to town and delete all resources if rant is over
        if (LocalDateTime.now().isAfter(LocalDateTime.parse(Term)) && Ranted) {
            GorodkiUniverse.getInstance().getGorod(HomeTown).changeBudget(Revenue);

            Ranted = false;
            Owner    = null;
            Revenue  = 0;
            Storage.clear();

            return;
        }

        var player = event.getClicker();
        try {
            if (Ranted) {
                if (Owner.equals(player.getUniqueId())) TraderWindow.getOwnerWindow(player, this);
                else TraderWindow.getWindow(player, this);
            }
            else {
                var town = TownyAPI.getInstance().getTown(this.getNPC().getStoredLocation());
                if (town == null) return;

                if (town.getMayor().getUUID().equals(player.getUniqueId())) TraderWindow.getMayorWindow(player, this);
                else TraderWindow.getRanterWindow(player, this);
            }
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
            if (Ranted) {
                if (Storage.size() >= (event.getNPC().getOrAddTrait(Trader.class).Level * 9)) {
                    player.sendMessage("Склад торговца переполнен!");
                    return;
                }

                if (Owner.equals(playerUUID) && Storage.size() < event.getNPC().getOrAddTrait(Trader.class).Level) {
                    player.sendMessage("Напишите свою цену. Учтите наценку города в " + Margin * 100 + "%");
                    chat.put(playerUUID, (Integer) event.getNPC().getId());
                }

                return;
            }

            if (Objects.requireNonNull(TownyAPI.getInstance().getTown(HomeTown)).getMayor().getUUID().equals(playerUUID)) {
                player.sendMessage("Удалить торговца? (д/н)");
                chat.put(playerUUID, (Integer) event.getNPC().getId());
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
                var sellingItem = player.getInventory().getItemInMainHand();
                if (sellingItem.getType().equals(Material.AIR)) {
                    player.sendMessage("Воздух, пока что, нельзя продавать.");
                    return;
                }

                try {
                    var cost = Double.parseDouble(playerMessage);
                    sellingItem.setDouble2Container(cost, "item-price");

                    trader.getOrAddTrait(Trader.class).Storage.add(sellingItem.setLore(cost + cost * Margin + Cash.currencySigh + "\nБез пошлины: " + cost + Cash.currencySigh));
                    player.getInventory().setItemInMainHand(null);
                    chat.remove(player.getUniqueId());

                    player.sendMessage("Предмет " + sellingItem.getName() + " выставлен на продажу за "
                        + (cost + cost * Margin) + " " + Cash.currencySigh);
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

    public void Buy(Player buyer) {
        if (buyer.amountOfCashInInventory(false) < Bank.getValueWithVat(
                PcConomy.Config.getDouble("npc.trader_cost", 3500d)
        )) return;

        var license = PcConomy.GlobalLicense.getLicense(buyer.getUniqueId(), LicenseType.Market);
        if (license == null) return;
        if (license.isOverdue()) return;

        buyer.takeCashFromPlayer(PcConomy.GlobalBank.getBank().addVAT(
                PcConomy.Config.getDouble("npc.trader_cost", 3500d)
        ), false);

        var npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Trader");
        linkToNPC(npc);
        npc.spawn(buyer.getLocation());
        npc.addTrait(this);

        GorodkiUniverse.getInstance().getGorod(TownyAPI.getInstance().getTownUUID(buyer.getLocation())).addTrader(getNPC().getId());
        buyer.sendMessage("Торговец куплен");
    }
}
