package view;

import java.util.Scanner;

public class LoginView {
    public void showHomeMenu() {
        System.out.println();
        System.out.println("==========CRICKET FRANCHISE MANAGEMENT==========");
        System.out.println("1. Admin Login");
        System.out.println("2. Player Login");
        System.out.println("3. Franchise Login");
        System.out.println("4. Player Registration");
        System.out.println("5. Franchise Registration");
        System.out.println("6. Exit");
    }

    public void showPlayerMenu() {
        System.out.println();
        System.out.println("==========PLAYER MENU==========");
        System.out.println("1. View Profile");
        System.out.println("2. Update Profile");
        System.out.println("3. View Registered Franchise");
        System.out.println("4. View Selection Status");
        System.out.println("5. View Training Details");
        System.out.println("6. Logout");
    }

    public void showFranchiseMenu() {
        System.out.println();
        System.out.println("==========FRANCHISE MENU==========");
        System.out.println("1. View Franchise Profile");
        System.out.println("2. View Registered Players");
        System.out.println("3. Run Player Selection");
        System.out.println("4. View Selected Players");
        System.out.println("5. Training Management");
        System.out.println("6. Update Franchise Details");
        System.out.println("7. Logout");
    }


    public void loginFailed() {
        System.out.println("Invalid username or password.");
    }
}
