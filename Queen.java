public class Queen extends Piece {
    public Queen() {
        this(WHITE);
    }

    public Queen(int colour) {
        super(colour, "WQueen.png", "BQueen.png");
    }

    public void act() {
        if (!dragging()) {
            int moveType = moveType();
            boolean straight = (currentX == getX() && currentY != getY())
                    || (currentX != getX() && currentY == getY());
            boolean diagonal = currentX != getX() && currentY != getY()
                    && Math.abs(currentX - getX()) == Math.abs(currentY - getY());
            if ((straight || diagonal) && moveType == 1) {
                move(false);
            } else if ((straight || diagonal) && moveType == 2) {
                capture(false);
            } else {
                setLocation(currentX, currentY);
            }
        }
    }
}
