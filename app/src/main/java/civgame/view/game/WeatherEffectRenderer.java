package civgame.view.game;

import civgame.model.game.SeasonType;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WeatherEffectRenderer {

    private static final int PARTICLE_COUNT = 90;
    private static final int SNOWFLAKE_SIZE = 3;

    private final List<double[]> particles;
    private final Random random;

    public WeatherEffectRenderer() {
        this.particles = new ArrayList<double[]>();
        this.random = new Random();
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            particles.add(new double[]{random.nextDouble(), random.nextDouble()});
        }
    }

    public void advance(SeasonType season) {
        for (int i = 0; i < particles.size(); i++) {
            double[] particle = particles.get(i);

            if (season == SeasonType.WINTER) {
                particle[1] = particle[1] + 0.004;
                particle[0] = particle[0] + 0.0008;
            } else if (season == SeasonType.AUTUMN) {
                particle[1] = particle[1] + 0.014;
                particle[0] = particle[0] - 0.004;
            } else {
                continue;
            }

            if (particle[1] > 1.0 || particle[0] > 1.0 || particle[0] < 0.0) {
                particle[0] = random.nextDouble();
                particle[1] = 0.0;
            }
        }
    }

    public void draw(Graphics2D graphics2D, SeasonType season, int panelWidth, int panelHeight) {
        if (season != SeasonType.WINTER && season != SeasonType.AUTUMN) {
            return;
        }

        if (season == SeasonType.WINTER) {
            graphics2D.setColor(Color.WHITE);
        } else {
            graphics2D.setColor(new Color(150, 170, 200));
        }

        for (int i = 0; i < particles.size(); i++) {
            double[] particle = particles.get(i);
            int x = (int) (particle[0] * panelWidth);
            int y = (int) (particle[1] * panelHeight);

            if (season == SeasonType.WINTER) {
                graphics2D.fillOval(x, y, SNOWFLAKE_SIZE, SNOWFLAKE_SIZE);
            } else {
                graphics2D.drawLine(x, y, x - 7, y + 12);
            }
        }
    }
}