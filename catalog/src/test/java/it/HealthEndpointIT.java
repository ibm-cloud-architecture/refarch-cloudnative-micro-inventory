package it;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import javax.json.JsonArray;

import org.junit.Test;

public class HealthEndpointIT {

    HealthUtil healthUitl = mock(HealthUtil.class);
    
    private JsonArray servicesStates;
    private static HashMap<String, String> servicesAreUp;
    private static HashMap<String, String> servicesAreDown;

    static {
    	servicesAreUp = new HashMap<String, String>();
    	servicesAreDown = new HashMap<String, String>();

        servicesAreUp.put("CatalogService", "UP");

        servicesAreDown.put("CatalogService", "DOWN");
    }

    @Test
    public void testIfServicesAreUp() {
    	when(healthUitl.makeRequest()).thenReturn(200);
    	int responseCode = healthUitl.makeRequest();
        servicesStates = healthUitl.checkEndPointConnection(responseCode);
        healthUitl.checkTheStates(servicesAreUp, servicesStates);
    }
    
    @Test
    public void testIfCatalogServiceIsDown() {
    	when(healthUitl.makeRequest()).thenReturn(503);
    	int responseCode = healthUitl.makeRequest();
        servicesStates = healthUitl.checkEndPointConnection(responseCode);
        healthUitl.checkTheStates(servicesAreDown, servicesStates);
    }
}
