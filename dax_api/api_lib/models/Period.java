package scripts.dax_api.api_lib.models;

import com.allatori.annotations.DoNotRename;

@DoNotRename
public enum Period {
    PAST_HOUR,
    PAST_6_HOURS,
    PAST_DAY,
    PAST_WEEK,
    PAST_MONTH,
    PAST_YEAR,
    LIFE_TIME,
}
