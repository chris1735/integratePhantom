package phantom.basics;

import com.google.gson.annotations.Expose;

import java.util.UUID;

/**
 * Created by Administrator on 2020/11/6 17:11.
 */
public class WayPointJson {
    @Expose
    String id;
    @Expose
    int order;
    @Expose
    double latitude;
    @Expose
    double longitude;

    @Override
    public String toString() {
        return "WayPoint{" +
                "id='" + id + '\'' +
                ", order=" + order +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
