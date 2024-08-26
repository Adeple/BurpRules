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
    private JButton editButton;
    private JButton saveButton;
    private JButton cancelButton;
    private DefaultTableModel model;
    private ProxyHandler ph;
    private int selectedRow;

    //Default Constructor
    public AddRules(){
    }

    //AddRules Constructor
    public AddRules(ProxyHandler ph){
        this.ph = ph;
        selectedRow = -1;

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
        this.editButton.addActionListener(this);
        this.cancelButton.addActionListener(this);
        this.saveButton.addActionListener(this);

        restoreDefaults();

    }

    //Pass JPanel to main class
    public JPanel getPanel(){
        return panel;
    }

    //Restore default UI
    public void restoreDefaults(){
        this.ruleName.setText("");
//      this.location.setSelectedIndex(0);
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

        //Buttons
        this.addButton.setVisible(true);
        this.editButton.setVisible(true);
        this.saveButton.setVisible(false);
        this.cancelButton.setVisible(false);
        this.deleteButton.setVisible(true);
        this.clearButton.setVisible(true);
        this.exportButton.setVisible(true);
        this.importButton.setVisible(true);
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

    public void updateActionContext(){
        if(this.action.getSelectedIndex() == 0){ //Highlight
            this.colorSelect.setVisible(true);
            this.highlightLabel.setVisible(true);
            this.noteField.setVisible(false);
            this.noteLabel.setVisible(false);
        }
        else if (this.action.getSelectedIndex() == 1){ //Add note
            this.colorSelect.setVisible(false);
            this.highlightLabel.setVisible(false);
            this.noteField.setVisible(true);
            this.noteLabel.setVisible(true);
            this.noteLabel.setText("Note:");
        }
        else if (this.action.getSelectedIndex() == 2){ //Drop req/res
            this.colorSelect.setVisible(false);
            this.highlightLabel.setVisible(false);
            this.noteField.setVisible(false);
            this.noteLabel.setVisible(false);
        }
        else if (this.action.getSelectedIndex() == 3){ // Add header
            this.colorSelect.setVisible(false);
            this.highlightLabel.setVisible(false);
            this.noteField.setVisible(true);
            this.noteLabel.setVisible(true);
            this.noteLabel.setText("Header:");
        }
        else if (this.action.getSelectedIndex() == 4){ // Replace header
            this.colorSelect.setVisible(false);
            this.highlightLabel.setVisible(false);
            this.noteField.setVisible(true);
            this.noteLabel.setVisible(true);
            this.noteLabel.setText("Header:");
        }
        else if (this.action.getSelectedIndex() == 5){ // Remove header
            this.colorSelect.setVisible(false);
            this.highlightLabel.setVisible(false);
            this.noteField.setVisible(true);
            this.noteLabel.setVisible(true);
            this.noteLabel.setText("Header:");
        }
    }

    //ActionListeners
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
        else if (e.getSource() == this.clearButton){ //Clear rules
            this.ph.clearRules();
            for(int i = this.model.getRowCount(); i >= 0; i--){
                this.model.removeRow(i-1);
            }
            this.model.fireTableDataChanged();
        }
        else if (e.getSource() == this.deleteButton){ // Delete rule
            int[] rows = this.rulesTable.getSelectedRows();
            for(int i = rows.length-1; i >= 0; i--) {
                int index = (int) this.rulesTable.getValueAt(rows[i], 0);
                this.model.removeRow(rows[i]);
                this.ph.deleteRule(index-1);
            }
            this.model.fireTableDataChanged();
        }
        else if(e.getSource() == this.importButton){ //Import rule(s)
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
        else if (e.getSource() == this.exportButton){ //Export Rule(s)
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
        else if(e.getSource() == this.action){ //Update action options
            updateActionContext();
        }
        else if(e.getSource() == this.editButton){
            //Display Edit interface
            this.cancelButton.setVisible(true);
            this.saveButton.setVisible(true);

            //Hide other buttons
            this.addButton.setVisible(false);
            this.editButton.setVisible(false);
            this.deleteButton.setVisible(false);
            this.clearButton.setVisible(false);
            this.exportButton.setVisible(false);
            this.importButton.setVisible(false);

            int row = this.rulesTable.getSelectedRows()[0];
            selectedRow = row;

            this.ruleName.setText((String) this.rulesTable.getValueAt(row, 1));

            if(((String)this.rulesTable.getValueAt(row, 2)).contains("Request")){
                this.requestCheckBox.setSelected(true);
            }
            if(((String)this.rulesTable.getValueAt(row, 2)).contains("Response")){
                this.responseCheckBox.setSelected(true);
            }

            switch((String)this.rulesTable.getValueAt(row, 3)) {
                case "Contains":
                    this.condition.setSelectedIndex(0);
                    break;
                case "Does not contain":
                    this.condition.setSelectedIndex(1);
                    break;
                case "Has header":
                    this.condition.setSelectedIndex(2);
                    break;
                case "Lacks header":
                    this.condition.setSelectedIndex(3);
                    break;
                default:
                    this.condition.setSelectedIndex(0);
                    break;
            }

            this.query.setText((String) this.rulesTable.getValueAt(row, 4));

            this.regexCheckBox.setSelected(this.ph.getRules().get(row).getIsRegex());

            switch((String)this.rulesTable.getValueAt(row, 5)) {
                case "Highlight":
                    this.action.setSelectedIndex(0);
                    switch((String)this.rulesTable.getValueAt(row, 6)){
                        case "Black":
                            this.colorSelect.setSelectedIndex(0);
                            break;
                        case "Red":
                            this.colorSelect.setSelectedIndex(1);
                            break;
                        case "Orange":
                            this.colorSelect.setSelectedIndex(2);
                            break;
                        case "Yellow":
                            this.colorSelect.setSelectedIndex(3);
                            break;
                        case "Green":
                            this.colorSelect.setSelectedIndex(4);
                            break;
                        case "Cyan":
                            this.colorSelect.setSelectedIndex(5);
                            break;
                        case "Blue":
                            this.colorSelect.setSelectedIndex(6);
                            break;
                        case "Pink":
                            this.colorSelect.setSelectedIndex(7);
                            break;
                        case "Magenta":
                            this.colorSelect.setSelectedIndex(8);
                            break;
                        case "Gray":
                            this.colorSelect.setSelectedIndex(9);
                            break;
                    }
                    break;
                case "Add Note":
                    this.action.setSelectedIndex(1);
                    break;
                case "Drop Req/Res":
                    this.action.setSelectedIndex(2);
                    break;
                case "Add Header":
                    this.action.setSelectedIndex(3);
                    break;
                case "Replace Header":
                    this.action.setSelectedIndex(4);
                    break;
                case "Remove Header":
                    this.action.setSelectedIndex(5);
                    break;
                default:
                    this.action.setSelectedIndex(0);
                    break;
            }
            updateActionContext();

            this.noteField.setText((String)this.rulesTable.getValueAt(row, 6));

        }
        else if(e.getSource() == this.cancelButton){
            restoreDefaults();
        }
        else if (e.getSource() == this.saveButton){
            Rule temp;
            if(selectedRow >= 0) {
                temp = this.ph.getRules().get(selectedRow);

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

                this.model.setValueAt(this.ruleName.getText(),selectedRow,1);
                this.model.setValueAt(location,selectedRow,2);
                this.model.setValueAt(this.condition.getSelectedItem(),selectedRow,3);
                this.model.setValueAt(this.query.getText(),selectedRow,4);
                this.model.setValueAt(this.action.getSelectedItem(),selectedRow,5);
                this.model.setValueAt(detail,selectedRow,6);

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

                temp.setRuleName(this.ruleName.getText());
                temp.setLocation(location);
                temp.setCondition((String)this.condition.getSelectedItem());
                temp.setQuery(this.query.getText());
                temp.setAction((String)this.action.getSelectedItem());
                temp.setNote(note);
                temp.setColor(hc);
                temp.setIsRegex(this.regexCheckBox.isSelected());

                this.model.fireTableDataChanged();
            }
            restoreDefaults();
        }
    }
}
