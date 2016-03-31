package api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.logging.log4j.LogManager;
import org.junit.After;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import business.api.Uris;
import business.api.exceptions.InvalidCourtReserveTrainingExistsException;
import business.wrapper.AvailableTime;
import business.wrapper.SimpleUserWrapper;
import business.wrapper.TrainingWrapper;

public class ReserveResourceFunctionalTesting {

    RestService restService = new RestService();
    
    final long DAY_MILLISECONDS = 86400000;

    final int WEEK_DAYS = 7;
    
    @Test
    public void testshowAvailability() {
        restService.createCourt("1");
        restService.createCourt("2");
        String token = restService.registerAndLoginPlayer();
        Calendar day = Calendar.getInstance();
        day.add(Calendar.DAY_OF_YEAR, 1);
        day.set(Calendar.HOUR_OF_DAY, 12);
        new RestBuilder<String>(RestService.URL).path(Uris.RESERVES).basicAuth(token, "").body(new AvailableTime(1, day)).post().build();
        day.set(Calendar.HOUR_OF_DAY, 14);
        new RestBuilder<String>(RestService.URL).path(Uris.RESERVES).basicAuth(token, "").body(new AvailableTime(2, day)).post().build();
        String day2 = "" + day.getTimeInMillis();
        String response = new RestBuilder<String>(RestService.URL).path(Uris.RESERVES).path(Uris.AVAILABILITY).basicAuth(token, "")
                .param("day", day2).clazz(String.class).get().build();
        LogManager.getLogger(this.getClass()).info("testshowAvailability (" + response + ")");
    }

    @Test
    public void testReserveCourt() {
        restService.createCourt("1");
        restService.createCourt("2");
        String token = restService.registerAndLoginPlayer();
        Calendar day = Calendar.getInstance();
        day.add(Calendar.DAY_OF_YEAR, 1);
        day.set(Calendar.HOUR_OF_DAY,12);
        new RestBuilder<String>(RestService.URL).path(Uris.RESERVES).basicAuth(token, "").body(new AvailableTime(1, day)).post().build();
        day.set(Calendar.HOUR_OF_DAY,14);
        new RestBuilder<String>(RestService.URL).path(Uris.RESERVES).basicAuth(token, "").body(new AvailableTime(2, day)).post().build();
    }

    @Test
    public void testCreateReserveMatchingTraining() {
        restService.createCourt("1");
        restService.registerTrainer(1);
        
        Calendar startDatetime = GregorianCalendar.getInstance();
        Calendar endDatetime = GregorianCalendar.getInstance();
        Calendar datetimeToQuery = GregorianCalendar.getInstance();

        datetimeToQuery.setTimeInMillis(datetimeToQuery.getTimeInMillis() + DAY_MILLISECONDS);
        datetimeToQuery.set(Calendar.HOUR_OF_DAY, 15);
        datetimeToQuery.set(Calendar.MINUTE, 0);

        startDatetime.setTimeInMillis(datetimeToQuery.getTimeInMillis() - (WEEK_DAYS * DAY_MILLISECONDS));
        ;
        startDatetime.set(Calendar.HOUR_OF_DAY, 15);
        startDatetime.set(Calendar.MINUTE, 0);

        endDatetime.setTimeInMillis(datetimeToQuery.getTimeInMillis() + (WEEK_DAYS * DAY_MILLISECONDS));
        ;
        endDatetime.set(Calendar.HOUR_OF_DAY, 15);
        endDatetime.set(Calendar.MINUTE, 0);

        String token = restService.loginAdmin();
        TrainingWrapper trainingWrapper = new TrainingWrapper();
        trainingWrapper.setStartDatetime(startDatetime);
        trainingWrapper.setEndDatetime(endDatetime);
        trainingWrapper.setTrainer(new SimpleUserWrapper("u1@gmail.com"));
        trainingWrapper.setCourtId(1);

        new RestBuilder<TrainingWrapper>(RestService.URL).path(Uris.TRAININGS).body(trainingWrapper).clazz(TrainingWrapper.class).post()
                .basicAuth(token, "").build();

        token = restService.registerAndLoginPlayer();
        
        try {
        new RestBuilder<String>(RestService.URL).path(Uris.RESERVES).basicAuth(token, "").body(new AvailableTime(1, datetimeToQuery)).post()
                .build();
        } catch(HttpClientErrorException httpError) {
            assertEquals(HttpStatus.CONFLICT, httpError.getStatusCode());
            assertTrue(httpError.getResponseBodyAsString().contains(InvalidCourtReserveTrainingExistsException.class.getSimpleName()));
            LogManager.getLogger(this.getClass())
                    .info("testCreateReserveMatchingTraining (" + httpError.getMessage() + "):\n    " + httpError.getResponseBodyAsString());
        }

    }
    
    @After
    public void deleteAll() {
        new RestService().deleteAll();
    }

}
