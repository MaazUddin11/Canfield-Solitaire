package canfield;

import ucb.gui.Pad;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import javax.imageio.ImageIO;

import java.io.InputStream;
import java.io.IOException;

/** A widget that displays a Pinball playfield.
 *  @author P. N. Hilfinger
 */
class GameDisplay extends Pad {

    /** Color of display field. */
    private static final Color BACKGROUND_COLOR = Color.green;

    /* Coordinates and lengths in pixels unless otherwise stated. */

    /** Preferred dimensions of the playing surface. */
    private static final int BOARD_WIDTH = 800, BOARD_HEIGHT = 600;

    /** Displayed dimensions of a card image. */
    private static final int CARD_HEIGHT = 125, CARD_WIDTH = 90;

    /** Dimensions of card to be used outside this class,
     * specifically for the mouse events. */
    static final int C_HEIGHT = CARD_HEIGHT, C_WIDTH = CARD_WIDTH;

    /** X coordinate for the RESERVE pile. */
    static final int R_X = 100;
    /** Y coordinate for the RESERVE pile. */
    static final int R_Y = 250;

    /** X coordinate for the STOCK pile. */
    static final int S_X = 100;
    /** Y coordinate for the STOCK pile. */
    static final int S_Y = 425;

    /** X coordinate for the WASTE pile. */
    static final int W_X = 225;
    /** Y coordinate for the WASTE pile. */
    static final int W_Y = 425;

    /** X coordinate for the first FOUNDATION rectangle. */
    static final int F_X = 350;
    /** Y coordinate for the first FOUNDATION rectangle. */
    static final int F_Y = 75;
    /** Number of FOUNDATION spots. */
    static final int F_LEN = 4;
    /** Space between each FOUNDATION spot. */
    static final int F_SPACE = 10;
    /** Distance from first FOUNDATION Rectangle to the second. */
    static final int F_DIST = F_SPACE + CARD_WIDTH;

    /** X coordinate for the first TABLEAU rectangle. */
    static final int T_X = 350;
    /** Y coordinate for the first TABLEAU rectangle. */
    static final int T_Y = 250;
    /** Number of TABLEAU spots. */
    static final int T_LEN = 4;
    /** Space between each TABLEAU spot. */
    static final int T_SPACE = 10;
    /** Distance from first TABLEAU Rectangle to the second. */
    static final int T_DIST = T_SPACE + CARD_WIDTH;

    /** A graphical representation of GAME. */
    public GameDisplay(Game game) {
        _game = game;
        setPreferredSize(BOARD_WIDTH, BOARD_HEIGHT);
    }

    /** A graphical representation of the empty WASTE, TABLEAU,
     *  and FOUNDATION slots using the graphics of G. */
    public void emptyRects(Graphics2D g) {
        g.setColor(Color.black);
        for (int i = F_X; i < F_X + (F_LEN * (F_DIST)); i += (F_DIST)) {
            g.drawRect(i, F_Y, CARD_WIDTH, CARD_HEIGHT);
        }
        for (int i = T_X; i < T_X + (T_LEN * (T_DIST)); i += (T_DIST)) {
            g.drawRect(i, T_Y, CARD_WIDTH, CARD_HEIGHT);
        }
        g.drawRect(W_X, W_Y, CARD_WIDTH, CARD_HEIGHT);
    }

    /** Method which paints all the cards in a TABLEAU PILE using the graphics
     * of G the TABSIZE to determine how many cards there are and TABNUMBER
     * to determine which TABLEAU PILE it is. */
    public void drawPile(Graphics2D g, int tabNumber, int tabSize) {
        int y = T_Y;
        for (int i = (tabSize - 1); i >= 0; i--) {
            Card t = _game.getTableau(tabNumber, i);
            paintCard(g, t, (tabNumber * (T_DIST)) + (T_X - (T_DIST)), y);
            y += (CARD_HEIGHT / 6);
        }
    }

    /** Return an Image read from the resource named NAME. */
    private Image getImage(String name) {
        InputStream in =
            getClass().getResourceAsStream("/canfield/resources/" + name);
        try {
            return ImageIO.read(in);
        } catch (IOException excp) {
            return null;
        }
    }

    /** Return an Image of CARD. */
    private Image getCardImage(Card card) {
        return getImage("playing-cards/" + card + ".png");
    }

    /** Return an Image of the back of a card. */
    private Image getBackImage() {
        return getImage("playing-cards/blue-back.png");
    }

    /** Draw CARD at X, Y on G. */
    private void paintCard(Graphics2D g, Card card, int x, int y) {
        if (card != null) {
            g.drawImage(getCardImage(card), x, y,
                        CARD_WIDTH, CARD_HEIGHT, null);
        }
    }

    /** Draw card back at X, Y on G. */
    private void paintBack(Graphics2D g, int x, int y) {
        g.drawImage(getBackImage(), x, y, CARD_WIDTH, CARD_HEIGHT, null);
    }

    @Override
    public synchronized void paintComponent(Graphics2D g) {
        super.paintComponent(g);
        g.setColor(BACKGROUND_COLOR);
        Rectangle b = g.getClipBounds();
        g.fillRect(0, 0, b.width, b.height);
        emptyRects(g);
        paintCard(g, _game.topWaste(), W_X, W_Y);
        for (int i = 1; i <= T_LEN; i++) {
            int size = _game.tableauSize(i);
            drawPile(g, i, size);
        }
        for (int i = 1; i <= F_LEN; i++) {
            Card f = _game.topFoundation(i);
            paintCard(g, f, (i * (F_DIST)) + (F_X - (F_DIST)), F_Y);
        }
        Card r = _game.topReserve();
        paintCard(g, r, R_X, R_Y);
        paintBack(g, S_X, S_Y);
    }

    /** Game I am displaying. */
    private final Game _game;

}
