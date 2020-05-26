package xyz.ifkash;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {

    private long window;
    private final int windowWidth = 1280;
    private final int windowHeight = 720;

    public void run() {
        System.out.println("LWJGL version: " + Version.getVersion());

        init();
        loop();

        // Free and destroy the window callback
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation will print the error message in System.err
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this
        if(!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        /* Configure GLFW */
        // The current window hints are already the default
        glfwDefaultWindowHints();
        // The window will stay hidden after its creation
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        // The window will be resizable
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        /* Create the window */
        window = glfwCreateWindow(windowWidth, windowHeight, "GUI Windows generated using Graphics Library", NULL, NULL);

        if(window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                // The following will be detected in the rendering loop
                glfwSetWindowShouldClose(window, true);
            }
        });

        // Get the thread stack and push a new frame
        try(MemoryStack stack = MemoryStack.stackPush()) {
            // FML, there's pointer in Java too? daga naze?
            IntBuffer pWidth = stack.mallocInt(1);  // int *
            IntBuffer pHeight = stack.mallocInt(1); // int *

            // Get the window size passed to glfwCreateWindow()
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidMode.width() - pWidth.get(0)) / 2,
                    (vidMode.height() - pHeight.get(0)) / 2
            );
        }   // The stack frame will be popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);

        // Enable V-Sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread, creates the GLCapabilities instance and makes the OpenGL bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(1.0f, 1.0f, 0.0f, 0.0f);

        // Run the rendering loop until the user has attempted to close the window or has pressed the ESCAPE key
        while(!glfwWindowShouldClose(window)) {
            // Clear the framebuffer
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            //Swap the color buffers
            glfwSwapBuffers(window);

            // Poll the windo events. The key callback above will only be invoked during this call.
            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        // Run the GL loop
        new Main().run();
    }
}
