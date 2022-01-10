package duck.switches.android.model;

public class StartupMessage {
    public final static String path = "zeroconf/startup";

    public static class Request {
        public Data data = new Data();

        public static class Data {
            public String startup = "";
        }
    }

    public static class Response {
        public int seq = 0;
        public int error = 0;
        public EmptyData data = new EmptyData();
    }
}
