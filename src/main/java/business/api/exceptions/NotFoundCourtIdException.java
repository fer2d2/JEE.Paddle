package business.api.exceptions;

public class NotFoundCourtIdException extends ApiException {

    private static final long serialVersionUID = 1363934878288065878L;

    public static final String DESCRIPTION = "La pista referenciada no existe";

    public static final int CODE = 1;

    public NotFoundCourtIdException() {
        this("");
    }

    public NotFoundCourtIdException(String detail) {
        super(DESCRIPTION + ". " + detail, CODE);
    }

}
