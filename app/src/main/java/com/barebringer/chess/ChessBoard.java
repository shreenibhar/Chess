package com.barebringer.chess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Vector;

public class ChessBoard extends View {
    public Vector<ChessPiece> board;
    public Vector<Bitmap> sprites;
    public Vector<Move> highlightMoves;
    public Vector<Move> undoMoveStack;
    public Vector<ChessPiece> undoPieceStack;
    public Bitmap boardImage;
    public Move clickedMove;
    int DEPTH = 4;
    public char turn = 'w', player;
    public boolean isSpriteLoaded = false;

    public ChessBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        newBoard();
    }

    public void newBoard() {
        board = new Vector<>(64);
        sprites = new Vector<>(12);
        highlightMoves = new Vector<>(0);
        undoMoveStack = new Vector<>(0);
        undoPieceStack = new Vector<>(0);
        clickedMove = new Move(-1, -1, -1, -1);
        isSpriteLoaded = false;
        turn = 'w';
        DEPTH = 4;
        for (int i = 0; i < 64; i++) board.add(new ChessPiece());
        for (int i = 0; i < 12; i++) sprites.add(boardImage);
        setInitialColors();
        setInitialPieces();
        render();
    }

    public int index(int i, int j) {
        assert i > -1 && j > -1 && i < 8 && j < 8;
        return i * 8 + j;
    }

    public void setInitialColors() {
        for (int i = 0; i < 8; i++) {
            board.get(index(0, i)).color = board.get(index(1, i)).color = 'b';
            board.get(index(6, i)).color = board.get(index(7, i)).color = 'w';
        }
    }

    public void setInitialPieces() {
        board.get(index(0, 4)).type = board.get(index(7, 4)).type = 'k';
        board.get(index(0, 3)).type = board.get(index(7, 3)).type = 'q';
        board.get(index(0, 2)).type = board.get(index(0, 5)).type = board.get(index(7, 2)).type = board.get(index(7, 5)).type = 'b';
        board.get(index(0, 1)).type = board.get(index(0, 6)).type = board.get(index(7, 1)).type = board.get(index(7, 6)).type = 'h';
        board.get(index(0, 0)).type = board.get(index(0, 7)).type = board.get(index(7, 0)).type = board.get(index(7, 7)).type = 'r';
        for (int i = 0; i < 8; i++) board.get(index(1, i)).type = board.get(index(6, i)).type = 'p';
    }

    public String getBoardState() {
        String state = "";
        state += Character.toString(player) + ":" + Character.toString(turn) + ":" + DEPTH + ":";
        for (int i = 0; i < board.size(); i++) {
            state += Character.toString(board.get(i).type) + "-" + Character.toString(board.get(i).color) + "-" +
                    board.get(i).noTouches + ",";
        }
        return state;
    }

    public void setBoardState(String state) {
        String[] split = state.split(":");
        player = split[0].charAt(0);
        turn = split[1].charAt(0);
        DEPTH = Integer.parseInt(split[2]);
        String[] boardString = split[3].split(",");
        for (int i = 0; i < board.size(); i++) {
            String[] pieceString = boardString[i].split("-");
            board.set(i,
                    new ChessPiece(
                            pieceString[0].charAt(0),
                            pieceString[1].charAt(0),
                            Integer.parseInt(pieceString[2])
                    )
            );
        }
        render();
        cpuSimulation();
    }

    public void cpuSimulation() {
        if (turn == player) return;
        Move optMove = optimumMove(turn, 1, 30000);
        undoPieceStack.addAll(movePiece(optMove));
        undoMoveStack.add(optMove);
        checkCheck(turn);
        winCheck(turn, true);
        turn = (turn == 'w') ? 'b' : 'w';
        render();
    }

    public Move optimumMove(char t, int depth, int alpha) {
        Move maxMove = new Move(-1, -1, -1, -1);
        int score;
        char ot = (t == 'w') ? 'b' : 'w';
        Vector<Move> moves = new MoveSet(board).allPiece(t);
        for (int i = 0; i < moves.size(); i++) {
            Vector<ChessPiece> backup = movePiece(moves.get(i));
            score = getScore(backup);
            if (!winCheck(t, false) && depth < DEPTH)
                score -= optimumMove(ot, depth + 1, score - maxMove.score).score;
            restorePiece(moves.get(i), backup);
            if (score > maxMove.score) {
                maxMove = moves.get(i);
                maxMove.score = score;
            }
            if (maxMove.score >= alpha)
                return maxMove;
        }
        return maxMove;
    }

    public boolean winCheck(char t, boolean check) {
        boolean tKing = false;
        boolean otKing = false;
        char ot = (t == 'w') ? 'b' : 'w';
        for (int i = 0; i < board.size(); i++) {
            if (board.get(i).type == 'k') {
                if (board.get(i).color == t) tKing = true;
                else if (board.get(i).color == ot) otKing = true;
            }
        }
        if (tKing && !otKing) {
            if (check) {
                Toast.makeText(getContext(), Character.toString(t) + " won", Toast.LENGTH_LONG).show();
                newBoard();
            }
            return true;
        } else if (!tKing && otKing) {
            if (check) {
                Toast.makeText(getContext(), Character.toString(ot) + " won", Toast.LENGTH_LONG).show();
                newBoard();
            }
            return true;
        } else return false;
    }

    public void checkCheck(char t) {
        char ot = (t == 'w') ? 'b' : 'w';
        Vector<Move> moves = new MoveSet(board).allPiece(t);
        for (int i = 0; i < moves.size(); i++) {
            int row = moves.get(i).trow, col = moves.get(i).tcol;
            if (board.get(index(row, col)).type == 'k' && board.get(index(row, col)).color == ot) {
                Toast.makeText(getContext(), "Check", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }

    public void undoMove() {
        if (turn != player) return;
        if (undoMoveStack.isEmpty()) {
            Toast.makeText(getContext(), "No more moves", Toast.LENGTH_LONG).show();
            return;
        }
        Move undoMove = undoMoveStack.get(undoMoveStack.size() - 1);
        undoMoveStack.remove(undoMoveStack.size() - 1);
        Vector<ChessPiece> undoPiece = new Vector<>(0);
        for (int i = 0; i < undoMove.noRemoved; i++) {
            undoPiece.add(undoPieceStack.get(undoPieceStack.size() - 1));
            undoPieceStack.remove(undoPieceStack.size() - 1);
        }
        for (int i = 0; i < undoPiece.size() / 2; i++) {
            ChessPiece temp = undoPiece.get(i);
            undoPiece.set(i, undoPiece.get(undoPiece.size() - i - 1));
            undoPiece.set(undoPiece.size() - i - 1, temp);
        }
        restorePiece(undoMove, undoPiece);
    }

    public Vector<ChessPiece> movePiece(Move move) {
        Vector<ChessPiece> backup = new Vector<>(0);
        ChessPiece fromPiece = new ChessPiece(
                board.get(index(move.frow, move.fcol)).type,
                board.get(index(move.frow, move.fcol)).color,
                board.get(index(move.frow, move.fcol)).noTouches
        );
        ChessPiece toPiece = new ChessPiece(
                board.get(index(move.trow, move.tcol)).type,
                board.get(index(move.trow, move.tcol)).color,
                board.get(index(move.trow, move.tcol)).noTouches
        );
        backup.add(fromPiece);
        backup.add(toPiece);
        board.set(index(move.trow, move.tcol),
                new ChessPiece(
                        board.get(index(move.frow, move.fcol)).type,
                        board.get(index(move.frow, move.fcol)).color,
                        board.get(index(move.frow, move.fcol)).noTouches + 1
                )
        );
        board.set(index(move.frow, move.fcol), new ChessPiece());
        pawnTransformHandler(move);
        backup.addAll(castleMoveHandler(move));
        return backup;
    }

    public void restorePiece(Move move, Vector<ChessPiece> backup) {
        board.set(index(move.frow, move.fcol), backup.get(0));
        backup.remove(0);
        board.set(index(move.trow, move.tcol), backup.get(0));
        backup.remove(0);
        if (move.isCastle) {
            if (move.tcol > 4) {
                board.set(index(move.trow, 7), backup.get(0));
                backup.remove(0);
                board.set(index(move.trow, move.tcol - 1), backup.get(0));
                backup.remove(0);
            } else if (move.tcol < 4) {
                board.set(index(move.trow, 0), backup.get(0));
                backup.remove(0);
                board.set(index(move.trow, move.tcol + 1), backup.get(0));
                backup.remove(0);
            }
        }
        assert backup.size() == 0;
    }

    public void pawnTransformHandler(Move move) {
        if (board.get(index(move.trow, move.tcol)).type == 'p' && (move.trow == 0 || move.trow == 7)) {
            board.set(index(move.trow, move.tcol),
                    new ChessPiece(
                            'q',
                            board.get(index(move.trow, move.tcol)).color,
                            board.get(index(move.trow, move.tcol)).noTouches
                    )
            );
        }
    }

    public Vector<ChessPiece> castleMoveHandler(Move move) {
        Vector<ChessPiece> backup = new Vector<>(0);
        if (move.isCastle) {
            if (move.tcol > 4) {
                ChessPiece fromPiece = new ChessPiece(
                        board.get(index(move.trow, 7)).type,
                        board.get(index(move.trow, 7)).color,
                        board.get(index(move.trow, 7)).noTouches
                );
                ChessPiece toPiece = new ChessPiece(
                        board.get(index(move.trow, move.tcol - 1)).type,
                        board.get(index(move.trow, move.tcol - 1)).color,
                        board.get(index(move.trow, move.tcol - 1)).noTouches
                );
                backup.add(fromPiece);
                backup.add(toPiece);
                board.set(index(move.trow, move.tcol - 1),
                        new ChessPiece(
                                board.get(index(move.trow, 7)).type,
                                board.get(index(move.trow, 7)).color,
                                board.get(index(move.trow, 7)).noTouches + 1
                        )
                );
                board.set(index(move.trow, 7), new ChessPiece());
            } else if (move.tcol < 4) {
                ChessPiece fromPiece = new ChessPiece(
                        board.get(index(move.trow, 0)).type,
                        board.get(index(move.trow, 0)).color,
                        board.get(index(move.trow, 0)).noTouches
                );
                ChessPiece toPiece = new ChessPiece(
                        board.get(index(move.trow, move.tcol + 1)).type,
                        board.get(index(move.trow, move.tcol + 1)).color,
                        board.get(index(move.trow, move.tcol + 1)).noTouches
                );
                backup.add(fromPiece);
                backup.add(toPiece);
                board.set(index(move.trow, move.tcol + 1),
                        new ChessPiece(
                                board.get(index(move.trow, 0)).type,
                                board.get(index(move.trow, 0)).color,
                                board.get(index(move.trow, 0)).noTouches + 1
                        )
                );
                board.set(index(move.trow, 0), new ChessPiece());
            }
        }
        return backup;
    }

    public int isValidMove(Move move) {
        for (int i = 0; i < highlightMoves.size(); i++) {
            if (move.frow == highlightMoves.get(i).frow && move.fcol == highlightMoves.get(i).fcol &&
                    move.trow == highlightMoves.get(i).trow && move.tcol == highlightMoves.get(i).tcol)
                return i;
        }
        return -1;
    }

    public void render() {
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = canvas.getWidth(), height = canvas.getHeight();
        if (!isSpriteLoaded) {
            isSpriteLoaded = true;
            setScaledSprites(width, height);
        }
        canvas.drawBitmap(boardImage, 0, 0, null);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.get(index(j, i)).type != ' ') {
                    int ind = getSpriteIndex(board.get(index(j, i)));
                    canvas.drawBitmap(sprites.get(ind),
                            i * width / 8, j * height / 8, null);
                }
            }
        }
        for (int i = 0; i < highlightMoves.size(); i++) {
            float left = highlightMoves.get(i).tcol * width / 8;
            float top = highlightMoves.get(i).trow * height / 8;
            float right = left + width / 8;
            float bottom = top + height / 8;
            Paint paint = new Paint();
            paint.setARGB(127, 255, 0, 0);
            canvas.drawRect(left, top, right, bottom, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX() / getWidth(), y = event.getY() / getHeight();
        char ecolor = (player == 'w') ? 'b' : 'w';
        int row = (int) (y * 8), col = (int) (x * 8);
        if (turn == ecolor) return true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                playerFiniteState(row, col);
                break;
        }
        return true;
    }

    public void playerFiniteState(int row, int col) {
        if (clickedMove.frow == -1 && clickedMove.fcol == -1 &&
                clickedMove.trow == -1 && clickedMove.tcol == -1) {
            if (board.get(index(row, col)).color != player) return;
            clickedMove.frow = row;
            clickedMove.fcol = col;
            highlightMoves = new MoveSet(board).piece(row, col);
            render();
        } else if (clickedMove.frow != -1 && clickedMove.fcol != -1 &&
                clickedMove.trow == -1 && clickedMove.tcol == -1) {
            if (clickedMove.frow == row && clickedMove.fcol == col) {
                clickedMove.frow = -1;
                clickedMove.fcol = -1;
            } else if (isValidMove(new Move(clickedMove.frow, clickedMove.fcol, row, col)) > -1) {
                clickedMove.trow = row;
                clickedMove.tcol = col;
                clickedMove.isCastle = highlightMoves.get(
                        isValidMove(
                                new Move(clickedMove.frow, clickedMove.fcol, row, col)
                        )
                ).isCastle;
                undoPieceStack.addAll(movePiece(clickedMove));
                undoMoveStack.add(clickedMove);
                checkCheck(turn);
                winCheck(turn, true);
                clickedMove = new Move(-1, -1, -1, -1);
                turn = (turn == 'w') ? 'b' : 'w';
                cpuSimulation();
            } else {
                clickedMove.frow = -1;
                clickedMove.fcol = -1;
            }
            highlightMoves = new Vector<>(0);
            render();
        }
    }

    public int getScore(Vector<ChessPiece> backup) {
        int score = 0;
        for (int i = 1; i < backup.size(); i += 2) {
            switch (backup.get(i).type) {
                case 'k':
                    score += DEPTH * 10;
                    break;
                case 'q':
                    score += 9;
                    break;
                case 'b':
                    score += 3;
                    break;
                case 'h':
                    score += 3;
                    break;
                case 'r':
                    score += 5;
                    break;
                case 'p':
                    score += 1;
                    break;
                default:
                    break;
            }
        }
        return score;
    }

    public int getSpriteIndex(ChessPiece piece) {
        char type = piece.type, color = piece.color;
        int ind = 0;
        switch (type) {
            case 'k':
                ind = 0;
                break;
            case 'q':
                ind = 1;
                break;
            case 'b':
                ind = 2;
                break;
            case 'h':
                ind = 3;
                break;
            case 'r':
                ind = 4;
                break;
            case 'p':
                ind = 5;
                break;
        }
        if (color == 'b') ind += 6;
        return ind;
    }

    public void setScaledSprites(float width, float height) {
        boardImage = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.board),
                (int) width, (int) height, true
        );
        sprites.set(0, Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.kw),
                (int) width / 8, (int) height / 8, true
        ));
        sprites.set(1, Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.qw),
                (int) width / 8, (int) height / 8, true
        ));
        sprites.set(2, Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.bw),
                (int) width / 8, (int) height / 8, true
        ));
        sprites.set(3, Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.hw),
                (int) width / 8, (int) height / 8, true
        ));
        sprites.set(4, Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.rw),
                (int) width / 8, (int) height / 8, true
        ));
        sprites.set(5, Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.pw),
                (int) width / 8, (int) height / 8, true
        ));
        sprites.set(6, Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.kb),
                (int) width / 8, (int) height / 8, true
        ));
        sprites.set(7, Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.qb),
                (int) width / 8, (int) height / 8, true
        ));
        sprites.set(8, Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.bb),
                (int) width / 8, (int) height / 8, true
        ));
        sprites.set(9, Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.hb),
                (int) width / 8, (int) height / 8, true
        ));
        sprites.set(10, Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.rb),
                (int) width / 8, (int) height / 8, true
        ));
        sprites.set(11, Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.pb),
                (int) width / 8, (int) height / 8, true
        ));
    }
}
