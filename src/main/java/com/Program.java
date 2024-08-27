package com;

import java.util.Scanner;

import com.ui.UserInterface;

public class Program {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserInterface ui = new UserInterface(scanner);

        ui.callMainMenu();
    }
}
