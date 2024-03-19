package economy.pcconomy;

import economy.pcconomy.backend.economy.credit.scripts.BorrowerManager;
import economy.pcconomy.backend.economy.share.ShareManager;
import economy.pcconomy.backend.license.scripts.LicenseManager;
import economy.pcconomy.backend.link.CommandManager;
import economy.pcconomy.backend.npc.NpcManager;
import economy.pcconomy.backend.npc.loader.NpcLoader;
import economy.pcconomy.backend.placeholderapi.PcConomyPAPI;
import economy.pcconomy.backend.save.Loader;
import economy.pcconomy.backend.economy.town.listener.TownyListener;
import economy.pcconomy.backend.economy.bank.Bank;
import economy.pcconomy.backend.economy.town.scripts.TownManager;
import economy.pcconomy.backend.scripts.BalanceManager;
import economy.pcconomy.frontend.ui.PlayerListener;
import economy.pcconomy.frontend.ui.windows.wallet.WalletListener;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;


public final class PcConomy extends JavaPlugin {
    public static FileConfiguration Config;
    public static BalanceManager GlobalBalanceManager;
    public static NpcManager GlobalNPC;
    public static Bank GlobalBank;
    public static BorrowerManager GlobalBorrowerManager;
    public static TownManager GlobalTownManager;
    public static LicenseManager GlobalLicenseManager;
    public static ShareManager GlobalShareManager;
    private final String pluginPath = "plugins\\PcConomy\\";

    @Override
    public void onEnable() {
        saveConfig();
        saveDefaultConfig();

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
            GlobalBalanceManager  = new BalanceManager();

        //============================================
        //  Init global objects
        //============================================
        //  Load objects
        //============================================

            try {
                if (new File(pluginPath + "npc_data.json").exists())
                    GlobalNPC = Loader.loadNPC(pluginPath + "npc_data");
                if (new File(pluginPath + "bank_data.json").exists())
                    GlobalBank = Bank.loadBank(pluginPath + "bank_data");
                if (new File(pluginPath + "towns_data.json").exists())
                    GlobalTownManager = Loader.loadTowns(pluginPath + "towns_data");
                if (new File(pluginPath + "license_data.json").exists())
                    GlobalLicenseManager = Loader.loadLicenses(pluginPath + "license_data");
                if (new File(pluginPath + "shares_data.json").exists())
                    GlobalLicenseManager = Loader.loadLicenses(pluginPath + "shares_data");
                if (new File(pluginPath + "borrowers_data.json").exists())
                    GlobalBorrowerManager = Loader.loadBorrowers(pluginPath + "borrowers_data");
            } catch (IOException error) {
                System.out.println(error.getMessage());
            }

        //============================================
        //  Load objects
        //============================================
        //  Register listeners
        //      - NpcLoader - load traits for NPC when Citizens enabled
        //      - TownyListener - listen all Towny events (like taxes, creating town etc.)
        //      - PlayerListener - listen all players actions in inventory (for windows)
        //      - WalletListener - listen all player actions with wallet object
        //============================================

            for (var listener : Arrays.asList(new NpcLoader(), new TownyListener(),
                    new PlayerListener(), new WalletListener()))
                Bukkit.getPluginManager().registerEvents(listener, this);

        //============================================
        //  Register listeners
        //============================================
        //  Register commands
        //============================================

            var command_manager = new CommandManager();
            for (var command : Arrays.asList("take_cash", "create_cash", "reload_towns", "save_data", "put_cash_to_bank",
                    "create_banker", "create_loaner", "create_npc_loaner", "create_trader", "create_npc_trader", "create_licensor",
                    "switch_town_to_npc", "town_menu", "add_trade_to_town", "reload_npc", "full_info", "set_day_bank_budget",
                    "create_wallet", "create_shareholder", "transfer_share", "shares_rate", "global_market_prices"))
                Objects.requireNonNull(getCommand(command)).setExecutor(command_manager);

        //============================================
        //  Register commands
        //============================================

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
        	new PcConomyPAPI().register();
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
