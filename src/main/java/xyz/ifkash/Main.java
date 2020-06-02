package xyz.ifkash;

import xyz.ifkash.engine.GameEngine;
import xyz.ifkash.engine.IGameLogic;
import xyz.ifkash.game.DummyGame;

public class Main {

    public static void main(String[] args) {
        try {
            boolean vSync = true;
            IGameLogic gameLogic = new DummyGame();
            GameEngine gameEngine = new GameEngine("Game Engine", 1280, 720, vSync, gameLogic);
            gameEngine.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
