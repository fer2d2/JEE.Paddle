package business.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import business.wrapper.SimpleUserWrapper;
import business.wrapper.TrainingWrapper;
import data.daos.AuthorizationDao;
import data.daos.CourtDao;
import data.daos.TrainingDao;
import data.daos.UserDao;
import data.entities.Court;
import data.entities.Role;
import data.entities.Training;
import data.entities.User;

@Controller
public class TrainingController {

    private TrainingDao trainingDao;

    private CourtDao courtDao;

    private UserDao userDao;

    private AuthorizationDao authorizationDao;
    
    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Autowired
    public void setCourtDao(CourtDao courtDao) {
        this.courtDao = courtDao;
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setAuthorizationDao(AuthorizationDao authorizationDao) {
        this.authorizationDao = authorizationDao;
    }

    public TrainingWrapper createTraining(TrainingWrapper trainingWrapper) {
        Training training = buildTrainingFromTrainingWrapper(trainingWrapper);
        training = trainingDao.saveAndFlush(training);
        
        trainingWrapper = buildTrainingWrapper(training);
        trainingWrapper.setId(training.getId());
        
        trainingDao.deleteReservesMathingTraining(training);
        
        return trainingWrapper;
    }

    public List<TrainingWrapper> showTrainings() {
        List<Training> trainings = trainingDao.findAll();
        List<TrainingWrapper> trainingWrappers = new ArrayList<TrainingWrapper>();

        if (trainings.size() > 0) {
            for (Training training : trainings) {
                trainingWrappers.add(buildTrainingWrapper(training));
            }
        }

        return trainingWrappers;
    }

    public TrainingWrapper showTraining(int id) {
        Training training = trainingDao.findById(id);
        TrainingWrapper trainingWrapper = buildTrainingWrapper(training);

        return trainingWrapper;
    }

    public TrainingWrapper updateTraining(int id, TrainingWrapper trainingWrapper) {
        Training training = trainingDao.findById(id);
        training = updateTrainingFromTrainingWrapper(training, trainingWrapper);
        training = trainingDao.saveAndFlush(training);
        trainingWrapper.setId(training.getId());
        
        trainingDao.deleteReservesMathingTraining(training);
        
        return trainingWrapper;
    }

    public boolean deleteTraining(int id) {
        return trainingDao.deleteByTrainingId(id);
    }

    public TrainingWrapper addTrainee(int trainingId, int traineeId) {

        if (!trainingDao.addTrainee(trainingId, traineeId)) {
            return null;
        }

        return buildTrainingWrapper(trainingDao.findById(trainingId));
    }

    public boolean deleteTrainee(int trainingId, int traineeId) {
        return trainingDao.deleteTrainee(trainingId, traineeId);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    
    public boolean trainingExists(int id) {
        return trainingDao.exists(id);
    }

    public boolean userExists(int id) {
        //return true;
        return userDao.exists(id);
    }

    public boolean userExists(String email) {
        return (userDao.findByUsernameOrEmail(email) != null);
    }

    public boolean userHasRole(int id, Role role) {
        return (authorizationDao.findOneRoleByUser(userDao.findById(id), role) != null);
    }

    public boolean userHasRole(String email, Role role) {
        return (authorizationDao.findOneRoleByUser(userDao.findByUsernameOrEmail(email), role) != null);
    }
    
    public boolean courtExists(int id) {
        return courtDao.exists(id);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    private TrainingWrapper buildTrainingWrapper(Training training) {

        if (training == null) {
            return null;
        }

        List<SimpleUserWrapper> traineesWrapper = buildTraineesWrapper(training);

        return new TrainingWrapper(training.getId(), training.getStartDatetime(), training.getEndDatetime(), training.getCourt().getId(),
                new SimpleUserWrapper(training.getTrainer().getId(), training.getTrainer().getEmail()), traineesWrapper);
    }

    private List<SimpleUserWrapper> buildTraineesWrapper(Training training) {
        List<User> trainees = training.getTrainees();
        List<SimpleUserWrapper> traineesWrapper = new ArrayList<SimpleUserWrapper>();

        for (User trainee : trainees) {
            traineesWrapper.add(new SimpleUserWrapper(trainee.getId(), trainee.getEmail()));
        }
        return traineesWrapper;
    }

    private Training buildTrainingFromTrainingWrapper(TrainingWrapper trainingWrapper) {
        Court court = courtDao.findOne(trainingWrapper.getCourtId());
        User trainer = userDao.findByUsernameOrEmail(trainingWrapper.getTrainer().getEmail());

        Training training = new Training(trainingWrapper.getStartDatetime(), trainingWrapper.getEndDatetime(), court, trainer);

        return training;
    }

    private Training updateTrainingFromTrainingWrapper(Training training, TrainingWrapper trainingWrapper) {
        Court court = courtDao.findOne(trainingWrapper.getCourtId());
        User trainer = userDao.findByUsernameOrEmail(trainingWrapper.getTrainer().getEmail());

        training.setStartDatetime(trainingWrapper.getStartDatetime());
        training.setEndDatetime(trainingWrapper.getEndDatetime());
        training.setCourt(court);
        training.setTrainer(trainer);

        return training;
    }
}
