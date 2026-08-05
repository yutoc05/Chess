import greenfoot.*;
import java.util.List;

public class Piece extends Actor {
    static int turn = 1; // white is 1, black is -1
    static boolean checkmate = false;
    static boolean stalemate = false;
    static boolean gameOver = false;
    static int winner = 0;
    int colour = 0;
    int currentX = 0;
    int currentY = 0;

    public static boolean isCheckmate() {
        return checkmate;
    }

    public static boolean isStalemate() {
        return stalemate;
    }

    public static int getWinner() {
        return winner;
    }

    public int moveType() {
        if (!(this instanceof WKnight) && !(this instanceof BKnight)) {
            int distance = Math.max(Math.abs(getX() - currentX), Math.abs(getY() - currentY));
            int dx = Integer.signum(getX() - currentX);
            int dy = Integer.signum(getY() - currentY);
            for (int i = 1; i < distance; i++) {
                int pathX = currentX + dx * i;
                int pathY = currentY + dy * i;
                if (!getWorld().getObjectsAt(pathX, pathY, Piece.class).isEmpty()) {
                    return 0;
                }
            }
        }
        Actor actor = getOneIntersectingObject(Piece.class);
        if (actor instanceof Piece && ((Piece) actor).colour == colour) {
            return 0;
        }
        return actor instanceof Piece ? 2 : 1;
    }

    public boolean move(boolean enPassant) {
        if (enPassant) {
            Actor captured = turn == -1
                ? getOneObjectAtOffset(0, -1, WPawn.class)
                : getOneObjectAtOffset(0, 1, BPawn.class);
            if (captured != null) {
                getWorld().removeObject(captured);
            }
        }
        if (!inCheck()) {
            currentX = getX();
            currentY = getY();
            turn *= -1;
            clearEnPassantFlags();
            if (getClass() == WKing.class) {
                WhitePiece.canCastleShort = false;
                WhitePiece.canCastleLong = false;
            } else if (getClass() == BKing.class) {
                BlackPiece.canCastleShort = false;
                BlackPiece.canCastleLong = false;
            }
            if (!(this instanceof WPawn) && !(this instanceof BPawn)) {
                updateGameState();
            }
            return true;
        }
        if (enPassant) {
            int restoredColour = turn == 1 ? -1 : 1;
            Piece restored = restoredColour == 1 ? new WPawn() : new BPawn();
            getWorld().addObject(restored, getX(), getY() + (turn == 1 ? 1 : -1));
            restored.colour = restoredColour;
            restored.currentX = restored.getX();
            restored.currentY = restored.getY();
        }
        setLocation(currentX, currentY);
        return false;
    }

