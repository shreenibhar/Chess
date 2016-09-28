package com.barebringer.chess;

import java.util.Collections;
import java.util.Vector;

public class MoveSet {
    public Vector<ChessPiece> board = new Vector<>(64);

    public MoveSet(Vector<ChessPiece> board) {
        this.board = board;
    }

    public int index(int i, int j) {
        return i * 8 + j;
    }

    public boolean isValid(int i, int j) {
        return i < 8 && j < 8 && i > -1 && j > -1;
    }

    public Vector<Move> allPiece(char color) {
        Vector<Move> moves = new Vector<>(0);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.get(index(i, j)).color == color) {
                    moves.addAll(piece(i, j));
                }
            }
        }
        Collections.shuffle(moves);
        return moves;
    }

    public Vector<Move> piece(int i, int j) {
        Vector<Move> moves = new Vector<>(0);
        switch (board.get(index(i, j)).type) {
            case 'k':
                moves.addAll(king(i, j));
                break;
            case 'q':
                moves.addAll(queen(i, j));
                break;
            case 'b':
                moves.addAll(bishop(i, j));
                break;
            case 'h':
                moves.addAll(horse(i, j));
                break;
            case 'r':
                moves.addAll(rook(i, j));
                break;
            case 'p':
                moves.addAll(pawn(i, j));
                break;
        }
        return moves;
    }

    public Vector<Move> king(int i, int j) {
        char color = board.get(index(i, j)).color;
        Vector<Move> moves = new Vector<>(0);
        if (isValid(i, j + 1) && board.get(index(i, j + 1)).color != color) {
            moves.add(new Move(i, j, i, j + 1));
        }
        if (isValid(i, j - 1) && board.get(index(i, j - 1)).color != color) {
            moves.add(new Move(i, j, i, j - 1));
        }
        if (isValid(i + 1, j) && board.get(index(i + 1, j)).color != color) {
            moves.add(new Move(i, j, i + 1, j));
        }
        if (isValid(i - 1, j) && board.get(index(i - 1, j)).color != color) {
            moves.add(new Move(i, j, i - 1, j));
        }
        if (isValid(i + 1, j + 1) && board.get(index(i + 1, j + 1)).color != color) {
            moves.add(new Move(i, j, i + 1, j + 1));
        }
        if (isValid(i + 1, j - 1) && board.get(index(i + 1, j - 1)).color != color) {
            moves.add(new Move(i, j, i + 1, j - 1));
        }
        if (isValid(i - 1, j + 1) && board.get(index(i - 1, j + 1)).color != color) {
            moves.add(new Move(i, j, i - 1, j + 1));
        }
        if (isValid(i - 1, j - 1) && board.get(index(i - 1, j - 1)).color != color) {
            moves.add(new Move(i, j, i - 1, j - 1));
        }
        if (board.get(index(i, j)).noTouches == 0) {
            if (board.get(index(i, 0)).type == 'r' && board.get(index(i, 0)).noTouches == 0 &&
                    board.get(index(i, 1)).type == ' ' && board.get(index(i, 2)).type == ' ' && board.get(index(i, 3)).type == ' ') {
                Move move = new Move(i, j, i, j - 2);
                move.isCastle = true;
                moves.add(move);
            }
            if (board.get(index(i, 7)).type == 'r' && board.get(index(i, 7)).noTouches == 0 &&
                    board.get(index(i, 5)).type == ' ' && board.get(index(i, 6)).type == ' ') {
                Move move = new Move(i, j, i, j + 2);
                move.isCastle = true;
                moves.add(move);
            }
        }
        return moves;
    }

    public Vector<Move> queen(int i, int j) {
        Vector<Move> moves = new Vector<>(0);
        moves.addAll(bishop(i, j));
        moves.addAll(rook(i, j));
        return moves;
    }

    public Vector<Move> bishop(int i, int j) {
        char color = board.get(index(i, j)).color;
        Vector<Move> moves = new Vector<>(0);
        for (int k = 1; k < 8; k++) {
            if (isValid(i + k, j + k) && board.get(index(i + k, j + k)).color != color) {
                moves.add(new Move(i, j, i + k, j + k));
                if (board.get(index(i + k, j + k)).color != ' ') {
                    break;
                }
            } else break;
        }
        for (int k = 1; k < 8; k++) {
            if (isValid(i + k, j - k) && board.get(index(i + k, j - k)).color != color) {
                moves.add(new Move(i, j, i + k, j - k));
                if (board.get(index(i + k, j - k)).color != ' ') {
                    break;
                }
            } else break;
        }
        for (int k = 1; k < 8; k++) {
            if (isValid(i - k, j + k) && board.get(index(i - k, j + k)).color != color) {
                moves.add(new Move(i, j, i - k, j + k));
                if (board.get(index(i - k, j + k)).color != ' ') {
                    break;
                }
            } else break;
        }
        for (int k = 1; k < 8; k++) {
            if (isValid(i - k, j - k) && board.get(index(i - k, j - k)).color != color) {
                moves.add(new Move(i, j, i - k, j - k));
                if (board.get(index(i - k, j - k)).color != ' ') {
                    break;
                }
            } else break;
        }
        return moves;
    }

    public Vector<Move> horse(int i, int j) {
        char color = board.get(index(i, j)).color;
        Vector<Move> moves = new Vector<>(0);
        if (isValid(i + 2, j + 1) && board.get(index(i + 2, j + 1)).color != color) {
            moves.add(new Move(i, j, i + 2, j + 1));
        }
        if (isValid(i + 2, j - 1) && board.get(index(i + 2, j - 1)).color != color) {
            moves.add(new Move(i, j, i + 2, j - 1));
        }
        if (isValid(i - 2, j + 1) && board.get(index(i - 2, j + 1)).color != color) {
            moves.add(new Move(i, j, i - 2, j + 1));
        }
        if (isValid(i - 2, j - 1) && board.get(index(i - 2, j - 1)).color != color) {
            moves.add(new Move(i, j, i - 2, j - 1));
        }
        if (isValid(i + 1, j + 2) && board.get(index(i + 1, j + 2)).color != color) {
            moves.add(new Move(i, j, i + 1, j + 2));
        }
        if (isValid(i + 1, j - 2) && board.get(index(i + 1, j - 2)).color != color) {
            moves.add(new Move(i, j, i + 1, j - 2));
        }
        if (isValid(i - 1, j + 2) && board.get(index(i - 1, j + 2)).color != color) {
            moves.add(new Move(i, j, i - 1, j + 2));
        }
        if (isValid(i - 1, j - 2) && board.get(index(i - 1, j - 2)).color != color) {
            moves.add(new Move(i, j, i - 1, j - 2));
        }
        return moves;
    }

    public Vector<Move> rook(int i, int j) {
        char color = board.get(index(i, j)).color;
        Vector<Move> moves = new Vector<>(0);
        for (int k = 1; k < 8; k++) {
            if (isValid(i + k, j) && board.get(index(i + k, j)).color != color) {
                moves.add(new Move(i, j, i + k, j));
                if (board.get(index(i + k, j)).color != ' ') {
                    break;
                }
            } else break;
        }
        for (int k = 1; k < 8; k++) {
            if (isValid(i - k, j) && board.get(index(i - k, j)).color != color) {
                moves.add(new Move(i, j, i - k, j));
                if (board.get(index(i - k, j)).color != ' ') {
                    break;
                }
            } else break;
        }
        for (int k = 1; k < 8; k++) {
            if (isValid(i, j + k) && board.get(index(i, j + k)).color != color) {
                moves.add(new Move(i, j, i, j + k));
                if (board.get(index(i, j + k)).color != ' ') {
                    break;
                }
            } else break;
        }
        for (int k = 1; k < 8; k++) {
            if (isValid(i, j - k) && board.get(index(i, j - k)).color != color) {
                moves.add(new Move(i, j, i, j - k));
                if (board.get(index(i, j - k)).color != ' ') {
                    break;
                }
            } else break;
        }
        return moves;
    }

    public Vector<Move> pawn(int i, int j) {
        char color = board.get(index(i, j)).color;
        char ecolor = (color == 'w') ? 'b' : 'w';
        int inc = (color == 'w') ? -1 : 1;
        Vector<Move> moves = new Vector<>(0);
        if (isValid(i + inc, j - 1) && board.get(index(i + inc, j - 1)).color == ecolor) {
            moves.add(new Move(i, j, i + inc, j - 1));
        }
        if (isValid(i + inc, j + 1) && board.get(index(i + inc, j + 1)).color == ecolor) {
            moves.add(new Move(i, j, i + inc, j + 1));
        }
        if (isValid(i + inc, j) && board.get(index(i + inc, j)).color == ' ') {
            moves.add(new Move(i, j, i + inc, j));
            if (isValid(i + 2 * inc, j) && board.get(index(i + 2 * inc, j)).color == ' ') {
                if (i == 1 || i == 6) {
                    moves.add(new Move(i, j, i + 2 * inc, j));
                }
            }
        }
        return moves;
    }
}
