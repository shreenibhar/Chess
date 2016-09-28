package com.barebringer.chess;

public class ChessPiece {
    public char type = ' ', color = ' ';
    public int noTouches = 0;

    public ChessPiece() {
        type = ' ';
        color = ' ';
        noTouches = 0;
    }

    public ChessPiece(char type, char color) {
        this.type = type;
        this.color = color;
        noTouches = 0;
    }

    public ChessPiece(char type, char color, int noTouches) {
        this.type = type;
        this.color = color;
        this.noTouches = noTouches;
    }
}
