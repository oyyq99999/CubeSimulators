package oyyq.cube.simulator.cube;

import static com.jogamp.opengl.GL2.*;
import static oyyq.cube.simulator.cube.CubeNNN.*;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.TextRenderer;

import oyyq.cube.simulator.cube.CubeNNN.Cubie;
import oyyq.cube.simulator.model.CommonData;
import oyyq.cube.util.CubeTimer.State;

public class CubeNNNRenderer implements GLEventListener {

    public static enum SolvingState {
        INITIALIZED, SCRAMBLED, SOLVING, DNF, SOLVED, MESSED_BY_USER
    }

    private static final float ZERO_F       = 0.0f;
    private static final float ONE_F        = 1.0f;
    private static final float TWO_F        = 2.0f;
    // how much does a cubie explode
    private static final float CUBIE_EXPL_F = 0.2f;
    // gap between cubies
    private static final float CUBIE_GAP_F  = 0.3f;
    private static final float CUBIE_OFFSET = TWO_F + CUBIE_GAP_F;
    private static final int   ANGLE_SPEED  = 18;
    private static final int   TURN_ANGLE   = 90;

    private GLU                glu          = new GLU();
    CubeNNN                    cube;
    private int[]              columnAnglesX;
    private int[]              rowAnglesY;
    private int[]              faceAnglesZ;
    private int[]              angleXGoals;
    private int[]              angleYGoals;
    private int[]              angleZGoals;
    private float[][]          colors       = {{1, 1, 1, 1}, {1, 0, 0, 1}, {0, 1, 0, 1},
            {1, 1, 0, 1}, {1, 0.5f, 0, 1}, {0, 0, 1, 1}, {0.7f, 0.7f, 0.7f, 1}};
    private TextRenderer       tr;
    SolvingState               state        = SolvingState.INITIALIZED;

    public CubeNNNRenderer(int size) {
        setCubeSize(size);
    }

