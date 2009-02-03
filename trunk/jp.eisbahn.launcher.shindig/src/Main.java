import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class Main {
  public static void main(String[] args) {
    try {
      SelectChannelConnector connector = new SelectChannelConnector();
      connector.setPort(Integer.parseInt(args[0]));
      Server server = new Server();
      server.setConnectors(new Connector[] {connector});
      WebAppContext web = new WebAppContext();
      web.setWar(args[1]);
      web.setContextPath("/");
      server.addHandler(web);
      server.start();
      server.join();
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
