package scripts.dax_api.api_lib.models.exceptions;

import com.allatori.annotations.DoNotRename;

@DoNotRename
public class UnknownException extends RuntimeException {
    public UnknownException(String message) {
        super(message);
    }
}
