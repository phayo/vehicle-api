package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CarServiceTest {

    @Mock
    CarRepository repository;

    @Mock
    MapsClient maps;

    @Mock
    PriceClient pricing;

    @InjectMocks
    CarService carService;

    @Test
    public void list() {
        Car car = getCar();
        List<Car> cars = new ArrayList<>();
        cars.add(car);

        when(repository.findAll()).thenReturn(cars);
        when(pricing.getPrice((Car) any())).thenReturn(car);
        List<Car> carList = carService.list();

        assertEquals(carList.get(0), car);
    }

    @Test
    public void findById() {
        Car car = getCar();

        when(repository.findById(anyLong())).thenReturn(Optional.of(car));
        when(maps.getAddress(any())).thenReturn(car.getLocation());
        when(pricing.getPrice((Long) any())).thenReturn("USD 10,000.00");

        Car car1 = carService.findById(1L);

        assertEquals(car, car1);
        assertEquals(car.getLocation(), car1.getLocation());
        assertEquals(car1.getPrice(), "USD 10,000.00");
    }

    @Test
    public void save() {
        Car car = getCar();
        car.setPrice("USD 10,000");

        when(repository.findById(anyLong())).thenReturn(Optional.of(car));
        when(repository.save(any())).thenReturn(car);
        when(pricing.updatePrice(anyString(), anyLong())).thenReturn("USD 10,000");

        Car savedCar = carService.save(car);
        assertEquals(savedCar.getPrice(), "USD 10,000");

    }

    /**
     *  Testing for when the id does not exist
     */

    @Test
    public void delete() {
//        when(repository.existsById(anyLong())).thenReturn(true);
//
//        doNothing().when(pricing).deletePrice(anyLong());
//        doNothing().when(repository).deleteById(anyLong());
//
//        assertT
    }

    /**
     * Creates an example Car object for use in testing.
     * @return an example Car object
     */
    private Car getCar() {
        Car car = new Car();
        car.setLocation(new Location(40.730610, -73.935242));
        Details details = new Details();
        Manufacturer manufacturer = new Manufacturer(101, "Chevrolet");
        details.setManufacturer(manufacturer);
        details.setModel("Impala");
        details.setMileage(32280);
        details.setExternalColor("white");
        details.setBody("sedan");
        details.setEngine("3.6L V6");
        details.setFuelType("Gasoline");
        details.setModelYear(2018);
        details.setProductionYear(2018);
        details.setNumberOfDoors(4);
        car.setDetails(details);
        car.setCondition(Condition.USED);
        car.setId(1L);
        return car;
    }
}
