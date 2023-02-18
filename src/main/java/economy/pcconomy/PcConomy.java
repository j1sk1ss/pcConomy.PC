package economy.pcconomy;

/*
 Что работает:
 - Купюры, банкноты и подобное. Все методы для работы с ней в "Cash"
 - Банк. Печатка денег, кредитование, расчёт процентов, взятие процентов
 - Towny. А именно снятие со счёта через город и пополнение счёта через город. Взаимосвязанно с "Cash"
 - Обьект города. См. папку "town"
 - Торговля меж игроками. Создание торговцев, настройка
 - Лицензии на торговлю со стороны мэра и со стороны игрока
 - НПС торговцы
  - Аренда на один день
 - Сохранение данных
  - Города
  - Банк
  - НПС

 Чего нету:

 */


import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.backend.bank.scripts.BorrowerWorker;
import economy.pcconomy.backend.license.scripts.LicenseWorker;
import economy.pcconomy.backend.link.Manager;
import economy.pcconomy.backend.npc.NPC;
import economy.pcconomy.backend.save.Loader;
import economy.pcconomy.backend.timer.GlobalTimer;
import economy.pcconomy.backend.town.listener.TownyListener;
import economy.pcconomy.backend.bank.Bank;
import economy.pcconomy.backend.town.scripts.TownWorker;
import economy.pcconomy.frontend.ui.windows.bank.BankerListener;
import economy.pcconomy.frontend.ui.windows.license.LicensorListener;
import economy.pcconomy.frontend.ui.windows.loan.LoanerListener;
import economy.pcconomy.frontend.ui.windows.mayor.MayorListener;
import economy.pcconomy.frontend.ui.windows.npcTrade.NPCTraderListener;
import economy.pcconomy.frontend.ui.windows.trade.TraderListener;

import me.yic.xconomy.api.XConomyAPI;

import net.citizensnpcs.api.event.CitizensEnableEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class PcConomy extends JavaPlugin { // Гл класс плагина. Тут обьявляйте в статике нужные API
    // Так же желательно тут регистрировать Listeners
    // Ну и обработчики команд с командами тоже bruh

    public static XConomyAPI xConomyAPI;
    public static Bank GlobalBank = new Bank();
    public static NPC GlobalNPC = new NPC();
    public static BorrowerWorker GlobalBorrowerWorker = new BorrowerWorker();
    public static TownWorker GlobalTownWorker = new TownWorker();
    public static LicenseWorker GlobalLicenseWorker = new LicenseWorker();

    @Override
    public void onEnable() {
        try {
            if (new File("NPCData.txt").exists()) GlobalNPC = Loader.LoadNPC("NPCData");
            if (new File("BankData.txt").exists()) GlobalBank = Loader.LoadBank("BankData");
            if (new File("TownsData.txt").exists()) GlobalTownWorker = Loader.LoadTowns("TownsData");
            if (new File("LicenseData.txt").exists()) GlobalLicenseWorker = Loader.LoadLicenses("LicenseData");
            if (new File("BorrowersData.txt").exists()) GlobalBorrowerWorker = Loader.LoadBorrowers("BorrowersData");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Bukkit.getPluginManager().registerEvents(new TownyListener(), this);
        Bukkit.getPluginManager().registerEvents(new MayorListener(), this);
        Bukkit.getPluginManager().registerEvents(new BankerListener(), this);
        Bukkit.getPluginManager().registerEvents(new LoanerListener(), this);
        Bukkit.getPluginManager().registerEvents(new TraderListener(), this);
        Bukkit.getPluginManager().registerEvents(new LicensorListener(), this);
        Bukkit.getPluginManager().registerEvents(new NPCTraderListener(), this);

        xConomyAPI  = new XConomyAPI(); // Общий API XConomy этого плагина. Брать только от сюда
        var manager = new Manager(); // Обработчик тестовых комманд
        new GlobalTimer(this);

        getCommand("create").setExecutor(manager);
        getCommand("createB").setExecutor(manager);
        getCommand("createL").setExecutor(manager);
        getCommand("take").setExecutor(manager);
        getCommand("withdraw").setExecutor(manager);
        getCommand("put").setExecutor(manager);
        getCommand("createt").setExecutor(manager);
        getCommand("createnpct").setExecutor(manager);
        getCommand("createLic").setExecutor(manager);
        getCommand("swnpc").setExecutor(manager);
        getCommand("tmenu").setExecutor(manager);
        getCommand("addTrade").setExecutor(manager);
    }

    @EventHandler
    public void loadNPC(CitizensEnableEvent event) {
        GlobalNPC.UpdateNPC();
    }

    @Override
    public void onDisable() { // Тут будет сохранение всего и вся. Делайте не каскадом из 9999 строк, а разбейте
        // на разные классы. Но кого я учу, верно?
        try {
            GlobalNPC.SaveNPC("NPCData");
            GlobalBank.SaveBank("BankData");
            GlobalTownWorker.SaveTown("TownsData");
            GlobalLicenseWorker.SaveLicenses("LicenseData");
            GlobalBorrowerWorker.SaveBorrowers("BorrowersData");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
