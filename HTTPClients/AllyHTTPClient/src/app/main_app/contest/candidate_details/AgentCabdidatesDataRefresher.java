package app.main_app.contest.candidate_details;
import DTO.DTOCandidate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import http.HttpClientUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static app.utils.AppConstants.BASE_URL;

public class AgentCabdidatesDataRefresher extends TimerTask {
    private final Consumer<String> httpRequestLoggerConsumer;
    private final Consumer<List<DTOCandidate>> usersListConsumer;
    private int requestNumber;

    public AgentCabdidatesDataRefresher(Consumer<String> httpRequestLoggerConsumer, Consumer<List<DTOCandidate>> usersListConsumer) {
        this.httpRequestLoggerConsumer = httpRequestLoggerConsumer;
        this.usersListConsumer = usersListConsumer;
        requestNumber = 0;
    }

    @Override
    public void run() {

        final int finalRequestNumber = ++requestNumber;
        httpRequestLoggerConsumer.accept("About to invoke: " + " | Users Request # " + finalRequestNumber);
        HttpClientUtil.runAsync(BASE_URL + "/get_all_candidate", new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                httpRequestLoggerConsumer.accept("Users Request # " + finalRequestNumber + " | Ended with failure...");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                assert response.body() != null;
                if(response.isSuccessful()) {
                    List<DTOCandidate> fromJsonToCollection = new Gson().fromJson(response.body().string(), new TypeToken<List<DTOCandidate>>() {}.getType());
                    if (!fromJsonToCollection.isEmpty())
                        usersListConsumer.accept(fromJsonToCollection);
                }
            }
        });
    }
}
