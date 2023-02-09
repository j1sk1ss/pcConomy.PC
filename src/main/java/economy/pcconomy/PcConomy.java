package economy.pcconomy;

/*
 Что работает:
 - Купюры, банкноты и подобное. Все методы для работы с ней в "Cash"
 - Банк. Печатка денег, кредитование, расчёт процентов, взятие процентов
 - Towny. А именно снятие со счёта через город и пополнение счёта через город. Взаимосвязанно с "Cash"
 - Обьект города. См. папку "town"

 Чего нету:
 - Цены хоть и в теории генерируются, но склад нормально не сделан. Это всё можно найти в папке "town"
 - Нет торговцев и в принципе ничего с этим связанного
 - Нет лицензий и всего что с ними связанно
 */


import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.frontend.ui.listener.BankerListener;
import economy.pcconomy.frontend.ui.listener.LoanerListener;
import economy.pcconomy.backend.link.Manager;
import economy.pcconomy.backend.timer.GlobalTimer;
import economy.pcconomy.backend.town.listener.TownyListener;
import economy.pcconomy.backend.bank.Bank;
import economy.pcconomy.frontend.ui.listener.TraderListener;
import me.yic.xconomy.api.XConomyAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class PcConomy extends JavaPlugin { // Гл класс плагина. Тут обьявляйте в статике нужные API
    // Так же желательно тут регистрировать Listeners
    // Ну и обработчики команд с командами тоже bruh

    public static XConomyAPI xConomyAPI;
    public static TownyAPI TownyAPI;
    public static Bank GlobalBank = new Bank(); // Глобальный банк

    @Override
    public void onEnable() {
        new GlobalTimer(this);

        Bukkit.getPluginManager().registerEvents(new TownyListener(), this);
        Bukkit.getPluginManager().registerEvents(new BankerListener(), this);
        Bukkit.getPluginManager().registerEvents(new LoanerListener(), this);
        Bukkit.getPluginManager().registerEvents(new TraderListener(), this);

        xConomyAPI  = new XConomyAPI(); // Общий API XConomy этого плагина. Брать только от сюда
        var manager = new Manager(); // Обработчик тестовых комманд

        getCommand("create").setExecutor(manager);
        getCommand("createB").setExecutor(manager);
        getCommand("createL").setExecutor(manager);
        getCommand("take").setExecutor(manager);
        getCommand("withdraw").setExecutor(manager);
        getCommand("put").setExecutor(manager);
        getCommand("createt").setExecutor(manager);
    }

    @Override
    public void onDisable() { // Тут будет сохранение всего и вся. Делайте не каскадом из 9999 строк, а разбейте
        // на разные классы. Но кого я учу, верно?
        // Plugin shutdown logic
    }
}
