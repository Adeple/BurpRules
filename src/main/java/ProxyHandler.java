import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.Annotations;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.proxy.http.*;
import burp.api.montoya.http.message.requests.HttpRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class ProxyHandler implements ProxyRequestHandler, ProxyResponseHandler {
    private ArrayList<Rule> rules;
    private int ruleCount;
    private final MontoyaApi api;

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
    public void deleteRule(int index){
        this.rules.set(index, new Rule());
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
        HttpRequest hr = (HttpRequest) interceptedRequest;
        for (Rule rule : this.rules) {
            if (rule.getIsEnabled() && rule.getLocation().contains("Request") && rule.checkRule(interceptedRequest)) {
                if (rule.getAction().equals("Highlight") || rule.getAction().equals("Add Note")) {
                    a = rule.annotateRequest(a);
                } else if (rule.getAction().equals("Drop Req/Res")) {
                    return ProxyRequestReceivedAction.drop();
                }
                else if (rule.getAction().equals("Add Header")){
                    String n = rule.getNote();
                    if(n.contains(": ")){
                        String headerName = n.substring(0, n.indexOf(": "));
                        String headerBody = n.substring(n.indexOf(": ")+2);
                        hr = hr.withAddedHeader(headerName, headerBody);
                    }
                }
                else if (rule.getAction().equals("Replace Header")){
                    String n = rule.getNote();
                    if(n.contains(": ")){
                        String headerName = n.substring(0, n.indexOf(": "));
                        String headerBody = n.substring(n.indexOf(": ")+2);
                        hr = hr.withHeader(headerName, headerBody);
                    }
                }
                else if (rule.getAction().equals("Remove Header")){
                    hr = hr.withRemovedHeader(rule.getNote());
                }
                else if(rule.getAction().equals("Find & Replace")){
                    hr = hr.withBody(URLEncoder.encode(URLDecoder.decode(hr.bodyToString(), StandardCharsets.UTF_8).replace(rule.getFind(), rule.getReplace()), StandardCharsets.UTF_8)
                            .replace("%3D", "=").replace("%26", "&"));                }
            }
        }
        return ProxyRequestReceivedAction.continueWith(hr, a);
    }
    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        return ProxyRequestToBeSentAction.continueWith(interceptedRequest);
    }
    @Override
    public ProxyResponseReceivedAction handleResponseReceived(InterceptedResponse interceptedResponse) {
        Annotations a = interceptedResponse.annotations();
        HttpResponse hr = (HttpResponse) interceptedResponse;
        for (Rule rule : this.rules) {
            if (rule.getIsEnabled() && rule.getLocation().contains("Response") && rule.checkRule(interceptedResponse)) {
                if (rule.getAction().equals("Highlight") || rule.getAction().equals("Add Note"))
                    a = rule.annotateResponse(a);
                else if (rule.getAction().equals("Drop Req/Res")) {
                    return ProxyResponseReceivedAction.drop();
                }
                else if (rule.getAction().equals("Add Header")){
                    String n = rule.getNote();
                    if(n.contains(": ")){
                        String headerName = n.substring(0, n.indexOf(": "));
                        String headerBody = n.substring(n.indexOf(": ")+2);
                        hr = hr.withAddedHeader(headerName, headerBody);
                    }
                }
                else if (rule.getAction().equals("Replace Header")){
                    String n = rule.getNote();
                    if(n.contains(": ")){
                        String headerName = n.substring(0, n.indexOf(": "));
                        String headerBody = n.substring(n.indexOf(": ")+2);
                        hr = hr.withUpdatedHeader(headerName, headerBody);
                    }
                }
                else if (rule.getAction().equals("Remove Header")){
                    hr = hr.withRemovedHeader(rule.getNote());
                }
                else if(rule.getAction().equals("Find & Replace")){
                    hr = hr.withBody(URLEncoder.encode(URLDecoder.decode(hr.bodyToString(), StandardCharsets.UTF_8).replace(rule.getFind(), rule.getReplace()), StandardCharsets.UTF_8)
                            .replace("%3D", "=").replace("%26", "&"));
                }

            }
        }
        return ProxyResponseReceivedAction.continueWith(hr, a);
    }
    @Override
    public ProxyResponseToBeSentAction handleResponseToBeSent(InterceptedResponse interceptedResponse) {
        return ProxyResponseToBeSentAction.continueWith(interceptedResponse);
    }
}
