import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Lightning {

    Random random = new Random();

    Node branches;

    public Lightning() {
        branches = new Node(null);
    }

    public void update() {
        branches.update();
    }

    public void draw(Graphics2D g2d) {
        for (int c = 1; c < 10; c++) {
            branches.draw(g2d);
        }
    }
}

class Node {
    List<Node> children = new ArrayList<Node>();
    Node parent;

    Random random = new Random();

    int originX, originY;
    int destX, destY;
    int strength;
    float fadeFactor;

    public Node(Node parent) {
        this.parent = parent;
        if (parent == null) {
            originY = 0;
            originX = 200 + random.nextInt(400);
            destX = originX = 200 + random.nextInt(450);
            destY = originY = 0;
            strength = 10;
        } else {
            originX = parent.destX;
            originY = parent.destY;
            destX = originX - 50 + random.nextInt(150);
            destY = originY + 50 + random.nextInt(100);
            strength = parent.strength;
        }
        fadeFactor = strength;
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(6 + fadeFactor));

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        float alpha = fadeFactor;
        if (alpha > 1) alpha = 1;

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
        fadeFactor *= 0.9;
        for (Node n : children) {
            n.update();
        }
        if (strength-- > 0) {
            children.add(new Node(this));
        }
    }
}
