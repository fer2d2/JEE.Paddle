package api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;

import business.api.Uris;
import business.api.exceptions.InvalidUserGrantException;
import business.api.exceptions.MaxUsersByTrainingReachedException;
import business.api.exceptions.NotFoundCourtIdException;
import business.api.exceptions.NotFoundTrainingIdException;
import business.api.exceptions.NotFoundUserIdException;
import business.services.BasicDataService;
import business.wrapper.AvailableTime;
import business.wrapper.SimpleUserWrapper;
import business.wrapper.TrainingWrapper;
import config.PersistenceConfig;
import config.TestsPersistenceConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceConfig.class, TestsPersistenceConfig.class})
public class TrainingResourceFunctionalTesting {

    RestService restService = new RestService();

    @Autowired
    private BasicDataService basicDataService;

    public final int NON_EXISTENT_TRAINING = 2000;

    public final int NON_EXISTENT_USER = 2000;

    public final String NON_EXISTENT_USER_EMAIL = "usuarioInventado@gmail.com";

    public final int MAX_TRAINEES = 4;

    final long DAY_MILLISECONDS = 86400000;

    final int WEEK_DAYS = 7;

    @Before
    public void initialize() {
        restService.deleteAll();
        generateCourts();
        generateUsers();
    }

    // createTraining

    @Test
    public void testCreateTrainingUnauthorized() {
        TrainingWrapper trainingWrapper = new TrainingWrapper();
        try {
            new RestBuilder<Object>(RestService.URL).path(Uris.TRAININGS).body(trainingWrapper).post().build();
            fail();
        } catch (HttpClientErrorException httpError) {
            assertEquals(HttpStatus.UNAUTHORIZED, httpError.getStatusCode());
            LogManager.getLogger(this.getClass())
                    .info("testCreateCourt (" + httpError.getMessage() + "):\n    " + httpError.getResponseBodyAsString());
        }
    }

    @Test
    public void testCreateTraining() {
        String token = restService.loginAdmin();

        TrainingWrapper trainingWrapper = new TrainingWrapper();
        trainingWrapper.setStartDatetime(new GregorianCalendar(2016, 1, 1, 17, 0, 0));
        trainingWrapper.setEndDatetime(new GregorianCalendar(2016, 2, 1, 17, 0, 0));
        trainingWrapper.setTrainer(new SimpleUserWrapper("u1@gmail.com"));
        trainingWrapper.setCourtId(1);

        new RestBuilder<Object>(RestService.URL).path(Uris.TRAININGS).body(trainingWrapper).post().basicAuth(token, "").build();
    }

    @Test
    public void testCreateTrainingDeletingReserves() {
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

        assertEquals(datetimeToQuery.get(Calendar.DAY_OF_WEEK), startDatetime.get(Calendar.DAY_OF_WEEK));
        assertEquals(datetimeToQuery.get(Calendar.HOUR_OF_DAY), startDatetime.get(Calendar.HOUR_OF_DAY));

        assertEquals(0, basicDataService.countReserves());

        String token = restService.registerAndLoginPlayer();
        new RestBuilder<String>(RestService.URL).path(Uris.RESERVES).basicAuth(token, "").body(new AvailableTime(1, datetimeToQuery)).post()
                .build();

        assertEquals(1, basicDataService.countReserves());

        token = restService.loginAdmin();
        TrainingWrapper trainingWrapper = new TrainingWrapper();
        trainingWrapper.setStartDatetime(startDatetime);
        trainingWrapper.setEndDatetime(endDatetime);
        trainingWrapper.setTrainer(new SimpleUserWrapper("u1@gmail.com"));
        trainingWrapper.setCourtId(1);

        new RestBuilder<TrainingWrapper>(RestService.URL).path(Uris.TRAININGS).body(trainingWrapper).clazz(TrainingWrapper.class).post()
                .basicAuth(token, "").build();

        assertEquals(0, basicDataService.countReserves());

    }

