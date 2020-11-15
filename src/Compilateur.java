import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;


class Compilateur extends JFrame implements ActionListener, ItemListener {
    TextEditor editorPane;
    MessagePanel messagePane;

    Compilateur(){
        super("Compilateur C--");
        addMenu();
        addCorps();
        
        //pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1080,640);
        setVisible(true);
    }
    

    public void addMenu(){
        JMenuBar menuBar = new JMenuBar();
        JMenu menu;
        JMenuItem item;

        menu = new JMenu("File");

        item = new JMenuItem("New File");
        item.addActionListener(this);
        menu.add(item);

        item = new JMenuItem("Open File");
        item.addActionListener(this);
        menu.add(item);

        menu.addSeparator();   
        menu.add(new JMenuItem("Open Folder"));
        menu.addSeparator();
        menu.add(new JMenuItem("Save"));
        menu.add(new JMenuItem("Save As"));

        menuBar.add(menu);

        menu = new JMenu("Build");
        item = new JMenuItem("Run");
        item.addActionListener(this);
        menu.add(item);

        menuBar.add(menu);

        /*
        menu = new JMenu("Help");
        menu.add(new JMenuItem("About..."));
        menuBar.add(menu);
        */
        setJMenuBar(menuBar);
    }

    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()){
            case "New File":
                editorPane.newTab();
                break;
            case "Open File":
                final JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
                int returnVal = fc.showOpenDialog(fc);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    editorPane.openTab(file,new JPanel());
                } 
                break;  
            case "Run":
                try{
                    String path = editorPane.getText();
                    BufferedWriter writer = new BufferedWriter(new FileWriter("temp"));
                    writer.write(path);
                    writer.close();
                    Lexical lex = new Lexical("temp");
                    Syntaxique syn = new Syntaxique("temp");
                    syn.start();

                    String lexicalMessage = lex.getResult();
                    String semantiqueMessage = syn.getSemanticErrors();
                    String syntaxiqueMessage = syn.getSyntaxicErrors();
                    String structure = syn.getStructure();

                    messagePane.setMessage(lexicalMessage,syntaxiqueMessage,semantiqueMessage,structure);
                }
                catch(Exception ex){
                    System.out.println("ex");
                }
        }
    }


    public void itemStateChanged(ItemEvent e) {
    }    

    public JTree newFileTree(String filename){
        File fileRoot = new File(filename);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(fileRoot.getName());
        DefaultTreeModel treeModel = new DefaultTreeModel(root);

        JTree tree = new JTree(treeModel);
        tree.setShowsRootHandles(true);    
        createChildren(fileRoot, root);
        tree.expandRow(0);

        tree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                TreePath tPath = tree.getPathForLocation(me.getX(), me.getY());
                if (tPath == null) return;

                //excluding root from path
                String path = tPath.toString().replaceAll("\\]|\\s|\\[", "").replaceAll(",",  Matcher.quoteReplacement(File.separator));                
                //System.out.println(path);

                if (me.getClickCount() == 2 && !me.isConsumed()) {
                    me.consume();
                    //System.out.println(path);
                    editorPane.openTab(new File(path),editorPane.lastPane);
                    editorPane.lastPane=null;
                }
                else{
                    //System.out.println(path);
                    editorPane.showTab(new File(path));
                }
            }
          });

        return tree;
    }

    private void createChildren(File fileRoot, DefaultMutableTreeNode node) {
        File[] files = fileRoot.listFiles();
        if (files == null) return;

        for (File file : files) {
            DefaultMutableTreeNode childNode = 
                new DefaultMutableTreeNode(file.getName());
            node.add(childNode);
            if (file.isDirectory()) {
                createChildren(file, childNode);
            }
        }
    }

    public void addCorps(){
        JPanel corps = new JPanel();
        corps.setLayout(new BorderLayout());
        editorPane =  new TextEditor();
        

        messagePane = new MessagePanel();

        JSplitPane splitEditor = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editorPane, messagePane);
        splitEditor.setDividerLocation(400);
        splitEditor.setDividerSize(5);
        corps.add(splitEditor,BorderLayout.CENTER);
        //System.getProperty("user.dir")
        JScrollPane scrollPane = new JScrollPane(newFileTree("Exemple")); //System.getProperty("user.dir")
        add(scrollPane);

        JSplitPane splitFileTree = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, scrollPane,corps);
        splitFileTree.setDividerLocation(200);
        splitFileTree.setDividerSize(5);

        pack();
        add(splitFileTree);
    }


    public static void main(String[] args) {
        new Compilateur();
    }
}


