package business.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    public TrainingWrapper createTraining(@RequestBody TrainingWrapper trainingWrapper) {
        return trainingController.createTraining(trainingWrapper);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<TrainingWrapper> showTrainings() {
        return trainingController.showTrainings();
    }

    @RequestMapping(value = Uris.ID, method = RequestMethod.GET)
    public TrainingWrapper showTraining(@PathVariable int id) {
        return trainingController.showTraining(id);
    }

    @RequestMapping(value = Uris.ID, method = RequestMethod.PUT)
    public TrainingWrapper updateTraining(@PathVariable int id, @RequestBody TrainingWrapper trainingWrapper) {
        return trainingController.updateTraining(id, trainingWrapper);
    }

    @RequestMapping(value = Uris.ID, method = RequestMethod.DELETE)
    public void deleteTraining(@PathVariable int id) {
        trainingController.deleteTraining(id);
    }

    @RequestMapping(value = Uris.ID + Uris.TRAINEE, method = RequestMethod.POST)
    public UserWrapper addTrainee(@PathVariable int trainingId, @PathVariable int traineeId) {
        return trainingController.addTrainee(trainingId, traineeId);
    }

    @RequestMapping(value = Uris.ID + Uris.TRAINEE + Uris.ID, method = RequestMethod.DELETE)
    public void deleteTrainee(@PathVariable int trainingId, @PathVariable int traineeId) {
        trainingController.deleteTrainee(trainingId, traineeId);
    }
}