    @Test
    public void testCreateTrainingInvalidUser() {
        String token = restService.loginAdmin();

        TrainingWrapper trainingWrapper = new TrainingWrapper();
        trainingWrapper.setStartDatetime(new GregorianCalendar(2016, 1, 1, 17, 0, 0));
        trainingWrapper.setEndDatetime(new GregorianCalendar(2016, 2, 1, 17, 0, 0));
        trainingWrapper.setTrainer(new SimpleUserWrapper(NON_EXISTENT_USER_EMAIL));
        trainingWrapper.setCourtId(1);

        try {
            new RestBuilder<Object>(RestService.URL).path(Uris.TRAININGS).body(trainingWrapper).post().basicAuth(token, "").build();
            fail();
        } catch (HttpClientErrorException httpError) {
            assertEquals(HttpStatus.NOT_FOUND, httpError.getStatusCode());
            assertTrue(httpError.getResponseBodyAsString().contains(NotFoundUserIdException.class.getSimpleName()));
            LogManager.getLogger(this.getClass())
                    .info("testCreateTraining (" + httpError.getMessage() + "):\n    " + httpError.getResponseBodyAsString());
        }
    }

    @Test
    public void testCreateTrainingInvalidCourt() {
        String token = restService.loginAdmin();

        TrainingWrapper trainingWrapper = new TrainingWrapper();
        trainingWrapper.setStartDatetime(new GregorianCalendar(2016, 1, 1, 17, 0, 0));
        trainingWrapper.setEndDatetime(new GregorianCalendar(2016, 2, 1, 17, 0, 0));
        trainingWrapper.setTrainer(new SimpleUserWrapper("u1@gmail.com"));
        trainingWrapper.setCourtId(3);

        try {
            new RestBuilder<Object>(RestService.URL).path(Uris.TRAININGS).body(trainingWrapper).post().basicAuth(token, "").build();
            fail();
        } catch (HttpClientErrorException httpError) {
            assertEquals(HttpStatus.NOT_FOUND, httpError.getStatusCode());
            assertTrue(httpError.getResponseBodyAsString().contains(NotFoundCourtIdException.class.getSimpleName()));
            LogManager.getLogger(this.getClass())
                    .info("testCreateTraining (" + httpError.getMessage() + "):\n    " + httpError.getResponseBodyAsString());
        }
    }

    @Test
    public void testCreateTrainingInvalidUserGrant() {
        String token = restService.loginAdmin();

        TrainingWrapper trainingWrapper = new TrainingWrapper();
        trainingWrapper.setStartDatetime(new GregorianCalendar(2016, 1, 1, 17, 0, 0));
        trainingWrapper.setEndDatetime(new GregorianCalendar(2016, 2, 1, 17, 0, 0));
        trainingWrapper.setTrainer(new SimpleUserWrapper("u3@gmail.com"));
        trainingWrapper.setCourtId(1);

        try {
            new RestBuilder<Object>(RestService.URL).path(Uris.TRAININGS).body(trainingWrapper).post().basicAuth(token, "").build();
            fail();
        } catch (HttpClientErrorException httpError) {
            assertEquals(HttpStatus.CONFLICT, httpError.getStatusCode());
            assertTrue(httpError.getResponseBodyAsString().contains(InvalidUserGrantException.class.getSimpleName()));
            LogManager.getLogger(this.getClass())
                    .info("testCreateTraining (" + httpError.getMessage() + "):\n    " + httpError.getResponseBodyAsString());
        }
    }

    // showTraining

    @Test
    public void testShowTrainings() {
        String token = restService.loginAdmin();

        List<TrainingWrapper> listBefore = Arrays.asList(new RestBuilder<TrainingWrapper[]>(RestService.URL).path(Uris.TRAININGS)
                .basicAuth(token, "").clazz(TrainingWrapper[].class).get().build());
        assertEquals(0, listBefore.size());

        TrainingWrapper trainingWrapper = generateTrainingWrapper();

        new RestBuilder<Object>(RestService.URL).path(Uris.TRAININGS).body(trainingWrapper).post().basicAuth(token, "").build();

        List<TrainingWrapper> listAfter = Arrays.asList(new RestBuilder<TrainingWrapper[]>(RestService.URL).path(Uris.TRAININGS)
                .basicAuth(token, "").clazz(TrainingWrapper[].class).get().build());
        assertEquals(1, listAfter.size());
    }

