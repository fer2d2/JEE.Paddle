package data.daos;

import java.util.Calendar;

import data.entities.Training;

public interface TrainingExtended {

    public boolean deleteByTrainingId(int id);

    public boolean addTrainee(int trainingId, int traineeId);

    public boolean deleteTrainee(int trainingId, int traineeId);

    public void deleteReservesMathingTraining(Training training);
    
    public boolean existsTrainingClassForDay(Calendar day);
}
