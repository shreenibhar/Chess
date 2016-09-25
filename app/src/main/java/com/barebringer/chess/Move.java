package com.barebringer.chess;

public class Move {
    public int frow = -1, fcol = -1, trow = -1, tcol = -1;
    public int score = -30000;
    public boolean isCastle = false;

    public Move(int frow, int fcol, int trow, int tcol) {
        this.frow = frow;
        this.fcol = fcol;
        this.trow = trow;
        this.tcol = tcol;
        this.score = -30000;
        this.isCastle = false;
    }
}
