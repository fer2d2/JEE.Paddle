package api;

import java.util.Calendar;

import business.api.Uris;
import business.wrapper.AvailableTime;
import business.wrapper.TokenWrapper;
import business.wrapper.TrainingWrapper;
import business.wrapper.UserWrapper;
import business.wrapper.UserWrapperBuilder;

public class RestService {

    public static final String URL = "http://localhost:8080/JEE.Paddle.0.0.1-SNAPSHOT" + Uris.SERVLET_MAP;

    public void deleteAll() {
        new RestBuilder<TokenWrapper>(RestService.URL).path(Uris.ADMINS).basicAuth(this.loginAdmin(), "").delete().build();
    }

    public String loginAdmin() {
        TokenWrapper token = new RestBuilder<TokenWrapper>(URL).path(Uris.TOKENS).basicAuth("admin", "admin").clazz(TokenWrapper.class)
                .post().build();
        return token.getToken();
    }

    public String registerAndLoginPlayer() {
        UserWrapper player = new UserWrapperBuilder().build();
        new RestBuilder<Object>(URL).path(Uris.USERS).body(player).post().build();
        TokenWrapper token = new RestBuilder<TokenWrapper>(URL).path(Uris.TOKENS).basicAuth(player.getUsername(), player.getPassword())
                .clazz(TokenWrapper.class).post().build();
        return token.getToken();
    }

    public void registerTrainer(int suffix) {
        UserWrapper player = new UserWrapperBuilder(suffix).build();
        new RestBuilder<Object>(URL).path(Uris.USERS).path(Uris.TRAINER).body(player).post().build();
    }

    public void registerTrainee(int suffix) {
        UserWrapper player = new UserWrapperBuilder(suffix).build();
        new RestBuilder<Object>(URL).path(Uris.USERS).body(player).post().build();
    }

    public void createCourt(String id) {
        new RestBuilder<Object>(URL).path(Uris.COURTS).param("id", id).basicAuth(this.loginAdmin(), "").post().build();
    }

    public void createReserve(Calendar datetimeToQuery, int courtId) {
        String token = registerAndLoginPlayer();
        new RestBuilder<String>(RestService.URL).path(Uris.RESERVES).basicAuth(token, "").body(new AvailableTime(courtId, datetimeToQuery))
                .post().build();
    }

    public TrainingWrapper createTraining(String token, TrainingWrapper trainingWrapper) {
        return new RestBuilder<TrainingWrapper>(RestService.URL).path(Uris.TRAININGS).body(trainingWrapper).clazz(TrainingWrapper.class)
                .post().basicAuth(token, "").build();
    }

    void generateDefaultUsers() {
        final int TRAINERS_NUMBER = 2;
        final int TRAINEES_NUMBER = 5;

        final int TRAINERS_OFFSET = 1;
        final int TRAINEES_LAST = TRAINERS_NUMBER + TRAINEES_NUMBER;

        int suffix = TRAINERS_OFFSET;

        while (suffix <= TRAINERS_NUMBER) {
            registerTrainer(suffix);
            suffix++;
        }

        while (suffix <= TRAINEES_LAST) {
            registerTrainee(suffix);
            suffix++;
        }
    }

}
