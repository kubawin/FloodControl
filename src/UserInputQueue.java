




import java.util.ArrayDeque;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class UserInputQueue {
	
	private ArrayDeque<KeyEvent> keyEvents;
	private ArrayDeque<MouseEvent> mouseEvents;
	
	public UserInputQueue() { 
		keyEvents = new ArrayDeque<KeyEvent>();
		mouseEvents = new ArrayDeque<MouseEvent>();
	}
	
	public void addKey(KeyEvent keyEvent) {
		keyEvents.add(keyEvent);
	}
	
	public KeyEvent getKeyEvent() {
		if (keyEvents.isEmpty()) return null;
		return keyEvents.poll();
	}
	
	public KeyCode getKeyCode() {
		if (keyEvents.isEmpty()) return null;
		return keyEvents.poll().getCode();
	}
	
	public void addMouse(MouseEvent mouseEvent) {
		mouseEvents.add(mouseEvent);
	}
	
	public MouseEvent getMouse() {
		if (mouseEvents.isEmpty()) return null;
		return mouseEvents.poll();
	}

}
