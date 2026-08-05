import greenfoot.*;

/** A non-modal result card shown over the board when the game ends. */
public class GameOverPopup extends Actor {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 180;
    private static final Color BACKGROUND = new Color(24, 29, 42);

    public GameOverPopup(boolean checkmate, int winner) {
        GreenfootImage image = new GreenfootImage(WIDTH, HEIGHT);
        image.setColor(BACKGROUND);
        image.fillRect(0, 0, WIDTH - 1, HEIGHT - 1);
        image.setColor(new Color(224, 178, 72));
        image.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);
        image.drawRect(4, 4, WIDTH - 9, HEIGHT - 9);

        String title = checkmate ? "CHECKMATE" : "STALEMATE";
        String result = checkmate ? winnerName(winner) + " wins" : "Draw";
        drawCentered(image, title, 30, 24);
        drawCentered(image, result, 25, 75);
        drawCentered(image, "The game is over", 20, 124);
        setImage(image);
    }

    private void drawCentered(GreenfootImage card, String text, int fontSize, int y) {
        GreenfootImage label = new GreenfootImage(text, fontSize, Color.WHITE, BACKGROUND);
        int x = (card.getWidth() - label.getWidth()) / 2;
        card.drawImage(label, x, y);
    }

    private String winnerName(int winner) {
        if (winner == 1) {
            return "White";
        }
        if (winner == -1) {
            return "Black";
        }
        return "Nobody";
    }
}
