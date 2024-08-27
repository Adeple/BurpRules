import burp.api.montoya.core.Annotations;
import burp.api.montoya.core.HighlightColor;
import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.InterceptedResponse;

import java.util.regex.Pattern;

public class Rule {
    private int id;
    private String ruleName, location, condition, query, action, note;
    private HighlightColor color;
    private Boolean isRegex, isEnabled;

    //Constructor
    public Rule(){
        this.ruleName = "";
        this.location = "";
        this.condition = "";
        this.query = "";
        this.action = "";
        this.id = -1;
        this.color = null;
        this.note = "";
        this.isRegex = false;
        this.isEnabled = false;
    }

    public Rule(String ruleName, String location, String condition, String query, String action, String note, HighlightColor color, Boolean isRegex){
        this.ruleName = ruleName;
        this.location = location;
        this.condition = condition;
        this.query = query;
        this.action = action;
        this.id = -1;
        this.color = color;
        this.note = note;
        this.isRegex = isRegex;
        this.isEnabled = true;
    }

    public Rule(Rule r){
        this.ruleName = r.getRuleName();
        this.location = r.getLocation();
        this.condition = r.getCondition();
        this.query = r.getQuery();
        this.action = r.getAction();
        this.color = r.getColor();
        this.note = r.getNote();
        this.isRegex = r.getIsRegex();
        this.id = -1;
        this.isEnabled = true;
    }

    public boolean checkRule(InterceptedRequest req){
        if (this.condition.equals("Contains") || this.condition.equals("Does not contain")){
            if(this.isRegex) {
                return Pattern.compile(this.query).matcher(req.toString()).find() == (this.condition.equals("Contains"));            }
            else {
                return req.toString().contains(this.query) == (this.condition.equals("Contains"));
            }
        }
        else if (this.condition.equals("Has header") || this.condition.equals("Lacks header")){
            return req.hasHeader(this.query) == (this.condition.equals("Has header"));
        }
        else return false;
    }
    public boolean checkRule(InterceptedResponse res){
        if (this.condition.equals("Contains") || this.condition.equals("Does not contain")){
            if(this.isRegex) {
                return Pattern.compile(this.query).matcher(res.toString()).find() == (this.condition.equals("Contains"));
            }
            else {
                return res.toString().contains(this.query) == (this.condition.equals("Contains"));
            }
        }
        else if (this.condition.equals("Has header") || this.condition.equals("Lacks header")){
            return res.hasHeader(this.query) == (this.condition.equals("Has header"));
        }
        else return false;
    }
    public Annotations annotateRequest(Annotations a){
        if(this.action.equals("Highlight")){
            a = a.withHighlightColor(this.color);
        }
        else if(this.action.equals("Add Note")){
            a = a.notes().isEmpty() ? a.withNotes(this.note) : a.withNotes(a.notes() + " | " + this.note);
        }
        return a;
    }
    public Annotations annotateResponse(Annotations a){
        if(this.action.equals("Highlight")){
            a = a.withHighlightColor(this.color);
        }
        else if(this.action.equals("Add Note")){
            a = a.notes().isEmpty() ? a.withNotes(this.note) : a.withNotes(a.notes() + " | " + this.note);
        }
        return a;
    }

    //Accessors
    public String getAction(){
        return this.action;
    }
    public HighlightColor getColor(){
        return this.color;
    }
    public String getCondition() {
        return condition;
    }
    public int getId(){return this.id;}
    public Boolean getIsRegex(){
        return this.isRegex;
    }
    public String getLocation(){
        return this.location;
    }
    public String getNote() {
        return note;
    }
    public String getQuery() {
        return query;
    }
    public String getRuleName() {
        return ruleName;
    }
    public Boolean getIsEnabled() {return this.isEnabled;}

    //Mutators
    public void setAction(String action) {
        this.action = action;
    }
    public void setColor(HighlightColor color){this.color = color;}
    public void setCondition(String condition) {
        this.condition = condition;
    }
    public void setID(int id){
        this.id = id;
    }
    public void setIsRegex(Boolean isRegex) {
        this.isRegex = isRegex;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public void setQuery(String query) {
        this.query = query;
    }
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
    public void setIsEnabled(Boolean isEnabled){this.isEnabled = isEnabled;}
}
