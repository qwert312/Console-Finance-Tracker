package com.FinanceTracker;

import java.util.Scanner;

import com.FinanceTracker.ui.UserInterface;

public class Program {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserInterface ui = new UserInterface(scanner);

        ui.callMainMenu();
    }
}
