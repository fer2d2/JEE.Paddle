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
import business.wrapper.SimpleUserWrapper;
import business.wrapper.TrainingWrapper;
import business.wrapper.TrainingWrapperBuilder;
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

    public final long DAY_MILLISECONDS = 86400000;

    public final int WEEK_DAYS = 7;

    @Before
    public void initialize() {
        restService.deleteAll();
        generateCourts();
        restService.generateDefaultUsers();
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
    public void testCreateTrainingInvalidUser() {
        String token = restService.loginAdmin();

        TrainingWrapper trainingWrapper = new TrainingWrapperBuilder().startDatetime(new GregorianCalendar(2016, 1, 1, 17, 0, 0))
                .endDatetime(new GregorianCalendar(2016, 2, 1, 17, 0, 0)).trainer(new SimpleUserWrapper(NON_EXISTENT_USER_EMAIL)).courtId(1)
                .build();

        try {
            restService.createTraining(token, trainingWrapper);
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

        TrainingWrapper trainingWrapper = new TrainingWrapperBuilder().startDatetime(new GregorianCalendar(2016, 1, 1, 17, 0, 0))
                .endDatetime(new GregorianCalendar(2016, 2, 1, 17, 0, 0)).trainer(new SimpleUserWrapper("u1@gmail.com")).courtId(3).build();

        try {
            restService.createTraining(token, trainingWrapper);
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

        TrainingWrapper trainingWrapper = new TrainingWrapperBuilder().startDatetime(new GregorianCalendar(2016, 1, 1, 17, 0, 0))
                .endDatetime(new GregorianCalendar(2016, 2, 1, 17, 0, 0)).trainer(new SimpleUserWrapper("u3@gmail.com")).courtId(1).build();

        try {
            restService.createTraining(token, trainingWrapper);
            fail();
        } catch (HttpClientErrorException httpError) {
            assertEquals(HttpStatus.CONFLICT, httpError.getStatusCode());
            assertTrue(httpError.getResponseBodyAsString().contains(InvalidUserGrantException.class.getSimpleName()));
            LogManager.getLogger(this.getClass())
                    .info("testCreateTraining (" + httpError.getMessage() + "):\n    " + httpError.getResponseBodyAsString());
        }
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
        startDatetime.set(Calendar.HOUR_OF_DAY, 15);
        startDatetime.set(Calendar.MINUTE, 0);

        endDatetime.setTimeInMillis(datetimeToQuery.getTimeInMillis() + (WEEK_DAYS * DAY_MILLISECONDS));
        endDatetime.set(Calendar.HOUR_OF_DAY, 15);
        endDatetime.set(Calendar.MINUTE, 0);

        assertEquals(0, basicDataService.countReserves());
        restService.createReserve(datetimeToQuery, 1);
        assertEquals(1, basicDataService.countReserves());

        String token = restService.loginAdmin();
        TrainingWrapper trainingWrapper = new TrainingWrapperBuilder().startDatetime(startDatetime).endDatetime(endDatetime)
                .trainer(new SimpleUserWrapper("u1@gmail.com")).courtId(1).build();

        restService.createTraining(token, trainingWrapper);
        assertEquals(0, basicDataService.countReserves());
    }

    // showTraining

    @Test
    public void testShowTrainings() {
        String token = restService.loginAdmin();

        List<TrainingWrapper> listBefore = getTrainingsList(token);
        assertEquals(0, listBefore.size());

        TrainingWrapper trainingWrapper = generateDefaultTrainingWrapper();
        restService.createTraining(token, trainingWrapper);
        List<TrainingWrapper> listAfter = getTrainingsList(token);
        assertEquals(1, listAfter.size());
    }

    private List<TrainingWrapper> getTrainingsList(String token) {
        return Arrays.asList(new RestBuilder<TrainingWrapper[]>(RestService.URL).path(Uris.TRAININGS).basicAuth(token, "")
                .clazz(TrainingWrapper[].class).get().build());
    }

    @Test
    public void testShowTraining() {
        String token = restService.loginAdmin();

        TrainingWrapper trainingWrapper = generateDefaultTrainingWrapper();
        TrainingWrapper trainingWrapper2 = restService.createTraining(token, trainingWrapper);

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

        TrainingWrapper trainingWrapper = generateDefaultTrainingWrapper();
        TrainingWrapper trainingWrapper2 = restService.createTraining(token, trainingWrapper);
        trainingWrapper2.setCourtId(1);

        TrainingWrapper trainingWrapper3 = new RestBuilder<TrainingWrapper>(RestService.URL).path(Uris.TRAININGS)
                .pathId(trainingWrapper2.getId()).body(trainingWrapper2).clazz(TrainingWrapper.class).put().basicAuth(token, "").build();

        assertEquals(1, trainingWrapper3.getCourtId());
    }

    // deleteTraining

    @Test
    public void testDeleteTraining() {
        String token = restService.loginAdmin();

        TrainingWrapper trainingWrapper = generateDefaultTrainingWrapper();
        TrainingWrapper trainingWrapper2 = restService.createTraining(token, trainingWrapper);

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

        TrainingWrapper trainingWrapper = generateDefaultTrainingWrapper();
        TrainingWrapper trainingWrapper2 = restService.createTraining(token, trainingWrapper);

        List<SimpleUserWrapper> trainees = getTrainees(token);

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

        TrainingWrapper trainingWrapper = generateDefaultTrainingWrapper();
        TrainingWrapper trainingWrapper2 = restService.createTraining(token, trainingWrapper);

        List<SimpleUserWrapper> trainees = getTrainees(token);

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

    private List<SimpleUserWrapper> getTrainees(String token) {
        return Arrays.asList(new RestBuilder<SimpleUserWrapper[]>(RestService.URL).path(Uris.USERS).path(Uris.TRAINEE)
                .clazz(SimpleUserWrapper[].class).get().basicAuth(token, "").build());
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

        TrainingWrapper trainingWrapper = generateDefaultTrainingWrapper();
        TrainingWrapper trainingWrapper2 = restService.createTraining(token, trainingWrapper);

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

        TrainingWrapper trainingWrapper = generateDefaultTrainingWrapper();
        TrainingWrapper trainingWrapper2 = restService.createTraining(token, trainingWrapper);

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

        TrainingWrapper trainingWrapper = generateDefaultTrainingWrapper();
        TrainingWrapper trainingWrapper2 = restService.createTraining(token, trainingWrapper);
        List<SimpleUserWrapper> trainees = getTrainees(token);

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

        TrainingWrapper trainingWrapper = generateDefaultTrainingWrapper();
        restService.createTraining(token, trainingWrapper);

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

        TrainingWrapper trainingWrapper = generateDefaultTrainingWrapper();
        TrainingWrapper trainingWrapper2 = restService.createTraining(token, trainingWrapper);
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

        TrainingWrapper trainingWrapper = generateDefaultTrainingWrapper();
        TrainingWrapper trainingWrapper2 = restService.createTraining(token, trainingWrapper);
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

    private TrainingWrapper generateDefaultTrainingWrapper() {
        return new TrainingWrapperBuilder().startDatetime(new GregorianCalendar(2016, 1, 1, 17, 0, 0))
                .endDatetime(new GregorianCalendar(2016, 2, 1, 17, 0, 0)).trainer(new SimpleUserWrapper("u2@gmail.com")).courtId(2).build();
    }

    private void generateCourts() {
        restService.createCourt("1");
        restService.createCourt("2");
    }
}
