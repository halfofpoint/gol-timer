package com.halfofpoint.gol;

import processing.core.PApplet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mart on 13/11/15.
 */
public class Main extends PApplet {

    final static int CELL_SIZE = 20;
    final static int WORLD_WIDTH = 100;
    final static int WORLD_HEIGHT = 70;

    final static int FRAME_RATE = 5;


    FigureReader fr = new PNGFigureReader();


    public static void main(String args[]) {
        PApplet.main(new String[]{"--present", "com.halfofpoint.gol.Main"});
    }

    public void settings() {
        fullScreen();
        //size(WORLD_WIDTH * CELL_SIZE, WORLD_HEIGHT * CELL_SIZE);
    }

    Map<String, List<World.Dot>> charactersDotMap = new HashMap<>(70);

    World counterWorld;
    World secondsWorld;
    World delimiterWorld;
    World worldEPAM;

    int m;
    int initialFreeze = 0;
    int start = 45;

    public void setup() {
        counterWorld = new World(WORLD_WIDTH, WORLD_HEIGHT);
        secondsWorld = new World(WORLD_WIDTH, WORLD_HEIGHT);
        delimiterWorld = new World(10, 40);
        worldEPAM = new World(226, 80);

        fillNumbersMap();

        fillWorldWithDigits(start, counterWorld);
        fr.readFigureWithShift('e', 0, 0).stream().forEach(worldEPAM::addDot);

        fr.readFigureWithShift('d', 0, 0).stream().forEach(delimiterWorld::addDot);

        background(0);
        noStroke();
        frameRate(FRAME_RATE);

        m = minute();
    }

    private void fillNumbersMap() {
        for (int i = 0; i <=59; i++) {
            String format = String.format("%02d", i);
            char[] number = format.toCharArray();
            List<World.Dot> dots = fr.readFigureWithShift(number[0], 10, 10);
            dots.addAll(fr.readFigureWithShift(number[1], 25, 10));
            charactersDotMap.put(format, dots);
        }
    }

    void fillWorldWithDigits(int digits, World world) {
        world.killAll();
        String number = String.format("%02d", digits);
        charactersDotMap.get(number).stream().forEach(world::addDot);
    }



    private void drawWorld(World world, Drawer drawer) {
        world.getDots().stream().forEach(drawer::draw);
    }

    public void draw() {
        background(0);

        drawWorld(counterWorld, new TypicalDrawer());

        drawSeconds();
        drawEPAM();


        drawWorld(delimiterWorld, new SmallDrawer());
        delimiterWorld.tick();

        if (initialFreeze > FRAME_RATE) initialFreeze = (FRAME_RATE * -4 + 1);

        if (initialFreeze < 0)
            counterWorld.tick();

        initialFreeze++;

        if (frameCount%(FRAME_RATE*5) == 0)
            fillWorldWithDigits(start, counterWorld);

        if (m != minute()) {
            m = minute();
            start--;
            fillWorldWithDigits(start, counterWorld);
        }
    }


    private void drawEPAM() {
        drawWorld(worldEPAM, new EPAMDrawer());
    }

    private void drawSeconds() {
        fillWorldWithDigits(59 - second(), secondsWorld);

        drawWorld(secondsWorld, new SmallDrawer());
    }

    private float getRandomFloat(int multiplier, int plus) {
        return (float) Math.random()*multiplier+plus;
    }

    interface Drawer {
        void draw(World.Dot dot);
    }

    class TypicalDrawer implements Drawer {
        @Override
        public void draw(World.Dot dot) {
            fill(getRandomFloat(155, 100), getRandomFloat(155, 100), getRandomFloat(155, 100));
            rect(dot.getX() * CELL_SIZE, dot.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
    }

    class SmallDrawer implements Drawer {
        @Override
        public void draw(World.Dot dot) {
            fill(getRandomFloat(155, 100), getRandomFloat(1, 100-second()), getRandomFloat(1, 100-second()));
            int cellSize = 20;
            int shift = 800;
            rect(dot.getX() * cellSize + shift, dot.getY() * cellSize, cellSize, cellSize);
        }
    }

    class EPAMDrawer implements Drawer {
        @Override
        public void draw(World.Dot dot) {
            fill(getRandomFloat(100, 0), getRandomFloat(100, 131), 211);
            int cellSize = 1;
            int shift = 1200;
            rect(dot.getX() * cellSize + shift, dot.getY() * cellSize, cellSize, cellSize);
        }
    }
}