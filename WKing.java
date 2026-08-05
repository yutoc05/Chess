import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
public class WKing extends WhitePiece
{
    public void act()
    {
        if (!dragging()) {
            int moveType = moveType();
            if (((currentX == getX()) && (Math.abs(currentY - getY())) == 1) || ((Math.abs(currentX - getX()) == 1) && (currentY == getY())) || ((Math.abs(currentX - getX()) == 1) && (Math.abs(currentY - getY()) == 1))) {
                if (moveType == 1) { // normal move
                    move(false);
                } else if (moveType == 2) { // capture
                    capture(false);
                } else {
                    setLocation(currentX, currentY);
                }
            } else if (canCastleShort && currentX == 4 && currentY == 7 && getX() == 6
                    && getY() == 7 && getWorld().getObjectsAt(5, 7, Piece.class).isEmpty()
                    && getWorld().getObjectsAt(6, 7, Piece.class).isEmpty()
                    && !Piece.isSquareAttacked(getWorld(), 4, 7, -1)
                    && !Piece.isSquareAttacked(getWorld(), 5, 7, -1)
                    && !Piece.isSquareAttacked(getWorld(), 6, 7, -1)) {
                Actor actor = getOneObjectAtOffset(1, 0, WRook.class);
                if (moveType != 0 && actor != null && actor instanceof WRook) {
                    canCastleShort = false;
                    canCastleLong = false;
                    setLocation(currentX + 2, currentY);
                    currentX = getX();
                    WRook wRook = getWorld().getObjectsAt(7, currentY, WRook.class).get(0);
                    wRook.setLocation(currentX - 1, currentY);
                    wRook.currentX = wRook.getX();
                    clearEnPassantFlags();
                    turn *= -1;
                    updateGameState();
                } else {
                    setLocation(currentX, currentY);
                }
            } else if (canCastleLong && currentX == 4 && currentY == 7 && getX() == 2
                    && getY() == 7 && getWorld().getObjectsAt(1, 7, Piece.class).isEmpty()
                    && getWorld().getObjectsAt(2, 7, Piece.class).isEmpty()
                    && getWorld().getObjectsAt(3, 7, Piece.class).isEmpty()
                    && !Piece.isSquareAttacked(getWorld(), 4, 7, -1)
                    && !Piece.isSquareAttacked(getWorld(), 3, 7, -1)
                    && !Piece.isSquareAttacked(getWorld(), 2, 7, -1)) {
                Actor actor = getOneObjectAtOffset(-2, 0, WRook.class);
                if (moveType != 0 && actor != null && actor instanceof WRook) {
                    canCastleShort = false;
                    canCastleLong = false;
                    setLocation(currentX - 2, currentY);
                    currentX = getX();
                    WRook wRook = getWorld().getObjectsAt(0, currentY, WRook.class).get(0);
                    wRook.setLocation(currentX + 1, currentY);
                    wRook.currentX = wRook.getX();
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
