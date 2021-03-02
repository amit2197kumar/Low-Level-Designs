import lombok.Getter;
import lombok.Setter;

import java.util.List;

enum GameStatus {
    ACTIVE, BLACK_WIN, WHITE_WIN, RESIGNATION
}

enum AccountStatus {
    ACTIVE, INACTIVE, BLACKLISTED
}

/* New Account Created */
@Getter
@Setter
class Person {
    private String name;
    private String email;
    private String phone;
}

@Getter
@Setter
class Player {
    private Person person;
    private boolean whiteSide = false;
}

/* Single BOX of 8 * 8 Board Chess */
@Getter
@Setter
class Box {
    private int x;
    private int y;
    private Piece piece;

    public Box(int x, int y, Piece piece) {
        this.x = x;
        this.y = y;
        this.piece = piece;
    }
}

/* An abstract class of all Chess Pieces */
@Getter
@Setter
abstract class Piece {
    private boolean killed = false;
    private boolean white = false;

    public Piece(boolean white) {
        this.setWhite(white);
    }

    /* All Pieces need to implement their own valid moves check as per Chess Rules */
    public abstract boolean canMove(Board board, Box start, Box end);
}

/* 8 * 8 Board Chess */
@Getter
@Setter
class Board {
    Box[][] boxes;

    public Board() {
        this.setBoard();
    }

    public Box getBox(int x, int y) throws Exception {
        if (x<0 || x >7 || y<0 || y>7) {
            throw new Exception("Index out of bound");
        }
        return boxes[x][y];
    }

    public void setBoard() {
        /******************************* Initialize White Pieces **********************************/
        boxes[0][0] = new Box(0, 0, new Rook(true));
        boxes[0][1] = new Box(0, 1, new Knight(true));
        boxes[0][2] = new Box(0, 2, new Bishop(true));
        boxes[0][3] = new Box(0, 3, new King(true));
        boxes[0][4] = new Box(0, 4, new Queen(true));
        // ... Initialize [0][5]...[0][7] with Bishop, Knight & Rook

        boxes[1][0] = new Box(1, 0, new Pawn(true));
        boxes[1][1] = new Box(1, 1, new Pawn(true));
        // ...
        boxes[1][7] = new Box(1, 7, new Pawn(true));

        /******************************* Initialize Black Pieces **********************************/
        boxes[7][0] = new Box(7, 0, new Rook(false));
        boxes[7][1] = new Box(7, 1, new Knight(false));
        boxes[7][2] = new Box(7, 2, new Bishop(false));
        boxes[7][3] = new Box(7, 3, new King(false));
        boxes[7][4] = new Box(7, 4, new Queen(false));
        // ... Initialize [7][5]...[7][7] with Bishop, Knight & Rook

        boxes[7][0] = new Box(7, 0, new Pawn(false));
        boxes[7][1] = new Box(7, 1, new Pawn(false));
        // ...
        boxes[7][7] = new Box(7, 7, new Pawn(false));

        /*********************** Initialize remaining boxes without pieces **************************/
        for (int i=2; i<6; i++) {
            for (int j=0; j<8; j++) {
                boxes[i][j] = new Box(i, j, null);
            }
        }
    }
}

@Getter
@Setter
class King extends Piece {
    // Implement castlingDone only when interviewer specifically asks or we can ask in requirement about the same
    private boolean castlingDone = false; // What is Castle: https://youtu.be/4jXQyGaeUV8
    public King(boolean white) {
        super(white);
    }

    @Override
    public boolean canMove(Board board, Box start, Box end) {

        // Can't move the piece to a box that has a piece of same color
        if (end.getPiece().isWhite() == this.isWhite()) {
            return false;
        }

        int x = Math.abs(start.getX() - end.getX());
        int y = Math.abs(start.getY() - end.getY());

        if (x + y == 1)
            return  true;
        return false; // If implementing Castle Move then return below.

        return this.isValidCastling(board, start,end);
    }

