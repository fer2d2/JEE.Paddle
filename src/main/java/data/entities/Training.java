package data.entities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Training {
    public final int MAX_TRAINEES_PER_TRAINING = 4;

    @Id
    @GeneratedValue
    private int id;

    private Calendar startDatetime;

    private Calendar endDatetime;

    @ManyToOne
    @JoinColumn
    private Court court;

    @ManyToOne
    @JoinColumn
    private User trainer;

    @OneToMany(fetch = FetchType.EAGER)
    private List<User> trainees;

    public Training() {
    }

    public Training(Calendar startDatetime, Calendar endDatetime, Court court, User trainer) {
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.court = court;
        this.trainer = trainer;
        this.trainees = new ArrayList<User>();
    }

    public Training(Calendar startDatetime, Calendar endDatetime, Court court, User trainer, List<User> trainees) {
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.court = court;
        this.trainer = trainer;
        this.trainees = trainees;
    }

    public int getId() {
        return id;
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

    public boolean addTrainee(User user) {
        if (this.trainees.size() == MAX_TRAINEES_PER_TRAINING) {
            return false;
        }

        if (this.trainees.contains(user)) {
            // if exists, this method returns true but doesn't
            // generate redundancy
            return true;
        }

        return this.trainees.add(user);
    }

    public boolean removeTrainee(User user) {
        return this.trainees.remove(user);
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return id == ((Training) obj).id;
    }

    @Override
    public String toString() {
        return "Training [id=" + id + ", startDatetime=" + startDatetime + ", endDatetime=" + endDatetime + ", court=" + court
                + ", trainer=" + trainer + ", trainees=" + trainees + "]";
    }

}
