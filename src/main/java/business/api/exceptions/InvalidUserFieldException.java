package business.api.exceptions;

public class InvalidUserFieldException extends ApiException {

    private static final long serialVersionUID = -1344640670884805385L;

    public static final String DESCRIPTION = "El usuario seleccionado no tiene los permisos suficientes para ser asignado";

    public static final int CODE = 1;

    public InvalidUserFieldException() {
        this("");
    }

    public InvalidUserFieldException(String detail) {
        super(DESCRIPTION + ". " + detail, CODE);
    }

}
