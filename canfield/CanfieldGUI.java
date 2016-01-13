package canfield;

import ucb.gui.TopLevel;
import ucb.gui.LayoutSpec;

import java.awt.event.MouseEvent;

/** A top-level GUI for Canfield solitaire.
 *  @author Maaz Uddin
 */
class CanfieldGUI extends TopLevel {

    /** A new window with given TITLE and displaying GAME. */
    CanfieldGUI(String title, Game game) {
        super(title, true);
        _game = game;

        addMenuButton("Menu->New Game", "newGame");
        addMenuButton("Menu->Undo", "undo");
        addMenuButton("Menu->Quit", "quit");

        _display = new GameDisplay(game);
        add(_display, new LayoutSpec("y", 2, "width", 2));
        _display.setMouseHandler("click", this, "mouseClicked");
        _display.setMouseHandler("release", this, "mouseReleased");
        _display.setMouseHandler("drag", this, "mouseDragged");
        display(true);
    }

    /** Respond to "Quit" button. */
    public void quit(String dummy) {
        if (showOptions("Are you sure you want to Quit?", "Quit?", "question",
                        "Yes", "Yes", "No") == 0) {
            System.exit(1);
        }
    }

    /** Respond to "Undo" button. */
    public void undo(String dummy) {
        _game.undo();
        _display.repaint();
    }

    /** Respond to "New" button. */
    public void newGame(String dummy) {
        if (showOptions("Are you sure you want to Start a New Game?",
            "New Game?", "question", "Yes", "Yes", "No") == 0) {
            _game.deal();
        }
        _display.repaint();
    }

    /** Variable to represent what CARD is selected. */
    private Card selectedCard;

    /* === Methods of mouse events. === */

    /** Action in response to mouse-clicking event EVENT. */
    public synchronized void mouseClicked(MouseEvent event) {
        int x = event.getX(), y = event.getY(), tabNumber = 0, foundNumber = 0;
        if (clickedStock(x, y)) {
            _game.stockToWaste();
        } else if (selectedCard == null) {
            if (clickedWaste(x, y)) {
                selectedCard = _game.topWaste();
            }
            if  (clickedReserve(x, y)) {
                selectedCard = _game.topReserve();
            }
            if (overAnyTableau(x, y)) {
                selectedCard = _game.topTableau(whichTableau(x, y));
            }
            if (overAnyFoundation(x, y)) {
                selectedCard = _game.topFoundation(whichFoundation(x, y));
            }
        } else if (selectedCard == _game.topReserve()) {
            if (overAnyFoundation(x, y)) {
                _game.reserveToFoundation();
            }
            if (overAnyTableau(x, y)) {
                _game.reserveToTableau(whichTableau(x, y));
            }
            selectedCard = null;
        } else if (selectedCard == _game.topWaste()) {
            if (overAnyFoundation(x, y)) {
                _game.wasteToFoundation();
            }
            if (overAnyTableau(x, y)) {
                _game.wasteToTableau(whichTableau(x, y));
            }
            selectedCard = null;
        } else {
            tabNumber = 0; foundNumber = 0;
            for (int i = 1; i <= tabLength; i++) {
                if (selectedCard == _game.topTableau(i)) {
                    tabNumber = i;
                    if (overAnyFoundation(x, y)) {
                        _game.tableauToFoundation(tabNumber);
                    } else if (overAnyTableau(x, y)) {
                        int tableauTemp = whichTableau(x, y);
                        _game.tableauToTableau(tabNumber, tableauTemp);
                    }
                }
            }
            for (int i = 1; i <= foundLength; i++) {
                if (selectedCard == _game.topFoundation(i)) {
                    foundNumber = i;
                    if (overAnyTableau(x, y)) {
                        tabNumber = whichTableau(x, y);
                        _game.foundationToTableau(foundNumber,
                            tabNumber);
                    }
                }
            }
            selectedCard = null;
        }
        _display.repaint();
    }

    /** Action in response to mouse-pressing event EVENT. */
    public synchronized void mousePressed(MouseEvent event) {
        int x = event.getX(), y = event.getY();
        _display.repaint();
    }

    /** Action in response to mouse-released event EVENT. */
    public synchronized void mouseReleased(MouseEvent event) {
        int x = event.getX(), y = event.getY();
        _display.repaint();
    }

