package de.schmiereck.smkEasyNN.gridworld;

public class GameStatistic {
    public final int netPos;
    public int hitGoalCounter = 0;
    public int hitPitCounter = 0;
    public int maxMoveCounter = 0;
    public int hitWallCounter = 0;
    public GridworldGameService.ActionResult actionResult = null;
    public int moveCounter = 0;
    public float mse = 0.0F;
    public int epoche = 0;
    public int level = 0;
    public float fitness = 0.0F;

    public int fittnesCounter = 0;
    public int fittnesHitGoalCounter = 0;

    public float learningRate = 0.3F;
    public float momentum = 0.6F;

    GameStatistic(final int netPos) {
        this.netPos = netPos;
    }
}
