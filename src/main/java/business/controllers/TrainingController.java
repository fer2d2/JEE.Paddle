package business.controllers;

import java.util.Calendar;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import business.api.Uris;
import business.wrapper.TrainingWrapper;
import business.wrapper.UserWrapper;

@Controller
public class TrainingController {

    public TrainingWrapper createTraining(int id, Calendar startDatetime, Calendar endDatetime) {
        return null;
    }
    
    public List<TrainingWrapper> showTrainings() {
        return null;
    }

    public TrainingWrapper showTraining(int id) {
        return null;
    }
    
    public TrainingWrapper updateTraining(int id) {
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