    @Test
    public void testShowTraining() {
        String token = restService.loginAdmin();

        TrainingWrapper trainingWrapper = generateTrainingWrapper();

        TrainingWrapper trainingWrapper2 = new RestBuilder<TrainingWrapper>(RestService.URL).path(Uris.TRAININGS).body(trainingWrapper)
                .clazz(TrainingWrapper.class).post().basicAuth(token, "").build();

        new RestBuilder<TrainingWrapper>(RestService.URL).path(Uris.TRAININGS).pathId(trainingWrapper2.getId()).basicAuth(token, "").get()
                .build();
    }

    @Test
    public void testShowInvalidTraining() {
        String token = restService.loginAdmin();

        try {
            new RestBuilder<Object>(RestService.URL).path(Uris.TRAININGS).pathId(NON_EXISTENT_TRAINING).basicAuth(token, "").get().build();
            fail();
        } catch (HttpClientErrorException httpError) {
            assertEquals(HttpStatus.NOT_FOUND, httpError.getStatusCode());
            assertTrue(httpError.getResponseBodyAsString().contains(NotFoundTrainingIdException.class.getSimpleName()));
            LogManager.getLogger(this.getClass())
                    .info("testShowTraining (" + httpError.getMessage() + "):\n    " + httpError.getResponseBodyAsString());
        }
    }

    // updateTraining

    @Test
    public void testUpdateTraining() {
        String token = restService.loginAdmin();

        TrainingWrapper trainingWrapper = generateTrainingWrapper();

        TrainingWrapper trainingWrapper2 = new RestBuilder<TrainingWrapper>(RestService.URL).path(Uris.TRAININGS).body(trainingWrapper)
                .clazz(TrainingWrapper.class).post().basicAuth(token, "").build();

        trainingWrapper2.setCourtId(1);

        TrainingWrapper trainingWrapper3 = new RestBuilder<TrainingWrapper>(RestService.URL).path(Uris.TRAININGS)
                .pathId(trainingWrapper2.getId()).body(trainingWrapper2).clazz(TrainingWrapper.class).put().basicAuth(token, "").build();

        assertEquals(1, trainingWrapper3.getCourtId());
    }

    // deleteTraining

    @Test
    public void testDeleteTraining() {
        String token = restService.loginAdmin();

        TrainingWrapper trainingWrapper = generateTrainingWrapper();

        TrainingWrapper trainingWrapper2 = new RestBuilder<TrainingWrapper>(RestService.URL).path(Uris.TRAININGS).body(trainingWrapper)
                .clazz(TrainingWrapper.class).post().basicAuth(token, "").build();

        new RestBuilder<TrainingWrapper>(RestService.URL).path(Uris.TRAININGS).pathId(trainingWrapper2.getId()).delete()
                .basicAuth(token, "").build();
    }

    @Test
    public void testDeleteTrainingInvalidTraining() {
        String token = restService.loginAdmin();

        try {
            new RestBuilder<TrainingWrapper>(RestService.URL).path(Uris.TRAININGS).pathId(NON_EXISTENT_TRAINING).delete()
                    .basicAuth(token, "").build();
            fail();
        } catch (HttpClientErrorException httpError) {
            assertEquals(HttpStatus.NOT_FOUND, httpError.getStatusCode());
            assertTrue(httpError.getResponseBodyAsString().contains(NotFoundTrainingIdException.class.getSimpleName()));
            LogManager.getLogger(this.getClass())
                    .info("testDeleteTraining (" + httpError.getMessage() + "):\n    " + httpError.getResponseBodyAsString());
        }
    }

    // addTrainee

