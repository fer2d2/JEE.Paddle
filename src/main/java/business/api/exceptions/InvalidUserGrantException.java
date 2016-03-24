package business.api.exceptions;

public class InvalidUserGrantException extends ApiException {

    private static final long serialVersionUID = -1344640670884805385L;

    public static final String DESCRIPTION = "Campo de User vacio o inexistente";

    public static final int CODE = 1;

    public InvalidUserGrantException() {
        this("");
    }

    public InvalidUserGrantException(String detail) {
        super(DESCRIPTION + ". " + detail, CODE);
    }

}
