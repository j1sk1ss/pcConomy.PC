package economy.pcconomy;

import economy.pcconomy.backend.economy.bank.Bank;
import economy.pcconomy.backend.economy.credit.BorrowerManager;
import economy.pcconomy.backend.economy.share.ShareManager;
import economy.pcconomy.backend.economy.TownyListener;
import economy.pcconomy.backend.economy.town.manager.TownManager;
import economy.pcconomy.backend.license.LicenseManager;
import economy.pcconomy.backend.link.CommandManager;
import economy.pcconomy.backend.npc.NpcManager;
import economy.pcconomy.backend.npc.traits.*;
import economy.pcconomy.backend.placeholderapi.PcConomyPAPI;

import economy.pcconomy.frontend.mayor.MayorManagerListener;
import economy.pcconomy.frontend.wallet.WalletListener;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;


// TODO List:
// 1) Refactoring
//   1.1) Change project structure
//   1.2) Cleanup junk code (If it exists)
// 2) Debugging
//   2.1) Wallet debugging
//   2.2) Shares debugging
//   2.3) Money transfer debugging
// 3) Load \ Save
//   3.1) Finish Town adaptor
//   3.2) Check save \ loading
// 4) Models
//   4.1) Set all model data where it needed
// 5) Bank
//   5.1) Check bank and credit working (Maybe change loan class structure?)
//
//  P.S. Don't forget about TODO


public final class PcConomy extends JavaPlugin {
    
    public static FileConfiguration Config;
    public static NpcManager        GlobalNPC;
    public static Bank              GlobalBank;
    public static BorrowerManager   GlobalBorrowerManager;
    public static TownManager       GlobalTownManager;
    public static LicenseManager    GlobalLicenseManager;
    public static ShareManager      GlobalShareManager;
    
    private final String pluginPath = "plugins\\PcConomy\\";

    @Override
    public void onEnable() {

        System.out.print("[PcConomy] Starting PcConomy.\n");

        saveConfig();
        saveDefaultConfig();

        System.out.print("[PcConomy] Enable plugin config.\n");

        //============================================
        //  Init global objects
        //============================================

            Config                = PcConomy.getPlugin(PcConomy.class).getConfig();
            GlobalNPC             = new NpcManager();
            GlobalBank            = new Bank();
            GlobalBorrowerManager = new BorrowerManager();
            GlobalTownManager     = new TownManager();
            GlobalLicenseManager  = new LicenseManager();
            GlobalShareManager    = new ShareManager();

            System.out.print("[PcConomy] Initializing global managers.\n");

        //============================================
        //  Init global objects
        //============================================
        //  Load objects
        //============================================

            try {
                if (new File(pluginPath + "npc_data.json").exists()) GlobalNPC = NpcManager.loadNPC(pluginPath + "npc_data");
                if (new File(pluginPath + "bank_data.json").exists()) GlobalBank = Bank.loadBank(pluginPath + "bank_data");
                if (new File(pluginPath + "towns_data.json").exists()) GlobalTownManager = TownManager.loadTowns(pluginPath + "towns_data");
                if (new File(pluginPath + "license_data.json").exists()) GlobalLicenseManager = LicenseManager.loadLicenses(pluginPath + "license_data");
                if (new File(pluginPath + "shares_data.json").exists()) GlobalShareManager = ShareManager.loadShares(pluginPath + "shares_data");
                if (new File(pluginPath + "borrowers_data.json").exists()) GlobalBorrowerManager = BorrowerManager.loadBorrowers(pluginPath + "borrowers_data");
            } catch (IOException error) {
                System.out.println(error.getMessage());
            }

            System.out.print("[PcConomy] Loading saved data complete.\n");

        //============================================
        //  Load objects
        //============================================
        //  Register listeners
        //      - NpcLoader - load traits for NPC when Citizens enabled
        //      - TownyListener - listen all Towny events (like taxes, creating town etc.)
        //      - PlayerListener - listen all players actions in inventory (for windows)
        //      - WalletListener - listen all player actions with wallet object
        //============================================

            for (var listener : Arrays.asList(new TownyListener(), new WalletListener(), new MayorManagerListener()))
                Bukkit.getPluginManager().registerEvents(listener, this);

            System.out.print("[PcConomy] Listeners registered.\n");

        //============================================
        //  Register listeners
        //============================================
        //  Register commands
        //============================================

            var command_manager = new CommandManager();
            for (var command : Arrays.asList("take_cash", "create_cash", "reload_towns", "put_cash2bank",
                    "create_banker", "create_npc_loaner", "create_trader", "create_npc_trader", "create_licensor",
                    "switch_town2npc", "switch_town2player", "town_menu", "add_trade2town", "reload_npc", "full_info", "set_day_bank_budget",
                    "create_wallet", "create_shareholder", "shares_rate", "global_market_prices"))
                Objects.requireNonNull(getCommand(command)).setExecutor(command_manager);

            System.out.print("[PcConomy] Commands registered.\n");

        //============================================
        //  Register commands
        //============================================

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
        	new PcConomyPAPI().register();

        System.out.print("[PcConomy] PAPI registered.\n");
        System.out.print("[PcConomy] Traits registered.\n");

        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Trader.class).withName("trader"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(NpcLoaner.class).withName("npcloaner"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(NpcTrader.class).withName("npctrader"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Banker.class).withName("banker"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Licensor.class).withName("licensor"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Shareholder.class).withName("shareholder"));

        System.out.print("[PcConomy] NPC reloading.\n");

        NpcManager.reloadNPC();
    }

    @Override
    public void onDisable() {
        saveData();
    }

    /**
     * Save all data into files
     */
    public void saveData() {
        try {
            GlobalNPC.saveNPC(pluginPath + "npc_data");
            GlobalBank.saveBank(pluginPath + "bank_data");
            GlobalTownManager.saveTown(pluginPath + "towns_data");
            GlobalShareManager.saveShares(pluginPath + "shares_data");
            GlobalLicenseManager.saveLicenses(pluginPath + "license_data");
            GlobalBorrowerManager.saveBorrowers(pluginPath + "borrowers_data");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
