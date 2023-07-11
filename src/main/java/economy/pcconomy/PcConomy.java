package economy.pcconomy;

import economy.pcconomy.backend.cash.items.Wallet;
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

import economy.pcconomy.frontend.ui.windows.bank.BankerListener;
import economy.pcconomy.frontend.ui.windows.license.LicensorListener;
import economy.pcconomy.frontend.ui.windows.loans.loan.LoanListener;
import economy.pcconomy.frontend.ui.windows.loans.npcLoan.NPCLoanerListener;
import economy.pcconomy.frontend.ui.windows.mayor.MayorListener;
import economy.pcconomy.frontend.ui.windows.npcTrade.NPCTraderListener;
import economy.pcconomy.frontend.ui.windows.shareholder.ShareholderListener;
import economy.pcconomy.frontend.ui.windows.trade.TraderListener;

import me.yic.xconomy.api.XConomyAPI;

import org.bukkit.Bukkit;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

//TODO: Think about local storage for cash (may contains a moneys via text in lore)

public final class PcConomy extends JavaPlugin {
    public static FileConfiguration Config;
    public static XConomyAPI xConomyAPI;
    public static NpcManager GlobalNPC;
    public static Bank GlobalBank;
    public static BorrowerManager GlobalBorrowerManager;
    public static TownManager GlobalTownManager;
    public static LicenseManager GlobalLicenseManager;
    public static ShareManager GlobalShareManager;

    @Override
    public void onEnable() {
        saveConfig();
        saveDefaultConfig();

        Config                = PcConomy.getPlugin(PcConomy.class).getConfig();
        GlobalBank            = new Bank();
        GlobalBorrowerManager = new BorrowerManager();
        GlobalTownManager     = new TownManager();
        GlobalLicenseManager  = new LicenseManager();
        GlobalShareManager    = new ShareManager();

        try {
            if (new File("npc_data.json").exists())
                GlobalNPC = Loader.loadNPC("npc_data");
            if (new File("bank_data.json").exists())
                GlobalBank = Loader.loadBank("bank_data");
            if (new File("towns_data.json").exists())
                GlobalTownManager = Loader.loadTowns("towns_data");
            if (new File("license_data.json").exists())
                GlobalLicenseManager = Loader.loadLicenses("license_data");
            if (new File("shares_data.json").exists())
                GlobalLicenseManager = Loader.loadLicenses("shares_data");
            if (new File("borrowers_data.json").exists())
                GlobalBorrowerManager = Loader.loadBorrowers("borrowers_data");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (var listener : Arrays.asList(new NpcLoader(), new LoanListener(), new TownyListener(),
                new MayorListener(), new BankerListener(), new TraderListener(), new LicensorListener(),
                new NPCTraderListener(), new NPCLoanerListener(), new ShareholderListener(), new Wallet()))
            Bukkit.getPluginManager().registerEvents(listener, this);

        xConomyAPI  = new XConomyAPI();

        for (var command : Arrays.asList("take_cash", "create_cash", "reload_towns", "save_data", "put_cash_to_bank",
                "create_banker", "create_loaner", "create_npc_loaner", "create_trader", "create_npc_trader", "create_licensor", "switch_town_to_npc",
                "town_menu", "add_trade_to_town", "reload_npc", "full_info", "set_day_bank_budget", "create_wallet",
                "create_shareholder"))
            Objects.requireNonNull(getCommand(command)).setExecutor(new CommandManager());

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
        	new PcConomyPAPI().register();
    }

    @Override
    public void onDisable() {
        SaveData();
    }

    /***
     * Save all data into files
     */
    public static void SaveData() {
        try {
            GlobalNPC.saveNPC("npc_data");
            GlobalBank.saveBank("bank_data");
            GlobalTownManager.saveTown("towns_data");
            GlobalLicenseManager.saveLicenses("license_data");
            GlobalShareManager.saveShares("shares_data");
            GlobalBorrowerManager.saveBorrowers("borrowers_data");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
