package business.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import business.api.exceptions.MaxUsersByTrainingReachedException;
import business.api.exceptions.NotFoundCourtIdException;
import business.api.exceptions.NotFoundTrainingIdException;
import business.api.exceptions.NotFoundUserIdException;
import business.api.validation.TrainingValidator;
import business.controllers.TrainingController;
import business.wrapper.TrainingWrapper;
import data.entities.Role;

@RestController
@RequestMapping(Uris.SERVLET_MAP + Uris.TRAININGS)
public class TrainingResource {

    private TrainingController trainingController;

    @Autowired
    public void setTrainingController(TrainingController trainingController) {
        this.trainingController = trainingController;
    }

    private TrainingValidator trainingValidator;

    @Autowired
    public void setTrainingValidator(TrainingValidator trainingValidator) {
        this.trainingValidator = trainingValidator;
    }

    @RequestMapping(method = RequestMethod.POST)
    public TrainingWrapper createTraining(@RequestBody TrainingWrapper trainingWrapper)
            throws NotFoundUserIdException, NotFoundCourtIdException {

        trainingValidator.validateUser(trainingWrapper.getTrainer().getEmail(), Role.TRAINER);
        trainingValidator.validateCourt(trainingWrapper.getCourtId());

        return trainingController.createTraining(trainingWrapper);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<TrainingWrapper> showTrainings() {
        return trainingController.showTrainings();
    }

    @RequestMapping(value = Uris.ID, method = RequestMethod.GET)
    public TrainingWrapper showTraining(@PathVariable int id) throws NotFoundTrainingIdException {
        TrainingWrapper trainingWrapper = trainingController.showTraining(id);

        if (trainingWrapper == null) {
            throw new NotFoundTrainingIdException();
        }

        return trainingWrapper;
    }

    @RequestMapping(value = Uris.ID, method = RequestMethod.PUT)
    public TrainingWrapper updateTraining(@PathVariable int id, @RequestBody TrainingWrapper trainingWrapper)
            throws NotFoundUserIdException, NotFoundCourtIdException, NotFoundTrainingIdException {

        trainingValidator.validateTraining(id);
        trainingValidator.validateUser(trainingWrapper.getTrainer().getEmail(), Role.TRAINER);
        trainingValidator.validateCourt(trainingWrapper.getCourtId());

        return trainingController.updateTraining(id, trainingWrapper);
    }

    @RequestMapping(value = Uris.ID, method = RequestMethod.DELETE)
    public void deleteTraining(@PathVariable int id) throws NotFoundTrainingIdException {
        if (!trainingController.deleteTraining(id)) {
            throw new NotFoundTrainingIdException();
        }
    }

    @RequestMapping(value = Uris.ID + Uris.TRAINEE, method = RequestMethod.POST)
    public TrainingWrapper addTrainee(@PathVariable int trainingId, @PathVariable int traineeId)
            throws NotFoundTrainingIdException, NotFoundUserIdException, MaxUsersByTrainingReachedException {

        trainingValidator.validateTraining(trainingId);
        trainingValidator.validateUser(traineeId, Role.PLAYER);

        TrainingWrapper trainingWrapper = trainingController.addTrainee(trainingId, traineeId);

        if (trainingWrapper == null) {
            throw new MaxUsersByTrainingReachedException();
        }

        return trainingWrapper;
    }

    @RequestMapping(value = Uris.ID + Uris.TRAINEE + Uris.ID, method = RequestMethod.DELETE)
    public void deleteTrainee(@PathVariable int trainingId, @PathVariable int traineeId)
            throws NotFoundTrainingIdException, NotFoundUserIdException {

        trainingValidator.validateTraining(trainingId);
        trainingValidator.validateUser(traineeId, Role.PLAYER);

        trainingController.deleteTrainee(trainingId, traineeId);
    }

}
