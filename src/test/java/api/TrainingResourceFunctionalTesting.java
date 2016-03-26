package api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import business.api.Uris;
import business.api.exceptions.InvalidUserGrantException;
import business.api.exceptions.NotFoundCourtIdException;
import business.api.exceptions.NotFoundTrainingIdException;
import business.api.exceptions.NotFoundUserIdException;
import business.wrapper.SimpleUserWrapper;
import business.wrapper.TrainingWrapper;

public class TrainingResourceFunctionalTesting {

    RestService restService = new RestService();

    public final int NON_EXISTENT_TRAINING = 2000;

    public final int NON_EXISTENT_USER = 2000;

    public final String NON_EXISTENT_USER_EMAIL = "usuarioInventado@gmail.com";

    @Before
    public void initialize() {
        // restService.deleteAll();
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
            new RestBuilder<Object>(RestService.URL).path(Uris.TRAININGS).pathId(trainingId).path(Uris.TRAINEE).pathId(traineeId)
                    .delete().basicAuth(token, "").build();
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
        int suffix = 1;
        final int MAX_TRAINERS = 2;
        final int MAX_TRAINEES = 5;

        while (suffix <= MAX_TRAINERS) {
            restService.registerTrainer(suffix);
            suffix++;
        }

        while (suffix <= MAX_TRAINEES) {
            restService.registerTrainee(suffix);
            suffix++;
        }
    }
}
