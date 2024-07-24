import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.Annotations;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.proxy.http.*;
import java.util.ArrayList;


public class ProxyHandler implements ProxyRequestHandler, ProxyResponseHandler {
    private ArrayList<Rule> rules;
    private int ruleCount;
    private MontoyaApi api;

    public ProxyHandler(){
        this.rules = new ArrayList<>();
        this.ruleCount = 0;
    }

    public ProxyHandler(MontoyaApi api){
        this.api = api;
        this.rules = new ArrayList<>();
        this.ruleCount = 0;
    }

    public void print(String s){
        Logging log = this.api.logging();
        log.logToOutput(s);
    }

    public int getRuleCount(){
        return this.ruleCount;
    }

    public void addRule(Rule r){
        r.setID(this.ruleCount+1);
        this.rules.add(r);
        this.ruleCount++;
    }
    public void clearRules(){
        this.rules = new ArrayList<>();
        this.ruleCount = 0;
    }

    public ArrayList<Rule> getRules() {
        return this.rules;
    }

    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        Annotations a = interceptedRequest.annotations();
        for (Rule rule : this.rules) {
            if (rule.getLocation().equals("Request") && rule.checkRule(interceptedRequest)) {
                if (rule.getAction().equals("Highlight") || rule.getAction().equals("Add Note"))
                    a = rule.annotateRequest(a);
                else if (rule.getAction().equals("Drop Req/Res")) {
                    return ProxyRequestReceivedAction.drop();
                }
            }
        }
        return ProxyRequestReceivedAction.continueWith(interceptedRequest, a);
    }

    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        return ProxyRequestToBeSentAction.continueWith(interceptedRequest);
    }

    @Override
    public ProxyResponseReceivedAction handleResponseReceived(InterceptedResponse interceptedResponse) {
        Annotations a = interceptedResponse.annotations();
        for (Rule rule : this.rules) {
            if (rule.getLocation().equals("Response") && rule.checkRule(interceptedResponse)) {
                if (rule.getAction().equals("Highlight") || rule.getAction().equals("Add Note"))
                    a = rule.annotateResponse(a);
                else if (rule.getAction().equals("Drop Req/Res")) {
                    return ProxyResponseReceivedAction.drop();
                }
            }
        }
        return ProxyResponseReceivedAction.continueWith(interceptedResponse, a);
    }

    @Override
    public ProxyResponseToBeSentAction handleResponseToBeSent(InterceptedResponse interceptedResponse) {
        return ProxyResponseToBeSentAction.continueWith(interceptedResponse);
    }
}
