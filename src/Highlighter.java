import java.awt.*;
import java.util.regex.*;

import javax.swing.*;
import javax.swing.text.*;


class Highlighter extends SwingWorker<Void,Object>{
    JTextPane textPane;
    public Highlighter(JTextPane textPane) {
        this.textPane = textPane;
    }
    private void matching(JTextPane textPane){
        int length = textPane.getDocument().getLength();
        String str;
        try{str = textPane.getDocument().getText(0, length);}
        catch(Exception BadLocationException){
            str = textPane.getText();
        }

        highlightRegx(str,"(.*)",Color.BLACK);
        highlightRegx(str, "\\b(int|void|extern)\\b", new Color(0,128,128));//CYAN
        highlightRegx(str,"([a-zA-Z_]\\w*)(\\s*\\()", new Color(205,133,63));//BROWN
        highlightRegx(str, "\\b(if|else|then|for|while|return)\\b",new Color(128,0,128));//PURPLE

    }

    public void highlightRegx(String input, String reg, Color color){
        StyleContext style = StyleContext.getDefaultStyleContext();
        AttributeSet textStyle, textStyle2;
        textStyle = style.addAttribute(style.getEmptySet(),StyleConstants.Foreground, color);
        textStyle = style.addAttribute(textStyle,Font.MONOSPACED, 15);
        
        textStyle2 = style.addAttribute(style.getEmptySet(),StyleConstants.Foreground, Color.BLACK);
        textStyle2 = style.addAttribute(textStyle,Font.MONOSPACED, 15);

        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(input);

        
        while(m.find())
            textPane.getStyledDocument().setCharacterAttributes(m.start(1),(m.end(1) - m.start(1)),textStyle, false);
    }

    @Override
    protected Void doInBackground() {
        matching(textPane);
        return null;
    }
    @Override
    protected void done() {
    }
}