    @Test
    public void testAddTrainee() {
        String token = restService.loginAdmin();

        TrainingWrapper trainingWrapper = generateTrainingWrapper();

        TrainingWrapper trainingWrapper2 = new RestBuilder<TrainingWrapper>(RestService.URL).path(Uris.TRAININGS).body(trainingWrapper)
                .clazz(TrainingWrapper.class).post().basicAuth(token, "").build();

        List<SimpleUserWrapper> trainees = Arrays.asList(new RestBuilder<SimpleUserWrapper[]>(RestService.URL).path(Uris.USERS)
                .path(Uris.TRAINEE).clazz(SimpleUserWrapper[].class).get().basicAuth(token, "").build());

        int trainingId = trainingWrapper2.getId();
        int traineeId = trainees.get(0).getId();

        assertEquals(0, trainingWrapper2.getTrainees().size());

        TrainingWrapper trainingWrapper3 = new RestBuilder<TrainingWrapper>(RestService.URL).path(Uris.TRAININGS).pathId(trainingId)
                .path(Uris.TRAINEE).pathId(traineeId).post().clazz(TrainingWrapper.class).basicAuth(token, "").build();

        assertEquals(1, trainingWrapper3.getTrainees().size());
    }

    @Test
    public void testAddTraineeExceedingMaxNumber() {
        final int MAX_TRAINEES = 4;
        String token = restService.loginAdmin();

        TrainingWrapper trainingWrapper = generateTrainingWrapper();

        TrainingWrapper trainingWrapper2 = new RestBuilder<TrainingWrapper>(RestService.URL).path(Uris.TRAININGS).body(trainingWrapper)
                .clazz(TrainingWrapper.class).post().basicAuth(token, "").build();

        List<SimpleUserWrapper> trainees = Arrays.asList(new RestBuilder<SimpleUserWrapper[]>(RestService.URL).path(Uris.USERS)
                .path(Uris.TRAINEE).clazz(SimpleUserWrapper[].class).get().basicAuth(token, "").build());

        int trainingId = trainingWrapper2.getId();

        for (int i = 0; i < MAX_TRAINEES; i++) {
            int traineeId = trainees.get(i).getId();
            new RestBuilder<TrainingWrapper>(RestService.URL).path(Uris.TRAININGS).pathId(trainingId).path(Uris.TRAINEE).pathId(traineeId)
                    .post().clazz(TrainingWrapper.class).basicAuth(token, "").build();
        }

        try {
            int traineeId = trainees.get(MAX_TRAINEES).getId();
            new RestBuilder<TrainingWrapper>(RestService.URL).path(Uris.TRAININGS).pathId(trainingId).path(Uris.TRAINEE).pathId(traineeId)
                    .post().clazz(TrainingWrapper.class).basicAuth(token, "").build();
            fail();
        } catch (HttpClientErrorException httpError) {
            assertEquals(HttpStatus.CONFLICT, httpError.getStatusCode());
            assertTrue(httpError.getResponseBodyAsString().contains(MaxUsersByTrainingReachedException.class.getSimpleName()));
            LogManager.getLogger(this.getClass())
                    .info("testDeleteTraining (" + httpError.getMessage() + "):\n    " + httpError.getResponseBodyAsString());
        }
    }

    @Test
    public void testAddTraineeInvalidTraining() {
        String token = restService.loginAdmin();

        try {
            new RestBuilder<Object>(RestService.URL).path(Uris.TRAININGS).pathId(NON_EXISTENT_TRAINING).path(Uris.TRAINEE)
                    .pathId(NON_EXISTENT_USER).post().basicAuth(token, "").build();
            fail();
        } catch (HttpClientErrorException httpError) {
            assertEquals(HttpStatus.NOT_FOUND, httpError.getStatusCode());
            assertTrue(httpError.getResponseBodyAsString().contains(NotFoundTrainingIdException.class.getSimpleName()));
            LogManager.getLogger(this.getClass())
                    .info("testDeleteTraining (" + httpError.getMessage() + "):\n    " + httpError.getResponseBodyAsString());
        }
    }

