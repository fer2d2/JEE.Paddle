package business.api.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import business.api.exceptions.NotFoundCourtIdException;
import business.api.exceptions.NotFoundTrainingIdException;
import business.api.exceptions.NotFoundUserIdException;
import business.controllers.TrainingController;
import data.entities.Role;

@Service
public class TrainingValidator {
    
    private TrainingController trainingController;

    @Autowired
    public void setTrainingController(TrainingController trainingController) {
        this.trainingController = trainingController;
    }
    
    public TrainingValidator() {
        
    }
    
    public void validateUser(String userEmail, Role grant) throws NotFoundUserIdException {
        if (!trainingController.userExists(userEmail)) {
            throw new NotFoundUserIdException();
        }
    }

    public void validateUser(int traineeId, Role grant) throws NotFoundUserIdException {
        if (!trainingController.userExists(traineeId)) {
            throw new NotFoundUserIdException();
        }
    }
    
    public void validateCourt(int courtId) throws NotFoundCourtIdException {
        if (!trainingController.courtExists(courtId)) {
            throw new NotFoundCourtIdException();
        }
    }

    public void validateTraining(int trainingId) throws NotFoundTrainingIdException {
        if (!trainingController.trainingExists(trainingId)) {
            throw new NotFoundTrainingIdException();
        }
    }
}
