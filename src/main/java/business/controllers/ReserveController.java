package business.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import data.daos.CourtDao;
import data.daos.ReserveDao;
import data.daos.TrainingDao;
import data.daos.UserDao;
import data.entities.Court;
import data.entities.Reserve;
import data.entities.Training;
import business.wrapper.Availability;

@Controller
public class ReserveController {

    private static final int START_TIME = 9;

    private static final int END_TIME = 23;

    private ReserveDao reserveDao;

    private CourtDao courtDao;

    private UserDao userDao;

    private TrainingDao trainingDao;
    
    @Autowired
    public void setReserveDao(ReserveDao reserveDao) {
        this.reserveDao = reserveDao;
    }

    @Autowired
    public void setCourtDao(CourtDao courtDao) {
        this.courtDao = courtDao;
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }
    
    public Availability showCourtAvailability(Calendar calendarDay) {
        Calendar endDay = (Calendar) calendarDay.clone();
        endDay.add(Calendar.DAY_OF_MONTH, 1);
        List<Court> courtList = courtDao.findAll();
        Map<Integer, List<Integer>> allTimesAvailable = new HashMap<>();

        int initialHour = START_TIME;
        if (Calendar.getInstance().get(Calendar.YEAR) == calendarDay.get(Calendar.YEAR)
                && Calendar.getInstance().get(Calendar.DAY_OF_YEAR) == calendarDay.get(Calendar.DAY_OF_YEAR)) {
            initialHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        }
        for (Court court : courtList) {
            List<Integer> hourList = new ArrayList<>();
            for (int hour = initialHour; hour <= END_TIME; hour++) {
                hourList.add(hour);
            }
            allTimesAvailable.put(court.getId(), hourList);
        }
        List<Reserve> reserveList = reserveDao.findByDateBetween(calendarDay, endDay);
        for (Reserve reserve : reserveList) {
            allTimesAvailable.get(reserve.getCourt().getId()).remove(new Integer(reserve.getDate().get(Calendar.HOUR_OF_DAY)));
        }
        
        allTimesAvailable = removeAllTrainingDates(allTimesAvailable);
        
        return new Availability(calendarDay, allTimesAvailable);
    }

    private Map<Integer, List<Integer>> removeAllTrainingDates(Map<Integer, List<Integer>> allTimesAvailable) {
        List<Training> trainingList = trainingDao.findAll();
        for (Training training : trainingList) {
            Court court = training.getCourt();
            List<Calendar> trainingDates = trainingDao.findAllDatetimesForTraining(training);
            
            for(Calendar trainingDate : trainingDates) {
                allTimesAvailable.get(court.getId()).remove(new Integer(trainingDate.get(Calendar.HOUR_OF_DAY)));
            }
        }
        
        return allTimesAvailable;
    }

    public boolean reserveCourt(int courtId, Calendar date, String username) {
        Reserve reserve = new Reserve(courtDao.findOne(courtId), date);
        
        if (reserveDao.findByCourtAndDate(reserve.getCourt(), reserve.getDate()) != null) {
            return false;
        }
        reserve.setUser(userDao.findByUsernameOrEmail(username));
        reserveDao.save(reserve);
        return true;
    }

    public boolean rightTime(int hour) {
        return hour >= START_TIME && hour <= END_TIME;
    }

    
    /////////////////////////////////////////////////////////////////////////////////////////////
    
    public boolean existsTrainingForReserveDatetime(Calendar calendar) {
        return trainingDao.existsTrainingClassForDay(calendar);
    }
    
}
