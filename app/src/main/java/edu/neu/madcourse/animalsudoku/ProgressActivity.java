package edu.neu.madcourse.animalsudoku;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ProgressActivity extends AppCompatActivity {

    static private int mIconIds[] = {
            R.id.icon1, R.id.icon2, R.id.icon3, R.id.icon4, R.id.icon5, R.id.icon6, R.id.icon7, R.id.icon8, R.id.icon9,
            R.id.icon10, R.id.icon11, R.id.icon12, R.id.icon13, R.id.icon14, R.id.icon15, R.id.icon16, R.id.icon17, R.id.icon18,
            R.id.icon19, R.id.icon20, R.id.icon21, R.id.icon22, R.id.icon23, R.id.icon24, R.id.icon25, R.id.icon26, R.id.icon27,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        SharedPreferences sharedPreferences = getSharedPreferences("progress", MODE_PRIVATE);
        int zooProgress_4x4 = sharedPreferences.getInt(
                GameActivity.THEME_ZOO + "_" + GameActivity.DIFFICULTY_EASY,
                0
        );
        int zooProgress_9x9 = sharedPreferences.getInt(
                GameActivity.THEME_ZOO + "_" + GameActivity.DIFFICULTY_HARD,
                0
        );
        int aquariumProgress_4x4 = sharedPreferences.getInt(
                GameActivity.THEME_AQUARIUM + "_" + GameActivity.DIFFICULTY_EASY,
                0
        );
        int aquariumProgress_9x9 = sharedPreferences.getInt(
                GameActivity.THEME_AQUARIUM + "_" + GameActivity.DIFFICULTY_HARD,
                0
        );
        int birdHabitatProgress_4x4 = sharedPreferences.getInt(
                GameActivity.THEME_BIRD_HABITAT + "_" + GameActivity.DIFFICULTY_EASY,
                0
        );

        // Zoo is always active
        TextView zooLabel = findViewById(R.id.zoo_label);
        zooLabel.setBackgroundColor(getResources().getColor(R.color.red_color));

        // Aquarium is active only if Zoo 9x9 has been finished 3 times
        TextView aquariumLabel = findViewById(R.id.aquarium_label);
        if (zooProgress_9x9 >= 3) {
            aquariumLabel.setBackgroundColor(getResources().getColor(R.color.red_color));
        }

        // Aquarium is active only if Aquarium 9x9 has been finished 3 times
        TextView birdHabitatLabel = findViewById(R.id.bird_habitat_label);
        if (aquariumProgress_9x9 >= 3) {
            birdHabitatLabel.setBackgroundColor(getResources().getColor(R.color.red_color));
        }

        for (int i = 0; i < 9; i++) {
            Tile zooTile = zooProgress_4x4 + 4 < i + 1
                            ? new Tile(i + 1, Tile.status.LOCKED)       // not collected
                            : new Tile(i + 1, Tile.status.FILLED_FAIL); // collected
            zooTile.setView(findViewById(mIconIds[i]));
            zooTile.updateDrawableState();

            Tile aquariumTile = zooProgress_9x9 < 3 || aquariumProgress_4x4 + 4 < i + 1
                    ? new Tile(i + 10, Tile.status.LOCKED)              // not collected
                    : new Tile(i + 10, Tile.status.FILLED_FAIL);        // collected
            aquariumTile.setView(findViewById(mIconIds[i + 9]));
            aquariumTile.updateDrawableState();

            Tile birdHabitatTile = aquariumProgress_9x9 < 3 || birdHabitatProgress_4x4 + 4 < i + 1
                    ? new Tile(i + 19, Tile.status.LOCKED)              // not collected
                    : new Tile(i + 19, Tile.status.FILLED_FAIL);        // collected
            birdHabitatTile.setView(findViewById(mIconIds[i + 18]));
            birdHabitatTile.updateDrawableState();
        }
    }
}
