package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokeapi.DefaultAdapter;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokeapi.DefaultApiModel;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.type.TypesResponseWrapper;

public class PokemonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);
        PokemonApi.makeRequest(getIntent().getStringExtra("url"), this::handleRequest);
    }

    private void handleRequest(Pokemon pokemon) {
        setPageTitle(pokemon.name);
        setPokemonTypes(pokemon.types);
    }

    private void setPageTitle(String pokemonName) {
        ((TextView) findViewById(R.id.page_title)).setText(pokemonName);
    }

    private void setPokemonTypes(List<TypesResponseWrapper> wrapper) {
        List<DefaultApiModel> types = new ArrayList<>();
        for (TypesResponseWrapper typesResponseWrapper : wrapper) {
            types.add(typesResponseWrapper.getType());
        }
        runOnUiThread(() -> {
            DefaultAdapter adapter = new DefaultAdapter(this, R.layout.activity_pokemon, types);
            ListView listView = findViewById(R.id.lv_types);
            listView.setAdapter(adapter);
        });
    }
}