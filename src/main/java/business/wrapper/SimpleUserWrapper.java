package business.wrapper;

public class SimpleUserWrapper {

    private int id;

    private String email;

    public SimpleUserWrapper() {
    }

    public SimpleUserWrapper(int id, String email) {
        super();
        this.id = id;
        this.email = email;
    }
    
    public SimpleUserWrapper(String email) {
        super();
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "SimpleUserWrapper [id=" + id + ", email=" + email + "]";
    }

}
