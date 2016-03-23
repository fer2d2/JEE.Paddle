package business.api;

public class SimpleUserWrapper {

    private String email;

    public SimpleUserWrapper(String email) {
        super();
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "SimpleUserWrapper [email=" + email + "]";
    }

}
