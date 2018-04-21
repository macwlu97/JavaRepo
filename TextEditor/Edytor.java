package pl.lipinski97;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.io.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

import static java.lang.System.exit;

public class Edytor extends JFrame {
    private JTabbedPane tabbedPane;
    private JTextPane textPane0;
    private JTextPane textPane1;
    private JMenuBar menuBar;
    private JFileChooser fileChooser;
    String zmienna = null;
    Clipboard clipboard = new Clipboard(zmienna);
    UndoManager undoManager = new UndoManager();
    private UndoAction undoAction = new UndoAction();
    private RedoAction redoAction = new RedoAction();





    private boolean SaveOne = false;
    private File source = null;
    private boolean SaveTwo = false;
    private File sourceTwo = null;
    public Edytor() {
        super("Edytor");

        createComponents();
        setContentPane(tabbedPane);

        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        textPane0.getDocument().addUndoableEditListener(new UndoListener());
        textPane1.getDocument().addUndoableEditListener(new UndoListener());
        textPane0.setFont(new Font("Consolas", Font.ITALIC, 14));
        textPane1.setFont(new Font("Consolas", Font.ITALIC, 14));



    }


    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
//FILE
        JMenu fileMenu = new JMenu("File");

        JMenuItem newFileMenuItem = new JMenuItem("New");
        newFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        fileMenu.add(newFileMenuItem);
        newFileMenuItem.addActionListener(e -> {
            JTextPane selectedPane = tabbedPane.getSelectedIndex() == 0 ? textPane0 : textPane1;
            selectedPane.setText("");
        });
        fileMenu.addSeparator();

