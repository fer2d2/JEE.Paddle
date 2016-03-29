package business.api.exceptions;

public class InvalidCourtReserveTrainingExistsException extends ApiException {

    private static final long serialVersionUID = -1344640670884805385L;

    public static final String DESCRIPTION = "Reserva de no puede ser realizada porque existen clases de entrenamiento en la franja horaria indicada";

    public static final int CODE = 1;

    public InvalidCourtReserveTrainingExistsException() {
        this("");
    }

    public InvalidCourtReserveTrainingExistsException(String detail) {
        super(DESCRIPTION + ". " + detail, CODE);
    }

}
