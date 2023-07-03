package economy.pcconomy.frontend.ui.objects.interactive;

import java.util.List;

public class Button {
    public Button(List<Integer> coordinates, String name) {
        this.coordinates = coordinates;
        this.name        = name;
    }

    private final List<Integer> coordinates;
    private final String name;

    public boolean isClicked(int click) {
        return coordinates.contains(click);
    }

    public String getName() {
        return name;
    }
}