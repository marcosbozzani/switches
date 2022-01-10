package duck.switches.android.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceInfoMessage {
    public final static String path = "zeroconf/info";

    public static class Request {
        public EmptyData data = new EmptyData();
    }

    public static class Response {
        public int seq = 0;
        public int error = 0;
        public Data data = new Data();

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Data {
            @JsonProperty("switch")
            public String aSwitch = "";
            public String startup = "";
            public String ssid = "";
            public String fwVersion = "";
            public String deviceid = "";
        }
    }
}