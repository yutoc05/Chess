import greenfoot.*;

public class Piece extends Actor {
    static final int WHITE = 1;
    static final int BLACK = -1;

    static int turn = WHITE;
    static boolean checkmate = false;
    static boolean stalemate = false;
    static boolean gameOver = false;
    static int winner = 0;
    static boolean whiteInCheck = false;
    static boolean blackInCheck = false;

    static boolean whiteCanCastleShort = true;
    static boolean whiteCanCastleLong = true;
    static boolean blackCanCastleShort = true;
    static boolean blackCanCastleLong = true;

    int colour = 0;
    int currentX = 0;
    int currentY = 0;
    boolean isDragging = false;

    public Piece() {
    }

    protected Piece(int colour, String whiteImage, String blackImage) {
        this.colour = colour;
        setImage(new GreenfootImage(colour == WHITE ? whiteImage : blackImage));
    }

    static boolean canCastleShort(int side) {
        return side == WHITE ? whiteCanCastleShort : blackCanCastleShort;
    }

    static boolean canCastleLong(int side) {
        return side == WHITE ? whiteCanCastleLong : blackCanCastleLong;
    }

    static void revokeCastleShort(int side) {
        if (side == WHITE) {
            whiteCanCastleShort = false;
        } else {
            blackCanCastleShort = false;
        }
    }

    static void revokeCastleLong(int side) {
        if (side == WHITE) {
            whiteCanCastleLong = false;
        } else {
            blackCanCastleLong = false;
        }
    }

    static void revokeCastling(int side) {
        revokeCastleShort(side);
        revokeCastleLong(side);
    }

