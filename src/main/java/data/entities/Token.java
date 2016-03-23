package data.entities;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Token {

    public static final long TOKEN_LIFETIME = 60 * 60 * 1000;

    @Id
    @GeneratedValue
    private int id;

    @Column(unique = true, nullable = false)
    private String value;

    @ManyToOne
    @JoinColumn
    private User user;

    @Column(nullable = false)
    private Calendar createdDatetime;

    public Token() {
    }

    public Token(User user) {
        assert user != null;
        this.user = user;
        this.value = new Encrypt()
                .encryptInBase64UrlSafe("" + user.getId() + user.getUsername() + Long.toString(new Date().getTime()) + user.getPassword());
        this.createdDatetime = Calendar.getInstance();
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public User getUser() {
        return user;
    }

    public Calendar getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(Calendar createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public boolean hasExpired() {
        return ((Calendar.getInstance().getTimeInMillis() - this.createdDatetime.getTimeInMillis()) > TOKEN_LIFETIME);
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
        return id == ((Token) obj).id;
    }

    @Override
    public String toString() {
        return "Token [id=" + id + ", value=" + value + ", user=" + user + ", createdDatetime=" + createdDatetime + "]";
    }
}
