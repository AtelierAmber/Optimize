package com.github.atelieramber.optimize.rendering.mixin.preworld;

import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.MainWindow;

@Mixin(MainWindow.class)
public class MainWindowMixin {

//    @Inject(at = @At(value = "INVOKE_CTOR", target = "Lnet/minecraftforge/fml/loading/progress/EarlyProgressVisualization;handOffWindow(Ljava/util/function/IntSupplier;Ljava/util/function/IntSupplier;Ljava/util/function/Supplier;Ljava/util/function/LongSupplier;)J"),
//            method = "<init>(Lnet/minecraft/client/renderer/IWindowEventListener;Lnet/minecraft/client/renderer/MonitorHandler;Lnet/minecraft/client/renderer/ScreenSize;Ljava/lang/String;Ljava/lang/String;)V",
//            locals = LocalCapture.CAPTURE_FAILHARD)
//    private void MainWindowINIT(IWindowEventListener mc, MonitorHandler monitonHandler, ScreenSize size, @Nullable String videoModeName, String titleIn, CallbackInfo ci, Optional<VideoMode> optional, Monitor monitor) {
//        System.out.println("`````");
//        handle = initNewWindow(handle, width, height, titleIn, (fullscreen && monitor != null) ? monitor.getMonitorPointer() : 0L);
//    }

    @Redirect(method = "<init>(Lnet/minecraft/client/renderer/IWindowEventListener;Lnet/minecraft/client/renderer/MonitorHandler;Lnet/minecraft/client/renderer/ScreenSize;Ljava/lang/String;Ljava/lang/String;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/loading/progress/EarlyProgressVisualization;handOffWindow(Ljava/util/function/IntSupplier;Ljava/util/function/IntSupplier;Ljava/util/function/Supplier;Ljava/util/function/LongSupplier;)J"))
    private long MainWindowINIT(net.minecraftforge.fml.loading.progress.EarlyProgressVisualization visualizer, IntSupplier width, IntSupplier height, Supplier<String> title, LongSupplier monitor) {
        System.out.println("`````");
        long tempHandle = visualizer.handOffWindow(width, height, title, monitor);
        return initNewWindow(tempHandle, width.getAsInt(), height.getAsInt(), title.get(), monitor.getAsLong());
    }

    private long initNewWindow(long oldWindow, final int width, final int height, final String title, final long monitor) {
        GLFWErrorCallback.createPrint(System.err).set();

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_OPENGL_API);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_CREATION_API, GLFW.GLFW_NATIVE_CONTEXT_API);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        //GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_ANY_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        long window = GLFW.glfwCreateWindow(width, height, title, monitor, 0L);
        if (window == 0L) {
            throw new RuntimeException("Failed to create the GLFW window"); // ignore it and make the GUI optional?
        }

        int[] xpos = new int[1], ypos = new int[1];
        GLFW.glfwGetWindowPos(oldWindow, xpos, ypos);

        GLFW.glfwSetWindowPos(window, xpos[0], ypos[0]);

        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();
        GL33C.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GL33C.glClear(GL33C.GL_COLOR_BUFFER_BIT);
        GLFW.glfwSwapInterval(0);
        GLFW.glfwSwapBuffers(window);
        GLFW.glfwSwapInterval(1);

        GLFW.glfwShowWindow(window);

        /* Cleanup old window */
        GLFW.glfwDestroyWindow(oldWindow);
        return window;
    }

}
