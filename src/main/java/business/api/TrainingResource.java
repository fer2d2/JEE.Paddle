package business.api;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import business.controllers.TrainingController;
import business.wrapper.TrainingWrapper;
import business.wrapper.UserWrapper;

@RestController
@RequestMapping(Uris.SERVLET_MAP + Uris.TRAININGS)
public class TrainingResource {

    private TrainingController trainingController;

    @Autowired
    public void setReserveController(TrainingController trainingController) {
        this.trainingController = trainingController;
    }

    @RequestMapping(method = RequestMethod.POST)
    public TrainingWrapper createTraining(@RequestParam(required = true) int id, @RequestParam(required = true) Calendar startDatetime,
            @RequestParam(required = true) Calendar endDatetime) {
        return null;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<TrainingWrapper> showTrainings() {
        return null;
    }

    @RequestMapping(method = RequestMethod.GET)
    public TrainingWrapper showTraining(@PathVariable int id) {
        return null;
    }
    
    @RequestMapping(value = Uris.ID, method = RequestMethod.PUT)
    public void updateTraining(@PathVariable int id) {

    }

    @RequestMapping(value = Uris.ID, method = RequestMethod.DELETE)
    public void deleteTraining(@PathVariable int id) {

    }

    @RequestMapping(value = Uris.ID + Uris.TRAINEE, method = RequestMethod.POST)
    public UserWrapper addTrainee(@PathVariable int trainingId, @PathVariable int traineeId) {
        return null;
    }

    @RequestMapping(value = Uris.ID + Uris.TRAINEE + Uris.ID, method = RequestMethod.DELETE)
    public void deleteTrainee(@PathVariable int trainingId, @PathVariable int traineeId) {

    }
}
