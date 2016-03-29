package api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.junit.After;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import business.api.Uris;
import business.wrapper.CourtState;

public class CourtResourceFunctionalTesting {

    RestService restService = new RestService();
    
    @Test
    public void testCreateCourt() {
        restService.createCourt("1");
    }

    @Test
    public void testCreateCourtUnauthorized() {
        try {
            new RestBuilder<Object>(RestService.URL).path(Uris.COURTS).param("id", "1").post().build();
            fail();
        } catch (HttpClientErrorException httpError) {
            assertEquals(HttpStatus.UNAUTHORIZED, httpError.getStatusCode());
            LogManager.getLogger(this.getClass()).info(
                    "testCreateCourt (" + httpError.getMessage() + "):\n    " + httpError.getResponseBodyAsString());
        }
    }

    @Test
    public void testChangeCourtActivationTrue() {
        restService.createCourt("1");
        String token = restService.loginAdmin();
        new RestBuilder<Object>(RestService.URL).path(Uris.COURTS).pathId(1).path(Uris.ACTIVE).basicAuth(token, "").post().build();
    }

    @Test
    public void testChangeCourtActivationFalse() {
        restService.createCourt("1");
        String token = restService.loginAdmin();
        new RestBuilder<Object>(RestService.URL).path(Uris.COURTS).pathId(1).path(Uris.ACTIVE).basicAuth(token, "").delete().build();
    }

    @Test
    public void testChangeCourtActivationTrueUnauthorization() {
        restService.createCourt("1");
        String token = restService.registerAndLoginPlayer();
        try {
            new RestBuilder<Object>(RestService.URL).path(Uris.COURTS).pathId(1).path(Uris.ACTIVE).basicAuth(token, "").post().build();
        } catch (HttpClientErrorException httpError) {
            LogManager.getLogger(this.getClass()).info(
                    "testChangeCourtActivationTrueUnauthorization (" + httpError.getMessage() + "):\n    "
                            + httpError.getResponseBodyAsString());
        }
    }

    @Test
    public void testShowCourts() {
        final int COURTS = 5;
        for (int i = 1; i <= COURTS; i++) {
            restService.createCourt("" + i);
        }
        String token = restService.loginAdmin();
        List<CourtState> list = Arrays.asList(new RestBuilder<CourtState[]>(RestService.URL).path(Uris.COURTS).basicAuth(token, "")
                .clazz(CourtState[].class).get().build());
        assertEquals(COURTS, list.size());
    }

    @After
    public void deleteAll() {
        new RestService().deleteAll();
    }

}
