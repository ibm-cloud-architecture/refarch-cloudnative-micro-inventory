package client;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class InventoryResponseExceptionMapper implements ResponseExceptionMapper<BaseInventoryException>{

    @Override
    public boolean handles(int statusCode, MultivaluedMap<String, Object> headers) {
        return statusCode == 404    // Not Found
            || statusCode == 503;   // Service Not Available
    }

    @Override
    public BaseInventoryException toThrowable(Response response) {
        switch(response.getStatus()) {
            case 404: return new UnknownUrlException();
            case 503: return new ServiceNotReadyException();
        }
        return null;
    }
}
