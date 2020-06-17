package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;

public class PokemonsActivity extends AppCompatActivity {
    private static final String LOG_TAG = PokemonsActivity.class.getSimpleName();

    private PokemonsAdapter adapter;
    private PokemonsApi pokemonsApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemons);
        pokemonsApi = new PokemonsApi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        beforeRequest();
        pokemonsApi.getInitialContent(this::handleResponse);
    }

    public void onPrevious(View view) {
        beforeRequest();
        pokemonsApi.getPrevious(this::handleResponse);
    }

    public void onNext(View view) {
        beforeRequest();
        pokemonsApi.getNext(this::handleResponse);
    }

    private void handleResponse(List<Pokemon> pokemons) {
        runOnUiThread(() -> {
            if (adapter == null)
                adapter = new PokemonsAdapter(this, R.layout.activity_pokemons, pokemons);
            else {
                adapter.clear();
                adapter.addAll(pokemons);
            }
            ListView listView = findViewById(R.id.pokemon_list);
            listView.setAdapter(adapter);
            setPaginationButtonState();
            setProgressBarState(false);
            setCurrentPage();
            setTotalItems();
        });
    }

    private void beforeRequest() {
        disableButtonsOnLoad();
        setProgressBarState(true);
    }

    private void setProgressBarState(boolean state) {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.indeterminateBar);
        progressBar.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private void disableButtonsOnLoad() {
        findViewById(R.id.btn_first).setEnabled(false);
        findViewById(R.id.btn_previous).setEnabled(false);
        findViewById(R.id.btn_next).setEnabled(false);
        findViewById(R.id.btn_last).setEnabled(false);
    }

    private void setPaginationButtonState() {
        findViewById(R.id.btn_first).setEnabled(pokemonsApi.hasPrevious());
        findViewById(R.id.btn_previous).setEnabled(pokemonsApi.hasPrevious());
        findViewById(R.id.btn_next).setEnabled(pokemonsApi.hasNext());
        findViewById(R.id.btn_last).setEnabled(pokemonsApi.hasNext());
    }

    private void setTotalItems() {
        String str = pokemonsApi.getTotalItems() + " Pokemons";
        ((TextView) findViewById(R.id.tv_total_itens)).setText(str);

    }

    private void setCurrentPage() {
        String str = "Page " + pokemonsApi.getCurrentPage() +
                " of " + pokemonsApi.getTotalPages();
        ((TextView) findViewById(R.id.tv_current_page)).setText(str);
    }
}