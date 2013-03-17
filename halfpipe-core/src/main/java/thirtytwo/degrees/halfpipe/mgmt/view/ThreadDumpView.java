package thirtytwo.degrees.halfpipe.mgmt.view;

import com.yammer.metrics.reporting.ThreadDumpServlet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: spencergibb
 * Date: 9/21/12
 * Time: 6:48 PM
 */
@Controller
public class ThreadDumpView extends ThreadDumpServlet {

    @RequestMapping("/mgmt/threads")
    public void get(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        doGet(req, res);
    }

}