package pro.gravit.launcher.news;

import java.util.HashMap;
import java.util.Map;

public final class NewsPhoto {

    private final Map<Size, String> sizesMap = new HashMap<>();

    public void put(Size photoSize, String photoUrl) {
        sizesMap.put(photoSize, photoUrl);
    }

    public String getUrl(Size size) {
        return sizesMap.get(size);
    }

    public boolean contains(Size size) {
        return sizesMap.containsKey(size);
    }

    public enum Size {

        S(40, 75), M(70, 130), O(87, 130), P(133, 200), Q(213, 320), R(288, 510), X(288, 534);

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        private final int height;
        private final int width;

        Size(int height, int width) {
            this.height = height;
            this.width = width;
        }

        @Override
        public String toString() {
            return "Size{height = " + height + ", width = " + width + "}";
        }
    }

    @Override
    public String toString() {
        return "NewsPhoto{sizesMap = [" + sizesMap.size() + "]}";
    }
}
