import burp.api.montoya.core.Annotations;
import burp.api.montoya.core.HighlightColor;
import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.InterceptedResponse;

public class Rule {
    private int id;
    private String ruleName, location, condition, query, action, note;
    private HighlightColor color;

    //Constructor
    public Rule(String ruleName, String location, String condition, String query, String action, String note, HighlightColor color){
        this.ruleName = ruleName;
        this.location = location;
        this.condition = condition;
        this.query = query;
        this.action = action;
        this.id = -1;
        this.color = color;
        this.note = note;
    }

    public boolean checkRule(InterceptedRequest req){
        return req.toString().contains(this.query) == (this.condition.equals("Contains"));
    }

    public boolean checkRule(InterceptedResponse res){
        return res.toString().contains(this.query) == (this.condition.equals("Contains"));
    }

    public Annotations annotateRequest(Annotations a){
        if(this.action.equals("Highlight")){
            a = a.withHighlightColor(this.color);
        }
        else if(this.action.equals("Add Note")){
            a = a.withNotes(this.note);
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
    public String getLocation(){
        return this.location;
    }
    public String getQuery() {
        return query;
    }
    public String getRuleName() {
        return ruleName;
    }

    //Mutators
    public void setAction(String action) {
        this.action = action;
    }
    public void setCondition(String condition) {
        this.condition = condition;
    }
    public void setID(int id){
        this.id = id;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setQuery(String query) {
        this.query = query;
    }
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
}
