import java.util.Scanner;

import ui.FinanceTrackerUserInterface;

public class Program {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FinanceTrackerUserInterface ui = new FinanceTrackerUserInterface(scanner);

        ui.start();
    }
}
