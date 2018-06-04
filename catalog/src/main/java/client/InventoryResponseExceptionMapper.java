package client;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

public class InventoryResponseExceptionMapper implements ResponseExceptionMapper<BaseInventoryException>{

    Logger LOG = Logger.getLogger(BaseInventoryException.class.getName());

    @Override
    public boolean handles(int statusCode, MultivaluedMap<String, Object> headers) {
        LOG.info("status = " + statusCode);
        return statusCode == 404    // Not Found
            || statusCode == 500;   // Generic Server Error (Service Not Available)
    }

    @Override
    public BaseInventoryException toThrowable(Response response) {
        switch(response.getStatus()) {
            case 404: return new UnknownUrlException();
            case 500: return new ServiceNotReadyException();
        }
        return null;
    }
}
