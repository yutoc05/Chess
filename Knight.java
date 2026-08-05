public class Knight extends Piece {
    public Knight() {
        this(WHITE);
    }

    public Knight(int colour) {
        super(colour, "WKnight.png", "BKnight.png");
    }

    public void act() {
        if (!dragging()) {
            int moveType = moveType();
            boolean knightMove = (Math.abs(currentX - getX()) == 1
                    && Math.abs(currentY - getY()) == 2)
                    || (Math.abs(currentX - getX()) == 2
                    && Math.abs(currentY - getY()) == 1);
            if (knightMove && moveType == 1) {
                move(false);
            } else if (knightMove && moveType == 2) {
                capture(false);
            } else {
                setLocation(currentX, currentY);
            }
        }
    }
}
