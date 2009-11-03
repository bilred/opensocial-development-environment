import java.io.File;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.deployer.ContextDeployer;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class Main {
  public static void main(String[] args) {
    try {
      SelectChannelConnector connector = new SelectChannelConnector();
      connector.setPort(Integer.parseInt(args[0]));
      Server server = new Server();
      server.setConnectors(new Connector[] {connector});
      //
      ContextDeployer deployer = new ContextDeployer();
      deployer.setConfigurationDir(new File(args[2]));
      deployer.setScanInterval(1);
      ContextHandlerCollection contexts = new ContextHandlerCollection();
      server.setHandler(contexts);
      deployer.setContexts(contexts);
      server.addLifeCycle(deployer);
      //
      WebAppContext web = new WebAppContext();

      /**
       * to avoid resource deletion failure, diable file mapped buffer [issue 156]
       * */
      web.setAttribute("useFileMappedBuffer", false);
      web.setWar(args[1]);
      web.setContextPath("/");
      contexts.addHandler(web);
      //
      server.start();
      server.join();
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
