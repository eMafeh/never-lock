package view.common;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import static view.common.ViewConstance.WHITE;

public interface ConstListener {
    FocusListener FOCUS_LISTENER = new FocusListener() {
        @Override
        public void focusGained(final FocusEvent e) {
            e.getComponent()
                    .setBackground(Color.WHITE);
        }

        @Override
        public void focusLost(final FocusEvent e) {
            e.getComponent()
                    .setBackground(WHITE);
        }
    };
}
