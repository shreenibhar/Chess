package com.barebringer.chess;

public class Move {
    public int frow = -1, fcol = -1, trow = -1, tcol = -1;
    public int score = -30000, noRemoved = 0;
    public boolean isCastle = false;

    public Move(int frow, int fcol, int trow, int tcol) {
        this.frow = frow;
        this.fcol = fcol;
        this.trow = trow;
        this.tcol = tcol;
        score = -30000;
        isCastle = false;
        noRemoved = 0;
    }
}
