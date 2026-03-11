package com.glaway.mpm.qmIntf.decoratePView.showPanel.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import com.ptc.pview.pvapi.ShapeSceneObserver;
import com.ptc.pview.pvapi.TreeObserver;
import com.ptc.pview.pvapps.PviewInit;
import com.ptc.pview.pvkapp.EmbeddedControl;
import com.ptc.pview.pvkapp.Kernel;
import com.ptc.pview.pvkapp.PVWindow;
import com.ptc.pview.pvkapp.PseControl;
import com.ptc.pview.pvkapp.SelectionController;
import com.ptc.pview.pvkapp.ShapeScene;
import com.ptc.pview.pvkapp.ShapeView;
import com.ptc.pview.pvkapp.Structure;
import com.ptc.pview.pvkapp.Tree;
import com.ptc.pview.pvkapp.Window;
import com.ptc.pview.pvkapp.World;
import com.ptc.pview.pvloader.RemoteIf;

public class OpenAssemblyInPViewBase extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e){}

    PviewInit pview;
    Panel     panel;
    JMenuBar  menuBar;
    JPanel    display;
    MyActor myActor;
    World theWorld;
    ShapeScene scene;
    ShapeView view;
    ShapeSceneObserver so;
    SelectionController sc;
    SelectionController selectionController;
    EmbeddedControl embeddedControl;
    Structure structure;
    RemoteIf remoteIf;
    Kernel kernel;
    PseControl thePseControl;
    Tree tree;
    JList visibleList, hiddenList;
    Window theWindow;
    PVWindow pvWindow;
    TreeObserver treeObserver;

    public void pviewShutdown() {
        pview.Stop();
    }

    public OpenAssemblyInPViewBase(String title){

        super(title);

        display = new JPanel();
        display.setName("PV Window");

        getContentPane().setLayout (new BorderLayout());
        getContentPane().add(display, BorderLayout.CENTER);

        display.setLayout( new GridLayout(1,1) );
        panel = new Panel();
        panel.setBackground( java.awt.Color.blue );
        add("South", panel);
        display.add(panel);
        setVisible(true);
        setSize(800, 600);
        pview = new PviewInit();
        validate();
    }
}
