package business.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import business.api.SimpleUserWrapper;
import business.wrapper.TrainingWrapper;
import data.daos.CourtDao;
import data.daos.TrainingDao;
import data.daos.UserDao;
import data.entities.Court;
import data.entities.Training;
import data.entities.User;

@Controller
public class TrainingController {

    private TrainingDao trainingDao;

    private CourtDao courtDao;

    private UserDao userDao;

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

    public TrainingWrapper createTraining(TrainingWrapper trainingWrapper) {
        // TODO exceptions
        Training training = buildTrainingFromTrainingWrapper(trainingWrapper);
        trainingDao.save(training);
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
        // TODO exceptions
        Training training = trainingDao.findById(id);
        TrainingWrapper trainingWrapper = buildTrainingWrapper(training);
        
        return trainingWrapper;
    }

    public TrainingWrapper updateTraining(int id, TrainingWrapper trainingWrapper) {
        // TODO exceptions
        Training training = trainingDao.findById(id);
        training = updateTrainingFromTrainingWrapper(training, trainingWrapper);
        trainingDao.save(training);
        
        return trainingWrapper;
    }

    public boolean deleteTraining(int id) {
        // TODO exceptions
        Training training = trainingDao.findById(id);
        trainingDao.delete(training);
        
        return true;
    }

    public SimpleUserWrapper addTrainee(int trainingId, int traineeId) {
        // TODO exceptions
        Training training = trainingDao.findById(trainingId);
        User trainee = userDao.findById(traineeId);
        
        training.addTrainee(trainee);
        return new SimpleUserWrapper(trainee.getEmail());
    }

    public boolean deleteTrainee(int trainingId, int traineeId) {
        // TODO exceptions
        Training training = trainingDao.findById(trainingId);
        User trainee = userDao.findById(traineeId);
        
        training.removeTrainee(trainee);
        
        return true;
    }
    
    private TrainingWrapper buildTrainingWrapper(Training training) {
        List<SimpleUserWrapper> traineesWrapper = buildTraineesWrapper(training);
        
        return new TrainingWrapper(training.getStartDatetime(), training.getEndDatetime(),
                training.getCourt().getId(), new SimpleUserWrapper(training.getTrainer().getEmail()), traineesWrapper);
    }

    private List<SimpleUserWrapper> buildTraineesWrapper(Training training) {
        List<User> trainees = training.getTrainees();
        List<SimpleUserWrapper> traineesWrapper = new ArrayList<SimpleUserWrapper>();

        for (User trainee : trainees) {
            traineesWrapper.add(new SimpleUserWrapper(trainee.getEmail()));
        }
        return traineesWrapper;
    }
    
    private Training buildTrainingFromTrainingWrapper(TrainingWrapper trainingWrapper) {
        Court court = courtDao.findOne(trainingWrapper.getCourtId());
        User trainer = userDao.findByUsernameOrEmail(trainingWrapper.getTrainer().getEmail());

        Training training = new Training(trainingWrapper.getStartDatetime(), trainingWrapper.getEndDatetime(), court, trainer, null);
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