    /** Action in response to mouse-dragging event EVENT. */
    public synchronized void mouseDragged(MouseEvent event) {
        _display.repaint();
    }

    /** The board widget. */
    private final GameDisplay _display;

    /** The game I am consulting. */
    private final Game _game;

    /* === Methods used in determining what PILE is being altered. === */

    /** Returns true if it the coordinates (X, Y) are in the range of
     * the PILE with starting coordinates A and B. */
    public boolean mouseInRangeOfPile(int x, int y, int a, int b) {
        return (((a <= x) && (x <= (a + GameDisplay.C_WIDTH)))
            && ((b <= y) && (y <= (b + GameDisplay.C_HEIGHT))));
    }

    /** Returns true if the coordinates (X, Y) are any distance L or S away
     * from points A and B for a collection of PILEs. */
    public boolean mouseInRangeOfCollection(int x, int y, int a,
        int b, int l, int s) {
        return (((a <= x) && (x <= (a + (GameDisplay.C_WIDTH * l)
            + (s * (l - 1))))) && ((b <= y)
            && (y <= (b + GameDisplay.C_HEIGHT))));
    }

    /** Returns an integer using the coordinates (X, Y) and the coordinates
     * of PILEX and PILEY. It takes into account how long
     * the PLENGTH is and the distance between each pile using PSPACE. */
    public int iterPile(int x, int y, int pileX,
        int pileY, int pSpace, int pLength) {
        if ((y >= pileY) && (y <= (pileY + GameDisplay.C_HEIGHT))) {
            for (int i = 1; i <= pLength; i++) {
                if ((x >= pileX) && (x <= (pileX + GameDisplay.C_WIDTH))) {
                    return i;
                } else {
                    pileX += (GameDisplay.C_WIDTH + pSpace);
                }
            }
        }
        return 0;
    }

    /** Returns true if the STOCK is clicked at point (X, Y). */
    public boolean clickedStock(int x, int y) {
        int stockX = GameDisplay.S_X, stockY = GameDisplay.S_Y;
        return mouseInRangeOfPile(x, y, stockX, stockY);
    }

    /** Returns true if the WASTE is clicked at point (X, Y). */
    public boolean clickedWaste(int x, int y) {
        int wasteX = GameDisplay.W_X, wasteY = GameDisplay.W_Y;
        return mouseInRangeOfPile(x, y, wasteX, wasteY);
    }

    /** Returns true if the RESERVE is clicked at point (X, Y). */
    public boolean clickedReserve(int x, int y) {
        int reserveX = GameDisplay.R_X, reserveY = GameDisplay.R_Y;
        return mouseInRangeOfPile(x, y, reserveX, reserveY);
    }

    /** FOUNDATION variables taken from GameDisplay. */
    private int foundX = GameDisplay.F_X, foundY = GameDisplay.F_Y,
        foundLength = GameDisplay.F_LEN, foundSpace = GameDisplay.F_SPACE;
    /** Return true if point (X, Y) is over the set of FOUNDATIONS. */
    public boolean overAnyFoundation(int x, int y) {
        return mouseInRangeOfCollection(x, y, foundX, foundY,
            foundLength, foundSpace);
    }

    /** TABLEAU variables taken from GameDisplay. */
    private int tabX = GameDisplay.T_X, tabY = GameDisplay.T_Y,
        tabLength = GameDisplay.T_LEN, tabSpace = GameDisplay.T_SPACE;
    /** Return true if point (X, Y) is over the set of TABLEAUS. */
    public boolean overAnyTableau(int x, int y) {
        return mouseInRangeOfCollection(x, y, tabX, tabY, tabLength, tabSpace);
    }


    /** Returns an int that determines which TABLEAU PILE to choose from
     * using the coordinates (X, Y). */
    public int whichTableau(int x, int y) {
        return iterPile(x, y, tabX, tabY, tabSpace, tabLength);
    }


    /** Returns an int that determines which FOUNDATION PILE to choose from
     * using the coordinates (X, Y). */
    public int whichFoundation(int x, int y) {
        return iterPile(x, y, foundX, foundY, foundSpace, foundLength);
    }
}
