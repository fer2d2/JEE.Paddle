package business.wrapper;

import java.util.Calendar;
import java.util.List;

import business.api.SimpleUserWrapper;

public class TrainingWrapper {

    private Calendar startDatetime;

    private Calendar endDatetime;

    private int courtId;

    private SimpleUserWrapper trainer;

    private List<SimpleUserWrapper> trainees;

    public TrainingWrapper(Calendar startDatetime, Calendar endDatetime, int courtId, SimpleUserWrapper trainer,
            List<SimpleUserWrapper> trainees) {
        super();
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.courtId = courtId;
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

    public int getCourtId() {
        return courtId;
    }

    public void setCourtId(int courtId) {
        this.courtId = courtId;
    }

    public SimpleUserWrapper getTrainer() {
        return trainer;
    }

    public void setTrainer(SimpleUserWrapper trainer) {
        this.trainer = trainer;
    }

    public List<SimpleUserWrapper> getTrainees() {
        return trainees;
    }

    public void setTrainees(List<SimpleUserWrapper> trainees) {
        this.trainees = trainees;
    }

    @Override
    public String toString() {
        return "TrainingWrapper [startDatetime=" + startDatetime + ", endDatetime=" + endDatetime + ", courtId=" + courtId + ", trainer="
                + trainer + ", trainees=" + trainees + "]";
    }

}
