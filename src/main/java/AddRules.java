import burp.api.montoya.core.HighlightColor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Objects;
import java.util.Scanner;


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
    private JButton exportButton;
    private JButton importButton;
    private JButton deleteButton;
    private JCheckBox regexCheckBox;
    private JCheckBox requestCheckBox;
    private JCheckBox responseCheckBox;
    private DefaultTableModel model;
    private ProxyHandler ph;

    //Default Constructor
    public AddRules(){
    }

    //AddRules Constructor
    public AddRules(ProxyHandler ph){
        this.ph = ph;

        //Hide options not in use
        this.noteField.setVisible(false);
        this.noteLabel.setVisible(false);

        //Create Table
        String[] columns = {"ID", "Rule Name", "Location", "Condition", "Query", "Action", "Note/Color/Header"};
        this.model = new DefaultTableModel(columns, 0);
        this.rulesTable.setModel(this.model);
        this.rulesTable.setDefaultEditor(Object.class, null);

        //Set width for each column
        TableColumnModel tcm = this.rulesTable.getColumnModel();
        tcm.getColumn(0).setMaxWidth(50);
        tcm.getColumn(0).setResizable(false);
        tcm.getColumn(1).setMaxWidth(1000);
        tcm.getColumn(2).setMaxWidth(1000);
        tcm.getColumn(3).setMaxWidth(1000);
        tcm.getColumn(4).setMaxWidth(1000);
        tcm.getColumn(5).setMaxWidth(1000);
        tcm.getColumn(6).setMaxWidth(1000);

        //Add ActionListeners
        this.addButton.addActionListener(this);
        this.clearButton.addActionListener(this);
        this.action.addActionListener(this);
        this.importButton.addActionListener(this);
        this.exportButton.addActionListener(this);
        this.deleteButton.addActionListener(this);

    }

    //Pass JPanel to main class
    public JPanel getPanel(){
        return panel;
    }

    //Restore default UI
    public void restoreDefaults(){
        this.ruleName.setText("");
//        this.location.setSelectedIndex(0);
        this.requestCheckBox.setSelected(false);
        this.responseCheckBox.setSelected(false);
        this.condition.setSelectedIndex(0);
        this.query.setText("");
        this.action.setSelectedIndex(0);
        this.colorSelect.setSelectedIndex(0);
        this.noteField.setText("");
        this.regexCheckBox.setSelected(false);

        this.colorSelect.setVisible(true);
        this.highlightLabel.setVisible(true);
        this.noteField.setVisible(false);
        this.noteLabel.setVisible(false);
    }

    //Import rules file (JSON)
    public void parseInFile(File infile) throws JSONException, IOException {
            Scanner scan = new Scanner(infile);
            StringBuilder sb = new StringBuilder();
            while(scan.hasNextLine()) {
                sb.append(scan.nextLine());
            }
            String content = sb.toString();
            JSONArray jarr = new JSONArray(content);
            for (int i = 0; i < jarr.length(); i++) {
                JSONObject r = jarr.getJSONObject(i);
                    HighlightColor hc = switch (r.getString("Color")) {
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

                      //Add row in table
                    this.model.addRow(new Object[]{this.ph.getRuleCount() + 1, r.getString("RuleName"), r.getString("Location"),
                            r.getString("Condition"), r.getString("Query").replaceAll("\\\\\\\\", "\\\\"), r.getString("Action"),
                            r.getString("Action").equals("Highlight") ? r.getString("Color") : r.getString("Note")});

                    //Add rule in rules list
                    this.ph.addRule(new Rule(r.getString("RuleName"), r.getString("Location"), r.getString("Condition"),
                            r.getString("Query"), r.getString("Action"), r.getString("Note"), hc, r.getString("QueryType").equals("RegEx")));
                }
            scan.close();
    }

    //Export rules file (JSON)
    public void parseOutFile(File outfile) {
            try{
            FileWriter write = new FileWriter(outfile);
            write.write("[\n");
            for(int i = 0; i < this.ph.getRules().size(); i++){
                String color = switch (this.ph.getRules().get(i).getColor()) {
                    case HighlightColor.RED -> "Red";
                    case HighlightColor.ORANGE -> "Orange";
                    case HighlightColor.YELLOW -> "Yellow";
                    case HighlightColor.GREEN -> "Green";
                    case HighlightColor.CYAN -> "Cyan";
                    case HighlightColor.BLUE -> "Blue";
                    case HighlightColor.PINK -> "Pink";
                    case HighlightColor.MAGENTA -> "Magenta";
                    case HighlightColor.GRAY -> "Gray";
                    default -> "";
                };
                String line = "{\"RuleName\": \"" + this.ph.getRules().get(i).getRuleName() + "\", \"Location\":\"" + this.ph.getRules().get(i).getLocation() + "\", \"Condition\":\""
                        + this.ph.getRules().get(i).getCondition() + "\", \"Query\":\"" + this.ph.getRules().get(i).getQuery().replaceAll("\\\\", "\\\\\\\\") + "\", \"Action\":\"" + this.ph.getRules().get(i).getAction()
                        + "\", \"Note\":\"" + this.ph.getRules().get(i).getNote() + "\", \"Color\":\"" + color +
                         "\", \"QueryType\":\"" + (this.ph.getRules().get(i).getIsRegex() ? "RegEx" : "String") + "\"}";
                if(i < this.ph.getRules().size() -1) {
                    line += ",";
                }
                line += "\n";
                write.write(line);
                write.flush();
            }
            write.write("]");
            write.close();
            }
            catch(IOException noFile){
                return;
            }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == this.addButton) {
            String detail = "";
            if(Objects.equals(this.action.getSelectedItem(), "Highlight")){
                detail = (String) this.colorSelect.getSelectedItem();
            }
            else if (((String)this.action.getSelectedItem()).equals("Add Note")){
                detail = this.noteField.getText();
            }
            else if(this.action.getSelectedItem().equals("Add Header")){
                detail = this.noteField.getText();
            }
            else if (this.action.getSelectedItem().equals("Replace Header")){
                detail = this.noteField.getText();
            }
            else if (this.action.getSelectedItem().equals("Remove Header")){
                detail = this.noteField.getText();
            }

            String location = "";
            if (this.requestCheckBox.isSelected() && this.responseCheckBox.isSelected()){
                    location += "Request & Response";
            } else if (this.requestCheckBox.isSelected()) {
                location += "Request";
            } else if (this.responseCheckBox.isSelected()){
                location += "Response";
            }

            this.model.addRow(new Object[]{this.ph.getRuleCount()+1, this.ruleName.getText(), location, this.condition.getSelectedItem(),
                    this.query.getText(), this.action.getSelectedItem(), detail});

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

            this.ph.addRule(new Rule(this.ruleName.getText(), location, (String) this.condition.getSelectedItem(),
                    this.query.getText(), (String) this.action.getSelectedItem(), note, hc, this.regexCheckBox.isSelected()));
            this.model.fireTableDataChanged();
            restoreDefaults();
        }
        else if (e.getSource() == this.clearButton){
            this.ph.clearRules();
            for(int i = this.model.getRowCount(); i >= 0; i--){
                this.model.removeRow(i-1);
            }
            this.model.fireTableDataChanged();
        }
        else if (e.getSource() == this.deleteButton){
            int[] rows = this.rulesTable.getSelectedRows();
            for(int i = rows.length-1; i >= 0; i--) {
                int index = (int) this.rulesTable.getValueAt(rows[i], 0);
                this.model.removeRow(rows[i]);
                this.ph.deleteRule(index-1);
            }
            this.model.fireTableDataChanged();
        }
        else if(e.getSource() == this.importButton){
            JFileChooser fc = new JFileChooser();
            FileNameExtensionFilter infil = new FileNameExtensionFilter("JSON", "json");
            fc.setFileFilter(infil);
            int temp = fc.showOpenDialog(null);
            if(temp == JFileChooser.APPROVE_OPTION){
                try {
                    parseInFile(fc.getSelectedFile());
                }
                catch (IOException | JSONException ex){
                    return;
                }
            }
        }
        else if (e.getSource() == this.exportButton){
            JFileChooser fc = new JFileChooser();
            int temp = fc.showSaveDialog(null);
            if(temp == JFileChooser.APPROVE_OPTION){
                if(fc.getSelectedFile().getPath().contains(".json")){
                    parseOutFile(fc.getSelectedFile());
                }
                else{
                    parseOutFile(new File(fc.getSelectedFile()+".json"));

                }
            }
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
                this.noteLabel.setText("Note:");
            }
            else if (this.action.getSelectedIndex() == 2){
                this.colorSelect.setVisible(false);
                this.highlightLabel.setVisible(false);
                this.noteField.setVisible(false);
                this.noteLabel.setVisible(false);
            }
            else if (this.action.getSelectedIndex() == 3){
                this.colorSelect.setVisible(false);
                this.highlightLabel.setVisible(false);
                this.noteField.setVisible(true);
                this.noteLabel.setVisible(true);
                this.noteLabel.setText("Header:");
            }
            else if (this.action.getSelectedIndex() == 4){
                this.colorSelect.setVisible(false);
                this.highlightLabel.setVisible(false);
                this.noteField.setVisible(true);
                this.noteLabel.setVisible(true);
                this.noteLabel.setText("Header:");
            }
            else if (this.action.getSelectedIndex() == 5){
                this.colorSelect.setVisible(false);
                this.highlightLabel.setVisible(false);
                this.noteField.setVisible(true);
                this.noteLabel.setVisible(true);
                this.noteLabel.setText("Header:");
            }
        }
    }
}
