package com.mxk.pad.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Test {

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                TestFrame frame = new TestFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setTitle("PAD");
                frame.setVisible(true);
            }
        });
    }
}

class TestFrame extends JFrame {
    public TestFrame() {
        setSize(500, 500);
        setLayout(new BorderLayout());
        final PlayPanel panel = new PlayPanel();
        add(panel, BorderLayout.CENTER);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                super.componentResized(componentEvent);
                panel.freshSize(false);
            }
        });
    }
}