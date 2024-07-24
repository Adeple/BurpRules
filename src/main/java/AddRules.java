import burp.api.montoya.core.HighlightColor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddRules implements ActionListener {
    private JTextField ruleName;
    private JPanel panel;
    private JButton addButton;
    private JLabel ifLabel;
    private JLabel ruleNameLabel;
    private JComboBox location;
    private JComboBox condition;
    private JTextField query;
    private JComboBox action;
    private JLabel thenLabel;
    private JTable rulesTable;
    private JComboBox colorSelect;
    private JLabel highlightLabel;
    private JScrollPane tablePane;
    private JButton clearButton;
    private JLabel noteLabel;
    private JTextField noteField;
    private DefaultTableModel model;
    private ProxyHandler ph;

    public AddRules(){
        this.addButton.addActionListener(this);
        String[] columns = {"Rule Name", "Location", "Condition", "Query", "Action", "Note/Color"};
        this.model = new DefaultTableModel(columns, 0);

        this.rulesTable.setModel(this.model);
        this.rulesTable.setDefaultEditor(Object.class, null);
        new JScrollPane();
    }

    public AddRules(ProxyHandler ph){
        this.ph = ph;

        //Add ActionListeners
        this.addButton.addActionListener(this);
        this.clearButton.addActionListener(this);
        this.action.addActionListener(this);

        //Hide options not in use
        this.noteField.setVisible(false);
        this.noteLabel.setVisible(false);

        //Create Table
        String[] columns = {"ID", "Rule Name", "Location", "Condition", "Query", "Action", "Note/Color"};
        this.model = new DefaultTableModel(columns, 0);
        this.rulesTable.setModel(this.model);
        this.rulesTable.setDefaultEditor(Object.class, null);
    }

    public JPanel getPanel(){
        return panel;
    }

    public String getQueryContents() {
        return this.query.getText();
    }

    public void restoreDefaults(){
        this.ruleName.setText("");
        this.location.setSelectedIndex(0);
        this.query.setText("");
        this.action.setSelectedIndex(0);
        this.colorSelect.setSelectedIndex(0);
        this.noteField.setText("");

        this.colorSelect.setVisible(true);
        this.highlightLabel.setVisible(true);
        this.noteField.setVisible(false);
        this.noteLabel.setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == this.addButton) {
            String detail = "";
            if(((String)this.action.getSelectedItem()).equals("Highlight")){
                detail = (String) this.colorSelect.getSelectedItem();
            }
            else if (((String)this.action.getSelectedItem()).equals("Add Note")){
                detail = this.noteField.getText();
            }
            this.model.addRow(new Object[]{this.ph.getRuleCount()+1, this.ruleName.getText(), (String) this.location.getSelectedItem(), (String) this.condition.getSelectedItem(),
                    this.query.getText(), (String) this.action.getSelectedItem(), detail});

            //Set Highlighter color
            HighlightColor hc = switch ((String) this.colorSelect.getSelectedItem()) {
                case "Red" -> HighlightColor.RED;
                case "Orange" -> HighlightColor.ORANGE;
                case "Yellow" -> HighlightColor.YELLOW;
                case "Green" -> HighlightColor.GREEN;
                case "Cyan" -> HighlightColor.CYAN;
                case "Blue" -> HighlightColor.BLUE;
                case "Pink" -> HighlightColor.PINK;
                case "Magenta" -> HighlightColor.MAGENTA;
                case "Gray" -> HighlightColor.GRAY;
                default -> HighlightColor.NONE;
            };

            String note = this.noteField.getText();


            this.ph.addRule(new Rule(this.ruleName.getText(), (String) this.location.getSelectedItem(), (String) this.condition.getSelectedItem(), (String) this.query.getText(), (String) this.action.getSelectedItem(), note, hc));
            this.model.fireTableDataChanged();
            restoreDefaults();
        }
        else if (e.getSource() == this.clearButton){
            this.ph.clearRules();
            for(int i = this.model.getRowCount(); i >= 0; i--){
                this.model.removeRow(i-1);
            }
            this.model.fireTableDataChanged();
            this.rulesTable.repaint();
            this.rulesTable.revalidate();
        }
        else if(e.getSource() == this.action){
            if(this.action.getSelectedIndex() == 0){
                this.colorSelect.setVisible(true);
                this.highlightLabel.setVisible(true);
                this.noteField.setVisible(false);
                this.noteLabel.setVisible(false);
            }
            else if (this.action.getSelectedIndex() == 1){
                this.colorSelect.setVisible(false);
                this.highlightLabel.setVisible(false);
                this.noteField.setVisible(true);
                this.noteLabel.setVisible(true);
            }
            else if (this.action.getSelectedIndex() == 2){
                this.colorSelect.setVisible(false);
                this.highlightLabel.setVisible(false);
                this.noteField.setVisible(false);
                this.noteLabel.setVisible(false);
            }
        }
    }
}
