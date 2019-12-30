package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.Price;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {
    private Logger log = LoggerFactory.getLogger(CarService.class);

    private final CarRepository repository;
    private final MapsClient maps;
    private final PriceClient pricing;

    @Autowired
    public CarService(CarRepository repository, MapsClient maps, PriceClient pricing) {
        /**
         * TODO: Add the Maps and Pricing Web Clients you create
         *   in `VehiclesApiApplication` as arguments and set them here.
         */
        this.repository = repository;
        this.maps = maps;
        this.pricing = pricing;
    }

    /**
     * Gathers a list of all vehicles
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {

        return repository.findAll()
                .stream()
                .map(pricing::getPrice)
                .collect(Collectors.toList());
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) throws CarNotFoundException {
        /**
         * TODO: Find the car by ID from the `repository` if it exists.
         *   If it does not exist, throw a CarNotFoundException
         *   Remove the below code as part of your implementation.
         */

        Optional<Car> carOptional = repository.findById(id);
        if(carOptional.isEmpty()){
            throw new CarNotFoundException();
        }
        Car car = carOptional.get();


        /**
         * TODO: Use the Pricing Web client you create in `VehiclesApiApplication`
         *   to get the price based on the `id` input'
         * TODO: Set the price of the car
         * Note: The car class file uses @transient, meaning you will need to call
         *   the pricing service each time to get the price.
         */
        String price = pricing.getPrice(id);
        car.setPrice(price);


        /**
         * TODO: Use the Maps Web client you create in `VehiclesApiApplication`
         *   to get the address for the vehicle. You should access the location
         *   from the car object and feed it to the Maps service.
         * TODO: Set the location of the vehicle, including the address information
         * Note: The Location class file also uses @transient for the address,
         * meaning the Maps service needs to be called each time for the address.
         */
        Location location = maps.getAddress(car.getLocation());
        car.setLocation(location);


        return car;
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        if (car.getId() != null) {
            String price = pricing.updatePrice(car.getPrice(), car.getId());

            return repository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setLocation(car.getLocation());
                        return repository.save(carToBeUpdated);
                    })
                    .map(savedCar -> {
                        savedCar.setPrice(price);
                        return savedCar;
                    })
                    .orElseThrow(CarNotFoundException::new);
        }
        log.info(car.toString());
        Car savedCar = repository.save(car);
        log.info("calling pricing service to save a price");
        savedCar.setPrice(pricing.savePrice(car.getPrice(), savedCar.getId()));
        return savedCar;
    }

    /**
     * Deletes a given car by ID
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {
        /**
         * TODO: Find the car by ID from the `repository` if it exists.
         *   If it does not exist, throw a CarNotFoundException
         */
        if(!repository.existsById(id)){
            throw new CarNotFoundException();
        }
        /**
         * TODO: Delete the car from the repository.
         */
        pricing.deletePrice(id);
        repository.deleteById(id);
    }
}
