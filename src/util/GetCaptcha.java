package util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class GetCaptcha {
    private static String[] fonts = new String[]{"TimesRoman", "Courier", "Aria"};

    public static class Captcha {
        private BufferedImage image;
        private String captcha;

        private Captcha(BufferedImage image, String captcha) {
            this.image = image;
            this.captcha = captcha;
        }

        public BufferedImage getImage() {
            return image;
        }

        public String getCaptcha() {
            return captcha;
        }
    }

    public static Captcha get() {
        int width = 200;
        int height = 50;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        int n = 4;
        String cap = GetRandomString(n);
        for (int i = 0; i < width / 10; i++) {
            for (int j = 0; j < height / 10; j++) {
                g.setColor(getRandomColor());
                g.fillRect(i * 10, j * 10, (i + 1) * 10, (j + 1) * 10);
            }
        }
        for (int i = 0; i < n; i++) {
            char c = cap.charAt(i);
            Font font = getRandomFont();
            g.setFont(font);
            g.setColor(getRandomColor());
            g.drawString(String.valueOf(c), (width / n) * i + 5, height - 3);
        }
        return new Captcha(image, cap);
    }

    @SuppressWarnings("all")
    private static Font getRandomFont() {
        Random r = new Random();
        int font_type = r.nextInt(3);
        int bolder = r.nextInt(3);
        int size = r.nextInt(20) + 46;
        return new Font(fonts[font_type], bolder, size);
    }

    private static Color getRandomColor() {
        Random r = new Random();
        int red = r.nextInt(256);
        int green = r.nextInt(256);
        int blue = r.nextInt(256);
        return new Color(red, green, blue);
    }

    public static String GetRandomString(int n) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int type = r.nextInt(3);
            if (type == 0) {
                sb.append((char) (r.nextInt(26) + 65));
            } else if (type == 1) {
                sb.append((char) (r.nextInt(26) + 97));
            } else {
                sb.append(r.nextInt(10));
            }
        }
        return sb.toString();
    }
}
