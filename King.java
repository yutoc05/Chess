import greenfoot.*;

public class King extends Piece {
    public King() {
        this(WHITE);
    }

    public King(int colour) {
        super(colour, "WKing.png", "BKing.png");
    }

    public void act() {
        if (!dragging()) {
            int moveType = moveType();
            boolean kingMove = (currentX == getX() && Math.abs(currentY - getY()) == 1)
                    || (Math.abs(currentX - getX()) == 1 && currentY == getY())
                    || (Math.abs(currentX - getX()) == 1 && Math.abs(currentY - getY()) == 1);
            if (kingMove && moveType == 1) {
                move(false);
            } else if (kingMove && moveType == 2) {
                capture(false);
            } else if (!tryCastle(true) && !tryCastle(false)) {
                setLocation(currentX, currentY);
            }
        }
    }

    private boolean tryCastle(boolean shortCastle) {
        int homeY = colour == WHITE ? 7 : 0;
        int destinationX = shortCastle ? 6 : 2;
        int rookX = shortCastle ? 7 : 0;
        int step = shortCastle ? 1 : -1;
        if (!(shortCastle ? canCastleShort(colour) : canCastleLong(colour))
                || currentX != 4 || currentY != homeY
                || getX() != destinationX || getY() != homeY) {
            return false;
        }
        int throughX = shortCastle ? 5 : 3;
        for (int x = currentX + step; x != rookX; x += step) {
            for (Piece piece : getWorld().getObjectsAt(x, homeY, Piece.class)) {
                if (piece != this) {
                    setLocation(currentX, currentY);
                    return false;
                }
            }
        }
        if (Piece.isSquareAttacked(getWorld(), currentX, homeY, -colour)
                || Piece.isSquareAttacked(getWorld(), throughX, homeY, -colour)
                || Piece.isSquareAttacked(getWorld(), destinationX, homeY, -colour)) {
            setLocation(currentX, currentY);
            return false;
        }
        Actor actor = getOneObjectAtOffset(shortCastle ? 1 : -2, 0, Rook.class);
        if (!(actor instanceof Rook) || ((Rook) actor).colour != colour) {
            setLocation(currentX, currentY);
            return false;
        }
        Rook rook = (Rook) actor;
        revokeCastling(colour);
        setLocation(destinationX, homeY);
        currentX = getX();
        rook.setLocation(shortCastle ? 5 : 3, homeY);
        rook.currentX = rook.getX();
        rook.currentY = rook.getY();
        clearEnPassantFlags();
        turn *= -1;
        updateGameState();
        return true;
    }
}
