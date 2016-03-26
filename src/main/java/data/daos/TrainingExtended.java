package data.daos;

public interface TrainingExtended {

    public boolean deleteByTrainingId(int id);

    public boolean findByDatetime();

    public boolean addTrainee(int trainingId, int traineeId);

    public boolean deleteTrainee(int trainingId, int traineeId);

}
