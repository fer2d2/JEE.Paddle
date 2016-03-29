package business.api;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import business.api.exceptions.InvalidCourtReserveException;
import business.api.exceptions.InvalidCourtReserveTrainingExistsException;
import business.api.exceptions.InvalidDateException;
import business.controllers.CourtController;
import business.controllers.ReserveController;
import business.wrapper.Availability;
import business.wrapper.AvailableTime;

@RestController
@RequestMapping(Uris.SERVLET_MAP + Uris.RESERVES)
public class ReserveResource {

    private ReserveController reserveController;

    private CourtController courtController;

    @Autowired
    public void setCourtController(CourtController courtController) {
        this.courtController = courtController;
    }

    @Autowired
    public void setReserveController(ReserveController reserveController) {
        this.reserveController = reserveController;
    }

    @RequestMapping(value = Uris.AVAILABILITY, method = RequestMethod.GET)
    public Availability showAvailability(@RequestParam(required = false) Long day) throws InvalidDateException {
        Calendar calendarDay = Calendar.getInstance();
        if (day != null) {
            calendarDay.setTimeInMillis(day);
            this.validateDay(calendarDay);
        }
        calendarDay.set(Calendar.HOUR, 0);
        calendarDay.set(Calendar.MINUTE, 0);
        calendarDay.set(Calendar.SECOND, 0);
        calendarDay.set(Calendar.MILLISECOND, 0);
        return reserveController.showCourtAvailability(calendarDay);
    }

    private void validateDay(Calendar day) throws InvalidDateException {
        Calendar calendarDay = Calendar.getInstance();
        calendarDay.add(Calendar.DAY_OF_YEAR, -1);
        if (calendarDay.after(day)) {
            throw new InvalidDateException("La fecha no puede ser un día pasado");
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public void reserveCourt(@AuthenticationPrincipal User activeUser, @RequestBody AvailableTime availableTime)
            throws InvalidCourtReserveException, InvalidDateException, InvalidCourtReserveTrainingExistsException {
        if (!courtController.exist(availableTime.getCourtId())) {
            throw new InvalidCourtReserveException("" + availableTime.getCourtId());
        }

        Calendar date = availableTime.getTime();
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        if (!reserveController.rightTime(date.get(Calendar.HOUR_OF_DAY))) {
            throw new InvalidCourtReserveException(date.get(Calendar.HOUR_OF_DAY) + " fuera de rango");
        }
        this.validateDay(date);

        if (!reserveController.existsTrainingForReserveDatetime(date)) {
            throw new InvalidCourtReserveTrainingExistsException();
        }

        if (!reserveController.reserveCourt(availableTime.getCourtId(), date, activeUser.getUsername())) {
            throw new InvalidCourtReserveException(availableTime.getCourtId() + "-" + availableTime.getTime());

        }
    }

}
