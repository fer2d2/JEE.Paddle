package api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.logging.log4j.LogManager;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;

import business.api.Uris;
import business.services.BasicDataService;
import business.wrapper.TrainingWrapper;
import config.PersistenceConfig;
import config.TestsPersistenceConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceConfig.class, TestsPersistenceConfig.class})
public class TokenResourceFunctionalTesting {
    
    @Autowired
    private BasicDataService basicDataService;
    
    @Test
    public void testLoginPlayer() {
        String token = new RestService().registerAndLoginPlayer();
        assertTrue(token.length() > 20);
        LogManager.getLogger(this.getClass()).info("testLoginPlayer (token:" + token + ")");
    }
    
    @Test
    public void testLoginAdminExpiredToken() {
        new RestService().loginAdmin();
        
        try {
            new RestBuilder<TrainingWrapper[]>(RestService.URL).path(Uris.TRAININGS)
                    .basicAuth(basicDataService.generateExpiredToken("admin"), "").get().build();
            fail();
        } catch(HttpClientErrorException httpError) {
            assertEquals(HttpStatus.UNAUTHORIZED, httpError.getStatusCode());
        }
    }
    
    @After
    public void deleteAll() {
        new RestService().deleteAll();
    }

}
