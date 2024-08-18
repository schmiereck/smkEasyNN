package de.schmiereck.smkEasyNN.engineWorld;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayDeque;
import java.util.Deque;

public class EngineWorldPanel extends JPanel {
    final EngineWorldService engineWorldService;

    class HistoryEntry {

        record LocationHistoryEntry(int countSum, int typeSum, int energySum, int impulseSum) {

        }

        LocationHistoryEntry[] locationHistoryEntryArr;

        public HistoryEntry(int locationCount) {
            this.locationHistoryEntryArr = new LocationHistoryEntry[locationCount];
        }
    }

    final FPSCounter fpsCounter = new FPSCounter();

    final Deque<HistoryEntry> historyDeque = new ArrayDeque<>();
    final int historyMaxSize = 900;

    public EngineWorldPanel(final EngineWorldService engineWorldService) {
        this.engineWorldService = engineWorldService;
    }

    public void view() {
        System.out.println("START: View...");

        while (true) {
            this.repaint();
            this.fpsCounter.frameRendered();

            try {
                Thread.sleep(25*1);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static final Color[] TypeColorArr = new Color[] {
            new Color(110, 180, 110),
            new Color(80, 120, 230),
            new Color(230, 200, 10)
    };

    public static final Color[] EnergyColorArr = new Color[] {
            new Color(110, 190, 250),
            new Color(70, 140, 250),
            new Color(80, 180, 170),
            new Color(80, 220, 170),
    };

    public static final Color[] ImpulseColorArr = new Color[] {
            new Color(180, 180, 70),
            new Color(220, 10, 130),
            new Color(250, 90, 90),
    };

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2d = (Graphics2D) g;

        final int viewHeight = this.getHeight();
        final int topHeight = viewHeight / 3;
        final int downHeight = viewHeight - topHeight;
        final int viewWidth = this.getWidth();

        // Translate the origin to the middle of the panel
        g2d.translate(0, topHeight);

        // Save the original transform
        final AffineTransform originalTransform = g2d.getTransform();

        // Flip the y-axis
        g2d.scale(1.0D, -1.0D);

        g2d.setColor(Color.GRAY);
        g2d.drawLine(0, 0, viewWidth, 0);

        final EwState[] locationEwStateArr = this.engineWorldService.locationEwStateArr;

        final int count = this.engineWorldService.locationCount * this.engineWorldService.typeCount * this.engineWorldService.energyCount * this.engineWorldService.impulseCount;

        final float stateViewWidth = (viewWidth / count);

        final float stateHeight = topHeight / 5;
        //final int stateHeight = stateViewWidth;

        //final int worldWidth = viewWidth - ((viewWidth) % (countSum));
        final float worldWidth = viewWidth - (stateViewWidth % (count));

        final float locationWidth = worldWidth / this.engineWorldService.locationCount;
        final float yLocation = 1;

        final float typeWidth = (locationWidth - 2) / this.engineWorldService.typeCount;
        final float yType = yLocation + stateHeight + 1;

        final float energyWidth = (typeWidth - 2) / this.engineWorldService.energyCount;
        final float yEnergy = yType + stateHeight + 1;

        final float impulseWidth = (energyWidth - 2) / this.engineWorldService.impulseCount;
        final float yImpulse = yEnergy + stateHeight + 1;

        int sumCount = 0;
        int sumEnergyCount = 0;

        final HistoryEntry historyEntry = new HistoryEntry(this.engineWorldService.locationCount);

        for (int locationPos = 0; locationPos < this.engineWorldService.locationCount; locationPos++) {
            final EwState locationEwState = locationEwStateArr[locationPos];

            int historyCountSum = 0;
            int historyTypeSum = 0;
            int historyEnergySum = 0;
            int historyImpulseSum = 0;

            final float xLocation = (locationPos * locationWidth);

            for (int typePos = 0; typePos < this.engineWorldService.typeCount; typePos++) {
                final EwState typeEwState = locationEwState.ewStateArr[typePos];

                final float xType = xLocation + 1 + typePos * typeWidth;
                int typeCountSum = 0;

                for (int energyPos = 0; energyPos < this.engineWorldService.energyCount; energyPos++) {
                    final EwState energyEwState = typeEwState.ewStateArr[energyPos];

                    final float xEnergy = xType + energyPos * energyWidth + 1;
                    int energyCountSum = 0;

                    for (int impulsePos = 0; impulsePos < this.engineWorldService.impulseCount; impulsePos++) {
                        final EwState impulseEwState = energyEwState.ewStateArr[impulsePos];

                        final float xImpulse = xEnergy + impulsePos * impulseWidth + 1;
                        int impulseCountSum = 0;

                        if (impulseEwState.count > 0) {

                            sumCount += impulseEwState.count;
                            sumEnergyCount += (typePos + 1) * (energyPos + 1) * (impulsePos + 1) * impulseEwState.count;

                            historyCountSum += impulseEwState.count;
                            //historyTypeSum |= (typePos + 1) * (energyPos + 1) * (impulsePos + 1);
                            historyTypeSum |= ((typePos + 1) * 32);
                            historyEnergySum |= ((energyPos + 1) * 49);
                            historyImpulseSum |= ((impulsePos + 1) * 67);

                            typeCountSum += impulseEwState.count;
                            energyCountSum += impulseEwState.count;
                            impulseCountSum += impulseEwState.count;
                        }
                        g2d.setColor(ImpulseColorArr[impulsePos]);
                        if (impulseWidth > 4) {
                            g2d.draw(new Rectangle2D.Float(xImpulse, yImpulse, impulseWidth, stateHeight));
                        }
                        final float impulseCountHeight = (impulseCountSum * stateHeight) / this.engineWorldService.stateMaxCount;
                        g2d.fill(new Rectangle2D.Float(xImpulse, yImpulse, impulseWidth, impulseCountHeight));
                    }
                    g2d.setColor(EnergyColorArr[energyPos]);
                    if (energyWidth > 4) {
                        g2d.draw(new Rectangle2D.Float(xEnergy, yEnergy, energyWidth, stateHeight));
                    }
                    final float energyCountHeight = (energyCountSum * stateHeight) / this.engineWorldService.stateMaxCount;
                    g2d.fill(new Rectangle2D.Float(xEnergy, yEnergy, energyWidth, energyCountHeight));
                }
                g2d.setColor(TypeColorArr[typePos]);
                if (typeWidth > 4) {
                    g2d.draw(new Rectangle2D.Float(xType, yType, typeWidth, stateHeight));
                }
                final float typeCountHeight = (typeCountSum * stateHeight) / this.engineWorldService.stateMaxCount;
                g2d.fill(new Rectangle2D.Float(xType, yType, typeWidth, typeCountHeight));
            }

            g2d.setColor(Color.ORANGE);
            g2d.draw(new Rectangle2D.Float(xLocation, yLocation, locationWidth, stateHeight));
            final float locationCountHeight = (historyCountSum * stateHeight) / this.engineWorldService.stateMaxCount;
            g2d.fill(new Rectangle2D.Float(xLocation, yLocation, locationWidth, locationCountHeight));

            historyEntry.locationHistoryEntryArr[locationPos] =
                    new HistoryEntry.LocationHistoryEntry(historyCountSum, historyTypeSum, historyEnergySum, historyImpulseSum);
        }

        while (this.historyDeque.size() >= this.historyMaxSize) {
            this.historyDeque.removeLast();
        }
        this.historyDeque.addFirst(historyEntry);

        // Statistics:
        g2d.setTransform(originalTransform);
        g.setColor(Color.BLACK);
        g.drawString("Sum Count: " + sumCount, 10, -topHeight + 16);
        g.drawString("Sum Energy Count: " + sumEnergyCount, 10, -topHeight + 16 * 2);

        g.drawString("Calc FPS: %d".formatted(this.engineWorldService.fpsCounter.getFPS()), 10 + 180, -topHeight + 16);
        g.drawString("View FPS: %d".formatted(this.fpsCounter.getFPS()), 10 + 180, -topHeight + 16 * 2);

        // History:
        final float historyEntryHeight = downHeight / (float)this.historyMaxSize;
        for (int historyPos = 0; historyPos < this.historyDeque.size(); historyPos++) {
            final HistoryEntry historyEntry1 = (HistoryEntry) this.historyDeque.toArray()[historyPos];

            final float yHistoryLocation = (historyEntryHeight * historyPos);
            //g.drawRect(0, yHistoryLocation, worldWidth, historyEntryHeight);

            for (int locationPos = 0; locationPos < this.engineWorldService.locationCount; locationPos++) {
                final HistoryEntry.LocationHistoryEntry locationHistoryEntry = historyEntry1.locationHistoryEntryArr[locationPos];
                if (locationHistoryEntry.countSum > 0) {
                    final float countPercent = ((float) locationHistoryEntry.countSum) / this.engineWorldService.stateMaxCount;
                    final int countAlpha = Math.min(255, (int) (countPercent * 255));

                    final float xHistoryLocation = (locationPos * locationWidth);
                    //g2d.setColor(new Color(255, 200, 0, countAlpha));
                    g2d.setColor(createColorById(locationHistoryEntry.typeSum, locationHistoryEntry.energySum, locationHistoryEntry.impulseSum, countAlpha));
                    //g2d.drawRect(xHistoryLocation, yHistoryLocation, locationWidth, historyEntryHeight);
                    //g2d.fillRect(xHistoryLocation, yHistoryLocation, locationWidth, (int)historyEntryHeight);
                    //g2d.fill(new Rectangle(xHistoryLocation, yHistoryLocation, locationWidth, (int)historyEntryHeight));
                    g2d.fill(new Rectangle2D.Float(xHistoryLocation, yHistoryLocation, locationWidth, historyEntryHeight));
                }
            }
        }
    }

    private static Color createColorById(final int id, final int countAlpha) {
        return new Color(
                (int) (id * 64) % 255,
                (int) ((id + 64) * 32) % 255,
                (int) ((id + 128) * 16) % 255,
                countAlpha);
    }

    private static Color createColorById(final int id1, final int id2, final int id3, final int countAlpha) {
        return new Color(
                (int) (id2 * 64) % 255,
                (int) ((id3 + 64) * 32) % 255,
                (int) ((id1 + 128) * 16) % 255,
                countAlpha);
    }

}