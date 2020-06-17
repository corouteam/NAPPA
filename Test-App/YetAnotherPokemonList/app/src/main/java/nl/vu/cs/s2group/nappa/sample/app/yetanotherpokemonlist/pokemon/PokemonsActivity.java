package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.Config;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.http.SingletonOkHttpClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PokemonsActivity extends AppCompatActivity {
    private static final String LOG_TAG = PokemonsActivity.class.getSimpleName();
    private static final String API_URL = Config.API_URL + "pokemon/";

    private PokemonsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemons);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Request request = new Request.Builder()
                .url(API_URL)
                .build();

        SingletonOkHttpClient.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(LOG_TAG, Log.getStackTraceString(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                ResponseBody body = Objects.requireNonNull(response.body());
                handleResponse(new Gson().fromJson(body.charStream(), PokemonsWrapper.class));
            }
        });
    }

    private void handleResponse(PokemonsWrapper response) {
        Log.d(LOG_TAG, "Fetched pokemons: " + response.getResults().toString());
        runOnUiThread(() -> {
            if (adapter == null)
                adapter = new PokemonsAdapter(this, R.layout.activity_pokemons, response.getResults());
            else adapter.clear();
            ListView listView = findViewById(R.id.pokemon_list);
            listView.setAdapter(adapter);
        });
    }
}