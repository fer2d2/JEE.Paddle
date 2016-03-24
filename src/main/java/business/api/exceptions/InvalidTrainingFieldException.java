package business.api.exceptions;

public class InvalidTrainingFieldException extends ApiException {

    private static final long serialVersionUID = -1344640670884805385L;

    public static final String DESCRIPTION = "Campo de User vacio o inexistente";

    public static final int CODE = 1;

    public InvalidTrainingFieldException() {
        this("");
    }

    public InvalidTrainingFieldException(String detail) {
        super(DESCRIPTION + ". " + detail, CODE);
    }

}