    @Test
    public void testAddTraineeInvalidUser() {
        String token = restService.loginAdmin();

        TrainingWrapper trainingWrapper = generateTrainingWrapper();

        TrainingWrapper trainingWrapper2 = new RestBuilder<TrainingWrapper>(RestService.URL).path(Uris.TRAININGS).body(trainingWrapper)
                .clazz(TrainingWrapper.class).post().basicAuth(token, "").build();

        try {
            new RestBuilder<TrainingWrapper>(RestService.URL).path(Uris.TRAININGS).pathId(trainingWrapper2.getId()).path(Uris.TRAINEE)
                    .pathId(NON_EXISTENT_USER).post().basicAuth(token, "").build();
            fail();
        } catch (HttpClientErrorException httpError) {
            assertEquals(HttpStatus.NOT_FOUND, httpError.getStatusCode());
            assertTrue(httpError.getResponseBodyAsString().contains(NotFoundUserIdException.class.getSimpleName()));
            LogManager.getLogger(this.getClass())
                    .info("testAddTrainee (" + httpError.getMessage() + "):\n    " + httpError.getResponseBodyAsString());
        }
    }

    @Test
    public void testAddTraineeInvalidUserGrant() {
        String token = restService.loginAdmin();

        TrainingWrapper trainingWrapper = generateTrainingWrapper();

        TrainingWrapper trainingWrapper2 = new RestBuilder<TrainingWrapper>(RestService.URL).path(Uris.TRAININGS).body(trainingWrapper)
                .clazz(TrainingWrapper.class).post().basicAuth(token, "").build();

        try {
            new RestBuilder<TrainingWrapper>(RestService.URL).path(Uris.TRAININGS).pathId(trainingWrapper2.getId()).path(Uris.TRAINEE)
                    .pathId(trainingWrapper2.getTrainer().getId()).post().basicAuth(token, "").build();
            fail();
        } catch (HttpClientErrorException httpError) {
            assertEquals(HttpStatus.CONFLICT, httpError.getStatusCode());
            assertTrue(httpError.getResponseBodyAsString().contains(InvalidUserGrantException.class.getSimpleName()));
            LogManager.getLogger(this.getClass())
                    .info("testAddTrainee (" + httpError.getMessage() + "):\n    " + httpError.getResponseBodyAsString());
        }
    }

    // deleteTrainee

    @Test
    public void testDeleteTrainee() {
        String token = restService.loginAdmin();

        TrainingWrapper trainingWrapper = generateTrainingWrapper();

        TrainingWrapper trainingWrapper2 = new RestBuilder<TrainingWrapper>(RestService.URL).path(Uris.TRAININGS).body(trainingWrapper)
                .clazz(TrainingWrapper.class).post().basicAuth(token, "").build();

        List<SimpleUserWrapper> trainees = Arrays.asList(new RestBuilder<SimpleUserWrapper[]>(RestService.URL).path(Uris.USERS)
                .path(Uris.TRAINEE).clazz(SimpleUserWrapper[].class).get().basicAuth(token, "").build());

        int trainingId = trainingWrapper2.getId();
        int traineeId = trainees.get(0).getId();

        TrainingWrapper trainingWrapper3 = new RestBuilder<TrainingWrapper>(RestService.URL).path(Uris.TRAININGS).pathId(trainingId)
                .path(Uris.TRAINEE).pathId(traineeId).post().clazz(TrainingWrapper.class).basicAuth(token, "").build();

        assertEquals(1, trainingWrapper3.getTrainees().size());

        new RestBuilder<Object>(RestService.URL).path(Uris.TRAININGS).pathId(trainingId).path(Uris.TRAINEE).pathId(traineeId).delete()
                .basicAuth(token, "").build();

        TrainingWrapper trainingWrapper4 = new RestBuilder<TrainingWrapper>(RestService.URL).path(Uris.TRAININGS).pathId(trainingId).get()
                .clazz(TrainingWrapper.class).basicAuth(token, "").build();

        assertEquals(0, trainingWrapper4.getTrainees().size());
    }

    @Test
    public void testDeleteTraineeInvalidTraining() {
        String token = restService.loginAdmin();

        TrainingWrapper trainingWrapper = generateTrainingWrapper();

        new RestBuilder<TrainingWrapper>(RestService.URL).path(Uris.TRAININGS).body(trainingWrapper).clazz(TrainingWrapper.class).post()
                .basicAuth(token, "").build();

        try {
            new RestBuilder<Object>(RestService.URL).path(Uris.TRAININGS).pathId(NON_EXISTENT_TRAINING).path(Uris.TRAINEE)
                    .pathId(NON_EXISTENT_USER).delete().basicAuth(token, "").build();
            fail();
        } catch (HttpClientErrorException httpError) {
            assertEquals(HttpStatus.NOT_FOUND, httpError.getStatusCode());
            assertTrue(httpError.getResponseBodyAsString().contains(NotFoundTrainingIdException.class.getSimpleName()));
            LogManager.getLogger(this.getClass())
                    .info("testDeleteTraining (" + httpError.getMessage() + "):\n    " + httpError.getResponseBodyAsString());
        }
    }

