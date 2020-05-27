package xyz.ifkash.learning;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class BasicsLWJGL {

    /* Variables */
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback keyCallback;

    private long window;

    private float sp = 0.0f;
    private boolean swapColor = false;

    /* Methods */
    private void init() {
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

        if(!glfwInit())
            throw new IllegalStateException("Failed to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        int windowWidth = 1280;
        int windowHeight = 720;
        window = glfwCreateWindow(windowWidth, windowHeight, "QUBE", NULL, NULL);

        if(window == NULL)
            throw new RuntimeException("Failed to create GLFW window.");

        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                    glfwSetWindowShouldClose(window, true);
                }
            }
        });

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        assert vidmode != null;
        glfwSetWindowPos(
                window,
                (vidmode.width() - windowWidth) / 2,
                (vidmode.height() - windowHeight) / 2
        );

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);

        glfwShowWindow(window);
    }

    private void update() {
        sp = sp + 0.001f;

        if(sp > 1.0f) {
            sp = 0.0f;
            swapColor = !swapColor;
        }
    }
    
    private void render() {
        drawQuad();
    }

    private void drawQuad() {
        if(!swapColor)
            glColor3f(0.0f, 1.0f, 0.0f);
        else
            glColor3f(0.0f, 0.0f, 1.0f);

        glBegin(GL_QUADS);
        {
            glVertex3f(-sp, -sp, 0.0f);
            glVertex3f(sp, -sp, 0.0f);
            glVertex3f(sp, sp, 0.0f);
            glVertex3f(-sp, sp, 0.0f);
        }
        glEnd();
    }

    private void loop() {
        GL.createCapabilities();

        System.out.println("--------------------------------");
        System.out.println("OpenGL version: " + glGetString(GL_VERSION));
        System.out.println("OpenGL max texture size: " + glGetInteger(GL_MAX_TEXTURE_SIZE));
        System.out.println("OpenGL vendor: " + glGetString(GL_VENDOR));
        System.out.println("OpenGL renderer: " + glGetString(GL_RENDERER));
        System.out.println("OpenGL extensions supported by my GPU: ");

        String extensions = glGetString(GL_EXTENSIONS);
        assert extensions != null;
        String[] extArr = extensions.split(" ");

        for (String extAr : extArr)
            System.out.println(extAr);
        System.out.println("--------------------------------");

        while(!glfwWindowShouldClose(window)) {
            if(!swapColor)
                glClearColor(0.0f, 0.0f, 1.0f, 0.0f);
            else
                glClearColor(0.0f, 1.0f, 0.0f, 0.0f);

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            update();
            render();
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    public void run() {
        System.out.println("QUBE v" + Version.getVersion());

        try {
            init();
            loop();
            glfwDestroyWindow(window);
            keyCallback.free();
        } finally {
            glfwTerminate();
            errorCallback.free();
        }
    }

}
