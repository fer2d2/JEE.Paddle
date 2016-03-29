package data.daos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import config.PersistenceConfig;
import config.TestsPersistenceConfig;
import data.entities.Court;
import data.entities.Reserve;
import data.entities.Training;
import data.entities.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceConfig.class, TestsPersistenceConfig.class})
public class TrainingDaoITest {

    @Autowired
    private ReserveDao reserveDao;

    @Autowired
    private TrainingDao trainingDao;
    
    @Autowired
    private CourtDao courtDao;
    
    @Autowired
    private DaosService daosService;
    
    @Test
    public void testAddTrainee() {
        Training training = (Training) daosService.getMap().get("training");
        User trainee = (User) daosService.getMap().get("u1");
        
        assertEquals(0, training.getTrainees().size());
        trainingDao.addTrainee(training.getId(), trainee.getId());
        training = trainingDao.findById(training.getId());
        assertEquals(1, training.getTrainees().size());
        
    }
    
    @Test
    public void testDeleteTrainee() {
        Training training = (Training) daosService.getMap().get("training");
        User trainee = (User) daosService.getMap().get("u1");

        trainingDao.addTrainee(training.getId(), trainee.getId());
        training = trainingDao.findById(training.getId());
        assertEquals(1, training.getTrainees().size());
        
        trainingDao.deleteTrainee(training.getId(), trainee.getId());
        training = trainingDao.findById(training.getId());
        assertEquals(0, training.getTrainees().size());
    }
    
    @Test
    public void testDeleteReservesMathingTraining() {
        Calendar startDatetime = Calendar.getInstance();
        Calendar endDatetime = Calendar.getInstance();
        Calendar reserveDatetime = Calendar.getInstance();
        
        int day = 3;
        int weekDays = 7;
        int numWeeksReserveDatetime = 1;
        int numWeeksEndDatetime = 2;
        
        startDatetime.set(2016, 1, day, 17, 0, 0);
        reserveDatetime.set(2016, 1, (weekDays * numWeeksReserveDatetime) + day, 17, 0, 0);
        endDatetime.set(2016, 1, (weekDays * numWeeksEndDatetime) + day, 17, 0, 0);
        
        Court court = courtDao.findOne(1);
        User trainer = (User) daosService.getMap().get("ut2");
        
        Training training = new Training(startDatetime, endDatetime, court, trainer);
        trainingDao.save(training);
        
        long reservesBeforeInsert = reserveDao.count();
        Reserve reserve = new Reserve(court, reserveDatetime);
        reserveDao.save(reserve);
        assertEquals(reservesBeforeInsert + 1, reserveDao.count());
        
        trainingDao.deleteReservesMathingTraining(training);
        
        assertEquals(reservesBeforeInsert, reserveDao.count());
        
    }
    
    @Test
    public void TestExistsTrainingClassForDay() {
        Calendar startDatetime = Calendar.getInstance();
        Calendar endDatetime = Calendar.getInstance();
        Calendar datetimeToQuery = Calendar.getInstance();
        
        int day = 3;
        int weekDays = 7;
        int numWeeksDatetimeToQuery = 1;
        int numWeeksEndDatetime = 2;
        
        startDatetime.set(2016, 1, day, 17, 0, 0);
        datetimeToQuery.set(2016, 1, (weekDays * numWeeksDatetimeToQuery) + day, 17, 0, 0);
        endDatetime.set(2016, 1, (weekDays * numWeeksEndDatetime) + day, 17, 0, 0);
        
        Court court = courtDao.findOne(1);
        User trainer = (User) daosService.getMap().get("ut2");
        
        Training training = new Training(startDatetime, endDatetime, court, trainer);
        
        assertFalse(trainingDao.existsTrainingClassForDay(datetimeToQuery));
        
        trainingDao.save(training);
        
        assertTrue(trainingDao.existsTrainingClassForDay(datetimeToQuery));
        
    }
    
    @Test
    public void TestFindAllDatetimesForTraining() {
        Calendar startDatetime = Calendar.getInstance();
        Calendar endDatetime = Calendar.getInstance();
        
        int day = 3;
        int weekDays = 7;
        int numWeeksEndDatetime = 2;
        
        startDatetime.set(2016, 1, day, 17, 0, 0);
        endDatetime.set(2016, 1, (weekDays * numWeeksEndDatetime) + day, 17, 0, 0);
        
        Court court = courtDao.findOne(1);
        User trainer = (User) daosService.getMap().get("ut2");
        
        Training training = new Training(startDatetime, endDatetime, court, trainer);
        trainingDao.save(training);
        
        assertEquals(3, trainingDao.findAllDatetimesForTraining(training).size());
        
    }
}
