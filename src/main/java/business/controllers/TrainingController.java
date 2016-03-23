package business.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;

import business.wrapper.TrainingWrapper;
import business.wrapper.UserWrapper;

@Controller
public class TrainingController {

    public TrainingWrapper createTraining(TrainingWrapper trainingWrapper) {
        return null;
    }
    
    public List<TrainingWrapper> showTrainings() {
        return null;
    }

    public TrainingWrapper showTraining(int id) {
        return null;
    }
    
    public TrainingWrapper updateTraining(int id, TrainingWrapper trainingWrapper) {
        return null;
    }

    public boolean deleteTraining(int id) {
        return false;
    }

    public UserWrapper addTrainee(int trainingId, int traineeId) {
        return null;
    }

    public boolean deleteTrainee(int trainingId, int traineeId) {
        return false;
    }
    
}
