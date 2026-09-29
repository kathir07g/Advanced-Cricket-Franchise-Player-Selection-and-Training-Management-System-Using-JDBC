package repository;

import model.Franchise;

import java.util.List;

public interface FranchiseRepository {

    boolean save(Franchise franchise);

    boolean saveDefaultFranchise(Franchise franchise);

    boolean updatePassword(int franchiseId,String password);

    Franchise findById(int franchiseId);

    Franchise findByUsername(String username);

    List<Franchise> findAll();

    List<Franchise> findActiveFranchises();

    boolean update(Franchise franchise);

    Franchise findByName(String name);

    boolean deleteAll();
}
