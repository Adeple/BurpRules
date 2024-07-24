import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.*;
import java.awt.event.ActionEvent;

public class BurpRules extends AddRules implements BurpExtension {
    AddRules a;
    ProxyHandler ph;

    @Override
    public void initialize(MontoyaApi api) {
            //Extension Name in Extensions Tab
            api.extension().setName("Burp Rule Manager");

            //Initialize ProxyHandler and register with API
            ph = new ProxyHandler(api);
            api.proxy().registerRequestHandler(ph);
            api.proxy().registerResponseHandler(ph);

            //Initialize GUI with ProxyHandler
            a = new AddRules(ph);

            //Load GUI
            api.userInterface().registerSuiteTab("Burp Rules", a.getPanel());

            //Load Message
            print("Burp Rules Manager v1.0\nBy Andrew Palmer\n\n\n\nLoaded Successfully", api);
  }

    //Simplified print function
    private void print(String s, MontoyaApi api){
        Logging log = api.logging();
        log.logToOutput(s);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
