package oyyq.cube.simulator;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import oyyq.cube.simulator.cube.CubeNNNController;
import oyyq.cube.simulator.cube.CubeNNNRenderer;

public class Main {

    public static void main(String[] args) {
        final GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        GLCanvas canvas = new GLCanvas(capabilities);
        canvas.setSize(800, 600);

        CubeNNNRenderer renderer = new CubeNNNRenderer(3);
        canvas.addGLEventListener(renderer);

        FPSAnimator animator = new FPSAnimator(canvas, 80, false);
        animator.start();

        CubeNNNController controller = new CubeNNNController(renderer, animator);
        canvas.addKeyListener(controller);
        controller.start();

        Frame mainFrame = new Frame("Virtual Cube");
        mainFrame.setLayout(new BorderLayout());
        mainFrame.add(canvas, BorderLayout.CENTER);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                controller.interrupt();
                animator.stop();
                mainFrame.dispose();
            }
        });
        mainFrame.addKeyListener(controller);
        // mainFrame.setResizable(false);
        mainFrame.setVisible(true);
    }
}
