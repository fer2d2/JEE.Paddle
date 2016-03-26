package business.api.exceptions;

public class NotFoundUserIdException extends ApiException {

    private static final long serialVersionUID = -6125795093600092263L;

    public static final String DESCRIPTION = "No se encuentra el identificador de usuario utilizado";

    public static final int CODE = 333;

    public NotFoundUserIdException() {
        this("");
    }

    public NotFoundUserIdException(String detail) {
        super(DESCRIPTION + ". " + detail, CODE);
    }

}
