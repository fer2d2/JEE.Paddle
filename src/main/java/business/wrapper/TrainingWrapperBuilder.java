package business.wrapper;

import java.util.Calendar;

public class TrainingWrapperBuilder {
    private TrainingWrapper trainingWrapper;

    public TrainingWrapperBuilder() {
        trainingWrapper = new TrainingWrapper();
    }

    public TrainingWrapperBuilder startDatetime(Calendar startDatetime) {
        trainingWrapper.setStartDatetime(startDatetime);
        return this;
    }

    public TrainingWrapperBuilder endDatetime(Calendar endDatetime) {
        trainingWrapper.setEndDatetime(endDatetime);
        return this;
    }
    
    public TrainingWrapperBuilder courtId(int courtId) {
        trainingWrapper.setCourtId(courtId);
        return this;
    }
    
    public TrainingWrapperBuilder trainer(SimpleUserWrapper trainer) {
        trainingWrapper.setTrainer(trainer);
        return this;
    }
    
    public TrainingWrapper build() {
        return trainingWrapper;
    }
}
