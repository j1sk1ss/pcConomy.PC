package economy.pcconomy;

import economy.pcconomy.backend.bank.scripts.BorrowerManager;

import economy.pcconomy.backend.license.scripts.LicenseManager;
import economy.pcconomy.backend.link.Manager;
import economy.pcconomy.backend.npc.NPC;
import economy.pcconomy.backend.npc.listener.NPCLoader;
import economy.pcconomy.backend.placeholderapi.PcConomyPAPI;
import economy.pcconomy.backend.save.Loader;
import economy.pcconomy.backend.town.listener.TownyListener;
import economy.pcconomy.backend.bank.Bank;
import economy.pcconomy.backend.town.scripts.TownManager;

import economy.pcconomy.frontend.ui.windows.bank.BankerListener;
import economy.pcconomy.frontend.ui.windows.license.LicensorListener;
import economy.pcconomy.frontend.ui.windows.loan.LoanListener;
import economy.pcconomy.frontend.ui.windows.loan.NPCLoanerListener;
import economy.pcconomy.frontend.ui.windows.mayor.MayorListener;
import economy.pcconomy.frontend.ui.windows.npcTrade.NPCTraderListener;
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

public final class PcConomy extends JavaPlugin {
    public static FileConfiguration Config;
    public static XConomyAPI xConomyAPI;
    public static NPC GlobalNPC;
    public static Bank GlobalBank;
    public static BorrowerManager GlobalBorrowerManager;
    public static TownManager GlobalTownWorker;
    public static LicenseManager GlobalLicenseWorker;

    @Override
    public void onEnable() {
        saveConfig();
        saveDefaultConfig();

        Config                = PcConomy.getPlugin(PcConomy.class).getConfig();
        GlobalBank            = new Bank();
        GlobalBorrowerManager = new BorrowerManager();
        GlobalTownWorker      = new TownManager();
        GlobalLicenseWorker   = new LicenseManager();

        try {
            if (new File("NPCData.txt").exists())
                GlobalNPC = Loader.loadNPC("NPCData");
            if (new File("BankData.txt").exists())
                GlobalBank = Loader.loadBank("BankData");
            if (new File("TownsData.txt").exists())
                GlobalTownWorker = Loader.loadTowns("TownsData");
            if (new File("LicenseData.txt").exists())
                GlobalLicenseWorker = Loader.loadLicenses("LicenseData");
            if (new File("BorrowersData.txt").exists())
                GlobalBorrowerManager = Loader.loadBorrowers("BorrowersData");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var listeners = Arrays.asList(new NPCLoader(), new LoanListener(), new TownyListener(),
                new MayorListener(), new BankerListener(), new TraderListener(), new LicensorListener(),
                new NPCTraderListener(), new NPCLoanerListener());

        for (Listener listener: listeners)
            Bukkit.getPluginManager().registerEvents(listener, this);

        xConomyAPI  = new XConomyAPI();
        var manager = new Manager();

        var commands = Arrays.asList("take_cash", "create_cash", "reload_towns", "save_data", "put_cash_to_bank",
                "create_banker", "create_loaner", "create_trader", "create_npc_trader", "create_licensor", "switch_town_to_npc",
                "town_menu", "add_trade_to_town", "reload_npc", "full_info");

        for (var command : commands)
            Objects.requireNonNull(getCommand(command)).setExecutor(manager);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)  // Регистрация PAPI
        	new PcConomyPAPI().register();
    }

    @Override
    public void onDisable() {
        SaveData();
    }

    public static void SaveData() {
        try {
            GlobalNPC.saveNPC("NPCData");
            GlobalBank.saveBank("BankData");
            GlobalTownWorker.saveTown("TownsData");
            GlobalLicenseWorker.saveLicenses("LicenseData");
            GlobalBorrowerManager.saveBorrowers("BorrowersData");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
