package data.daos;

import java.util.Calendar;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import data.entities.Court;
import data.entities.Reserve;

public interface ReserveDao extends JpaRepository<Reserve, Integer> {

    List<Reserve> findByDateBetween(Calendar date1, Calendar date2);

    Reserve findByCourtAndDate(Court court, Calendar date);
}