    @Test
    public void testDeleteTraineeInvalidUser() {
        String token = restService.loginAdmin();

        TrainingWrapper trainingWrapper = generateTrainingWrapper();

        TrainingWrapper trainingWrapper2 = new RestBuilder<TrainingWrapper>(RestService.URL).path(Uris.TRAININGS).body(trainingWrapper)
                .clazz(TrainingWrapper.class).post().basicAuth(token, "").build();

        int trainingId = trainingWrapper2.getId();

        try {
            new RestBuilder<Object>(RestService.URL).path(Uris.TRAININGS).pathId(trainingId).path(Uris.TRAINEE).pathId(NON_EXISTENT_USER)
                    .delete().basicAuth(token, "").build();
            fail();
        } catch (HttpClientErrorException httpError) {
            assertEquals(HttpStatus.NOT_FOUND, httpError.getStatusCode());
            assertTrue(httpError.getResponseBodyAsString().contains(NotFoundUserIdException.class.getSimpleName()));
            LogManager.getLogger(this.getClass())
                    .info("testAddTrainee (" + httpError.getMessage() + "):\n    " + httpError.getResponseBodyAsString());
        }
    }

    @Test
    public void testDeleteTraineeInvalidUserRole() {
        String token = restService.loginAdmin();

        TrainingWrapper trainingWrapper = generateTrainingWrapper();

        TrainingWrapper trainingWrapper2 = new RestBuilder<TrainingWrapper>(RestService.URL).path(Uris.TRAININGS).body(trainingWrapper)
                .clazz(TrainingWrapper.class).post().basicAuth(token, "").build();

        int trainingId = trainingWrapper2.getId();
        int traineeId = trainingWrapper2.getTrainer().getId();

        try {
            new RestBuilder<Object>(RestService.URL).path(Uris.TRAININGS).pathId(trainingId).path(Uris.TRAINEE).pathId(traineeId).delete()
                    .basicAuth(token, "").build();
            fail();
        } catch (HttpClientErrorException httpError) {
            assertEquals(HttpStatus.CONFLICT, httpError.getStatusCode());
            assertTrue(httpError.getResponseBodyAsString().contains(InvalidUserGrantException.class.getSimpleName()));
            LogManager.getLogger(this.getClass())
                    .info("testAddTrainee (" + httpError.getMessage() + "):\n    " + httpError.getResponseBodyAsString());
        }
    }

    @After
    public void clean() {
        restService.deleteAll();
    }

    private TrainingWrapper generateTrainingWrapper() {
        TrainingWrapper trainingWrapper = new TrainingWrapper();
        trainingWrapper.setStartDatetime(new GregorianCalendar(2016, 1, 1, 17, 0, 0));
        trainingWrapper.setEndDatetime(new GregorianCalendar(2016, 2, 1, 17, 0, 0));
        trainingWrapper.setTrainer(new SimpleUserWrapper("u2@gmail.com"));
        trainingWrapper.setCourtId(2);
        return trainingWrapper;
    }

    private void generateCourts() {
        restService.createCourt("1");
        restService.createCourt("2");
    }

    private void generateUsers() {
        final int TRAINERS_NUMBER = 2;
        final int TRAINEES_NUMBER = 5;

        final int TRAINERS_OFFSET = 1;
        final int TRAINEES_LAST = TRAINERS_NUMBER + TRAINEES_NUMBER;

        int suffix = TRAINERS_OFFSET;

        while (suffix <= TRAINERS_NUMBER) {
            restService.registerTrainer(suffix);
            suffix++;
        }

        while (suffix <= TRAINEES_LAST) {
            restService.registerTrainee(suffix);
            suffix++;
        }
    }
}
