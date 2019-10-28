package FrameForms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Form extends JFrame {
    public Form(){
        super();
        this.setLocation(10,10);
        this.setSize(800,500);
        this.setVisible(true);
    }
    public void init(){

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });
    }
}
