package oyyq.cube.simulator.cube;

import static oyyq.cube.simulator.cube.CubeNNN.X;
import static oyyq.cube.simulator.cube.CubeNNN.Y;
import static oyyq.cube.simulator.cube.CubeNNN.Z;

import java.awt.CheckboxMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Queue;

import com.jogamp.opengl.util.FPSAnimator;

import oyyq.cube.simulator.cube.CubeNNNRenderer.SolvingState;
import oyyq.cube.simulator.events.CommonDataListener;
import oyyq.cube.simulator.model.CommonData;
import oyyq.cube.util.CubeTimer;

public class CubeNNNController extends Thread
        implements KeyListener, CommonDataListener, ActionListener, ItemListener {

    private static final String X_MOVES      = "bdeikmnrtuvy";
    private static final String Y_MOVES      = "acfjlsx;,.";
    private static final String Z_MOVES      = "ghopqw";

    private CubeNNNRenderer     renderer;
    private FPSAnimator         animator;
    private Queue<Character>    controlCodes = new LinkedList<>();
    private int                 shiftRight   = 1;
    private int                 shiftLeft    = 1;

    public CubeNNNController(CubeNNNRenderer renderer, FPSAnimator animator) {
        setRenderer(renderer);
        setAnimator(animator);
        CommonData.addListener(this);
    }

    public void setRenderer(CubeNNNRenderer renderer) {
        this.renderer = renderer;
    }

    public void setAnimator(FPSAnimator animator) {
        this.animator = animator;
    }

    private void resetShifts() {
        shiftLeft = shiftRight = 1;
    }

    private void alterShift(int amount, boolean left) {
        int cubeSize = CommonData.getCubeSize();
        int shift = left ? shiftLeft : shiftRight;
        shift += amount;
        if (shift <= 0) {
            shift = 1;
        }
        if (shift >= cubeSize) {
            shift = cubeSize - 1;
        }
        if (left) {
            shiftLeft = shift;
        } else {
            shiftRight = shift;
        }
    }

    @Override
    public void run() {
        while (true) {
            if (controlCodes.isEmpty()) {
                try {
                    Thread.sleep(10);
                    continue;
                } catch (InterruptedException e) {
                    break;
                }
            }
            char nextMove = controlCodes.peek();
            int axis = getAxis(nextMove);
            if (axis != -1 && "qtypa;bn".indexOf(nextMove) < 0) {
                if (renderer.state == SolvingState.SOLVED || renderer.state == SolvingState.DNF) {
                    controlCodes.poll();
                    continue;
                }
                if (renderer.state == SolvingState.SCRAMBLED) {
                    if (CommonData.getTimerState() == CubeTimer.State.DNF) {
                        renderer.state = SolvingState.DNF;
                        continue;
                    }
                    CommonData.startTimer();
                    renderer.state = SolvingState.SOLVING;
                }
            }
            int currentAxis = renderer.getAnimatingAxis();
            if (axis != -1 && axis != currentAxis && currentAxis != -1) {
                continue;
            }
            controlCodes.poll();
            int cubeSize = CommonData.getCubeSize();
            switch (nextMove) {
                case 'i': // R
                    for (int i = 0; i < shiftRight; i++) {
                        renderer.increaseGoal(axis, cubeSize - 1 - i, -1);
                    }
                    break;
                case 'k': // R'
                    for (int i = 0; i < shiftRight; i++) {
                        renderer.increaseGoal(axis, cubeSize - 1 - i, 1);
                    }
                    break;
                case 'j': // U
                    renderer.increaseGoal(axis, cubeSize - 1, -1);
                    break;
                case 'f': // U'
                    renderer.increaseGoal(axis, cubeSize - 1, 1);
                    break;
                case 'h': // F
                    renderer.increaseGoal(axis, cubeSize - 1, -1);
                    break;
                case 'g': // F'
                    renderer.increaseGoal(axis, cubeSize - 1, 1);
                    break;
                case 'd': // L
                    for (int i = 0; i < shiftLeft; i++) {
                        renderer.increaseGoal(axis, i, 1);
                    }
                    break;
                case 'e': // L'
                    for (int i = 0; i < shiftLeft; i++) {
                        renderer.increaseGoal(axis, i, -1);
                    }
                    break;
                case 's': // D
                    renderer.increaseGoal(axis, 0, 1);
                    break;
                case 'l': // D'
                    renderer.increaseGoal(axis, 0, -1);
                    break;
                case 'w': // B
                    renderer.increaseGoal(axis, 0, 1);
                    break;
                case 'o': // B'
                    renderer.increaseGoal(axis, 0, -1);
                    break;
                case 'u': // Rw
                    for (int i = 0; i < shiftRight + 1; i++) {
                        renderer.increaseGoal(axis, cubeSize - 1 - i, -1);
                    }
                    break;
                case 'm': // Rw'
                    for (int i = 0; i < shiftRight + 1; i++) {
                        renderer.increaseGoal(axis, cubeSize - 1 - i, 1);
                    }
                    break;
                case 'v': // Lw
                    for (int i = 0; i < shiftLeft + 1; i++) {
                        renderer.increaseGoal(axis, i, 1);
                    }
                    break;
                case 'r': // Lw'
                    for (int i = 0; i < shiftLeft + 1; i++) {
                        renderer.increaseGoal(axis, i, -1);
                    }
                    break;
                case ',': // Uw
                    renderer.increaseGoal(axis, cubeSize - 1, -1);
                    renderer.increaseGoal(axis, cubeSize - 2, -1);
                    break;
                case 'c': // Uw'
                    renderer.increaseGoal(axis, cubeSize - 1, 1);
                    renderer.increaseGoal(axis, cubeSize - 2, 1);
                    break;
                case 'x': // Dw
                    renderer.increaseGoal(axis, 0, 1);
                    renderer.increaseGoal(axis, 1, 1);
                    break;
                case '.':// Dw'
                    renderer.increaseGoal(axis, 0, -1);
                    renderer.increaseGoal(axis, 1, -1);
                    break;
                case 'y':
                case 't': // x
                    for (int i = 0; i < cubeSize; i++) {
                        renderer.increaseGoal(axis, i, -1);
                    }
                    break;
                case 'n':
                case 'b':// x'
                    for (int i = 0; i < cubeSize; i++) {
                        renderer.increaseGoal(axis, i, 1);
                    }
                    break;
                case 'a':// y'
                    for (int i = 0; i < cubeSize; i++) {
                        renderer.increaseGoal(axis, i, 1);
                    }
                    break;
                case ';':// y
                    for (int i = 0; i < cubeSize; i++) {
                        renderer.increaseGoal(axis, i, -1);
                    }
                    break;
                case 'q':// z'
                    for (int i = 0; i < cubeSize; i++) {
                        renderer.increaseGoal(axis, i, 1);
                    }
                    break;
                case 'p':// z
                    for (int i = 0; i < cubeSize; i++) {
                        renderer.increaseGoal(axis, i, -1);
                    }
                    break;
                case '3':
                    alterShift(-1, true);
                    break;
                case '4':
                    alterShift(1, true);
                    break;
                case '7':
                    alterShift(1, false);
                    break;
                case '8':
                    alterShift(-1, false);
                    break;
                case 0x1B:
                    if (CommonData.timerIsRunning()) {
                        CommonData.setDNF();
                        renderer.state = SolvingState.DNF;
                    } else {
                        renderer.cube.reset();
                        resetShifts();
                        CommonData.resetTimer();
                        renderer.state = SolvingState.INITIALIZED;
                    }
                    break;
                case ' ':
                    if (renderer.cube.isSolved()) {
                        CommonData.resetTimer();
                        renderer.cube.scramble();
                        boolean blindfolded = CommonData.isBlindfolded();
                        if (blindfolded) {
                            CommonData.startInspection(true);
                        } else {
                            CommonData.startInspection(false);
                        }
                        renderer.state = SolvingState.SCRAMBLED;
                    }
                    resetShifts();
                    break;
                default:
            }
        }
    }

    private void setCubeSize(int cubeSize) {
        animator.pause();
        renderer.setCubeSize(cubeSize);
        renderer.state = SolvingState.INITIALIZED;
        animator.resume();
    }

    private int getAxis(char move) {
        if (X_MOVES.indexOf(move) >= 0) {
            return X;
        }
        if (Y_MOVES.indexOf(move) >= 0) {
            return Y;
        }
        if (Z_MOVES.indexOf(move) >= 0) {
            return Z;
        }
        return -1;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        this.controlCodes.add(e.getKeyChar());
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void commonDataChanged(int dataId) {
        if (dataId == CommonData.DATA_ID_CUBE_SIZE) {
            setCubeSize(CommonData.getCubeSize());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().startsWith("setCubeSize")) {
            int size = Integer.parseInt(e.getActionCommand().substring(11));
            CommonData.setCubeSize(size);
        } else if ("IncreaseCubeSize".equals(e.getActionCommand())) {
            CommonData.setCubeSize(CommonData.getCubeSize() + 1);
        } else if ("DecreaseCubeSize".equals(e.getActionCommand())) {
            CommonData.setCubeSize(CommonData.getCubeSize() - 1);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Object obj = e.getSource();
        if (obj instanceof CheckboxMenuItem) {
            CheckboxMenuItem cbox = (CheckboxMenuItem) obj;
            if ("setBlindfolded".equals(cbox.getActionCommand())) {
                CommonData.setBlindfolded(cbox.getState());
            }
        }
    }

}
