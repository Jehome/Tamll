package tmall.util;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by 15001 on 2017/7/23.
 */
public class DateUtil {

    public static Timestamp dateToTimeStamp(Date date) {
        if(date == null){
            return  null;
        }
        return  new Timestamp(date.getTime());
    }

    public static Date timestampToDate(Timestamp timestamp) {
        if(timestamp == null){
            return  null;
        }
        return new Date(timestamp.getTime());
    }
}
