package view.common;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import static view.common.ViewConstance.WHITE;

/**
 * @author 88382571
 * 2019/5/15
 */
public interface ConstListener {
	FocusListener FOCUS_WHITE = new FocusListener() {
		@Override
		public void focusGained(FocusEvent e) {
			e.getComponent()
					.setBackground(Color.WHITE);
		}
		
		@Override
		public void focusLost(FocusEvent e) {
			e.getComponent()
					.setBackground(WHITE);
		}
	};
}
