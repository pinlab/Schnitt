package info.pinlab.test;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class ButtonTest {

	public static void main(String[] args) {
		JButton btn = new JButton("Click");
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Hello!");
			}
		});
		
		JFrame frame = new JFrame("Button test");
		frame.setLayout(new GridBagLayout());
		frame.getContentPane().add(btn);
		frame.setSize(400, 400);
		frame.setVisible(true);
		
	}
}

