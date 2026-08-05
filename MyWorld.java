import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public class MyWorld extends World {
    public MyWorld() {
        super(8, 8, 100);
        prepare();
    }

    public void prepare() {
        Piece.turn = Piece.WHITE;
        Piece.whiteCanCastleShort = true;
        Piece.whiteCanCastleLong = true;
        Piece.blackCanCastleShort = true;
        Piece.blackCanCastleLong = true;
        Piece.checkmate = false;
        Piece.stalemate = false;
        Piece.gameOver = false;
        Piece.winner = 0;
        Piece.whiteInCheck = false;
        Piece.blackInCheck = false;

        for (int row = 0; row < 4; ++row) {
            for (int col = 0; col < 4; ++col) {
                addObject(new LightSquare(), col * 2, row * 2);
            }
        }
        for (int row = 0; row < 4; ++row) {
            for (int col = 0; col < 4; ++col) {
                addObject(new LightSquare(), 1 + col * 2, 1 + row * 2);
            }
        }
        for (int row = 0; row < 4; ++row) {
            for (int col = 0; col < 4; ++col) {
                addObject(new DarkSquare(), 1 + col * 2, row * 2);
            }
        }
        for (int row = 0; row < 4; ++row) {
            for (int col = 0; col < 4; ++col) {
                addObject(new DarkSquare(), col * 2, 1 + row * 2);
            }
        }

        for (int i = 0; i < 8; ++i) {
            place(new Pawn(Piece.WHITE), i, 6);
            place(new Pawn(Piece.BLACK), i, 1);
        }
        for (int i = 0; i < 2; ++i) {
            place(new Rook(Piece.WHITE), i * 7, 7);
            place(new Rook(Piece.BLACK), i * 7, 0);
            place(new Knight(Piece.WHITE), 1 + i * 5, 7);
            place(new Knight(Piece.BLACK), 1 + i * 5, 0);
            place(new Bishop(Piece.WHITE), 2 + i * 3, 7);
            place(new Bishop(Piece.BLACK), 2 + i * 3, 0);
        }
        place(new Queen(Piece.WHITE), 3, 7);
        place(new King(Piece.WHITE), 4, 7);
        place(new Queen(Piece.BLACK), 3, 0);
        place(new King(Piece.BLACK), 4, 0);
    }

    private void place(Piece piece, int x, int y) {
        addObject(piece, x, y);
        piece.currentX = x;
        piece.currentY = y;
    }
}
