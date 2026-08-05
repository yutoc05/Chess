import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public class MyWorld extends World
{
    public MyWorld()
    {    
        super(8, 8, 100);
        prepare();
    }

    public void prepare() {
        Piece.turn = 1;
        WhitePiece.canCastleShort = true;
        WhitePiece.canCastleLong = true;
        Piece.checkmate = false;
        Piece.stalemate = false;
        Piece.gameOver = false;
        Piece.winner = 0;
        WhitePiece.inCheck = false;
        BlackPiece.canCastleShort = true;
        BlackPiece.canCastleLong = true;
        BlackPiece.inCheck = false;
        // Setting up board
        for (int row = 0; row < 4; ++row) {
            for (int col = 0; col < 4; ++col) {
                LightSquare lsquare = new LightSquare();
                addObject(lsquare,(col * 2),(row * 2));
            }
        }
        for (int row = 0; row < 4; ++row) {
            for (int col = 0; col < 4; ++col) {
                LightSquare lsquare = new LightSquare();
                addObject(lsquare,1 + (col * 2),1 + (row * 2));
            }
        }
        for (int row = 0; row < 4; ++row) {
            for (int col = 0; col < 4; ++col) {
                DarkSquare dsquare = new DarkSquare();
                addObject(dsquare,1 + (col * 2),(row * 2));
            }
        }
        for (int row = 0; row < 4; ++row) {
            for (int col = 0; col < 4; ++col) {
                DarkSquare dsquare = new DarkSquare();
                addObject(dsquare,(col * 2),1 + (row * 2));
            }
        }

        // Setting up pieces
        for (int i = 0; i < 8; ++i) {
            WPawn wPawn = new WPawn();
            addObject(wPawn,i,6);
            wPawn.currentX = i;
            wPawn.currentY = 6;
            wPawn.colour = 1;
        }
        for (int i = 0; i < 2; ++i) {
            WRook wRook = new WRook();
            addObject(wRook,(i * 7), 7);
            wRook.currentX = (i * 7);
            wRook.currentY = 7;
            wRook.colour = 1;
        }
        for (int i = 0; i < 2; ++i) {
            WKnight wKnight = new WKnight();
            addObject(wKnight,1 + (i * 5), 7);
            wKnight.currentX = 1 + (i * 5);
            wKnight.currentY = 7;
            wKnight.colour = 1;
        }
        for (int i = 0; i < 2; ++i) {
            WBishop wBishop = new WBishop();
            addObject(wBishop,2 + (i * 3), 7);
            wBishop.currentX = 2 + (i * 3);
            wBishop.currentY = 7;
            wBishop.colour = 1;
        }
        WQueen wQueen = new WQueen();
        addObject(wQueen,3,7);
        wQueen.currentX = 3;
        wQueen.currentY = 7;
        wQueen.colour = 1;
        WKing wKing = new WKing();
        addObject(wKing,4,7);
        wKing.currentX = 4;
        wKing.currentY = 7;
        wKing.colour = 1;
        for (int i = 0; i < 8; ++i) {
            BPawn bPawn = new BPawn();
            addObject(bPawn,i,1);
            bPawn.currentX = i;
            bPawn.currentY = 1;
            bPawn.colour = -1;
        }
        for (int i = 0; i < 2; ++i) {
            BRook bRook = new BRook();
            addObject(bRook,(i * 7), 0);
            bRook.currentX = (i * 7);
            bRook.currentY = 0;
            bRook.colour = -1;
        }
        for (int i = 0; i < 2; ++i) {
            BKnight bKnight = new BKnight();
            addObject(bKnight,1 + (i * 5), 0);
            bKnight.currentX = 1 + (i * 5);
            bKnight.currentY = 0;
            bKnight.colour = -1;
        }
        for (int i = 0; i < 2; ++i) {
            BBishop bBishop = new BBishop();
            addObject(bBishop,2 + (i * 3), 0);
            bBishop.currentX = 2 + (i * 3);
            bBishop.currentY = 0;
            bBishop.colour = -1;
        }
        BQueen bQueen = new BQueen();
        addObject(bQueen,3,0);
        bQueen.currentX = 3;
        bQueen.currentY = 0;
        bQueen.colour = -1;
        BKing bKing = new BKing();
        addObject(bKing,4,0);
        bKing.currentX = 4;
        bKing.currentY = 0;
        bKing.colour = -1;
    }
}