    void clearEnPassantFlags() {
        for (WPawn pawn : getWorld().getObjects(WPawn.class)) {
            pawn.enPassantable = false;
        }
        for (BPawn pawn : getWorld().getObjects(BPawn.class)) {
            pawn.enPassantable = false;
        }
    }
    public boolean capture(boolean enPassant) {
        Actor actor = getOneIntersectingObject(Piece.class);
        if (!(actor instanceof Piece) || actor instanceof WKing || actor instanceof BKing) {
            setLocation(currentX, currentY);
            return false;
        }
        Class<? extends Piece> capturedClass = actor.getClass().asSubclass(Piece.class);
        boolean moved = false;
        boolean enPassantable = false;
        if (actor instanceof WPawn) {
            moved = ((WPawn) actor).moved;
            enPassantable = ((WPawn) actor).enPassantable;
        } else if (actor instanceof BPawn) {
            moved = ((BPawn) actor).moved;
            enPassantable = ((BPawn) actor).enPassantable;
        }
        removeTouching(Piece.class);
        if (!inCheck()) {
            currentX = getX();
            currentY = getY();
            turn *= -1;
            clearEnPassantFlags();
            if (!(this instanceof WPawn) && !(this instanceof BPawn)) {
                updateGameState();
            }
            return true;
        }
        try {
            Piece restored = capturedClass.newInstance();
            getWorld().addObject(restored, getX(), getY());
            restored.currentX = restored.getX();
            restored.currentY = restored.getY();
            restored.colour = -colour;
            if (restored instanceof WPawn) {
                ((WPawn) restored).moved = moved;
                ((WPawn) restored).enPassantable = enPassantable;
            } else if (restored instanceof BPawn) {
                ((BPawn) restored).moved = moved;
                ((BPawn) restored).enPassantable = enPassantable;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        setLocation(currentX, currentY);
        return false;
    }

    public boolean inCheck() {
        BoardState state = new BoardState(getWorld());
        WhitePiece.inCheck = state.isInCheck(1);
        BlackPiece.inCheck = state.isInCheck(-1);
        return state.isInCheck(turn);
    }

    void updateGameState() {
        World world = getWorld();
        BoardState state = new BoardState(world);
        WhitePiece.inCheck = state.isInCheck(1);
        BlackPiece.inCheck = state.isInCheck(-1);
        checkmate = false;
        stalemate = false;
        winner = 0;
        if (!state.hasAnyLegalMove(turn)) {
            gameOver = true;
            if (state.isInCheck(turn)) {
                checkmate = true;
                winner = -turn;
            } else {
                stalemate = true;
            }
            if (world.getObjects(GameOverPopup.class).isEmpty()) {
                world.addObject(new GameOverPopup(checkmate, winner), 4, 4);
            }
        } else {
            gameOver = false;
        }
    }

    static boolean isSquareAttacked(World world, int x, int y, int attacker) {
        return new BoardState(world).isSquareAttacked(x, y, attacker);
    }
    private static class BoardState {
        private final Piece[][] board = new Piece[8][8];

        BoardState(World world) {
            for (Piece piece : world.getObjects(Piece.class)) {
                if (inside(piece.getX(), piece.getY())) {
                    board[piece.getX()][piece.getY()] = piece;
                }
            }
        }

        BoardState(BoardState other) {
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    board[x][y] = other.board[x][y];
                }
            }
        }

        boolean hasAnyLegalMove(int side) {
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    Piece piece = board[x][y];
                    if (piece == null || piece.colour != side) {
                        continue;
                    }
                    for (int targetX = 0; targetX < 8; targetX++) {
                        for (int targetY = 0; targetY < 8; targetY++) {
                            if (isLegalMove(piece, x, y, targetX, targetY, side)) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }

        boolean isLegalMove(Piece piece, int fromX, int fromY,
                            int toX, int toY, int side) {
            if (!inside(toX, toY) || (fromX == toX && fromY == toY)) {
                return false;
            }
            Piece destination = board[toX][toY];
            if (destination != null && destination.colour == side) {
                return false;
            }
            if (destination instanceof WKing || destination instanceof BKing) {
                return false;
            }
            if ((piece instanceof WKing || piece instanceof BKing)
                    && Math.abs(toX - fromX) == 2 && fromY == toY) {
                return isLegalCastle(fromX, fromY, toX, side);
            }
            if (!isPseudoMove(piece, fromX, fromY, toX, toY)) {
                return false;
            }
            BoardState next = new BoardState(this);
            next.board[fromX][fromY] = null;
            if ((piece instanceof WPawn || piece instanceof BPawn)
                    && destination == null && Math.abs(toX - fromX) == 1) {
                next.board[toX][fromY] = null;
            }
            next.board[toX][toY] = piece;
            return !next.isInCheck(side);
        }

        private boolean isLegalCastle(int fromX, int fromY, int toX, int side) {
            boolean shortCastle = toX > fromX;
            int rookX = shortCastle ? 7 : 0;
            int throughX = shortCastle ? 5 : 3;
            int destinationX = shortCastle ? 6 : 2;
            boolean rights = side == 1
                ? (shortCastle ? WhitePiece.canCastleShort : WhitePiece.canCastleLong)
                : (shortCastle ? BlackPiece.canCastleShort : BlackPiece.canCastleLong);
            if (!rights || fromX != 4 || toX != destinationX
                    || (side == 1 ? fromY != 7 : fromY != 0)) {
                return false;
            }
            Piece rook = board[rookX][fromY];
            if ((side == 1 && !(rook instanceof WRook))
                    || (side == -1 && !(rook instanceof BRook))) {
                return false;
            }
            int step = shortCastle ? 1 : -1;
            for (int x = fromX + step; x != rookX; x += step) {
                if (board[x][fromY] != null) {
                    return false;
                }
            }
            if (isInCheck(side) || isSquareAttacked(throughX, fromY, -side)
                    || isSquareAttacked(destinationX, fromY, -side)) {
                return false;
            }
            BoardState next = new BoardState(this);
            next.board[fromX][fromY] = null;
            next.board[rookX][fromY] = null;
            next.board[destinationX][fromY] = board[fromX][fromY];
            next.board[shortCastle ? 5 : 3][fromY] = rook;
            return !next.isInCheck(side);
        }

        private boolean isPseudoMove(Piece piece, int fromX, int fromY,
                                     int toX, int toY) {
            int dx = toX - fromX;
            int dy = toY - fromY;
            int absX = Math.abs(dx);
            int absY = Math.abs(dy);
            Piece destination = board[toX][toY];
            if (piece instanceof WPawn || piece instanceof BPawn) {
                int direction = piece.colour == 1 ? -1 : 1;
                int distance = dy * direction;
                if (dx == 0 && destination == null && distance == 1) {
                    return true;
                }
                if (dx == 0 && destination == null && distance == 2
                        && pawnHasNotMoved(piece)
                        && board[fromX][fromY + direction] == null) {
                    return true;
                }
                if (absX == 1 && distance == 1) {
                    if (destination != null && destination.colour != piece.colour) {
                        return true;
                    }
                    Piece adjacent = board[toX][fromY];
                    return destination == null
                        && isEnPassantPawn(adjacent, -piece.colour);
                }
                return false;
            }
            if (piece instanceof WKnight || piece instanceof BKnight) {
                return (absX == 1 && absY == 2) || (absX == 2 && absY == 1);
            }
            if (piece instanceof WKing || piece instanceof BKing) {
                return absX <= 1 && absY <= 1;
            }
            if (piece instanceof WRook || piece instanceof BRook) {
                return (dx == 0 || dy == 0) && clearPath(fromX, fromY, toX, toY);
            }
            if (piece instanceof WBishop || piece instanceof BBishop) {
                return absX == absY && clearPath(fromX, fromY, toX, toY);
            }
            if (piece instanceof WQueen || piece instanceof BQueen) {
                return ((dx == 0 || dy == 0) || absX == absY)
                    && clearPath(fromX, fromY, toX, toY);
            }
            return false;
        }
        private boolean pawnHasNotMoved(Piece piece) {
            return piece instanceof WPawn
                ? !((WPawn) piece).moved
                : !((BPawn) piece).moved;
        }

        private boolean isEnPassantPawn(Piece piece, int expectedColour) {
            if (piece == null || piece.colour != expectedColour) {
                return false;
            }
            if (piece instanceof WPawn) {
                return ((WPawn) piece).enPassantable;
            }
            if (piece instanceof BPawn) {
                return ((BPawn) piece).enPassantable;
            }
            return false;
        }

        private boolean clearPath(int fromX, int fromY, int toX, int toY) {
            int stepX = Integer.signum(toX - fromX);
            int stepY = Integer.signum(toY - fromY);
            int x = fromX + stepX;
            int y = fromY + stepY;
            while (x != toX || y != toY) {
                if (board[x][y] != null) {
                    return false;
                }
                x += stepX;
                y += stepY;
            }
            return true;
        }

        boolean isInCheck(int side) {
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    Piece piece = board[x][y];
                    if (piece != null && piece.colour == side
                            && (piece instanceof WKing || piece instanceof BKing)) {
                        return isSquareAttacked(x, y, -side);
                    }
                }
            }
            return true;
        }

        boolean isSquareAttacked(int targetX, int targetY, int attacker) {
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    Piece piece = board[x][y];
                    if (piece == null || piece.colour != attacker) {
                        continue;
                    }
                    int dx = targetX - x;
                    int dy = targetY - y;
                    int absX = Math.abs(dx);
                    int absY = Math.abs(dy);
                    if (piece instanceof WPawn || piece instanceof BPawn) {
                        int direction = attacker == 1 ? -1 : 1;
                        if (absX == 1 && dy == direction) {
                            return true;
                        }
                    } else if (piece instanceof WKnight || piece instanceof BKnight) {
                        if ((absX == 1 && absY == 2) || (absX == 2 && absY == 1)) {
                            return true;
                        }
                    } else if (piece instanceof WKing || piece instanceof BKing) {
                        if (absX <= 1 && absY <= 1 && (absX != 0 || absY != 0)) {
                            return true;
                        }
                    } else if (piece instanceof WRook || piece instanceof BRook) {
                        if ((dx == 0 || dy == 0) && clearPath(x, y, targetX, targetY)) {
                            return true;
                        }
                    } else if (piece instanceof WBishop || piece instanceof BBishop) {
                        if (absX == absY && clearPath(x, y, targetX, targetY)) {
                            return true;
                        }
                    } else if (piece instanceof WQueen || piece instanceof BQueen) {
                        if (((dx == 0 || dy == 0) || absX == absY)
                                && clearPath(x, y, targetX, targetY)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        private static boolean inside(int x, int y) {
            return x >= 0 && x < 8 && y >= 0 && y < 8;
        }
    }
}
