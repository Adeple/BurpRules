import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.Annotations;
import burp.api.montoya.proxy.http.*;

import java.util.ArrayList;

import static burp.api.montoya.core.HighlightColor.*;

public class ProxyHandler implements ProxyRequestHandler, ProxyResponseHandler {
    private ArrayList<Rule> rules;
    private int ruleCount;
    private MontoyaApi api;

    public ProxyHandler(){
        this.rules = new ArrayList<Rule>();
        this.ruleCount = 0;
    }

    public ProxyHandler(MontoyaApi api){
        this.api = api;
        this.rules = new ArrayList<Rule>();
        this.ruleCount = 0;
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
        this.rules = new ArrayList<Rule>();
        this.ruleCount = 0;
    }

    public ArrayList<Rule> getRules() {
        return this.rules;
    }

    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        Annotations a = interceptedRequest.annotations();
        for(int i = 0; i < this.rules.size(); i++){
            if (this.rules.get(i).getLocation().equals("Request") && this.rules.get(i).checkRule(interceptedRequest)){
                if(this.rules.get(i).getAction().equals("Highlight") || this.rules.get(i).getAction().equals("Add Note"))
                    a = this.rules.get(i).annotateRequest(a);
                else if (this.rules.get(i).getAction().equals("Drop Req/Res")){
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
        for(int i = 0; i < this.rules.size(); i++){
            if (this.rules.get(i).getLocation().equals("Response") && rules.get(i).checkRule(interceptedResponse)){
                return ProxyResponseReceivedAction.continueWith(interceptedResponse, interceptedResponse.annotations().withHighlightColor(this.rules.get(i).getColor()));
            }
        }
        return ProxyResponseReceivedAction.continueWith(interceptedResponse);
    }

    @Override
    public ProxyResponseToBeSentAction handleResponseToBeSent(InterceptedResponse interceptedResponse) {
        return ProxyResponseToBeSentAction.continueWith(interceptedResponse);
    }
}
