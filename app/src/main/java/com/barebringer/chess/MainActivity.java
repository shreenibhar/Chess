package com.barebringer.chess;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.NumberPicker;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public ChessBoard board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        board = (ChessBoard) findViewById(R.id.main_board_chessboard);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.custom_menu, menu);
        return true;
    }

    public void save() {
        SharedPreferences sharedPreferences = getSharedPreferences("Chess", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String state = board.getBoardState();
        if (state.isEmpty()) return;
        editor.putString("BoardState", state);
        editor.apply();
        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
    }

    public void load() {
        SharedPreferences sharedPreferences = getSharedPreferences("Chess", MODE_PRIVATE);
        String state = sharedPreferences.getString("BoardState", "");
        if (state.equals("")) {
            getInput("");
            return;
        }
        board.setBoardState(state);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                board.newBoard();
                getInput("");
                SharedPreferences sharedPreferences = getSharedPreferences("Chess", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                return true;
            case R.id.action_depth:
                getInput(0);
                return true;
            case R.id.action_undo:
                board.undoMove();
                board.undoMove();
                board.render();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getInput(String temp) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Choose Sides");
        alert.setPositiveButton("Black", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                board.player = 'b';
                board.cpuSimulation();
            }
        });
        alert.setNegativeButton("White", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                board.player = 'w';
                board.cpuSimulation();
            }
        });
        alert.setCancelable(false);
        alert.show();
    }

    public void getInput(int temp) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(5);
        alert.setView(numberPicker);
        alert.setTitle("Choose Depth");
        alert.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                board.DEPTH = numberPicker.getValue();
                Toast.makeText(getApplicationContext(), "DEPTH = " + board.DEPTH, Toast.LENGTH_SHORT).show();
                board.cpuSimulation();
            }
        });
        alert.setNegativeButton("Default", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                board.DEPTH = 4;
                Toast.makeText(getApplicationContext(), "default DEPTH = " + board.DEPTH, Toast.LENGTH_SHORT).show();
                board.cpuSimulation();
            }
        });
        alert.setCancelable(false);
        alert.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        save();
    }

    @Override
    protected void onStart() {
        super.onStart();
        load();
    }
}