class TextEditor extends JTabbedPane{
    Map<String, Integer> openedFile;

    public JPanel lastPane;

    TextEditor(){
        super();    
        newTab();
        openedFile = new HashMap<String, Integer>();
    }   

    public String getText(){
        JScrollPane scrollPane = null;
        JPanel pane =(JPanel) getSelectedComponent();
        Component[] components = pane.getComponents();

        for(Component component : components){
            if(component instanceof JScrollPane){
                scrollPane = (JScrollPane) component;
                break;
            }
        }

        if(scrollPane!=null){
            JViewport viewport = scrollPane.getViewport(); 
            JTextPane textPane = (JTextPane)viewport.getView();
            return textPane.getText();
        }
        else return "";
    }

    public String readFile(File file){
            try{
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String str="";
                
                while(bufferedReader.ready()){
                    str += bufferedReader.readLine();
                    if(bufferedReader.ready()) str+="\n";
                }
                bufferedReader.close();
                return str;
        
            }
            catch(Exception e){
                System.out.println(e.getMessage());
                return "";
            }
    }

    public void openTab(File file,JPanel pane){
        if(!openedFile.containsKey(file.getAbsolutePath())){
            int index =  createPane(pane, file);
            openedFile.put(file.getAbsolutePath(), index);
        }
        else{
            setSelectedIndex(openedFile.get(file.getAbsolutePath()));
        }
    }

    public void showTab(File file){
        if(!openedFile.containsKey(file.getAbsolutePath())){
            if(lastPane == null)    lastPane=new JPanel();
            createPane(lastPane, file, true);
        }
        else{
            openTab(file,null);
        }

    }

    public void newTab(){
        JPanel pane = new JPanel();
        String fileName = "untitled.cmm";
        pane.setLayout(new BorderLayout());
        if(pane == lastPane){
            pane.removeAll();
            pane.setOpaque(false);
            pane.updateUI();
        }
            
            pane.add(createZoneText(null),BorderLayout.CENTER);


        addTab(fileName,pane);
        setSelectedComponent (pane);

        int index = indexOfComponent(pane);
        JPanel pnlTab = new JPanel();
        pnlTab.setLayout(new BorderLayout());
        pnlTab.setOpaque(false);
        JLabel lblTitle = new JLabel(fileName);
        

        JButton btnClose = new JButton(" x");
        btnClose.setOpaque(false);
        btnClose.setBorder(BorderFactory.createEmptyBorder());
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);

