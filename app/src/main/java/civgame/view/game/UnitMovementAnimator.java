package civgame.view.game;

import civgame.model.map.HexCoordinate;
import civgame.model.unit.Unit;
import civgame.util.HexGeometry;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

public class UnitMovementAnimator {

    private static final int TIMER_DELAY_MILLISECONDS = 16;
    private static final double PROGRESS_PER_TICK = 0.08;

    private Unit animatingUnit;
    private LinkedList<HexCoordinate> remainingPath;
    private HexCoordinate segmentStart;
    private HexCoordinate segmentEnd;
    private double segmentProgress;
    private Timer timer;
    private Runnable onMovementFinished;
    private Runnable repaintCallback;

    public void setRepaintCallback(Runnable repaintCallback) {
        this.repaintCallback = repaintCallback;
    }

    public boolean isAnimating() {
        return animatingUnit != null;
    }

    public Unit getAnimatingUnit() {
        return animatingUnit;
    }

    public void startMovement(Unit unit, List<HexCoordinate> path, Runnable onMovementFinished) {
        this.animatingUnit = unit;
        this.remainingPath = new LinkedList<HexCoordinate>(path);
        this.onMovementFinished = onMovementFinished;
        this.segmentStart = unit.getCoordinate();
        this.segmentProgress = 0;
        startNextSegmentOrFinish();
    }

    private void startNextSegmentOrFinish() {
        if (remainingPath.isEmpty()) {
            finishMovement();
            return;
        }

        segmentEnd = remainingPath.poll();
        segmentProgress = 0;
        runTimer();
    }

    private void runTimer() {
        timer = new Timer(TIMER_DELAY_MILLISECONDS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                advanceAnimation();
            }
        });
        timer.start();
    }

    private void advanceAnimation() {
        segmentProgress = segmentProgress + PROGRESS_PER_TICK;

        if (segmentProgress >= 1.0) {
            timer.stop();
            animatingUnit.setCoordinate(segmentEnd);
            segmentStart = segmentEnd;
            startNextSegmentOrFinish();
            return;
        }

        triggerRepaint();
    }

    private void finishMovement() {
        Runnable callback = onMovementFinished;

        animatingUnit = null;
        remainingPath = null;
        onMovementFinished = null;

        triggerRepaint();

        if (callback != null) {
            callback.run();
        }
    }

    private void triggerRepaint() {
        if (repaintCallback != null) {
            repaintCallback.run();
        }
    }

    public Point2D getInterpolatedPixelPosition(double hexSize) {
        Point2D startPixel = HexGeometry.axialToPixel(segmentStart, hexSize);
        Point2D endPixel = HexGeometry.axialToPixel(segmentEnd, hexSize);

        double interpolatedX = startPixel.getX() + (endPixel.getX() - startPixel.getX()) * segmentProgress;
        double interpolatedY = startPixel.getY() + (endPixel.getY() - startPixel.getY()) * segmentProgress;

        return new Point2D.Double(interpolatedX, interpolatedY);
    }

}