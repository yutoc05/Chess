import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
public class BKing extends BlackPiece
{
    public void act()
    {
        if (!dragging()) {
            int moveType = moveType();
            if (((currentX == getX()) && (Math.abs(currentY - getY())) == 1) || ((Math.abs(currentX - getX()) == 1) && (currentY == getY())) || ((Math.abs(currentX - getX()) == 1) && (Math.abs(currentY - getY()) == 1))) {
                if (moveType == 1) { // normal move
                    move(false);
                    canCastleShort = false;
                    canCastleLong = false;
                } else if (moveType == 2) { // capture
                    capture(false);
                    canCastleShort = false;
                    canCastleLong = false;
                } else {
                    setLocation(currentX, currentY);
                }
            } else if (canCastleShort && currentX == 4 && currentY == 0 && getX() == 6
                    && getY() == 0 && getWorld().getObjectsAt(5, 0, Piece.class).isEmpty()
                    && getWorld().getObjectsAt(6, 0, Piece.class).isEmpty()
                    && !Piece.isSquareAttacked(getWorld(), 4, 0, 1)
                    && !Piece.isSquareAttacked(getWorld(), 5, 0, 1)
                    && !Piece.isSquareAttacked(getWorld(), 6, 0, 1)) {
                Actor actor = getOneObjectAtOffset(1, 0, BRook.class);
                if (moveType != 0 && actor != null && actor instanceof BRook) {
                    canCastleShort = false;
                    canCastleLong = false;
                    setLocation(currentX + 2, currentY);
                    currentX = getX();
                    BRook bRook = getWorld().getObjectsAt(7, currentY, BRook.class).get(0);
                    bRook.setLocation(currentX - 1, currentY);
                    bRook.currentX = bRook.getX();
                    clearEnPassantFlags();
                    turn *= -1;
                    updateGameState();
                } else {
                    setLocation(currentX, currentY);
                }
            } else if (canCastleLong && currentX == 4 && currentY == 0 && getX() == 2
                    && getY() == 0 && getWorld().getObjectsAt(1, 0, Piece.class).isEmpty()
                    && getWorld().getObjectsAt(2, 0, Piece.class).isEmpty()
                    && getWorld().getObjectsAt(3, 0, Piece.class).isEmpty()
                    && !Piece.isSquareAttacked(getWorld(), 4, 0, 1)
                    && !Piece.isSquareAttacked(getWorld(), 3, 0, 1)
                    && !Piece.isSquareAttacked(getWorld(), 2, 0, 1)) {
                Actor actor = getOneObjectAtOffset(-2, 0, BRook.class);
                if (moveType != 0 && actor != null && actor instanceof BRook) {
                    canCastleShort = false;
                    canCastleLong = false;
                    setLocation(currentX - 2, currentY);
                    currentX = getX();
                    BRook bRook = getWorld().getObjectsAt(0, currentY, BRook.class).get(0);
                    bRook.setLocation(currentX + 1, currentY);
                    bRook.currentX = bRook.getX();
                    clearEnPassantFlags();
                    turn *= -1;
                    updateGameState();
                } else {
                    setLocation(currentX, currentY);
                }
            } else {
                setLocation(currentX, currentY);
            }
        }
    }
}