        btnClose.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(pane);
            }
        });

        pnlTab.add(lblTitle, BorderLayout.CENTER);
        pnlTab.add(btnClose, BorderLayout.LINE_END);

        setTabComponentAt(index, pnlTab);
    }

    public int createPane(JPanel pane,File file){
        return createPane(pane, file, false);
    }

    public int createPane(JPanel pane,File file, boolean toShow){
        String fileName = file.getName();
        pane.setLayout(new BorderLayout());
        if(pane == lastPane){
            pane.removeAll();
            pane.setOpaque(false);
            pane.updateUI();
        }
            
        
        if(file.getName().matches("^\\w+\\.cmm$")){
            pane.add(createZoneText(file),BorderLayout.CENTER);
        }
        else{
            JLabel text = new JLabel("Le fichier n'est pas au format cmm.");
            pane.add(text,BorderLayout.NORTH);
        }

        addTab(fileName,pane);
        setSelectedComponent (pane);

        int index = indexOfComponent(pane);
        JPanel pnlTab = new JPanel();
        pnlTab.setLayout(new BorderLayout());
        pnlTab.setOpaque(false);
        JLabel lblTitle = new JLabel(fileName);
        
        if(toShow)
            lblTitle.setFont(new Font(lblTitle.getFont().getName(),Font.ITALIC,lblTitle.getFont().getSize()));

        JButton btnClose = new JButton(" x");
        btnClose.setOpaque(false);
        btnClose.setBorder(BorderFactory.createEmptyBorder());
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);

        btnClose.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(pane);
                openedFile.remove(file.getAbsolutePath());
            }
        });

        pnlTab.add(lblTitle, BorderLayout.CENTER);
        pnlTab.add(btnClose, BorderLayout.LINE_END);

        setTabComponentAt(index, pnlTab);
        return index;
    }

    public JScrollPane createZoneText(File file){
        JTextPane textPane = new JTextPane();
        if(file!=null)
            textPane.setText(readFile(file));
        else
            textPane.setText("");

        Highlighter hl= new Highlighter(textPane);
        hl.execute();
        Font font = new Font(Font.MONOSPACED, Font.PLAIN , 15);
        textPane.setFont(font);
        
        textPane.getDocument().addDocumentListener(new DocumentListener(){
            @Override
            public void removeUpdate(DocumentEvent e) {
                Highlighter hl= new Highlighter(textPane);
                hl.execute();
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                Highlighter hl= new Highlighter(textPane);
                hl.execute();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        

        JScrollPane scrollPane = new JScrollPane(textPane);        
        TextLineNumber tln = new TextLineNumber(textPane);
        scrollPane.setRowHeaderView(tln);
        
        //defil.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        return scrollPane;
    }

    public String getPath(Map<String, Integer> openedFile, int index){
        String path= null;
        for(Map.Entry entry: openedFile.entrySet()){
            if(index == (int) entry.getValue()){
                path = (String) entry.getKey();
                break;
            }
        }
        return path;
    }

}

class MessagePanel extends JTabbedPane{
    JPanel lexical;
    JPanel syntaxique;
    JPanel semantique;
    JPanel structure;

    MessagePanel(){
        super();
        lexical = new JPanel();
        lexical.setOpaque(false);
        syntaxique = new JPanel();
        syntaxique.setOpaque(false);
        semantique = new JPanel();
        semantique.setOpaque(false);
        structure = new JPanel();
        structure.setOpaque(false);
        
        addMessage(lexical," ");
        addMessage(syntaxique," ");
        addMessage(semantique," ");
        addMessage(structure," ");

        addTab("Semantique",semantique);
        addTab("Syntaxique",syntaxique);
        addTab("Lexical",lexical);
        addTab("Structure",structure);
    }
   
    public void setMessage(String lexicalMessage, String syntaxiqueMessage, String semantiqueMessage, String structureMessage){
        lexical.removeAll();
        lexical.updateUI();
        syntaxique.removeAll();
        syntaxique.updateUI();
        semantique.removeAll();
        semantique.updateUI();
        structure.removeAll();
        structure.updateUI();

        addMessage(lexical,lexicalMessage);
        addMessage(syntaxique,syntaxiqueMessage);
        addMessage(semantique,semantiqueMessage);
        addMessage(structure,structureMessage);

        setSelectedComponent(semantique);
    }

    public void addMessage(JPanel panel, String message){
        panel.setLayout(new BorderLayout());
        JTextPane text = new JTextPane();
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN , 13);
        text.setFont(font);
        text.setText(message);
        text.setEditable(false);
        JScrollPane defil = new JScrollPane(text);

        panel.add(defil,BorderLayout.CENTER);
    }
}
 