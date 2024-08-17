package de.schmiereck.smkEasyNN.engineWorld;

import javax.swing.*;
import java.awt.*;

public class EngineWorldPanel extends JPanel {
    final EngineWorldService engineWorldService;

    public EngineWorldPanel(final EngineWorldService engineWorldService) {
        this.engineWorldService = engineWorldService;
    }

    public void simulate() {
        System.out.println("START: Simulating...");

        for (int t = 0; t < 3000; t++) {

            this.engineWorldService.run();

            //if (t % 1 == 0)
            {
                this.repaint();
                try {
                    //Thread.sleep(25*1);
                    Thread.sleep(5*1);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("END: Simulating.");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2d = (Graphics2D) g;

        // Translate the origin to the middle of the panel
        g2d.translate(0, this.getHeight() / 2);

        // Flip the y-axis
        g2d.scale(1.0D, -1.0D);

        g2d.setColor(Color.GRAY);
        g2d.drawLine(0, 0, this.getWidth(), 0);

        final EwState[] locationEwStateArr = this.engineWorldService.locationEwStateArr;

        final int viewWidth = this.getWidth();

        final int count = this.engineWorldService.locationCount * this.engineWorldService.typeCount * this.engineWorldService.energyCount * this.engineWorldService.impulseCount;

        final int stateViewWidth = (viewWidth / count);

        final int stateHeight = this.getHeight() / 2 / 8;
        //final int stateHeight = stateViewWidth;

        //final int width = viewWidth - ((viewWidth) % (count));
        final int width = viewWidth - (stateViewWidth % (count));

        final int locationWidth = width / this.engineWorldService.locationCount;
        final int locationY = 1;

        final int typeWidth = (locationWidth - 2) / this.engineWorldService.typeCount;
        final int yType = locationY + stateHeight + 1;

        final int energyWidth = (typeWidth - 2) / this.engineWorldService.energyCount;
        final int yEnergy = yType + stateHeight + 1;

        final int impulseWidth = (energyWidth - 2) / this.engineWorldService.impulseCount;
        final int yImpulse = yEnergy + stateHeight + 1;

        for (int locationPos = 0; locationPos < this.engineWorldService.locationCount; locationPos++) {
            final EwState locationEwState = locationEwStateArr[locationPos];

            final int xLocation = (locationPos * locationWidth);
            g2d.setColor(Color.ORANGE);
            g2d.drawRect(xLocation, locationY, locationWidth, stateHeight);

            for (int typePos = 0; typePos < this.engineWorldService.typeCount; typePos++) {
                final EwState typeEwState = locationEwState.ewStateArr[typePos];

                final int xType = xLocation + 1 + typePos * typeWidth;
                g2d.setColor(Color.GREEN);
                g2d.drawRect(xType, yType, typeWidth, stateHeight);

                for (int energyPos = 0; energyPos < this.engineWorldService.energyCount; energyPos++) {
                    final EwState energyEwState = typeEwState.ewStateArr[energyPos];

                    final int xEnergy = xType + energyPos * energyWidth + 1;
                    g2d.setColor(Color.BLUE);
                    g2d.drawRect(xEnergy, yEnergy, energyWidth, stateHeight);

                    for (int impulsePos = 0; impulsePos < this.engineWorldService.impulseCount; impulsePos++) {
                        final EwState impulseEwState = energyEwState.ewStateArr[impulsePos];

                        final int xImpulse = xEnergy + impulsePos * impulseWidth + 1;
                        g2d.setColor(Color.RED);
                        g2d.drawRect(xImpulse, yImpulse, impulseWidth, stateHeight);

                        if (impulseEwState.count > 0) {
                            final int countHeight = (impulseEwState.count * stateHeight) / this.engineWorldService.stateMaxCount;
                            g2d.fillRect(xImpulse, yImpulse, impulseWidth, countHeight);
                        }
                    }
                }
            }
        }
    }
}