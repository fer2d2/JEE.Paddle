package data.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Court {

    @Id
    private int id;

    private boolean active;

    public Court() {
    }

    public Court(int id) {
        this.id = id;
        active = true;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getId() {
        return id;
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
        return id == ((Court) obj).id;
    }

    @Override
    public String toString() {
        return "Court [id=" + id + ", active=" + active + "]";
    }

}
