package scripts.dax_api.api_lib.models.exceptions;

import com.allatori.annotations.DoNotRename;

@DoNotRename
public class RateLimitException extends RuntimeException {
    public RateLimitException(String message) {
        super(message);
    }
}
