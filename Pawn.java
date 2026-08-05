import greenfoot.*;

public class Pawn extends Piece {
    boolean moved = false;
    boolean enPassantable = false;

    public Pawn() {
        this(WHITE);
    }

    public Pawn(int colour) {
        super(colour, "WPawn.png", "BPawn.png");
    }

    public void act() {
        if (!dragging()) {
            boolean moveAccepted = false;
            int moveType = moveType();
            int direction = forwardDirection();
            if (moveType == 1) {
                if (currentX == getX() && getY() == currentY + direction) {
                    moveAccepted = move(false);
                    if (moveAccepted) {
                        moved = true;
                    }
                } else if (currentX == getX() && getY() == currentY + 2 * direction
                        && !moved && currentY == (colour == WHITE ? 6 : 1)) {
                    moveAccepted = move(false);
                    if (moveAccepted) {
                        enPassantable = true;
                    }
                } else if ((currentX == getX() + 1 || currentX == getX() - 1)
                        && getY() == currentY + direction) {
                    Actor actor = getOneObjectAtOffset(0, -direction, Pawn.class);
                    if (actor instanceof Pawn
                            && ((Pawn) actor).colour == -colour
                            && ((Pawn) actor).enPassantable) {
                        moveAccepted = move(true);
                    } else {
                        setLocation(currentX, currentY);
                    }
                } else {
                    setLocation(currentX, currentY);
                }
            } else if (moveType == 2) {
                if ((currentX == getX() + 1 || currentX == getX() - 1)
                        && getY() == currentY + direction) {
                    moveAccepted = capture(false);
                } else {
                    setLocation(currentX, currentY);
                }
            } else {
                setLocation(currentX, currentY);
            }

            if (moveAccepted) {
                moved = true;
            }
            if (moveAccepted && getY() == (colour == WHITE ? 0 : 7)) {
                String choice = Greenfoot.ask(
                    "Choose a piece for promotion: Queen, Rook, Bishop, or Knight");
                if (choice == null) {
                    choice = "Queen";
                }
                choice = choice.trim();
                Piece piece;
                if ("Rook".equalsIgnoreCase(choice)) {
                    piece = new Rook(colour);
                } else if ("Bishop".equalsIgnoreCase(choice)) {
                    piece = new Bishop(colour);
                } else if ("Knight".equalsIgnoreCase(choice)) {
                    piece = new Knight(colour);
                } else {
                    piece = new Queen(colour);
                }
                promoteTo(piece);
            }
            if (moveAccepted && getWorld() != null) {
                updateGameState();
            }
        }
    }

    private void promoteTo(Piece piece) {
        getWorld().addObject(piece, currentX, currentY);
        piece.currentX = currentX;
        piece.currentY = currentY;
        getWorld().removeObject(this);
        piece.updateGameState();
    }
}
