package data.daos;

import org.springframework.beans.factory.annotation.Autowired;

import data.entities.Training;
import data.entities.User;

public class TrainingDaoImpl implements TrainingExtended {

    @Autowired
    private TrainingDao trainingDao;
    
    @Autowired
    private UserDao userDao;
    
    @Override
    public boolean create() {
        // TODO si no sirve, eliminar
        return false;
    }

    @Override
    public boolean deleteByTrainingId(int id) {
        Training training = trainingDao.findById(id);
        
        if(training == null) {
            return false;
        }
        
        trainingDao.delete(training);
        return true;
    }

    @Override
    public boolean findByDatetime() {
        // TODO para ser utilizado en un servicio que pueda comprobar si existe la posibilidad de añadir una reserva.
        return false;
    }

    @Override
    public boolean addTrainee(int trainingId, int traineeId) {
        Training training = trainingDao.findById(trainingId);
        User trainee = userDao.findById(traineeId);
        
        if(training == null || trainee == null) {
            return false;
        }
        
        training.addTrainee(trainee);
        trainingDao.save(training);
        
        return true;
    }

    @Override
    public boolean deleteTrainee(int trainingId, int traineeId) {
        Training training = trainingDao.findById(trainingId);
        User trainee = userDao.findById(traineeId);
        
        if(training == null || trainee == null) {
            return false;
        }
        
        training.removeTrainee(trainee);
        trainingDao.save(training);
        
        return true;
    }

}
