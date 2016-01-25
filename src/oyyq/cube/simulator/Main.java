package oyyq.cube.simulator;

import java.awt.BorderLayout;
import java.awt.CheckboxMenuItem;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import oyyq.cube.simulator.cube.CubeNNN;
import oyyq.cube.simulator.cube.CubeNNNController;
import oyyq.cube.simulator.cube.CubeNNNRenderer;
import oyyq.cube.simulator.events.CommonDataListener;
import oyyq.cube.simulator.model.CommonData;

public class Main extends Frame implements CommonDataListener {

    private static final long serialVersionUID = 5480375639393060161L;

    private GLProfile         profile;
    private GLCapabilities    capab;

    private GLCanvas          canvas;
    private MenuBar           menuBar;

    private Menu              cubeMenu;
    private Menu              gameMenu;

    private CheckboxMenuItem  blindfolded;

    private CubeNNNRenderer   renderer;
    private CubeNNNController controller;
    private FPSAnimator       animator;

    private Main() {
        CommonData.addListener(this);

        profile = GLProfile.get(GLProfile.GL2);
        capab = new GLCapabilities(profile);
        canvas = new GLCanvas(capab);
        canvas.setSize(960, 600);

        renderer = new CubeNNNRenderer(3);
        canvas.addGLEventListener(renderer);

        animator = new FPSAnimator(canvas, 80, true);
        animator.start();

        controller = new CubeNNNController(renderer, animator);
        canvas.addKeyListener(controller);
        controller.start();

        menuBar = new MenuBar();
        gameMenu = new Menu("Game");
        cubeMenu = new Menu("Cube");
        menuBar.add(gameMenu);
        menuBar.add(cubeMenu);

        blindfolded = new CheckboxMenuItem("Blindfolded");
        blindfolded.setShortcut(new MenuShortcut(KeyEvent.VK_B));
        blindfolded.setActionCommand("setBlindfolded");
        blindfolded.addItemListener(controller);
        gameMenu.add(blindfolded);

        Menu cubeSize = new Menu("Choose Cube");
        for (int n = CubeNNN.MIN_CUBE_SIZE; n <= CubeNNN.MAX_CUBE_SIZE; n++) {
            MenuItem mi = new MenuItem(CubeNNN.getName(n));
            mi.setActionCommand("setCubeSize" + n);
            mi.addActionListener(controller);
            if (2 <= n && n <= 9) {
                mi.setShortcut(new MenuShortcut(KeyEvent.getExtendedKeyCodeForChar('0' + n)));
            }
            if (n % 10 == 1) {
                cubeSize.addSeparator();
            }
            cubeSize.add(mi);
        }
        cubeMenu.add(cubeSize);

        cubeMenu.addSeparator();
        MenuItem mi = new MenuItem("Increase Cube Size", new MenuShortcut(KeyEvent.VK_EQUALS));
        mi.setActionCommand("IncreaseCubeSize");
        mi.addActionListener(controller);
        cubeMenu.add(mi);
        mi = new MenuItem("Decrease Cube Size", new MenuShortcut(KeyEvent.VK_MINUS));
        mi.setActionCommand("DecreaseCubeSize");
        mi.addActionListener(controller);
        cubeMenu.add(mi);

        this.setLayout(new BorderLayout());
        this.add(canvas, BorderLayout.CENTER);
        this.pack();
        this.setLocationRelativeTo(null);
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                controller.interrupt();
                animator.stop();
                dispose();
            }
        });
        this.setMenuBar(menuBar);
        canvas.requestFocus();
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new Main();
    }

    @Override
    public void commonDataChanged(int dataId) {
        if (dataId == CommonData.DATA_ID_CUBE_SIZE) {
            this.setTitle("Virtual Cube - " + CubeNNN.getName(CommonData.getCubeSize()));
        }
        if (dataId == CommonData.DATA_ID_TIMER) {
            boolean timerRunning = CommonData.timerIsRunning();
            blindfolded.setEnabled(!timerRunning);
        }
    }
}
