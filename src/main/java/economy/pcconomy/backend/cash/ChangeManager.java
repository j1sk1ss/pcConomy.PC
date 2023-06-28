package economy.pcconomy.backend.cash;

import java.util.Arrays;
import java.util.List;

public class ChangeManager {
    public static final List<Double> Denomination = // Все номиналы доступные для взаимодействия
            Arrays.asList(5000.0, 2000.0, 1000.0, 500.0, 200.0, 100.0, 50.0, 10.0, 1.0, 0.5, 0.1, 0.05, 0.01);

    public static List<Integer> getChange(double amount) { // Получение кол-ва каждой из всех банкнот для сдачи
        List<Integer> change = Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

        for (int i = 0; i < Denomination.size(); i++) {
            while (amount - Denomination.get(i) >= 0) {
                amount -= Denomination.get(i);
                change.set(i, change.get(i) + 1);
            }
        }

        return change;
    }

}
