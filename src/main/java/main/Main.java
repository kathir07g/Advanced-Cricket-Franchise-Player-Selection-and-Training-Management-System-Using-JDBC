package main;

import controller.FranchiseController;
import controller.LoginController;
import controller.PlayerController;
import repository.FranchiseRepository;
import repository.FranchiseRepositoryImpl;
import repository.PlayerRepository;
import repository.PlayerRepositoryImpl;
import repository.SelectionRepository;
import repository.SelectionRepositoryImpl;
import service.FranchiseService;
import service.PlayerService;
import service.SelectionService;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        //        Repository
        PlayerRepository playerRepository = new PlayerRepositoryImpl();
        FranchiseRepository franchiseRepository = new FranchiseRepositoryImpl();
        SelectionRepository selectionRepository = new SelectionRepositoryImpl();

        //        Services
        FranchiseService franchiseService = new FranchiseService(franchiseRepository);
        PlayerService playerService = new PlayerService(playerRepository,franchiseService);
        SelectionService selectionService = new SelectionService(playerRepository, selectionRepository);

        //        Controller
        PlayerController playerController = new PlayerController(playerService, franchiseService, selectionService, scanner);
        FranchiseController franchiseController = new FranchiseController(franchiseService, playerService, selectionService, scanner);
        LoginController loginController = new LoginController(playerService, franchiseService, selectionService, playerController, franchiseController, scanner);

        loginController.start();
    }
}