    void setCubeSize(int size) {
        cube = new CubeNNN(size);
        columnAnglesX = new int[size];
        rowAnglesY = new int[size];
        faceAnglesZ = new int[size];
        angleXGoals = new int[size];
        angleYGoals = new int[size];
        angleZGoals = new int[size];
        CommonData.setCubeSize(size);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        gl.glEnable(GL_DEPTH_TEST);
//        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
//        gl.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
//        gl.glShadeModel(GL_SMOOTH);
        Font f = new Font(Font.SANS_SERIF, Font.BOLD, 60);
        try {
            f = Font.createFont(Font.TRUETYPE_FONT, new File("font/Digiface.ttf"));
            f = f.deriveFont(Font.BOLD, 60);
        } catch (FontFormatException | IOException e) {}
        tr = new TextRenderer(f);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {}

    @Override
    public void display(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
        int cubeSize = CommonData.getCubeSize();
        glu.gluLookAt(0, cubeSize * 4, cubeSize * 4, 0, 0, 0, 0, 1, 0);
        if (cube.isSolvedByUser()) {
            CommonData.stopTimer();
            state = SolvingState.SOLVED;
        }
        drawText(drawable);
        updateAngles();
        drawCube(gl);
    }

    private void drawText(GLAutoDrawable drawable) {
        int width = drawable.getSurfaceWidth();
        int height = drawable.getSurfaceHeight();
        String text = CommonData.getTime();
        tr.beginRendering(width, height);
        tr.setColor(0, 1, 0, 1);
        tr.draw(text, (int) ((width - tr.getBounds(text).getWidth()) / 2), height * 8 / 9);
        tr.endRendering();
    }

    private void updateAngles() {
        int cubeSize = CommonData.getCubeSize();
        for (int i = 0; i < cubeSize; i++) {
            if (columnAnglesX[i] != angleXGoals[i]) {
                if (columnAnglesX[i] < angleXGoals[i]) {
                    columnAnglesX[i] += ANGLE_SPEED;
                } else {
                    columnAnglesX[i] -= ANGLE_SPEED;
                }
            } else {
                if (angleXGoals[i] != 0) {
                    synchronized (this) {
                        if (angleXGoals[i] < 0) {
                            angleXGoals[i] = 360 - ((-angleXGoals[i]) % 360);
                        }
                        angleXGoals[i] %= 360;
                        int turns = angleXGoals[i] / TURN_ANGLE;
                        cube.moveX(i, 4 - turns);
                        angleXGoals[i] = 0;
                        columnAnglesX[i] = 0;
                    }
                }
            }

            if (rowAnglesY[i] != angleYGoals[i]) {
                if (rowAnglesY[i] < angleYGoals[i]) {
                    rowAnglesY[i] += ANGLE_SPEED;
                } else {
                    rowAnglesY[i] -= ANGLE_SPEED;
                }
            } else {
                if (angleYGoals[i] != 0) {
                    synchronized (this) {
                        if (angleYGoals[i] < 0) {
                            angleYGoals[i] = 360 - ((-angleYGoals[i]) % 360);
                        }
                        angleYGoals[i] %= 360;
                        int turns = angleYGoals[i] / TURN_ANGLE;
                        cube.moveY(i, 4 - turns);
                        angleYGoals[i] = 0;
                        rowAnglesY[i] = 0;
                    }
                }
            }

            if (faceAnglesZ[i] != angleZGoals[i]) {
                if (faceAnglesZ[i] < angleZGoals[i]) {
                    faceAnglesZ[i] += ANGLE_SPEED;
                } else {
                    faceAnglesZ[i] -= ANGLE_SPEED;
                }
            } else {
                if (angleZGoals[i] != 0) {
                    synchronized (this) {
                        if (angleZGoals[i] < 0) {
                            angleZGoals[i] = 360 - ((-angleZGoals[i]) % 360);
                        }
                        angleZGoals[i] %= 360;
                        int turns = angleZGoals[i] / TURN_ANGLE;
                        cube.moveZ(i, 4 - turns);
                        angleZGoals[i] = 0;
                        faceAnglesZ[i] = 0;
                    }
                }
            }
        }
    }

    private void drawCube(GL2 gl) {
        int cubeSize = CommonData.getCubeSize();
        float t = (float) ((cubeSize - 1) / 2.0);
        for (int x = 0; x < cubeSize; x++) {
            for (int y = 0; y < cubeSize; y++) {
                for (int z = 0; z < cubeSize; z++) {
                    gl.glPushMatrix();

                    gl.glRotatef(columnAnglesX[x], ONE_F, ZERO_F, ZERO_F);
                    gl.glRotatef(rowAnglesY[y], ZERO_F, ONE_F, ZERO_F);
                    gl.glRotatef(faceAnglesZ[z], ZERO_F, ZERO_F, ONE_F);

                    gl.glTranslatef((x - t) * CUBIE_OFFSET, (y - t) * CUBIE_OFFSET,
                            (z - t) * CUBIE_OFFSET);
                    drawCubie(gl, cube.getCubie(x, y, z));
                    gl.glPopMatrix();
                }
            }
        }
    }

    private void drawCubie(GL2 gl, Cubie cubie) {
        int visibility = cubie.getVisibility();
        gl.glLineWidth(5.0f);
        for (int i = 0; i < 6; i++) {
            int color = cubie.getColor(i);
            if (CommonData.isBlindfolded() && CommonData.getTimerState() == State.RUNNING) {
                color = 6;
            }
            if ((visibility & (1 << i)) != 0) {
                gl.glColor4f(colors[color][0], colors[color][1], colors[color][2],
                        colors[color][3]);
                switch (i) {
                    case U:
                        gl.glBegin(GL_QUADS);
                        gl.glVertex3f(ONE_F, ONE_F + CUBIE_EXPL_F, -ONE_F);
                        gl.glVertex3f(-ONE_F, ONE_F + CUBIE_EXPL_F, -ONE_F);
                        gl.glVertex3f(-ONE_F, ONE_F + CUBIE_EXPL_F, ONE_F);
                        gl.glVertex3f(ONE_F, ONE_F + CUBIE_EXPL_F, ONE_F);
                        gl.glEnd();
                        gl.glColor4f(0, 0, 0, 1);
                        gl.glBegin(GL_LINE_LOOP);
                        gl.glVertex3f(ONE_F, ONE_F + CUBIE_EXPL_F, -ONE_F);
                        gl.glVertex3f(-ONE_F, ONE_F + CUBIE_EXPL_F, -ONE_F);
                        gl.glVertex3f(-ONE_F, ONE_F + CUBIE_EXPL_F, ONE_F);
                        gl.glVertex3f(ONE_F, ONE_F + CUBIE_EXPL_F, ONE_F);
                        gl.glEnd();
                        break;
                    case R:
                        gl.glBegin(GL_QUADS);
                        gl.glVertex3f(ONE_F + CUBIE_EXPL_F, ONE_F, -ONE_F);
                        gl.glVertex3f(ONE_F + CUBIE_EXPL_F, ONE_F, ONE_F);
                        gl.glVertex3f(ONE_F + CUBIE_EXPL_F, -ONE_F, ONE_F);
                        gl.glVertex3f(ONE_F + CUBIE_EXPL_F, -ONE_F, -ONE_F);
                        gl.glEnd();
                        gl.glColor4f(0, 0, 0, 1);
                        gl.glBegin(GL_LINE_LOOP);
                        gl.glVertex3f(ONE_F + CUBIE_EXPL_F, ONE_F, -ONE_F);
                        gl.glVertex3f(ONE_F + CUBIE_EXPL_F, ONE_F, ONE_F);
                        gl.glVertex3f(ONE_F + CUBIE_EXPL_F, -ONE_F, ONE_F);
                        gl.glVertex3f(ONE_F + CUBIE_EXPL_F, -ONE_F, -ONE_F);
                        gl.glEnd();
                        break;
                    case F:
                        gl.glBegin(GL_QUADS);
                        gl.glVertex3f(ONE_F, ONE_F, ONE_F + CUBIE_EXPL_F);
                        gl.glVertex3f(-ONE_F, ONE_F, ONE_F + CUBIE_EXPL_F);
                        gl.glVertex3f(-ONE_F, -ONE_F, ONE_F + CUBIE_EXPL_F);
                        gl.glVertex3f(ONE_F, -ONE_F, ONE_F + CUBIE_EXPL_F);
                        gl.glEnd();
                        gl.glColor4f(0, 0, 0, 1);
                        gl.glBegin(GL_LINE_LOOP);
                        gl.glVertex3f(ONE_F, ONE_F, ONE_F + CUBIE_EXPL_F);
                        gl.glVertex3f(-ONE_F, ONE_F, ONE_F + CUBIE_EXPL_F);
                        gl.glVertex3f(-ONE_F, -ONE_F, ONE_F + CUBIE_EXPL_F);
                        gl.glVertex3f(ONE_F, -ONE_F, ONE_F + CUBIE_EXPL_F);
                        gl.glEnd();
                        break;
                    case D:
                        gl.glBegin(GL_QUADS);
                        gl.glVertex3f(ONE_F, -ONE_F - CUBIE_EXPL_F, ONE_F);
                        gl.glVertex3f(-ONE_F, -ONE_F - CUBIE_EXPL_F, ONE_F);
                        gl.glVertex3f(-ONE_F, -ONE_F - CUBIE_EXPL_F, -ONE_F);
                        gl.glVertex3f(ONE_F, -ONE_F - CUBIE_EXPL_F, -ONE_F);
                        gl.glEnd();
                        gl.glColor4f(0, 0, 0, 1);
                        gl.glBegin(GL_LINE_LOOP);
                        gl.glVertex3f(ONE_F, -ONE_F - CUBIE_EXPL_F, ONE_F);
                        gl.glVertex3f(-ONE_F, -ONE_F - CUBIE_EXPL_F, ONE_F);
                        gl.glVertex3f(-ONE_F, -ONE_F - CUBIE_EXPL_F, -ONE_F);
                        gl.glVertex3f(ONE_F, -ONE_F - CUBIE_EXPL_F, -ONE_F);
                        gl.glEnd();
                        break;
                    case L:
                        gl.glBegin(GL_QUADS);
                        gl.glVertex3f(-ONE_F - CUBIE_EXPL_F, ONE_F, ONE_F);
                        gl.glVertex3f(-ONE_F - CUBIE_EXPL_F, ONE_F, -ONE_F);
                        gl.glVertex3f(-ONE_F - CUBIE_EXPL_F, -ONE_F, -ONE_F);
                        gl.glVertex3f(-ONE_F - CUBIE_EXPL_F, -ONE_F, ONE_F);
                        gl.glEnd();
                        gl.glColor4f(0, 0, 0, 1);
                        gl.glBegin(GL_LINE_LOOP);
                        gl.glVertex3f(-ONE_F - CUBIE_EXPL_F, ONE_F, ONE_F);
                        gl.glVertex3f(-ONE_F - CUBIE_EXPL_F, ONE_F, -ONE_F);
                        gl.glVertex3f(-ONE_F - CUBIE_EXPL_F, -ONE_F, -ONE_F);
                        gl.glVertex3f(-ONE_F - CUBIE_EXPL_F, -ONE_F, ONE_F);
                        gl.glEnd();
                        break;
                    case B:
                        gl.glBegin(GL_QUADS);
                        gl.glVertex3f(ONE_F, -ONE_F, -ONE_F - CUBIE_EXPL_F);
                        gl.glVertex3f(-ONE_F, -ONE_F, -ONE_F - CUBIE_EXPL_F);
                        gl.glVertex3f(-ONE_F, ONE_F, -ONE_F - CUBIE_EXPL_F);
                        gl.glVertex3f(ONE_F, ONE_F, -ONE_F - CUBIE_EXPL_F);
                        gl.glEnd();
                        gl.glColor4f(0, 0, 0, 1);
                        gl.glBegin(GL_LINE_LOOP);
                        gl.glVertex3f(ONE_F, -ONE_F, -ONE_F - CUBIE_EXPL_F);
                        gl.glVertex3f(-ONE_F, -ONE_F, -ONE_F - CUBIE_EXPL_F);
                        gl.glVertex3f(-ONE_F, ONE_F, -ONE_F - CUBIE_EXPL_F);
                        gl.glVertex3f(ONE_F, ONE_F, -ONE_F - CUBIE_EXPL_F);
                        gl.glEnd();
                        break;
                }
            }
        }
        gl.glLineWidth(1.0f);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        final GL2 gl = drawable.getGL().getGL2();

        if (height <= 0) {
            height = 1;
        }
        float aspect = (float) width / height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0, aspect, 0.1, 200.0);

        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    int getAnimatingAxis() {
        int cubeSize = CommonData.getCubeSize();
        for (int i = 0; i < cubeSize; i++) {
            if (angleXGoals[i] != 0) {
                return X;
            }
            if (angleYGoals[i] != 0) {
                return Y;
            }
            if (angleZGoals[i] != 0) {
                return Z;
            }
        }
        return -1;
    }

    synchronized void increaseGoal(int axis, int slice, int amount) {
        amount *= TURN_ANGLE;
        switch (axis) {
            case X:
                angleXGoals[slice] += amount;
                break;
            case Y:
                angleYGoals[slice] += amount;
                break;
            case Z:
                angleZGoals[slice] += amount;
                break;
        }
    }

}
