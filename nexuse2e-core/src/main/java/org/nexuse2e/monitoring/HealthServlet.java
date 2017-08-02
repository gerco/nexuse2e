package org.nexuse2e.monitoring;

import org.json.JSONException;
import org.json.JSONStringer;
import org.nexuse2e.Engine;
import org.nexuse2e.EngineStatusSummary;
import org.nexuse2e.NexusException;
import org.nexuse2e.StatusSummary;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Guido Esch
 */
public class HealthServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        Writer writer = response.getWriter();
        try {
            String healthJSON = new JSONStringer().object()
                    .key("healthStatus").value(false).endObject().toString();

            if (Engine.getInstance().getEngineController() != null && Engine.getInstance().getEngineController().getEngineMonitor() != null) {
                EngineStatusSummary summary = Engine.getInstance().getEngineController().getEngineMonitor().getStatus();
                if (summary != null) {
                    String summaryStatus = "unknown";
                    switch(summary.getStatus()) {
                        case ACTIVE:
                            summaryStatus = "active";
                            break;
                        case ERROR:
                            summaryStatus = "down";
                            break;
                        case UNKNOWN:
                            summaryStatus = "down";
                            break;
                        case INACTIVE:
                            summaryStatus = "maintenance";
                            break;
                    }
                    healthJSON = new JSONStringer().object()
                            .key("healthStatus").value(summaryStatus)
                            .key("databases").array()
                            .object().key("default").value(summary.getDatabaseStatus().equals(StatusSummary.Status.ACTIVE)).endObject()
                            .endArray()
                            .key("services").array()
                            .endArray()
                            .key("details").array()
                            .endArray()
                            .endObject().toString();
                }
            }
            writer.write(healthJSON);
        } catch (NexusException | JSONException ignored) {
        }
        writer.flush();
    }
}
