import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Lightning {

    private static final float FADE_FACTOR = 0.95f;

    private static final float FADE_MIN_VALUE = 0.1f;

    Random random = new Random();

    Node branches;

    public boolean dead;

    public boolean touchDown = false;

    public float flashStrength;
    public float fadeFactor;

    List<LightningSegment> flash = new ArrayList<LightningSegment>();

    public Node touchDownSegment;

    public Lightning() {
        branches = new Node(this, null);
        flashStrength = fadeFactor = 10f;
    }

    public void update() {
        if (flash.size() > 0 && fadeFactor < FADE_MIN_VALUE) {
            dead = true;
        }

        branches.update();

        if (touchDownSegment != null) {
            addFlash(touchDownSegment);
        }

        if (flash.size() > 0) {
            fadeFactor = FADE_FACTOR * fadeFactor;
        }
    }

    public void draw(Graphics2D g2d) {
        branches.draw(g2d);

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(10));

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        float alpha = fadeFactor;
        if (alpha > 1) alpha = 1f;
        if (alpha > 0) {
            int type = AlphaComposite.SRC_OVER;
            AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
            g2d.setComposite(composite);

        }
        for (LightningSegment segment : flash) {
            g2d.drawLine(segment.x, segment.y, segment.x2, segment.y2);
        }
    }

    public void addFlash(Node node) {
        if (node.parent != null) {
            flash.add(new LightningSegment(node.destX, node.destY, node.originX, node.originY));
            touchDownSegment = node.parent;
        }
    }
}

class Node {
    private static final int PROGRESS_Y_FACTOR = 150;
    private static final int PROGRESS_Y_MIN = -50;
    private static final int PROGRESS_X_FACTOR = 200;
    private static final int PROGRESS_X_MIN = 100;
    private static final int START_X_FACTOR = 400;
    private static final int START_X_MIN = 200;
    private static final int INITIAL_STRENGTH = 10;
    private static final int MAX_CHILDREN = 2;
    List<Node> children = new ArrayList<Node>();
    Node parent;

    Random random = new Random();

    int originX, originY;
    int destX, destY;
    int strength;
    int originalStrength;
    float fadeFactor;
    private final Lightning lightning;

    public Node(Lightning lightning, Node parent) {
        this.lightning = lightning;

        this.parent = parent;
        if (parent == null) {
            originY = 0;
            originX = START_X_MIN + random.nextInt(START_X_FACTOR);
            destX = originX = 200 + random.nextInt(850);
            destY = originY = 0;
            originalStrength = strength = INITIAL_STRENGTH;
        } else {
            originX = parent.destX;
            originY = parent.destY;
            destX = originX - PROGRESS_X_MIN + random.nextInt(PROGRESS_X_FACTOR);
            destY = originY - PROGRESS_Y_MIN + random.nextInt(PROGRESS_Y_FACTOR);
            originalStrength = strength = parent.strength;
        }
        fadeFactor = strength;
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);

        // g2d.setStroke(new BasicStroke(originalStrength));
        int width = 1;
        if (parent != null && parent.parent == null) {
            width = 10;
        }
        g2d.setStroke(new BasicStroke(width));

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        float alpha = fadeFactor / 10;
        if (alpha > 0.5) alpha = 0.5f;
        if (alpha > 0) {
            int type = AlphaComposite.SRC_OVER;
            AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
            g2d.setComposite(composite);

            g2d.drawLine(originX, originY, destX, destY);
        }
        for (Node n : children) {
            n.draw(g2d);
        }

    }

    public void update() {
        if (destY > 1000 && !lightning.touchDown) {
            lightning.touchDown = true;
            lightning.touchDownSegment = this;
        }
        fadeFactor *= 0.9;
        for (Node n : children) {
            n.update();
        }
        if (!lightning.touchDown && children.size() < MAX_CHILDREN && strength-- > 0) {
            children.add(new Node(lightning, this));
        }
    }
}

class LightningSegment {

    int x, y, x2, y2;

    public LightningSegment(int destX, int destY, int originX, int originY) {
        x = destX;
        y = destY;
        x2 = originX;
        y2 = originY;
    }

}