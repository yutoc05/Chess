import greenfoot.*;

public class WPawn extends WhitePiece {
    boolean moved = false;
    boolean enPassantable = false;

    public void act() {
        if (!dragging()) {
            boolean moveAccepted = false;
            int moveType = moveType();
            if (moveType == 1) {
                if (currentX == getX() && currentY == getY() + 1) {
                    moveAccepted = move(false);
                    if (moveAccepted) {
                        moved = true;
                    }
                } else if (currentX == getX() && currentY == getY() + 2 && !moved) {
                    moveAccepted = move(false);
                    if (moveAccepted) {
                        moved = true;
                        enPassantable = true;
                    }
                } else if ((currentX == getX() + 1 || currentX == getX() - 1)
                        && currentY == getY() + 1) {
                    Actor actor = getOneObjectAtOffset(0, 1, BPawn.class);
                    if (actor instanceof BPawn && ((BPawn) actor).enPassantable) {
                        moveAccepted = move(true);
                    } else {
                        setLocation(currentX, currentY);
                    }
                } else {
                    setLocation(currentX, currentY);
                }
            } else if (moveType == 2) {
                if ((currentX == getX() + 1 || currentX == getX() - 1)
                        && currentY == getY() + 1) {
                    moveAccepted = capture(false);
                } else {
                    setLocation(currentX, currentY);
                }
            } else {
                setLocation(currentX, currentY);
            }
            if (moveAccepted && currentY == 0) {
                String choice = Greenfoot.ask("Choose a piece for promotion: Queen, Rook, Bishop, or Knight");
                if (choice == null) choice = "Queen";
                choice = choice.trim();
                if ("Rook".equalsIgnoreCase(choice)) {
                    WRook piece = new WRook();
                    promoteTo(piece);
                } else if ("Bishop".equalsIgnoreCase(choice)) {
                    WBishop piece = new WBishop();
                    promoteTo(piece);
                } else if ("Knight".equalsIgnoreCase(choice)) {
                    WKnight piece = new WKnight();
                    promoteTo(piece);
                } else {
                    WQueen piece = new WQueen();
                    promoteTo(piece);
                }
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
        piece.colour = 1;
        getWorld().removeObject(this);
        piece.updateGameState();
    }
}
