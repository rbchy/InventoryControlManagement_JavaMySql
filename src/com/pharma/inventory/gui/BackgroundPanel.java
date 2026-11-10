package com.pharma.inventory.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;

/**
 * Paints the home-screen background: if a real photo exists at
 * resources/images/home_banner.(jpg|png) (relative to the working directory the
 * app is launched from), it's drawn scaled to cover the panel with a dark
 * gradient overlay for text contrast. Otherwise falls back to a self-contained
 * Java2D pharmaceutical-themed graphic (gradient + capsule/cross iconography) —
 * so the screen looks good out of the box with zero external assets, but drop
 * a real image in resources/images/ any time for an even more authentic look.
 */
public class BackgroundPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final String[] CANDIDATE_PATHS = {
            "resources/images/home_banner.jpg",
            "resources/images/home_banner.jpeg",
            "resources/images/home_banner.png",
    };

    private final Image photo;

    public BackgroundPanel() {
        setLayout(new BorderLayout());
        photo = loadPhotoIfPresent();
    }

    private Image loadPhotoIfPresent() {
        for (String path : CANDIDATE_PATHS) {
            File f = new File(path);
            if (f.isFile()) {
                try {
                    return ImageIO.read(f);
                } catch (IOException ignored) {
                    // fall through to the next candidate / programmatic fallback
                }
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();

        if (photo != null) {
            drawPhotoCover(g2, w, h);
        } else {
            drawProgrammaticBackground(g2, w, h);
        }
        g2.dispose();
    }

    private void drawPhotoCover(Graphics2D g2, int w, int h) {
        int iw = photo.getWidth(this);
        int ih = photo.getHeight(this);
        if (iw <= 0 || ih <= 0) {
            drawProgrammaticBackground(g2, w, h);
            return;
        }
        double scale = Math.max((double) w / iw, (double) h / ih);
        int drawW = (int) Math.ceil(iw * scale);
        int drawH = (int) Math.ceil(ih * scale);
        int x = (w - drawW) / 2;
        int y = (h - drawH) / 2;
        g2.drawImage(photo, x, y, drawW, drawH, this);

        // Dark overlay so white title/stat text stays readable over any photo.
        GradientPaint overlay = new GradientPaint(
                0, 0, new Color(0x0F, 0x25, 0x22, 140),
                0, h, new Color(0x0F, 0x25, 0x22, 200));
        g2.setPaint(overlay);
        g2.fillRect(0, 0, w, h);
    }

    private void drawProgrammaticBackground(Graphics2D g2, int w, int h) {
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(0x0F, 0x3D, 0x37),
                w, h, new Color(0x1F, 0x7A, 0x6C));
        g2.setPaint(gradient);
        g2.fillRect(0, 0, w, h);

        // Faint decorative capsule shapes scattered across the banner.
        g2.setColor(new Color(255, 255, 255, 22));
        drawCapsule(g2, w * 0.75, h * 0.20, 130, 46, -20);
        drawCapsule(g2, w * 0.90, h * 0.55, 100, 36, 25);
        drawCapsule(g2, w * 0.60, h * 0.75, 150, 52, 10);
        g2.setColor(new Color(255, 255, 255, 14));
        drawCapsule(g2, w * 0.15, h * 0.65, 120, 42, -15);
        drawCapsule(g2, w * 0.35, h * 0.15, 90, 32, 30);

        // A simple medical cross emblem, top-right, very subtle.
        g2.setColor(new Color(255, 255, 255, 26));
        int crossSize = (int) (Math.min(w, h) * 0.18);
        int cx = (int) (w * 0.88);
        int cy = (int) (h * 0.35);
        int arm = crossSize / 3;
        g2.fill(new RoundRectangle2D.Double(cx - crossSize / 2.0, cy - arm / 2.0, crossSize, arm, 10, 10));
        g2.fill(new RoundRectangle2D.Double(cx - arm / 2.0, cy - crossSize / 2.0, arm, crossSize, 10, 10));

        // Bottom gradient so text/stat cards sitting near the bottom stay legible.
        GradientPaint bottomFade = new GradientPaint(
                0, h * 0.55f, new Color(0, 0, 0, 0),
                0, h, new Color(0, 0, 0, 90));
        g2.setPaint(bottomFade);
        g2.fillRect(0, 0, w, h);
    }

    private void drawCapsule(Graphics2D g2, double cx, double cy, double length, double thickness, double angleDeg) {
        Graphics2D rotated = (Graphics2D) g2.create();
        rotated.rotate(Math.toRadians(angleDeg), cx, cy);
        RoundRectangle2D capsule = new RoundRectangle2D.Double(
                cx - length / 2, cy - thickness / 2, length, thickness, thickness, thickness);
        rotated.fill(capsule);
        // A thin dividing line in the middle, like a real capsule's seam.
        rotated.setStroke(new BasicStroke(1.5f));
        rotated.draw(new Ellipse2D.Double(cx - 1, cy - thickness / 2, 2, thickness));
        rotated.dispose();
    }
}
