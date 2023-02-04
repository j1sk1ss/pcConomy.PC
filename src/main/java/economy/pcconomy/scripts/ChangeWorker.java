package economy.pcconomy.scripts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChangeWorker {
    private static final List<Integer> Denomination = Arrays.asList(1,5,10,50,100,500,1000,5000);

    public static List<Integer> getDenomination(double amount) {
        List<Integer> change = new ArrayList<>();

        for (int sum:Denomination) {
            while (amount - sum > 0) {
                amount -= sum;
                change.add(sum);
            }
        }

        return change;
    }
}
