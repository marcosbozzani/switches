package duck.switches.android.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SwitchMessage {
    public final static String path = "zeroconf/switch";

    public static class Request {
        public Data data = new Data();

        public static class Data {
            @JsonProperty("switch")
            public String aSwtich = "";
        }
    }

    public static class Response {
        public int seq = 0;
        public int error = 0;
        public EmptyData data = new EmptyData();
    }
}
