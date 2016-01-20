package oyyq.cube.simulator.cube;

import static oyyq.cube.simulator.cube.CubeNNN.X;
import static oyyq.cube.simulator.cube.CubeNNN.Y;
import static oyyq.cube.simulator.cube.CubeNNN.Z;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Queue;

import com.jogamp.opengl.util.FPSAnimator;

public class CubeNNNController extends Thread implements KeyListener {

    public static final int     MAX_CUBE_SIZE = 27;
    public static final int     MIN_CUBE_SIZE = 2;

    private static final String X_MOVES       = "bdeikmnrtuvy";
    private static final String Y_MOVES       = "acfjlsx;,.";
    private static final String Z_MOVES       = "ghopqw";

    private CubeNNNRenderer     renderer;
    private FPSAnimator         animator;
    private Queue<Character>    controlCodes  = new LinkedList<>();
    private int                 cubeSize;
    private int                 shiftRight    = 1;
    private int                 shiftLeft     = 1;

    public CubeNNNController(CubeNNNRenderer renderer, FPSAnimator animator) {
        setRenderer(renderer);
        setAnimator(animator);
    }

    public void setRenderer(CubeNNNRenderer renderer) {
        this.renderer = renderer;
        cubeSize = renderer.getCubeSize();
    }

    public void setAnimator(FPSAnimator animator) {
        this.animator = animator;
    }

    private void increaseShiftRight() {
        shiftRight++;
        if (shiftRight > cubeSize) {
            shiftRight = cubeSize;
        }
    }

    private void decreaseShiftRight() {
        shiftRight--;
        if (shiftRight <= 0) {
            shiftRight = 1;
        }
    }

    private void increaseShiftLeft() {
        shiftLeft++;
        if (shiftLeft > cubeSize) {
            shiftLeft = cubeSize;
        }
    }

    private void resetShifts() {
        shiftLeft = shiftRight = 1;
    }

    private void decreaseShiftLeft() {
        shiftLeft--;
        if (shiftLeft <= 0) {
            shiftLeft = 1;
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
            int currentAxis = renderer.getAnimatingAxis();
            if (axis != -1 && axis != currentAxis && currentAxis != -1) {
                continue;
            }
            controlCodes.poll();
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
                    decreaseShiftLeft();
                    break;
                case '4':
                    increaseShiftLeft();
                    break;
                case '7':
                    increaseShiftRight();
                    break;
                case '8':
                    decreaseShiftRight();
                    break;
                case 0x1B:
                    renderer.cube.reset();
                    resetShifts();
                    break;
                case ' ':
                    if (renderer.cube.isSolved()) {
                        renderer.cube.scramble();
                    }
                    resetShifts();
                    break;
                case '+':
                case '=':
                    if (++cubeSize > MAX_CUBE_SIZE) {
                        cubeSize = MAX_CUBE_SIZE;
                        break;
                    }
                    animator.pause();
                    renderer.setCubeSize(cubeSize);
                    animator.resume();
                    break;
                case '-':
                    if (--cubeSize < MIN_CUBE_SIZE) {
                        cubeSize = MIN_CUBE_SIZE;
                        break;
                    }
                    animator.pause();
                    renderer.setCubeSize(cubeSize);
                    animator.resume();
                default:
            }
        }
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

}
