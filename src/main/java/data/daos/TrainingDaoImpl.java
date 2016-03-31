package data.daos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

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

        /**
         * Shared TimeZone conversion required for APIs time headers
         */
        Calendar spainTimeStartDatetime = new GregorianCalendar(TimeZone.getTimeZone("Europe/Madrid"));
        spainTimeStartDatetime.setTimeInMillis(startDatetime.getTimeInMillis());

        List<Reserve> reserves = reserveDao.findByDateBetween(startDatetime, endDatetime);

        int trainingDayOfWeek = spainTimeStartDatetime.get(Calendar.DAY_OF_WEEK);
        int trainingHour = spainTimeStartDatetime.get(Calendar.HOUR_OF_DAY);
        int trainingMinutes = spainTimeStartDatetime.get(Calendar.MINUTE);

        for (Reserve reserve : reserves) {
            Calendar reserveDate = reserve.getDate();
            Calendar spainTimeReserveDate = new GregorianCalendar(TimeZone.getTimeZone("Europe/Madrid"));
            spainTimeReserveDate.setTimeInMillis(reserveDate.getTimeInMillis());

            int reserveDayOfWeek = spainTimeReserveDate.get(Calendar.DAY_OF_WEEK);
            int reserveHour = spainTimeReserveDate.get(Calendar.HOUR_OF_DAY);
            int reserveMinutes = spainTimeReserveDate.get(Calendar.MINUTE);

            if (reserve.getCourt().getId() == training.getCourt().getId()) {
                if (reserveDayOfWeek == trainingDayOfWeek && reserveHour == trainingHour && reserveMinutes == trainingMinutes) {
                    reserveDao.delete(reserve);
                }
            }
        }
    }

    @Override
    public boolean existsTrainingClassForDay(Calendar day) {
        List<Training> trainings = trainingDao.findAll();

        /**
         * Shared TimeZone conversion required for APIs time headers
         */
        Calendar spainTimeGivenDay = new GregorianCalendar(TimeZone.getTimeZone("Europe/Madrid"));
        spainTimeGivenDay.setTimeInMillis(day.getTimeInMillis());

        int dayOfWeek = spainTimeGivenDay.get(Calendar.DAY_OF_WEEK);
        int hour = spainTimeGivenDay.get(Calendar.HOUR_OF_DAY);
        int minutes = spainTimeGivenDay.get(Calendar.MINUTE);

        for (Training training : trainings) {
            Calendar startDatetime = training.getStartDatetime();
            Calendar endDatetime = training.getEndDatetime();

            Calendar spainTimeStartDatetime = new GregorianCalendar(TimeZone.getTimeZone("Europe/Madrid"));
            spainTimeStartDatetime.setTimeInMillis(startDatetime.getTimeInMillis());

            Calendar spainTimeEndDatetime = new GregorianCalendar(TimeZone.getTimeZone("Europe/Madrid"));
            spainTimeEndDatetime.setTimeInMillis(endDatetime.getTimeInMillis());

            int trainingDayOfWeek = spainTimeStartDatetime.get(Calendar.DAY_OF_WEEK);
            int trainingHour = spainTimeStartDatetime.get(Calendar.HOUR_OF_DAY);
            int trainingMinutes = spainTimeStartDatetime.get(Calendar.MINUTE);

            if (startDatetime.getTimeInMillis() <= day.getTimeInMillis() && day.getTimeInMillis() <= endDatetime.getTimeInMillis()) {
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

        for (long date = startDatetime; date <= endDatetime; date += (DAY_MILLISECONDS * WEEK_DAYS)) {
            Calendar datetimeToAdd = Calendar.getInstance();
            datetimeToAdd.setTimeInMillis(date);
            datetimes.add(datetimeToAdd);
        }

        return datetimes;
    }
}
