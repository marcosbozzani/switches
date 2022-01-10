package duck.switches.android.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.net.SocketException;
import java.util.Objects;

import duck.switches.android.model.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpService {
    private static final ObjectMapper mapper;
    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    static {
        mapper = new ObjectMapper(new JsonFactory());
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public static <T> void post(String address, String path, Object content, Class<T> responseType,
                                Callback<T> success, Callback<Throwable> error) {
        RequestBody body = RequestBody.create(serialize(content), JSON);
        Request request = new Request.Builder()
                .url(String.format("http://%s/%s", address, path))
                .post(body)
                .build();
        Result<String> result = makeCall(request);
        if (result.ok) {
            success.execute(deserialize(result.success, responseType));
        } else {
            error.execute(result.error);
        }
    }

    private static Result<String> makeCall(Request request) {
        for (int tries = 0; tries < 5; tries++) {
            try (Response response = client.newCall(request).execute()) {
                return new Result<>(Objects.requireNonNull(response.body()).string());
            } catch (Exception e) {
                if (filter(e, SocketException.class, "Connection reset")) {
                    try {
                        Thread.sleep(tries * 100);
                    } catch (InterruptedException ignored) {
                    }
                } else {
                    return new Result<>(e);
                }
            }
        }
        return new Result<>(new RuntimeException("max post retries reached"));
    }

    private static String serialize(Object data) {
        try {
            String result = mapper.writeValueAsString(data);
            return result;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T deserialize(String data, Class<T> type) {
        try {
            T result = mapper.readValue(data, type);
            return result;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean filter(Exception e, Class<?> type, String message) {
        return e.getClass().isAssignableFrom(type) && Objects.equals(e.getMessage(), message);
    }

    private static class Result<S> {
        S success;
        Throwable error;
        boolean ok;

        public Result(S success) {
            this.ok = true;
            this.success = success;
        }

        public Result(Throwable error) {
            this.ok = false;
            this.error = error;
        }
    }
}
