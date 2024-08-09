package Lab.Service;

import Lab.Exceptions.InvalidTonnageException;
import Lab.Model.Ship;
import Lab.Repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Using the @Transactional annotation, the methods of this Service class will roll back the current
 * database transaction when the InvalidTonnageException is thrown. This will wrap all
 * methods in this class inside a database transaction, preventing incomplete updates if an exception is thrown.
 * The @Transactional annotation is set to rollback for InvalidTonnageException.
 */
@Service
@Transactional(rollbackFor = InvalidTonnageException.class)
public class ShipService {
    private final ShipRepository shipRepository;

    @Autowired
    public ShipService(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    /**
     * This is a less efficient way to save a list to the repository as you can use the .saveAll method if the table
     * has a CHECK constraint to check tonnage. This example demonstrates the importance of @Transactional.
     * 
     * @param ships transient ship entities
     * @throws InvalidTonnageException ships cannot have negative tonnage (they'd sink)
     */
    public List<Ship> addListShips(List<Ship> ships) throws InvalidTonnageException {
        List<Ship> persistedShips = new ArrayList<>();
        for (Ship ship : ships) {
            if (ship.getTonnage() <= 0) {
                throw new InvalidTonnageException();
            }
            persistedShips.add(shipRepository.save(ship));
        }
        return persistedShips;
    }

    /**
     * @return all ship entities
     */
    public List<Ship> getAllShips() {
        return shipRepository.findAll();
    }

    /**
     * @return ship entity by id
     */
    public Ship getShipById(long id) {
        return shipRepository.findById(id).orElse(null);
    }
}
