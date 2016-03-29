package data.daos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import data.entities.Reserve;
import data.entities.Training;
import data.entities.User;

public class TrainingDaoImpl implements TrainingExtended {

    final long DAY_MILLISECONDS = 86400000;
    final int WEEK_DAYS = 7;
    
    @Autowired
    private TrainingDao trainingDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ReserveDao reserveDao;

    @Override
    public boolean deleteByTrainingId(int id) {
        Training training = trainingDao.findById(id);

        if (training == null) {
            return false;
        }

        trainingDao.delete(training);
        return true;
    }

    @Override
    public boolean addTrainee(int trainingId, int traineeId) {
        Training training = trainingDao.findById(trainingId);
        User trainee = userDao.findById(traineeId);

        if (training == null || trainee == null) {
            return false;
        }

        if (!training.addTrainee(trainee)) {
            return false;
        }

        trainingDao.save(training);

        return true;
    }

    @Override
    public boolean deleteTrainee(int trainingId, int traineeId) {
        Training training = trainingDao.findById(trainingId);
        User trainee = userDao.findById(traineeId);

        if (training == null || trainee == null) {
            return false;
        }

        if (!training.removeTrainee(trainee)) {
            return false;
        }

        trainingDao.save(training);
        return true;
    }

    @Override
    public void deleteReservesMathingTraining(Training training) {
        Calendar startDatetime = training.getStartDatetime();
        Calendar endDatetime = training.getEndDatetime();
        
        List<Reserve> reserves = reserveDao.findByDateBetween(startDatetime, endDatetime);

        int trainingDayOfWeek = startDatetime.get(Calendar.DAY_OF_WEEK);
        int trainingHour = startDatetime.get(Calendar.HOUR_OF_DAY);
        int trainingMinutes = startDatetime.get(Calendar.MINUTE);

        for (Reserve reserve : reserves) {
            Calendar reserveDate = reserve.getDate();
            int reserveDayOfWeek = reserveDate.get(Calendar.DAY_OF_WEEK);
            int reserveHour = reserveDate.get(Calendar.HOUR_OF_DAY);
            int reserveMinutes = reserveDate.get(Calendar.MINUTE);
            
            if(reserve.getCourt().getId() == training.getCourt().getId()) {
                if (reserveDayOfWeek == trainingDayOfWeek && reserveHour == trainingHour && reserveMinutes == trainingMinutes) {
                    reserveDao.delete(reserve);
                }
            }
        }
    }
    
    @Override
    public boolean existsTrainingClassForDay(Calendar day) {
        List<Training> trainings = trainingDao.findAll();
        
        int dayOfWeek = day.get(Calendar.DAY_OF_WEEK);
        int hour = day.get(Calendar.HOUR_OF_DAY);
        int minutes = day.get(Calendar.MINUTE);
        
        for(Training training : trainings) {
            Calendar startDatetime = training.getStartDatetime();
            Calendar endDatetime = training.getEndDatetime();
            
            int trainingDayOfWeek = startDatetime.get(Calendar.DAY_OF_WEEK);
            int trainingHour = startDatetime.get(Calendar.HOUR_OF_DAY);
            int trainingMinutes = startDatetime.get(Calendar.MINUTE);
            
            if(startDatetime.getTimeInMillis() <= day.getTimeInMillis() && day.getTimeInMillis() <= endDatetime.getTimeInMillis()) {
                if (dayOfWeek == trainingDayOfWeek && hour == trainingHour && minutes == trainingMinutes) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public List<Calendar> findAllDatetimesForTraining(Training training) {
        List<Calendar> datetimes = new ArrayList<>();
        
        long startDatetime = training.getStartDatetime().getTimeInMillis();
        long endDatetime = training.getEndDatetime().getTimeInMillis();
        
        for(long date = startDatetime; date <= endDatetime; date+=(DAY_MILLISECONDS * WEEK_DAYS)) {
            Calendar datetimeToAdd = Calendar.getInstance();
            datetimeToAdd.setTimeInMillis(date);
            datetimes.add(datetimeToAdd);
        }
        
        return datetimes;
    }
}
