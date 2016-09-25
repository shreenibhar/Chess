package com.barebringer.chess;

public class ChessPiece {
    public char type = ' ', color = ' ';
    public boolean touched = false;

    public ChessPiece() {
        type = ' ';
        color = ' ';
        touched = false;
    }

    public ChessPiece(char type, char color) {
        this.type = type;
        this.color = color;
        this.touched = false;
    }

    public ChessPiece(char type, char color, boolean touched) {
        this.type = type;
        this.color = color;
        this.touched = touched;
    }
}
