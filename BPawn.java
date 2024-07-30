import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import javax.swing.JOptionPane;
public class BPawn extends BlackPiece
{
    boolean moved = false;
    public boolean enPassantable = false;
    public void act()
    {
        if (!dragging()) {
            int moveType = moveType();
            if (moveType == 1) { // normal move
                if (currentX == getX() && currentY == getY() - 1) {
                    move(false);
                } else if ((currentX == getX()) && (currentY == getY() - 2) && !moved) {
                    move(false);
                    moved = true;
                    enPassantable = true;
                } else if (((currentX == getX() + 1) || (currentX == getX() - 1)) && (currentY == getY() - 1)) {
                    Actor actor = getOneObjectAtOffset(0, -1, WPawn.class);
                    if (actor != null && actor instanceof WPawn) {
                        WPawn wPawn = (WPawn) actor;
                        if (wPawn.enPassantable) {
                            move(true);
                        } else {
                            setLocation(currentX, currentY);
                        }
                    } else {
                        setLocation(currentX, currentY);
                    }
                } else {
                    setLocation(currentX, currentY);
                }
            } else if (moveType == 2) { // capture
                if (((currentX == getX() + 1) || (currentX == getX() - 1)) && (currentY == getY() - 1)) {
                    capture(false);
                } else {
                    setLocation(currentX, currentY);
                }
            } else {
                setLocation(currentX, currentY);
            }
            if ((moveType != 0) && (currentY == 7)) { // promotion
                String[] options = {"Queen", "Rook", "Bishop", "Knight"};
                String choice = (String) JOptionPane.showInputDialog(
                        null,
                        "Choose piece for promotion:",
                        "Pawn Promotion",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]
                    );
                if (choice == "Queen") {
                    BQueen bQueen = new BQueen();
                    getWorld().addObject(bQueen,currentX,currentY);
                    bQueen.currentX = currentX;
                    bQueen.currentY = currentY;
                    bQueen.colour = -1;
                } else if (choice == "Rook") {
                    BRook bRook = new BRook();
                    getWorld().addObject(bRook,currentX,currentY);
                    bRook.currentX = currentX;
                    bRook.currentY = currentY;
                    bRook.colour = -1;
                } else if (choice == "Bishop") {
                    BBishop bBishop = new BBishop();
                    getWorld().addObject(bBishop,currentX,currentY);
                    bBishop.currentX = currentX;
                    bBishop.currentY = currentY;
                    bBishop.colour = -1;
                } else if (choice == "Knight") {
                    BKnight bKnight = new BKnight();
                    getWorld().addObject(bKnight,currentX,currentY);
                    bKnight.currentX = currentX;
                    bKnight.currentY = currentY;
                    bKnight.colour = -1;
                }
                getWorld().removeObject(this);
            }
        }
    }
}
