package economy.pcconomy;

import economy.pcconomy.backend.db.Loadable;
import economy.pcconomy.backend.npc.NpcManager;
import economy.pcconomy.backend.economy.bank.Bank;
import economy.pcconomy.backend.link.CommandManager;
import economy.pcconomy.backend.economy.bank.BankManager;
import economy.pcconomy.backend.economy.share.ShareManager;
import economy.pcconomy.backend.economy.town.TownyListener;
import economy.pcconomy.backend.placeholderapi.PcConomyPAPI;
import economy.pcconomy.backend.economy.credit.BorrowerManager;
import economy.pcconomy.backend.economy.license.LicenseManager;

import economy.pcconomy.frontend.WalletWindow;
import economy.pcconomy.frontend.MayorManagerWindow;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.io.IOException;


// TODO List:
// 2) Debugging
//   2.2) Shares debugging ?
// 3) Load \ Save
//   3.2) Check save \ loading ?
//
//  P.S. Don't forget about TODO


public final class PcConomy extends JavaPlugin {
    public static FileConfiguration Config;
    public static NpcManager        GlobalNPC;
    public static BankManager       GlobalBank;
    public static BorrowerManager   GlobalBorrower;
    public static LicenseManager    GlobalLicense;
    public static ShareManager      GlobalShare;
    
    private final String pluginPath = "plugins" + File.separator + "PcConomy" + File.separator;
    private PcConomy instance;

    @Override
    public void onEnable() {
        System.out.print("[PcConomy] Starting PcConomy.\n");

        var file = new File(getDataFolder() + File.separator + "config.yml");
        if (!file.exists()) this.saveDefaultConfig();

        System.out.print("[PcConomy] Enable plugin config.\n");

        //============================================
        //  Init global objects
        //============================================

            Config         = PcConomy.getPlugin(PcConomy.class).getConfig();
            GlobalNPC      = new NpcManager();
            GlobalBank     = new BankManager(new Bank());
            GlobalBorrower = new BorrowerManager();
            GlobalLicense  = new LicenseManager();
            GlobalShare    = new ShareManager();

            System.out.print("[PcConomy] Initializing global managers.\n");

        //============================================
        //  Init global objects
        //============================================
        //  Load objects
        //============================================

            try {
                if (!Loadable.isFileEmpty(new File(pluginPath + "npc_data.json")))
                    GlobalNPC = GlobalNPC.load(pluginPath + "npc_data", NpcManager.class);
                if (!Loadable.isFileEmpty(new File(pluginPath + "bank_data.json")))
                    GlobalBank = GlobalBank.load(pluginPath + "bank_data", BankManager.class);
                if (!Loadable.isFileEmpty(new File(pluginPath + "license_data.json")))
                    GlobalLicense = GlobalLicense.load(pluginPath + "license_data", LicenseManager.class);
                if (!Loadable.isFileEmpty(new File(pluginPath + "shares_data.json")))
                    GlobalShare = GlobalShare.load(pluginPath + "shares_data", ShareManager.class);
                if (!Loadable.isFileEmpty(new File(pluginPath + "borrowers_data.json")))
                    GlobalBorrower = GlobalBorrower.load(pluginPath + "borrowers_data", BorrowerManager.class);
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

            for (var listener : Arrays.asList(new TownyListener(), new WalletWindow(), new MayorManagerWindow(), new NpcManager()))
                Bukkit.getPluginManager().registerEvents(listener, this);

            System.out.print("[PcConomy] Listeners registered.\n");

        //============================================
        //  Register listeners
        //============================================
        //  Register commands
        //============================================

            var command_manager = new CommandManager();
            for (var command : Arrays.asList("take_cash", "create_cash", "put_cash2bank",
                    "create_banker", "create_npc_loaner", "create_trader", "create_npc_trader", "create_licensor",
                    "town_menu", "reload_npc", "full_info", "set_day_bank_budget", "bank_new_day",
                    "create_wallet", "create_shareholder", "shares_rate", "global_market_prices"))
                Objects.requireNonNull(getCommand(command)).setExecutor(command_manager);

            System.out.print("[PcConomy] Commands registered.\n");

        //============================================
        //  Register commands
        //============================================

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
        	new PcConomyPAPI().register();

        System.out.print("[PcConomy] PAPI registered.\n");
    }

    @Override
    public void onDisable() {
        saveData();
    }

    public PcConomy getInstance() {
        if (instance == null) instance = new PcConomy();
        return instance;
    }

    /**
     * Save all data into files
     */
    private void saveData() {
        try {
            for (var manager : Arrays.asList(GlobalBank, GlobalBorrower, GlobalLicense, GlobalShare))
                manager.save(pluginPath + manager.getName());
        } catch (IOException e) {
            System.out.println("IO exception" + e.getMessage());
        }
        catch (Exception e) {
            System.out.println("Unhandled exception:" + e.getMessage());
        }
    }
}
