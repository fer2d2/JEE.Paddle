package business.api.exceptions;

public class MaxUsersByTrainingReachedException extends ApiException {

    private static final long serialVersionUID = -1344640670884805385L;

    public static final String DESCRIPTION = "No se admiten más usuarios en la clase especificada";

    public static final int CODE = 1;

    public MaxUsersByTrainingReachedException() {
        this("");
    }

    public MaxUsersByTrainingReachedException(String detail) {
        super(DESCRIPTION + ". " + detail, CODE);
    }

}