    private static void revokeCornerRight(int side, int x, int y) {
        int homeY = side == WHITE ? 7 : 0;
        if (y != homeY) {
            return;
        }
        if (x == 0) {
            revokeCastleLong(side);
        } else if (x == 7) {
            revokeCastleShort(side);
        }
    }

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
        if (!(this instanceof Knight)) {
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

    public boolean dragging() {
        if (Greenfoot.mousePressed(this) && turn == colour && !gameOver) {
            isDragging = true;
        }
        if (isDragging) {
            MouseInfo mouse = Greenfoot.getMouseInfo();
            if (mouse != null) {
                setLocation(mouse.getX(), mouse.getY());
            }
        }
        if (isDragging && Greenfoot.mouseDragEnded(this)) {
            isDragging = false;
            return false;
        }
        return true;
    }

    public boolean move(boolean enPassant) {
        Actor enPassantCaptured = null;
        int capturedX = -1;
        int capturedY = -1;
        if (enPassant) {
            Actor captured = getOneObjectAtOffset(0, -forwardDirection(), Pawn.class);
            if (!(captured instanceof Pawn)
                    || ((Pawn) captured).colour != -colour
                    || !((Pawn) captured).enPassantable) {
                setLocation(currentX, currentY);
                return false;
            }
            enPassantCaptured = captured;
            capturedX = captured.getX();
            capturedY = captured.getY();
            getWorld().removeObject(captured);
        }
        if (!inCheck()) {
            int fromX = currentX;
            int fromY = currentY;
            currentX = getX();
            currentY = getY();
            turn *= -1;
            clearEnPassantFlags();
            if (this instanceof King) {
                revokeCastling(colour);
            } else if (this instanceof Rook) {
                revokeCornerRight(colour, fromX, fromY);
            }
            if (!(this instanceof Pawn)) {
                updateGameState();
            }
            return true;
        }
        if (enPassantCaptured != null) {
            getWorld().addObject(enPassantCaptured, capturedX, capturedY);
        }
        setLocation(currentX, currentY);
        return false;
    }

    protected int forwardDirection() {
        return colour == WHITE ? -1 : 1;
    }

    void clearEnPassantFlags() {
        for (Pawn pawn : getWorld().getObjects(Pawn.class)) {
            pawn.enPassantable = false;
        }
    }

    public boolean capture(boolean enPassant) {
        Actor actor = getOneIntersectingObject(Piece.class);
        if (!(actor instanceof Piece) || actor instanceof King) {
            setLocation(currentX, currentY);
            return false;
        }

        Piece capturedPiece = (Piece) actor;
        int fromX = currentX;
        int fromY = currentY;
        int capturedX = capturedPiece.getX();
        int capturedY = capturedPiece.getY();
        getWorld().removeObject(capturedPiece);
        if (!inCheck()) {
            currentX = getX();
            currentY = getY();
            turn *= -1;
            clearEnPassantFlags();
            if (this instanceof King) {
                revokeCastling(colour);
            } else if (this instanceof Rook) {
                revokeCornerRight(colour, fromX, fromY);
            }
            if (capturedPiece instanceof Rook) {
                revokeCornerRight(capturedPiece.colour, capturedX, capturedY);
            }
            if (!(this instanceof Pawn)) {
                updateGameState();
            }
            return true;
        }

        getWorld().addObject(capturedPiece, capturedX, capturedY);
        setLocation(currentX, currentY);
        return false;
    }

    public boolean inCheck() {
        BoardState state = new BoardState(getWorld());
        whiteInCheck = state.isInCheck(WHITE);
        blackInCheck = state.isInCheck(BLACK);
        return state.isInCheck(turn);
    }

    void updateGameState() {
        World world = getWorld();
        BoardState state = new BoardState(world);
        whiteInCheck = state.isInCheck(WHITE);
        blackInCheck = state.isInCheck(BLACK);
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
            if (destination instanceof King) {
                return false;
            }
            if (piece instanceof King && Math.abs(toX - fromX) == 2 && fromY == toY) {
                return isLegalCastle(fromX, fromY, toX, side);
            }
            if (!isPseudoMove(piece, fromX, fromY, toX, toY)) {
                return false;
            }
            BoardState next = new BoardState(this);
            next.board[fromX][fromY] = null;
            if (piece instanceof Pawn && destination == null && Math.abs(toX - fromX) == 1) {
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
            boolean rights = shortCastle ? canCastleShort(side) : canCastleLong(side);
            if (!rights || fromX != 4 || toX != destinationX
                    || (side == WHITE ? fromY != 7 : fromY != 0)) {
                return false;
            }
            Piece rook = board[rookX][fromY];
            if (!(rook instanceof Rook) || rook.colour != side) {
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
            if (piece instanceof Pawn) {
                int direction = piece.forwardDirection();
                int distance = dy * direction;
                if (dx == 0 && destination == null && distance == 1) {
                    return true;
                }
                if (dx == 0 && destination == null && distance == 2
                        && pawnHasNotMoved(piece, fromY)
                        && board[fromX][fromY + direction] == null) {
                    return true;
                }
                if (absX == 1 && distance == 1) {
                    if (destination != null && destination.colour != piece.colour) {
                        return true;
                    }
                    Piece adjacent = board[toX][fromY];
                    return destination == null && isEnPassantPawn(adjacent, -piece.colour);
                }
                return false;
            }
            if (piece instanceof Knight) {
                return (absX == 1 && absY == 2) || (absX == 2 && absY == 1);
            }
            if (piece instanceof King) {
                return absX <= 1 && absY <= 1;
            }
            if (piece instanceof Rook) {
                return (dx == 0 || dy == 0) && clearPath(fromX, fromY, toX, toY);
            }
            if (piece instanceof Bishop) {
                return absX == absY && clearPath(fromX, fromY, toX, toY);
            }
            if (piece instanceof Queen) {
                return ((dx == 0 || dy == 0) || absX == absY)
                    && clearPath(fromX, fromY, toX, toY);
            }
            return false;
        }

        private boolean pawnHasNotMoved(Piece piece, int fromY) {
            return piece instanceof Pawn && !((Pawn) piece).moved
                && fromY == (piece.colour == WHITE ? 6 : 1);
        }

        private boolean isEnPassantPawn(Piece piece, int expectedColour) {
            return piece instanceof Pawn
                && piece.colour == expectedColour
                && ((Pawn) piece).enPassantable;
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
                    if (piece != null && piece.colour == side && piece instanceof King) {
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
                    if (piece instanceof Pawn) {
                        if (absX == 1 && dy == piece.forwardDirection()) {
                            return true;
                        }
                    } else if (piece instanceof Knight) {
                        if ((absX == 1 && absY == 2) || (absX == 2 && absY == 1)) {
                            return true;
                        }
                    } else if (piece instanceof King) {
                        if (absX <= 1 && absY <= 1 && (absX != 0 || absY != 0)) {
                            return true;
                        }
                    } else if (piece instanceof Rook) {
                        if ((dx == 0 || dy == 0) && clearPath(x, y, targetX, targetY)) {
                            return true;
                        }
                    } else if (piece instanceof Bishop) {
                        if (absX == absY && clearPath(x, y, targetX, targetY)) {
                            return true;
                        }
                    } else if (piece instanceof Queen) {
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
