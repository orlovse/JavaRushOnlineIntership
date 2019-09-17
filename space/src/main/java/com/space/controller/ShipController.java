package com.space.controller;

import com.space.exceptions.BadRequestException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ShipController {

    @Autowired
    private ShipService shipService = new ShipService();

    @GetMapping("/rest/ships")
    public List<Ship> getShipList(@RequestParam(required = false) String name,
                                  @RequestParam(required = false) String planet,
                                  @RequestParam(required = false) ShipType shipType,
                                  @RequestParam(required = false) Long after,
                                  @RequestParam(required = false) Long before,
                                  @RequestParam(required = false) Boolean isUsed,
                                  @RequestParam(required = false) Double minSpeed,
                                  @RequestParam(required = false) Double maxSpeed,
                                  @RequestParam(required = false) Integer minCrewSize,
                                  @RequestParam(required = false) Integer maxCrewSize,
                                  @RequestParam(required = false) Double minRating,
                                  @RequestParam(required = false) Double maxRating,
                                  @RequestParam(required = false) ShipOrder order,
                                  @RequestParam(required = false) Integer pageNumber,
                                  @RequestParam(required = false) Integer pageSize) {
        List<Ship> filteredShips = shipService.getShipsList(name, planet, shipType, after, before, isUsed, minSpeed,
                maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
        return shipService.prepareFilteredShips(filteredShips, order, pageNumber, pageSize);
    }

    @GetMapping("/rest/ships/count")
    public int getShipCount(@RequestParam(required = false) String name,
                            @RequestParam(required = false) String planet,
                            @RequestParam(required = false) ShipType shipType,
                            @RequestParam(required = false) Long after,
                            @RequestParam(required = false) Long before,
                            @RequestParam(required = false) Boolean isUsed,
                            @RequestParam(required = false) Double minSpeed,
                            @RequestParam(required = false) Double maxSpeed,
                            @RequestParam(required = false) Integer minCrewSize,
                            @RequestParam(required = false) Integer maxCrewSize,
                            @RequestParam(required = false) Double minRating,
                            @RequestParam(required = false) Double maxRating) {
        return shipService.getShipsList(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize,
                maxCrewSize, minRating, maxRating).size();
    }

    @PostMapping("/rest/ships")
    public @ResponseBody
    Ship createShip (@RequestBody Ship ship) {
        Ship createdShip = shipService.createShip(ship);
        if (createdShip == null) {
            throw new BadRequestException();
        }
        return createdShip;
    }

    @PostMapping("/rest/ships/{id}")
    public @ResponseBody Ship updateShip(@RequestBody Ship ship, @PathVariable Long id) {
        if (!isIdValid(id)) {
            throw new BadRequestException();
        }
        return shipService.updateShip(ship, id);
    }

    @GetMapping("/rest/ships/{id}")
    public Ship getShipById(@PathVariable Long id) {
        if (!isIdValid(id)) {
            throw new BadRequestException();
        }
        return shipService.getShipById(id);
    }

    @DeleteMapping("/rest/ships/{id}")
    public void deleteShip(@PathVariable Long id) {
        if (!isIdValid(id)) {
            throw new BadRequestException();
        }
        shipService.delete(id);
    }

    private Boolean isIdValid(Long id) {
        if (id == null ||
                id != Math.floor(id) ||
                id <= 0) {
            return false;
        }
        return true;
    }
}

