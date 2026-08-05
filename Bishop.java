public class Bishop extends Piece {
    public Bishop() {
        this(WHITE);
    }

    public Bishop(int colour) {
        super(colour, "WBishop.png", "BBishop.png");
    }

    public void act() {
        if (!dragging()) {
            int moveType = moveType();
            boolean diagonal = currentX != getX() && currentY != getY()
                    && Math.abs(currentX - getX()) == Math.abs(currentY - getY());
            if (diagonal && moveType == 1) {
                move(false);
            } else if (diagonal && moveType == 2) {
                capture(false);
            } else {
                setLocation(currentX, currentY);
            }
        }
    }
}
