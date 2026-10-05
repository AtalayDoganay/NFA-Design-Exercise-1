import automata.Automaton;
import automata.fsa.FSAStepWithClosureSimulator;
import file.XMLCodec;
import gui.environment.*;
import gui.action.*;
import gui.sim.multiple.InputTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.swing.*;
import javax.imageio.ImageIO;

public class Capture {
  static EnvironmentFrame frame;
  static Automaton machine;
  static Path root;
  static class Batch extends MultipleSimulateAction {
    Batch(Automaton a, Environment e){super(a,e);}
    JTable getTable(){return table;}
  }
  static class Steps extends SimulateAction {
    String input;
    Steps(Automaton a,Environment e,String s){super(a,e);input=s;}
    protected Object initialInput(Component c,String title){return input;}
    protected automata.AutomatonSimulator getSimulator(Automaton a){return new FSAStepWithClosureSimulator(a);}
  }
  static java.util.List<Component> all(Component c){
    java.util.List<Component> a=new ArrayList<>();a.add(c);
    if(c instanceof Container)for(Component d:((Container)c).getComponents())a.addAll(all(d));
    return a;
  }
  static JButton button(Component c,String s){
    for(Component d:all(c))if(d instanceof JButton && ((JButton)d).getText().equals(s))return (JButton)d;
    throw new IllegalStateException("Missing button "+s);
  }
  static void edt(Runnable r)throws Exception{SwingUtilities.invokeAndWait(r);}
  static void shot(String name)throws Exception{
    edt(()->{frame.validate();frame.repaint();});
    Thread.sleep(250);
    new Robot().waitForIdle();
    ImageIO.write(new Robot().createScreenCapture(frame.getBounds()),"png",root.resolve("images/"+name+".png").toFile());
  }
  static void split(Component c,boolean batch){
    for(Component d:all(c))if(d instanceof JSplitPane){JSplitPane p=(JSplitPane)d;p.setDividerLocation(p.getOrientation()==JSplitPane.HORIZONTAL_SPLIT?800:470);}
  }
  public static void main(String[] args)throws Exception{
    root=Paths.get(args[0]);
    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    for(Object k:Collections.list(UIManager.getDefaults().keys()))if(UIManager.get(k) instanceof javax.swing.plaf.FontUIResource)UIManager.put(k,new javax.swing.plaf.FontUIResource("Dialog",Font.PLAIN,16));
    edt(()->NewAction.showNew());
    StringBuilder log=new StringBuilder("JFLAP 7.1 actual Multiple Run results\nOfficial JAR SHA256: a22c095ddc56b18163e8ebeeef165b3a04cb570f35eb46b96105166e06c99406\n\n");
    for(int n:new int[]{7,8,11,23,24}){
      if(args.length>1 && n!=Integer.parseInt(args[1]))continue;
      String tag=String.format("n%02d",n);
      edt(()->{
        machine=(Automaton)new XMLCodec().decode(root.resolve(tag+".jff").toFile(),new HashMap<>());
        frame=FrameFactory.createFrame(machine);frame.getEnvironment().setFile(root.resolve(tag+".jff").toFile());
        frame.setBounds(0,0,1400,940);frame.setVisible(true);
      });
      shot(tag+"-jflap-nfa");
      java.util.List<String> inputs=Files.readAllLines(root.resolve(tag+"t.txt"));
      final Batch[] b=new Batch[1];
      edt(()->{
        b[0]=new Batch(machine,frame.getEnvironment());b[0].performAction(frame);
        JTable t=b[0].getTable();((InputTableModel)t.getModel()).clear();
        for(int i=0;i<inputs.size();i++)t.getModel().setValueAt(inputs.get(i),i,0);
        t.setRowSelectionInterval(inputs.size(),inputs.size());
        button((Component)frame.getEnvironment().getActive(),"Enter Lambda").doClick();
        t.setRowHeight(30);
        button((Component)frame.getEnvironment().getActive(),"Run Inputs").doClick();
        split((Component)frame.getEnvironment().getActive(),true);
        for(int r=0;r<t.getRowCount()-1;r++){
          String s=(String)t.getModel().getValueAt(r,0);
          String result=String.valueOf(t.getModel().getValueAt(r,t.getModel().getColumnCount()-1));
          log.append(tag+"\t"+(s.isEmpty()?"<empty>":s)+"\t"+result+"\n");
          String expected=r<8?"Accept":"Reject";
          if(!result.equals(expected))throw new AssertionError(tag+" "+s+" = "+result+" expected "+expected);
        }
        if(t.getRowCount()!=18)throw new AssertionError("Expected 17 tests plus trailing blank row; got "+t.getRowCount());
      });
      shot(tag+"-batch");
      System.out.println(tag+": 17 JFLAP batch results match");
      String input=n==8?"01110":n==11?"11001":n==24?"0010":null;
      if(input!=null){
        edt(()->{new Steps(machine,frame.getEnvironment(),input).actionPerformed(new ActionEvent(frame,0,"step"));split((Component)frame.getEnvironment().getActive(),false);});
        shot(tag+"-step-00");
        for(int i=1;i<=input.length();i++){
          edt(()->button((Component)frame.getEnvironment().getActive(),"Step").doClick());
          shot(tag+String.format("-step-%02d",i));
        }
        System.out.println(tag+": captured initial state and "+input.length()+" steps");
        if(n==11){
          edt(()->button((Component)frame.getEnvironment().getActive(),"Step").doClick());
          shot(tag+"-step-final");
        }
      }
      edt(()->frame.setVisible(false));
    }
    if(args.length==1)Files.writeString(root.resolve("jflap-validation.txt"),log.toString());
    System.exit(0);
  }
}
