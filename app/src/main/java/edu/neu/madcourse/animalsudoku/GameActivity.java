package edu.neu.madcourse.animalsudoku;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    public static final String KEY_DIFFICULTY = "edu.neu.madcourse.animalsodoku.difficulty";
    public static final String KEY_THEME = "edu.neu.madcourse.animalsodoku.theme";

    static private int mLargeIds[] = {R.id.large1, R.id.large2, R.id.large3, R.id.large4, R.id.large5, R.id.large6, R.id.large7, R.id.large8, R.id.large9,};
    static private int mSmallIds[] = {R.id.small1, R.id.small2, R.id.small3, R.id.small4, R.id.small5, R.id.small6, R.id.small7, R.id.small8, R.id.small9,};
    static private int mStockIds[] = {R.id.stock1, R.id.stock2, R.id.stock3, R.id.stock4, R.id.stock5, R.id.stock6, R.id.stock7, R.id.stock8, R.id.stock9};

    private Tile mEntireBoard = null;
    private Tile mLargeTiles[] = new Tile[9];
    private Tile mSmallTiles[][] = new Tile[9][9];
    private AlertDialog mDialog;

    private int puzzleSize;     // 4 or 9
    private Tile stockTiles[];  // tiles on the left for choose
    private int currentNumber;  // selected number
    private Deque<Tile> moves;  // the moves a player has made

    private int theme;
    private int difficulty;

    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_HARD = 1;
    public static final int THEME_ZOO = 0;
    public static final int THEME_AQUARIUM = 1;
    public static final int THEME_BIRD_HABITAT = 2;

    // Pools for generating games
    private final String[][] puzzlePools = {{
            "0001000200030004",
            "0200030041000200",
            "0003000000100020",
    }, {
            "360004000000230004000800200070820500460000013003014020001007000900048000000300045",
            "650000014000506000070000005007002000009314700000700800500000030000201000630000097",
            "009080501000605078000020000000706004000040000700102000000090000720301000903080600",
    }};

    // Randomly select game from pools
    private String getPuzzle() {
        String[] pool = puzzlePools[difficulty];
        int rnd = new Random().nextInt(pool.length);
        return pool[rnd];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Choose 4x4 or 9x9
        difficulty = getIntent().getIntExtra(KEY_DIFFICULTY, DIFFICULTY_EASY);
        if (difficulty == DIFFICULTY_EASY) {
            puzzleSize = 4;
            setContentView(R.layout.activity_game_4x4);
        } else if (difficulty == DIFFICULTY_HARD) {
            puzzleSize = 9;
            setContentView(R.layout.activity_game_9x9);
        } else {
            puzzleSize = 4;
            setContentView(R.layout.activity_game_4x4);
        }

        // Choose background image to use
        theme = getIntent().getIntExtra(KEY_THEME, THEME_ZOO);
        ImageView background = findViewById(R.id.theme_background);
        if (theme == THEME_ZOO) {
            background.setImageResource(R.drawable.background_1);
        } else if (theme == THEME_AQUARIUM) {
            background.setImageResource(R.drawable.background_2);
        } else if (theme == THEME_BIRD_HABITAT) {
            background.setImageResource(R.drawable.background_3);
        } else {
            background.setImageResource(R.drawable.background_1);
        }

        initGame();
        initViews();
        updateAllTiles();
    }

    private void initGame() {
        String puzzle = getPuzzle();
        mEntireBoard = new Tile(this, 0, Tile.status.LOCKED);
        // Create all the tiles
        for (int large = 0; large < puzzleSize; large++) {
            mLargeTiles[large] = new Tile(this, 0, Tile.status.LOCKED);
            for (int small = 0; small < puzzleSize; small++) {
                int number = puzzle.charAt(large * puzzleSize + small) - '0';
                // if number is 0, then it's empty, need to fill
                // if number is not empty, set 1 ~ 9 for Zoo, 10 ~ 18 for aquarium, 19 ~ 27 for bird habitat
                if (number == 0) {
                    mSmallTiles[large][small] = new Tile(this, number, Tile.status.AVAILABLE);
                } else {
                    mSmallTiles[large][small] = new Tile(this, number + theme * 9, Tile.status.LOCKED);
                }
            }
            mLargeTiles[large].setSubTiles(mSmallTiles[large]);
        }
        mEntireBoard.setSubTiles(mLargeTiles);

        // initialize the tiles on the left to choose
        // 1 ~ 9 for Zoo, 10 ~ 18 for aquarium, 19 ~ 27 for bird habitat
        stockTiles = new Tile[puzzleSize];
        for (int i = 0; i < puzzleSize; i++) {
            stockTiles[i] = new Tile(this, i + 1 + theme * 9, Tile.status.NUMBER_NOT_SELECTED);
        }

        currentNumber = 0;
        moves = new ArrayDeque<Tile>();
    }

    private void initViews() {
        View entire = findViewById(R.id.entire);
        mEntireBoard.setView(entire);
        for (int large = 0; large < puzzleSize; large++) {
            View outer = findViewById(mLargeIds[large]);
            mLargeTiles[large].setView(outer);

            for (int small = 0; small < puzzleSize; small++) {
                ImageButton inner = outer.findViewById(mSmallIds[small]);
                final int fLarge = large;
                final int fSmall = small;
                final Tile smallTile = mSmallTiles[large][small];
                smallTile.setView(inner);
                inner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Tile operatedTile = mSmallTiles[fLarge][fSmall];
                        if (currentNumber == 0) {
                            // if use want to fill a empty tile, but has not selected a number first
                            AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                            builder.setMessage(R.string.select_number_first_text);
                            builder.setCancelable(false);
                            builder.setPositiveButton(
                                    R.string.ok_label,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            // nothing
                                        }
                                    }
                            );
                            mDialog = builder.show();
                        } else if (operatedTile.getStatus() != Tile.status.LOCKED) {
                            operatedTile.setNumber(currentNumber);
                            if (checkPass(fLarge, fSmall)) {
                                operatedTile.setStatus(Tile.status.FILLED_PASS);

                                if (checkPass()) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                                    LayoutInflater inflater = getLayoutInflater();
                                    builder.setView(inflater.inflate(R.layout.finish_game, null));
                                    builder.setPositiveButton(
                                            R.string.play_again,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    initGame();
                                                    initViews();
                                                    updateAllTiles();
                                                }
                                            });
                                    builder.setNegativeButton(
                                            R.string.back_to_main,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Intent intent = new Intent(GameActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                }
                                            });
                                    mDialog = builder.show();
                                }
                            } else {
                                operatedTile.setStatus(Tile.status.FILLED_FAIL);
                            }
                            moves.addLast(operatedTile);
                            updateAllTiles();
                        }
                    }
                });
            }
        }

        // if choose one number, deselect other numbers
        for (int i = 0; i < puzzleSize; i++) {
            ImageButton stockButton = findViewById(mStockIds[i]);
            final Tile stockTile = stockTiles[i];
            stockTile.setView(stockButton);
            stockButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentNumber = stockTile.getNumber();
                    for (Tile t : stockTiles) {
                        t.setStatus(Tile.status.NUMBER_NOT_SELECTED);
                    }
                    stockTile.setStatus(Tile.status.NUMBER_SELECTED);
                    updateAllTiles();
                }
            });
        }

        ImageButton revertButton = findViewById(R.id.revert_button);
        revertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moves.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                    builder.setMessage(R.string.no_move_text);
                    builder.setCancelable(false);
                    builder.setPositiveButton(
                            R.string.ok_label,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // nothing
                                }
                            }
                    );
                    mDialog = builder.show();
                } else {
                    Tile revertedTile = moves.pollLast();
                    revertedTile.setNumber(0);
                    revertedTile.setStatus(Tile.status.AVAILABLE);

                    for (Tile t : stockTiles) {
                        t.setStatus(Tile.status.NUMBER_NOT_SELECTED);
                    }
                    currentNumber = 0;

                    updateAllTiles();
                }
            }
        });
    }

    private void updateAllTiles() {
        mEntireBoard.updateDrawableState();
        for (int large = 0; large < puzzleSize; large++) {
            mLargeTiles[large].updateDrawableState();
            for (int small = 0; small < puzzleSize; small++) {
                mSmallTiles[large][small].updateDrawableState();
            }
        }

        for (int i = 0; i < puzzleSize; i++) {
            stockTiles[i].updateDrawableState();
        }
    }

    public boolean checkPass(int l, int s) {
        Tile currentTile = mSmallTiles[l][s];
        for (int small = 0; small < puzzleSize; small++) {
            Tile smallTile = mSmallTiles[l][small];
            if (s != small && smallTile.getNumber() == currentTile.getNumber()) {
                return false;
            }
        }

        int baseSize = (int) Math.sqrt(puzzleSize);
        int largeRow = l / baseSize;
        int smallRow = s / baseSize;

        for (int i = 0; i < baseSize; i++) {
            for (int j = 0; j < baseSize; j++) {
                int large = largeRow * baseSize + i;
                int small = smallRow * baseSize + j;
                Tile smallTile = mSmallTiles[large][small];
                if (l == large && s == small) {
                    continue;
                }
                if (smallTile.getNumber() == currentTile.getNumber()) {
                    return false;
                }
            }
        }

        int largeCol = l % baseSize;
        int smallCol = s % baseSize;
        for (int i = 0; i < baseSize; i++) {
            for (int j = 0; j < baseSize; j++) {
                int large = i * baseSize + largeCol;
                int small = j * baseSize + smallCol;
                Tile smallTile = mSmallTiles[large][small];
                if (l == large && s == small) {
                    continue;
                }
                if (smallTile.getNumber() == currentTile.getNumber()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkPass() {
        for (int large = 0; large < puzzleSize; large++) {
            for (int small = 0; small < puzzleSize; small++) {
                Tile tile = mSmallTiles[large][small];
                if (tile.getStatus() == Tile.status.AVAILABLE || tile.getStatus() == Tile.status.FILLED_FAIL) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();

        // Get rid of the about dialog if it's still up
        if (mDialog != null)
            mDialog.dismiss();
    }
}
