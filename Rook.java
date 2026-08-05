public class Rook extends Piece {
    public Rook() {
        this(WHITE);
    }

    public Rook(int colour) {
        super(colour, "WRook.png", "BRook.png");
    }

    public void act() {
        if (!dragging()) {
            int moveType = moveType();
            boolean straight = (currentX == getX() && currentY != getY())
                    || (currentX != getX() && currentY == getY());
            if (straight && (moveType == 1 || moveType == 2)) {
                revokeCornerRight();
                if (moveType == 1) {
                    move(false);
                } else {
                    capture(false);
                }
            } else {
                setLocation(currentX, currentY);
            }
        }
    }

    private void revokeCornerRight() {
        int homeY = colour == WHITE ? 7 : 0;
        if (currentY == homeY && currentX == 0) {
            revokeCastleLong(colour);
        } else if (currentY == homeY && currentX == 7) {
            revokeCastleShort(colour);
        }
    }
}
