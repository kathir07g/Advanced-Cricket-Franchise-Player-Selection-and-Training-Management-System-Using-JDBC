package main;

import controller.FranchiseController;
import controller.LoginController;
import controller.PlayerController;

import model.Franchise;

import repository.FranchiseRepository;
import repository.FranchiseRepositoryImpl;
import repository.PlayerRepository;
import repository.PlayerRepositoryImpl;
import repository.SelectionRepository;
import repository.SelectionRepositoryImpl;

import service.FranchiseService;
import service.PlayerService;
import service.SelectionService;

public class Main {

    public static void main(String[] args) {

        //        Repository
        PlayerRepository PlayerRepository = new PlayerRepositoryImpl();
        FranchiseRepository franchiseRepository = new FranchiseRepositoryImpl();
        SelectionRepository selectionRepository = new SelectionRepositoryImpl();

        //        Services
        PlayerService playerService = new PlayerService(PlayerRepository);
        FranchiseService franchiseService = new FranchiseService(franchiseRepository);
        SelectionService selectionService = new SelectionService(PlayerRepository,selectionRepository);

        //        Controller
        PlayerController playerController = new PlayerController(playerService, franchiseService, selectionService);

        FranchiseController franchiseController = new FranchiseController(franchiseService, playerService, selectionService);

        LoginController loginController = new LoginController(playerService, franchiseService, selectionService, playerController, franchiseController);

        loginController.start();
    }
}