        //openfileitem
        JMenuItem openFileMenuItem = new JMenuItem("Open...");
        openFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        fileMenu.add(openFileMenuItem);
        openFileMenuItem.addActionListener(evt -> {
            if (fileChooser.showOpenDialog(tabbedPane) == JFileChooser.APPROVE_OPTION) {
                try (FileReader fileReader = new FileReader(fileChooser.getSelectedFile())) {
                    BufferedReader br = new BufferedReader(fileReader);
                    String line;
                    StringBuilder sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append(System.lineSeparator());
                    }
                    JTextPane selectedTextPane = tabbedPane.getSelectedIndex() == 0 ? textPane0 : textPane1;
                    selectedTextPane.setText(sb.toString());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(fileChooser, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }

            }

        });
        //savefileitem
        JMenuItem saveFileMenuItem = new JMenuItem("Save...");
        saveFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        fileMenu.add(saveFileMenuItem);
        //action save file Item
        saveFileMenuItem.addActionListener(evt -> {
            JTextPane selectedTextPane = tabbedPane.getSelectedIndex() == 0 ? textPane0 : textPane1;
            boolean tempSave = false;
            File tempFile = null;

//            if(SaveOne == true) {
//
//            }
            if(tabbedPane.getSelectedIndex() == 0) {
                tempSave = SaveOne;
                tempFile = source;
            }
            else{
                tempSave = SaveTwo;
                tempFile = sourceTwo;
            }
                        if(tempSave == true) {

                            try {
                                BufferedWriter writer;
                                writer = new BufferedWriter(new FileWriter(tempFile));
                                selectedTextPane.write(writer);
                                writer.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            JOptionPane.showMessageDialog(null, "File has been saved", "File Saved", JOptionPane.INFORMATION_MESSAGE);
            }
            else {
                String filename = JOptionPane.showInputDialog("Name this file");
                JFileChooser savefile = new JFileChooser();
                savefile.setSelectedFile(new File(filename + ".txt"));
                // savefile.showSaveDialog(savefile);
                BufferedWriter writer;
                int sf = savefile.showSaveDialog(tabbedPane);
                if (sf == JFileChooser.APPROVE_OPTION) {
                    try {
                        writer = new BufferedWriter(new FileWriter(savefile.getSelectedFile()));
                        if(tabbedPane.getSelectedIndex() == 0) {
                            source = savefile.getSelectedFile();
                            System.out.println(source);
                        }
                        else{
                            sourceTwo = savefile.getSelectedFile();
                        }
                       // JTextPane selectedTextPane = tabbedPane.getSelectedIndex() == 0 ? textPane0 : textPane1;
                        selectedTextPane.write(writer);
                        //text.write(writer);
                        writer.close();
                        JOptionPane.showMessageDialog(null, "File has been saved", "File Saved", JOptionPane.INFORMATION_MESSAGE);
                        // true for rewrite, false for override
                        tempSave = true;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (sf == JFileChooser.CANCEL_OPTION) {
                    JOptionPane.showMessageDialog(null, "File save has been canceled");
                }
                            if(tabbedPane.getSelectedIndex() == 0) {
                                SaveOne = tempSave;
                            }
                            else{
                                SaveTwo = tempSave;
                            }
            }

        });

        JMenuItem saveAsFileMenuItem = new JMenuItem("Save as...");
        fileMenu.addSeparator();
        fileMenu.add(saveAsFileMenuItem);

        //action save file Item
        saveAsFileMenuItem.addActionListener(evt -> {
            String filename = JOptionPane.showInputDialog("Name this file");
            JFileChooser savefile = new JFileChooser();
            savefile.setSelectedFile(new File(filename + ".txt"));
            // savefile.showSaveDialog(savefile);
            BufferedWriter writer;
            int sf = savefile.showSaveDialog(tabbedPane);
            if(sf == JFileChooser.APPROVE_OPTION){
                try {
                    writer = new BufferedWriter(new FileWriter(savefile.getSelectedFile()));
                    if(tabbedPane.getSelectedIndex() == 0) {
                        source = savefile.getSelectedFile();
                        System.out.println(source);
                    }
                    else{
                        sourceTwo = savefile.getSelectedFile();
                    }

                    JTextPane selectedTextPane = tabbedPane.getSelectedIndex() == 0 ? textPane0 : textPane1;
                    selectedTextPane.write(writer);
                    //text.write(writer);
                    writer.close();
                    JOptionPane.showMessageDialog(null, "File has been saved","File Saved",JOptionPane.INFORMATION_MESSAGE);
                    // true for rewrite, false for override
                    SaveOne = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(sf == JFileChooser.CANCEL_OPTION){
                JOptionPane.showMessageDialog(null, "File save has been canceled");
            }


        });

        JMenuItem exitFileMenuItem = new JMenuItem("Exit");
        fileMenu.add(exitFileMenuItem);
        exitFileMenuItem.addActionListener(evt -> { exit(0); });
///* MY CODE *///////////

        JMenu editMenu = new JMenu("Edit");

//         cofnijMenuItem = new JMenuItem("Cofnij");
//        JMenuItem WytnijMenuItem = new JMenuItem("Wytnij");
//        cofnijMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
//        WytnijMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        JMenuItem WytnijMenuItem = new JMenuItem("Wytnij");
        JMenuItem KopiujMenuItem = new JMenuItem("Kopiuj");
        JMenuItem PasteMenuItem = new JMenuItem("Wklej");
        editMenu.add(undoAction);
        editMenu.add(redoAction);
        editMenu.add(WytnijMenuItem);
        editMenu.add(KopiujMenuItem);
        editMenu.add(PasteMenuItem);

        WytnijMenuItem.addActionListener(evt -> {
            String selection;
            if(tabbedPane.getSelectedIndex() == 0) {
                 selection=textPane0.getSelectedText();
            }
            else{
                 selection=textPane1.getSelectedText();
            }


            if(selection==null){
                return;
            }
            StringSelection clipString=new StringSelection(selection);
            clipboard.setContents(clipString,clipString);
            if(tabbedPane.getSelectedIndex() == 0) {
                textPane0.replaceSelection("");
            }
            else{
                textPane1.replaceSelection("");
            }


                });
        KopiujMenuItem.addActionListener(evt -> {
            String selection;
            if(tabbedPane.getSelectedIndex() == 0) {
                selection=textPane0.getSelectedText();
            }
            else{
                selection=textPane1.getSelectedText();
            }


            if(selection==null){
                return;
            }
            StringSelection clipString=new StringSelection(selection);
            clipboard.setContents(clipString, clipString);

        });
        PasteMenuItem.addActionListener(evt -> {

            Transferable clip_data=clipboard.getContents(this);

            try{
                String clip_string=(String)clip_data.getTransferData(DataFlavor.stringFlavor);
                if(tabbedPane.getSelectedIndex() == 0) {
                    textPane0.replaceSelection(clip_string);
                }
                else{
                    textPane1.replaceSelection(clip_string);
                }


            }catch(Exception excpt){

            }

        });
//        cofnijMenuItem.setEnabled(false);

        ////////////////////CODE -> EDIT//////////////////////





        ///////////////////////////////////////////////////////

        ////////////////////////////////////////////
        //HELP
        JMenu helpMenu = new JMenu("Help");

        JMenuItem abouteMenuItem = new JMenuItem("About");
        abouteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        helpMenu.add(abouteMenuItem);

        abouteMenuItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Author: Maciej Wnuk Lipiński");
        });

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }
    //////////////////////////////////edit///////////

    class UndoListener implements UndoableEditListener {
        public void undoableEditHappened(UndoableEditEvent e) {
            undoManager.addEdit(e.getEdit());
            undoAction.update();
            redoAction.update();
        }
    }

    class UndoAction extends AbstractAction {
        public UndoAction() {
            this.putValue(Action.NAME, undoManager.getUndoPresentationName());
            this.setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            if (this.isEnabled()) {
                undoManager.undo();
                undoAction.update();
                redoAction.update();
            }
        }

        public void update() {
            this.putValue(Action.NAME, undoManager.getUndoPresentationName());
            this.setEnabled(undoManager.canUndo());
        }
    }


    class RedoAction extends AbstractAction {
        public RedoAction() {
            this.putValue(Action.NAME, undoManager.getRedoPresentationName());
            this.setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            if (this.isEnabled()) {
                undoManager.redo();
                undoAction.update();
                redoAction.update();
            }
        }

        public void update() {
            this.putValue(Action.NAME, undoManager.getRedoPresentationName());
            this.setEnabled(undoManager.canRedo());
        }

}
        //////////////////////////////////////////


    private void createComponents() {
        fileChooser = new JFileChooser() {
            @Override
            public void approveSelection() {
                if (getSelectedFile().isFile()) {
                    super.approveSelection();
                } else {
                    JOptionPane.showMessageDialog(fileChooser, "File not found: " + getSelectedFile(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }; // klasa pochodna

        tabbedPane = new JTabbedPane();
        textPane0 = new JTextPane();
        textPane1 = new JTextPane();


        JScrollPane scrollPane0 = new JScrollPane(textPane0);
        scrollPane0.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        JScrollPane scrollPane1 = new JScrollPane(textPane1);
        scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        textPane0.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                System.out.println("i");
//                cofnijMenuItem.setEnabled(true);
               // textPane0.setText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                System.out.println("r");
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }


        });

        tabbedPane.add("tab0", scrollPane0);
        tabbedPane.add("tab1", scrollPane1);


        /*menuusy*/
        JMenuItem cut = new JMenuItem("Wytnij");
        JMenuItem copy = new JMenuItem("Kopiuj");
        JMenuItem paste  = new JMenuItem("Wklej");



        /////popup///



        class PopUpDemo extends JPopupMenu {

            public PopUpDemo(){

                add(cut);

                add(copy);

                add(paste);

            }
        }


        cut.addActionListener(evt -> {
            String selection;
            if(tabbedPane.getSelectedIndex() == 0) {
                selection=textPane0.getSelectedText();
            }
            else{
                selection=textPane1.getSelectedText();
            }


            if(selection==null){
                return;
            }
            StringSelection clipString=new StringSelection(selection);
            clipboard.setContents(clipString,clipString);
            if(tabbedPane.getSelectedIndex() == 0) {
                textPane0.replaceSelection("");
            }
            else{
                textPane1.replaceSelection("");
            }


        });
        copy.addActionListener(evt -> {
            String selection;
            if(tabbedPane.getSelectedIndex() == 0) {
                selection=textPane0.getSelectedText();
            }
            else{
                selection=textPane1.getSelectedText();
            }


            if(selection==null){
                return;
            }
            StringSelection clipString=new StringSelection(selection);
            clipboard.setContents(clipString, clipString);

        });

        paste.addActionListener(evt -> {

            Transferable clip_data=clipboard.getContents(this);

            try{
                String clip_string=(String)clip_data.getTransferData(DataFlavor.stringFlavor);
                if(tabbedPane.getSelectedIndex() == 0) {
                    textPane0.replaceSelection(clip_string);
                }
                else{
                    textPane1.replaceSelection(clip_string);
                }


            }catch(Exception excpt){

            }

        });

        class PopClickListener extends MouseAdapter {
            public void mousePressed(MouseEvent e){
                if (e.isPopupTrigger())
                    doPop(e);
            }

            public void mouseReleased(MouseEvent e){
                if (e.isPopupTrigger())
                    doPop(e);
            }

            private void doPop(MouseEvent e){
                PopUpDemo menu = new PopUpDemo();
                menu.show(e.getComponent(), e.getX(), e.getY());

            }
        }

        ///if(tabbedPane.getSelectedIndex() == 0) {
            textPane0.addMouseListener(new PopClickListener());
       // } else {
             textPane1.addMouseListener(new PopClickListener());
       // }






        //////

    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Edytor edytor = new Edytor();

        edytor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        edytor.setSize(500, 300);
       //edytor.pack();
        edytor.setVisible(true);



    }
}