    private boolean isValidCastling(Board board, Box start, Box end) {

        // Castle move can be played only ONCE
        if (this.isCastlingDone()) {
            return false;
        }

        /* Check for correct WHITE king castling */
        if (this.isWhite() && start.getX() == 0 && start.getY() == 4 && end.getY() == 0) {
            /* Confirm White KING moves to correct ending box */
            if (Math.abs(end.getY() - start.getY()) == 2) {
                /*
                1. Check Rook is at correct position
                2. Check NO piece between Rook and King
                3. Check Rook & King not been moved before
                4. Check King move at this point won't be in attacking
                */
                this.setCastlingDone(true);
                return true;
            }
        } else {
            if (start.getX() == 7 && start.getY() == 4 && end.getY() == 7) {
                /* Confirm Black KING moves to correct ending box */
                if (Math.abs(end.getY() - start.getY()) == 2) {
                    /*
                    1. Check Rook is at correct position
                    2. Check NO piece between Rook and King
                    3. Check Rook & King not been moved before
                    4. Check King move at this point won't be in attacking
                    */
                    this.setCastlingDone(true);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isCastlingMove(Box start, Box end) {
        /* Check if starting and Ending Position are correct */
    }
}

class Knight extends Piece {
    public Knight(boolean white) {
        super(white);
    }

    @Override
    public boolean canMove(Board board, Box start, Box end) {

        // Can't move the piece to a box that has a piece of same color
        if (end.getPiece().isWhite() == this.isWhite()) {
            return false;
        }

        int x = Math.abs(start.getX() - end.getX());
        int y = Math.abs(start.getY() - end.getY());

        // When Knight moves to a valid end box from a start box
        // The index x and y will always follow the rule x*y = 2
        if (x*y == 2)
            return true;
        return false;
    }
}

class Rook extends Piece {
    public Rook(boolean white) {
        super(white);
    }

    @Override
    public boolean canMove(Board board, Box start, Box end) {
        // Can't move the piece to a box that has a piece of same color
        if (end.getPiece().isWhite() == this.isWhite()) {
            return false;
        }

        int x = Math.abs(start.getX() - end.getX());
        int y = Math.abs(start.getY() - end.getY());

        if ((x==0 && y>0) || (x>0 && y==0))
            return true;
        return false;
    }
}

class Bishop extends Piece {
    public Bishop(boolean white) {
        super(white);
    }

    @Override
    public boolean canMove(Board board, Box start, Box end) {
        // Can't move the piece to a box that has a piece of same color
        if (end.getPiece().isWhite() == this.isWhite()) {
            return false;
        }

        int x = Math.abs(start.getX() - end.getX());
        int y = Math.abs(start.getY() - end.getY());

        if (x == y)
            return true;
        return false;
    }
}

class Queen extends Piece {
    public Queen(boolean white) {
        super(white);
    }

    @Override
    public boolean canMove(Board board, Box start, Box end) {
        // Can't move the piece to a box that has a piece of same color
        if (end.getPiece().isWhite() == this.isWhite()) {
            return false;
        }

        int x = Math.abs(start.getX() - end.getX());
        int y = Math.abs(start.getY() - end.getY());

        // Move like Rook
        if ((x==0 && y>0) || (x>0 && y==0))
            return true;
        // Move like Bishop
        if (x == y)
            return true;

        return false;
    }
}

class Pawn extends Piece {
    public Pawn(boolean white) {
        super(white);
    }

    @Override
    public boolean canMove(Board board, Box start, Box end) {
        // Can't move the piece to a box that has a piece of same color
        if (end.getPiece().isWhite() == this.isWhite()) {
            return false;
        }

        int x = Math.abs(start.getX() - end.getX());
        int y = Math.abs(start.getY() - end.getY());
        if (x + y == 1)
            return  true;
        return false;
    }
}

/*
Date/Information regarding every MOVE in the game is saved in object of this Class
We are also saving/logging every move in GAME in { List<Move> movesPlayed }, where we just add the object of below Class
*/
@Getter
@Setter
class Move {
    private Player player;
    private Box start;
    private Box end;
    private Piece pieceMoved;
    private Piece pieceKilled;
    private boolean castlingMove = false;

    public Move(Player player, Box start, Box end) {
        this.player = player;
        this.start = start;
        this.end = end;

        this.pieceMoved = start.getPiece();
    }
}

/*
Main GAME Class which control & track a game going between 2 players
What all this Class does:
1. Track & Toggle the player Turn.
2. Saves all the moves played till now in current Game.
3. Update with Chess Pieces are DEAD.
4. Check that current player is making a valid move as per Chess rules or not.
5. Update the Moves of Pieces.
6. Check for CHECK-MATE.
7. Update who won in last.
*/
@Getter
@Setter
class Game {
    private Player[] players;
    private Board board;
    private Player currentTurn;
    private GameStatus status;
    private List<Move> movesPlayed; //Keep log/save all past moves

    private void initialize(Player p1, Player p2) {
        players[0] = p1;
        players[1] = p2;

        board.setBoard();

        // 1st Turn is played by WHITE player
        if (p1.isWhiteSide()) {
            this.currentTurn = p1;
        } else {
            this.currentTurn = p2;
        }
    }

    private boolean isGameEnd() {
        return this.getStatus() != GameStatus.ACTIVE;
    }

    public boolean playerMove (Player player, int startX, int startY, int endX, int endY) throws Exception {
        Box startBox = board.getBox(startX, startY);
        Box endBox = board.getBox(endX, endY);

        Move move = new Move(player, startBox, endBox);
        return this.makeMove(move, player);
    }

    private boolean makeMove(Move move, Player player) {
        Piece sourcePiece = move.getStart().getPiece();

        if (sourcePiece == null) {
            return false;
        }

        /* Checking TURN correction */
        if (player != currentTurn) {
            return false;
        }

        /* Player is trying to place piece of opponent */
        if (player.isWhiteSide() != sourcePiece.isWhite()) {
            return false;
        }

        /* Checking in canMove() that is movement of piece as per piece rules or not! */
        if (! sourcePiece.canMove(board, move.getStart(), move.getEnd())) {
            return false;
        }

        Piece destinationPiece = move.getEnd().getPiece();

        /*
        Checking:
        If Destination Box has no Piece, we can directly make the move (Start -> End)
        If Destination Box has a Piece, that need to killed. (Same color check is already been done in sourcePiece.canMove() method above)
        1. We mark killed to the destinationPiece.
        2. We save in current Move instance which Piece was killed (As we are keeping the log of every move)
        */
        if (destinationPiece != null) {
            destinationPiece.setKilled(true);
            move.setPieceKilled(destinationPiece);
        }

        /* It can be a Castling Move as well */
        if (sourcePiece != null && sourcePiece instanceof King && ((King) sourcePiece).isCastlingMove(move.getStart(), move.getEnd())) {
            move.setCastlingMove(true);
        }

        /* Saving all the moves happening in current Game */
        movesPlayed.add(move);

        /* Making the actual move of Piece from StartPoint -> EndPoint */
        move.getEnd().setPiece(move.getStart().getPiece());
        move.getStart().setPiece(null);

        /* CHECK-MATE (Check need to seen by player own its own, system won't tell which player is on check) */
        if (destinationPiece != null && destinationPiece instanceof King) {
            if (player.isWhiteSide()) {
                this.setStatus(GameStatus.WHITE_WIN);
            } else {
                this.setStatus(GameStatus.BLACK_WIN);
            }
        }

        /* Toggle the player turn (Turn check is already done in code separately) */
        if (this.currentTurn == players[0]) {
            this.currentTurn = players[1];
        } else {
            this.currentTurn = players[0];
        }

        return true;
    }
}