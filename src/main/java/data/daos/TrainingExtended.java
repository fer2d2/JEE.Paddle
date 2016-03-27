package data.daos;

import java.util.Calendar;

public interface TrainingExtended {

    public boolean deleteByTrainingId(int id);

    public boolean addTrainee(int trainingId, int traineeId);

    public boolean deleteTrainee(int trainingId, int traineeId);

    public void deleteReservesMathingTraining(Calendar startDatetime, Calendar endDatetime);
    
    public boolean existsTrainingClassForDay(Calendar day);
}
