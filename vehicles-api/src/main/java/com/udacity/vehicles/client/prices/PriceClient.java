package com.udacity.vehicles.client.prices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udacity.vehicles.domain.car.Car;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements a class to interface with the Pricing Client for price data.
 */
@Component
public class PriceClient {
    private String CURRENCY = "USD";

    private static final Logger log = LoggerFactory.getLogger(PriceClient.class);

    private final WebClient client;

    public PriceClient(WebClient pricing) {
        this.client = pricing;
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // In a real-world application we'll want to add some resilience
    // to this method with retries/CB/failover capabilities
    // We may also want to cache the results so we don't need to
    // do a request every time
    /**
     * Gets a vehicle price from the pricing client, given vehicle ID.
     * @param vehicleId ID number of the vehicle for which to get the price
     * @return Currency and price of the requested vehicle,
     *   error message that the vehicle ID is invalid, or note that the
     *   service is down.
     */
    public String getPrice(Long vehicleId) {
        try {
            Price price = client
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("prices/" + vehicleId)
//                            .queryParam("vehicleId", vehicleId)
                            .build()
                    )
                    .retrieve().bodyToMono(Price.class).block();

            return String.format("%s %s", price.getCurrency(), price.getPrice());

        } catch (Exception e) {
            log.error("Unexpected error retrieving price for vehicle {}", vehicleId, e);
        }
        return "(consult price)";
    }

    public Car getPrice(Car car) {
        try {
            Price price = client
                    .get()
                    .uri(uriBuilder -> uriBuilder
                                    .path("prices/" + car.getId())
                                    .build()
                    )
                    .retrieve().bodyToMono(Price.class).block();

            String newPrice = String.format("%s %s", price.getCurrency(), price.getPrice());
            car.setPrice(newPrice);
            return car;

        } catch (Exception e) {
            log.error("Unexpected error retrieving price for vehicle {}", car.getId(), e);
        }
        return car;
    }

    private Map<String, Object> getUriVariables(String price, Long vehicleId){
        log.info(String.valueOf(vehicleId));
        if(!price.matches("\\d+\\.*\\d{0,2}")){ price = "13000.00";}
        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("currency", CURRENCY);
        uriVariables.put("vehicleId", vehicleId);
        uriVariables.put("price", new BigDecimal(price));
        return uriVariables;
    }

    public String savePrice(String price, Long vehicleId){
        Map<String, Object> uriVariables = getUriVariables(price, vehicleId);
        try {
            Price price2 = client.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("prices")
                            .build()
                    )
                    .body(BodyInserters.fromObject(uriVariables))
                    .retrieve().bodyToMono(Price.class).block();
            return String.format("%s %s", price2.getCurrency(), price2.getPrice());
        } catch (Exception ex){
            log.error(ex.getMessage());
        }
        return "(consult price)";
    }

    public String updatePrice(String price, Long vehicleId){
        Map<String, Object> uriVariables = getUriVariables(price, vehicleId);
        try {
            Price price1 = client.patch()
                    .uri(uriBuilder -> uriBuilder
                            .path("prices/" + vehicleId)
                            .build()
                    )
                    .body(BodyInserters.fromObject(uriVariables))
                    .retrieve().bodyToMono(Price.class).block();
            return String.format("%s %s", price1.getCurrency(), price1.getPrice());
        } catch (Exception ex){
            log.error(ex.getMessage());
        }
        return "(consult price)";
    }

    public void deletePrice(Long vehicleId){
        try {
            client.delete()
                    .uri(uriBuilder -> uriBuilder
                            .path("prices/" + vehicleId)
                            .build()
                    )
                    .retrieve();
        } catch (Exception ex){
            log.error(ex.getMessage());
        }
    }
}
