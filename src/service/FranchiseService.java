package service;

import model.Franchise;
import repository.FranchiseRepository;

import java.util.List;

public class FranchiseService {

    private FranchiseRepository franchiseRepository;

    public FranchiseService(FranchiseRepository franchiseRepository) {
        this.franchiseRepository = franchiseRepository;
    }

    public Franchise registerFranchise(String username, String password, String name,
                                       String location, String trainingDate, int availableSpots, String roleCount) {

        if (franchiseRepository.findByUsername(username) != null) {
            return null;
        }
        Franchise franchise = new Franchise(0, username, password, name,
                location, trainingDate, availableSpots, roleCount);

        franchiseRepository.save(franchise);
        return franchise;
    }

    public Franchise login(String username, String password) {
        Franchise franchise = franchiseRepository.findByUsername(username);

        if (franchise == null) {
            return null;
        }
        if (franchise.getPassword().equals(password)) {
            return franchise;
        }
        return null;
    }

    public Franchise getFranchise(int franchiseId) {
        return franchiseRepository.findById(franchiseId);
    }

    public List<Franchise> getAllFranchises() {
        return franchiseRepository.findAll();
    }

    public void updateFranchise(Franchise franchise) {
        franchiseRepository.update(franchise);
    }

    public Franchise getFranchiseByName(String name) {
        return franchiseRepository.findByName(name);
    }

    public void clearFranchises() {
        franchiseRepository.deleteAll();
    }
}