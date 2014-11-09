package com.mxk.pad.test;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				TestFrame frame = new TestFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}
}

class TestFrame extends JFrame {
	public TestFrame() {
		// TODO Auto-generated constructor stub
		setSize(500, 500);
		setLayout(new BorderLayout());
		add(new PlayPanel(), BorderLayout.CENTER);
	}
}