package xyz.ifkash.engine;

public class GameEngine implements Runnable {

    // Static constants
    public static final int TARGET_FPS = 75;
    public static final int TARGET_UPS = 30;

    /* Variables */
    private final Window window;
    private final Timer timer;
    private final IGameLogic gameLogic;

    public GameEngine(String windowTitle, int width, int height, boolean vSync, IGameLogic gameLogic) throws Exception {
        window = new Window(windowTitle, width, height, vSync);
        this.gameLogic = gameLogic;
        timer = new Timer();
    }

    @Override
    public void run() {
        try {
            init();
            gameLoop();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void init() throws Exception {
        window.init();
        timer.init();
        gameLogic.init();
    }

    public void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;

        while (!window.windowShouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator = accumulator + elapsedTime;

            input();

            while (accumulator >= interval) {
                update(interval);
                accumulator = accumulator - interval;
            }

            render();

            if(!window.isvSync()) {
                sync();
            }
        }
    }

    private void sync() {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;

        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void input() {
        gameLogic.input(window);
    }

    public void update(float interval) {
        gameLogic.update(interval);
    }

    public void render() {
        gameLogic.render(window);
        window.update();
    }

}
