package edu.neu.madcourse.animalsudoku;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MapActivity extends AppCompatActivity {
    private static final String TAG = "Animal Sudoku";

    private int zooProgress_4x4, zooProgress_9x9, aquariumProgress_4x4, aquariumProgress_9x9, birdHabitatProgress_4x4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SharedPreferences sharedPreferences = getSharedPreferences("progress", MODE_PRIVATE);
        zooProgress_4x4 = sharedPreferences.getInt(
                GameActivity.THEME_ZOO + "_" + GameActivity.DIFFICULTY_EASY,
                0
        );
        zooProgress_9x9 = sharedPreferences.getInt(
                GameActivity.THEME_ZOO + "_" + GameActivity.DIFFICULTY_HARD,
                0
        );
        aquariumProgress_4x4 = sharedPreferences.getInt(
                GameActivity.THEME_AQUARIUM + "_" + GameActivity.DIFFICULTY_EASY,
                0
        );
        aquariumProgress_9x9 = sharedPreferences.getInt(
                GameActivity.THEME_AQUARIUM + "_" + GameActivity.DIFFICULTY_HARD,
                0
        );
        birdHabitatProgress_4x4 = sharedPreferences.getInt(
                GameActivity.THEME_BIRD_HABITAT + "_" + GameActivity.DIFFICULTY_EASY,
                0
        );

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
                if (zooProgress_9x9 < 3) {
                    openAlertDialog("You need to play Zoo more to unlock Aquarium");
                } else {
                    openNewGameDialog(1);
                }
            }
        });

        View birdHabitatButton = findViewById(R.id.bird_habitat_button);
        birdHabitatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aquariumProgress_9x9 < 3) {
                    openAlertDialog("You need to play Aquarium more to unlock Bird Habitat");
                } else {
                    openNewGameDialog(2);
                }
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
                                if (i == GameActivity.DIFFICULTY_HARD) {
                                    if (theme == GameActivity.THEME_ZOO && zooProgress_4x4 < 5) {
                                        openAlertDialog("Play more 4x4 Zoo to unlock 9x9");
                                    } else if (theme == GameActivity.THEME_AQUARIUM && aquariumProgress_4x4 < 5) {
                                        openAlertDialog("Play more 4x4 Aquarium to unlock 9x9");
                                    } else if (theme == GameActivity.THEME_BIRD_HABITAT && birdHabitatProgress_4x4 < 5) {
                                        openAlertDialog("Play more 4x4 Bird Habitat to unlock 9x9");
                                    } else {
                                        startGame(theme, i);
                                    }
                                } else {
                                    startGame(theme, i);
                                }
                            }
                        })
                .show();
    }

    private void openAlertDialog(String msg) {
        new AlertDialog.Builder(this)
                .setMessage(msg)
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
