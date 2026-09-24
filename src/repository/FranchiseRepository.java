package repository;

import model.Franchise;

import java.util.List;

public interface FranchiseRepository {

    void save(Franchise franchise);

    Franchise findById(int franchiseId);

    Franchise findByUsername(String username);

    List<Franchise> findAll();

    void update(Franchise franchise);

    Franchise findByName(String name);

    void deleteAll();
}