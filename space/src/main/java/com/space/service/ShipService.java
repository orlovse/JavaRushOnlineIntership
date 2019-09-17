package com.space.service;

import com.space.controller.ShipOrder;
import com.space.exceptions.BadRequestException;
import com.space.exceptions.NotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShipService {

    @Autowired
    private ShipRepository shipRepository;

    @Transactional
    public Ship createShip(final Ship ship) {
        if (ship == null ||
                ship.getName() == null ||
                ship.getName().isEmpty() ||
                ship.getName().length() > 50 ||
                ship.getPlanet() == null ||
                ship.getPlanet().isEmpty() ||
                ship.getPlanet().length() > 50 ||
                ship.getShipType() == null ||
                ship.getProdDate() == null ||
                ship.getProdDate().getTime() < 0 ||
                ship.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() < 2800 ||
                ship.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() > 3019 ||
                ship.getSpeed() == null ||
                ship.getSpeed() < 0.01d ||
                ship.getSpeed() > 0.99d ||
                ship.getCrewSize() == null ||
                ship.getCrewSize() < 1 ||
                ship.getCrewSize() > 9999) {
            throw new BadRequestException();
        }
        if (ship.isUsed() == null) {
            ship.setUsed(false);
        }
        ship.setSpeed((double) Math.round(ship.getSpeed() * 100) / 100);
        ship.setRating(computeRating(ship));
        return shipRepository.saveAndFlush(ship);
    }

    public Ship getShipById(Long id) throws NotFoundException {
        if (!shipRepository.existsById(id)) {
            throw new NotFoundException();
        }
        return shipRepository.findById(id).orElse(null);
    }

    @Transactional
    public Ship updateShip(Ship newShip, Long id) {
        Ship shipToUpdate = getShipById(id);
        if (newShip == null || shipToUpdate == null) {
            throw new BadRequestException();
        }
        if (newShip.getName() != null) {
            if (newShip.getName().isEmpty() || newShip.getName().length() > 50) {
                throw new BadRequestException();
            }
            shipToUpdate.setName(newShip.getName());
        }
        if (newShip.getPlanet() != null) {
            if (newShip.getPlanet().isEmpty() || newShip.getPlanet().length() > 50) {
                throw new BadRequestException();
            }
            shipToUpdate.setPlanet(newShip.getPlanet());
        }
        if (newShip.getShipType() != null) {
            shipToUpdate.setShipType(newShip.getShipType());
        }
        if (newShip.getProdDate() != null) {
            if (newShip.getProdDate().getTime() < 0 ||
                    newShip.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() < 2800 ||
                    newShip.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() > 3019) {
                throw new BadRequestException();
            }
            shipToUpdate.setProdDate(newShip.getProdDate());
        }
        if (newShip.isUsed() != null) {
            shipToUpdate.setUsed(newShip.isUsed());
        }
        if (newShip.getSpeed() != null) {
            if (newShip.getSpeed() < 0.01d || newShip.getSpeed() > 0.99d) {
                throw new BadRequestException();
            }
            shipToUpdate.setSpeed((double) Math.round(newShip.getSpeed() * 100) / 100);
        }
        if (newShip.getCrewSize() != null) {
            if (newShip.getCrewSize() < 1 ||
                    newShip.getCrewSize() > 9999) {
                throw new BadRequestException();
            }
            shipToUpdate.setCrewSize(newShip.getCrewSize());
        }
        shipToUpdate.setRating(computeRating(shipToUpdate));
        return shipRepository.saveAndFlush(shipToUpdate);
    }

    @Transactional
    public void delete(Long id) {
        if (!shipRepository.existsById(id)) {
            throw new NotFoundException();
        }
        shipRepository.deleteById(id);
    }

    public List<Ship> getShipsList(String name, String planet, ShipType shipType, Long after, Long before,
                                   Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                                   Integer maxCrewSize, Double minRating, Double maxRating) {
        List<Ship> filteredShips = shipRepository.findAll();
        if (name != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getName().contains(name))
                    .collect(Collectors.toList());
        }
        if (planet != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getPlanet().contains(planet))
                    .collect(Collectors.toList());
        }
        if (shipType != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getShipType().equals(shipType))
                    .collect(Collectors.toList());
        }
        if (after != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getProdDate().after(new Date(after)))
                    .collect(Collectors.toList());
        }
        if (before != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getProdDate().before(new Date(before)))
                    .collect(Collectors.toList());
        }
        if (isUsed != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.isUsed().equals(isUsed))
                    .collect(Collectors.toList());
        }
        if (minSpeed != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getSpeed() >= minSpeed)
                    .collect(Collectors.toList());
        }
        if (maxSpeed != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getSpeed() <= maxSpeed)
                    .collect(Collectors.toList());
        }
        if (minCrewSize != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getCrewSize() >= minCrewSize)
                    .collect(Collectors.toList());
        }
        if (maxCrewSize != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getCrewSize() <= maxCrewSize)
                    .collect(Collectors.toList());
        }
        if (minRating != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getRating() >= minRating)
                    .collect(Collectors.toList());
        }
        if (maxRating != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getRating() <= maxRating)
                    .collect(Collectors.toList());
        }
        return filteredShips;
    }

    public List<Ship> prepareFilteredShips(final List<Ship> filteredShips, ShipOrder order, Integer pageNumber, Integer pageSize) {
        pageNumber = pageNumber == null ?  0 : pageNumber;
        pageSize = pageSize == null ? 3 : pageSize;
        return filteredShips.stream()
                .sorted(getComparator(order))
                .skip(pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    private Comparator<Ship> getComparator(ShipOrder order) {
        if (order == null) {
            return Comparator.comparing(Ship::getId);
        }
        Comparator<Ship> comparator = null;
        if (order.getFieldName().equals("id")) {
            comparator = Comparator.comparing(Ship::getId);
        } else if (order.getFieldName().equals("speed")) {
            comparator = Comparator.comparing(Ship::getSpeed);
        } else if (order.getFieldName().equals("prodDate")) {
            comparator = Comparator.comparing(Ship::getProdDate);
        } else if (order.getFieldName().equals("rating")) {
            comparator = Comparator.comparing(Ship::getRating);
        }
        return comparator;
    }

    private double computeRating(Ship ship) {
        double speed = ship.getSpeed();
        double coefficientUsed = ship.isUsed() ? 0.5d : 1d;
        int currentYear = 3019;
        int productionYear = ship.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
        double rating = (80 * speed * coefficientUsed) / (double) (currentYear - productionYear + 1);
        return (double) Math.round(rating * 100) / 100;
    }
}

