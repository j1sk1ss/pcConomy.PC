package economy.pcconomy.frontend.ui.objects;

import economy.pcconomy.frontend.ui.objects.interactive.Button;

import java.util.List;

public class Panel {
    public Panel(List<Button> buttons) {
        this.buttons = buttons;
    }

    private final List<Button> buttons;

    public Button click(int click) {
        for (var button : buttons)
            if (button.isClicked(click)) return button;

        return null;
    }
}
