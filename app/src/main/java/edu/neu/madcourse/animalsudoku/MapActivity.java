package edu.neu.madcourse.animalsudoku;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MapActivity extends AppCompatActivity {
    private static final String TAG = "Animal Sudoku";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        View zooButton = findViewById(R.id.zoo_button);
        zooButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewGameDialog(0);
            }
        });

        View aquariumButton = findViewById(R.id.aquarium_button);
        aquariumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewGameDialog(1);
            }
        });

        View birdHabitatButton = findViewById(R.id.bird_habitat_button);
        birdHabitatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewGameDialog(2);
            }
        });
    }

    /** Ask the user what difficulty level they want */
    private void openNewGameDialog(final int theme) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.new_game_title)
                .setItems(R.array.difficulty,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                startGame(theme, i);
                            }
                        })
                .show();
    }

    /** Start a new game with the given difficulty level */
    private void startGame(int theme, int difficulty) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(GameActivity.KEY_THEME, theme);
        intent.putExtra(GameActivity.KEY_DIFFICULTY, difficulty);
        startActivity(intent);
    }
}
