package civgame.view.game;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class DiceFacePanel extends JPanel {

    private static final int DIE_SIZE = 54;
    private static final int PIP_RADIUS = 5;

    private int value;
    private boolean highlightWin;
    private boolean highlightLoss;

    public DiceFacePanel() {
        this.value = 1;
        setPreferredSize(new Dimension(DIE_SIZE, DIE_SIZE));
        setOpaque(false);
    }

    public void setValue(int value) {
        this.value = value;
        repaint();
    }

    public void setHighlight(boolean win, boolean loss) {
        this.highlightWin = win;
        this.highlightLoss = loss;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color faceColor = Color.WHITE;
        if (highlightWin) {
            faceColor = new Color(170, 230, 170);
        } else if (highlightLoss) {
            faceColor = new Color(230, 170, 170);
        }

        graphics2D.setColor(faceColor);
        graphics2D.fillRoundRect(2, 2, DIE_SIZE - 4, DIE_SIZE - 4, 10, 10);
        graphics2D.setColor(Color.BLACK);
        graphics2D.drawRoundRect(2, 2, DIE_SIZE - 4, DIE_SIZE - 4, 10, 10);

        drawPips(graphics2D);
    }

    private void drawPips(Graphics2D graphics2D) {
        graphics2D.setColor(Color.BLACK);

        int left = DIE_SIZE / 4;
        int center = DIE_SIZE / 2;
        int right = DIE_SIZE - DIE_SIZE / 4;
        int top = DIE_SIZE / 4;
        int middle = DIE_SIZE / 2;
        int bottom = DIE_SIZE - DIE_SIZE / 4;

        if (value == 1) {
            drawPip(graphics2D, center, middle);
        } else if (value == 2) {
            drawPip(graphics2D, left, top);
            drawPip(graphics2D, right, bottom);
        } else if (value == 3) {
            drawPip(graphics2D, left, top);
            drawPip(graphics2D, center, middle);
            drawPip(graphics2D, right, bottom);
        } else if (value == 4) {
            drawPip(graphics2D, left, top);
            drawPip(graphics2D, right, top);
            drawPip(graphics2D, left, bottom);
            drawPip(graphics2D, right, bottom);
        } else if (value == 5) {
            drawPip(graphics2D, left, top);
            drawPip(graphics2D, right, top);
            drawPip(graphics2D, center, middle);
            drawPip(graphics2D, left, bottom);
            drawPip(graphics2D, right, bottom);
        } else {
            drawPip(graphics2D, left, top);
            drawPip(graphics2D, right, top);
            drawPip(graphics2D, left, middle);
            drawPip(graphics2D, right, middle);
            drawPip(graphics2D, left, bottom);
            drawPip(graphics2D, right, bottom);
        }
    }

    private void drawPip(Graphics2D graphics2D, int centerX, int centerY) {
        graphics2D.fillOval(centerX - PIP_RADIUS, centerY - PIP_RADIUS, PIP_RADIUS * 2, PIP_RADIUS * 2);
    }
}