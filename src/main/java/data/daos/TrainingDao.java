package data.daos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import data.entities.Training;
import data.entities.User;

public interface TrainingDao extends JpaRepository<Training, Integer>, TrainingExtended {

    public Training findById(int idTraining);

    public List<Training> findByTrainer(User user);

}
