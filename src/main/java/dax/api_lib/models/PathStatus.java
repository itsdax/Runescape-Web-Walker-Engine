package dax.api_lib.models;


import com.allatori.annotations.DoNotRename;

@DoNotRename
public enum PathStatus {
    UNMAPPED_REGION(0),
    SUCCESS(0),
    BLOCKED(0),
    EXCEEDED_SEARCH_LIMIT(0),
    UNREACHABLE(0),
    NO_WEB_PATH(0),

    INVALID_CREDENTIALS(3000),
    RATE_LIMIT_EXCEEDED(3000),


    //OFFLINE
    NO_RESPONSE_FROM_SERVER(5000),

    // Server Issue
    BAD_RESPONSE_FROM_SERVER(5000),

    //WHAT IS GOING ON
    UNKNOWN(5000);

    // Backoff duration in milliseconds
    private long backoffDuration;

    PathStatus(long backoffDuration) {
        this.backoffDuration = backoffDuration;
    }

    public boolean shouldBackOff() {
        return backoffDuration > 0;
    }

    public long generateBackoffDuration() {
        if (backoffDuration == 0) {
            return 0;
        }

        return (long) (backoffDuration * (0.5 + Math.random()));
    }

}
