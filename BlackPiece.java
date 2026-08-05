import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
public class BlackPiece extends Piece
{
    boolean isDragging = false;
    static boolean canCastleShort = true;
    static boolean canCastleLong = true;
    static boolean inCheck = false;
    
    public boolean dragging() {
        if (Greenfoot.mousePressed(this) && turn == -1 && !gameOver) {
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
}