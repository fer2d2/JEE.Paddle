package business.wrapper;

import java.util.Calendar;
import java.util.List;

import data.entities.Court;
import data.entities.User;

public class TrainingWrapper {

    private Calendar startDatetime;

    private Calendar endDatetime;

    private Court court;

    private User trainer;

    private List<User> trainees;

    public TrainingWrapper(Calendar startDatetime, Calendar endDatetime, Court court, User trainer, List<User> trainees) {
        super();
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.court = court;
        this.trainer = trainer;
        this.trainees = trainees;
    }

    public Calendar getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(Calendar startDatetime) {
        this.startDatetime = startDatetime;
    }

    public Calendar getEndDatetime() {
        return endDatetime;
    }

    public void setEndDatetime(Calendar endDatetime) {
        this.endDatetime = endDatetime;
    }

    public Court getCourt() {
        return court;
    }

    public void setCourt(Court court) {
        this.court = court;
    }

    public User getTrainer() {
        return trainer;
    }

    public void setTrainer(User trainer) {
        this.trainer = trainer;
    }

    public List<User> getTrainees() {
        return trainees;
    }

    public void setTrainees(List<User> trainees) {
        this.trainees = trainees;
    }

    @Override
    public String toString() {
        return "TrainingWrapper [startDatetime=" + startDatetime + ", endDatetime=" + endDatetime + ", court=" + court + ", trainer="
                + trainer + ", trainees=" + trainees + "]";
    }

}
