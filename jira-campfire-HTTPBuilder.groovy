import com.atlassian.jira.event.issue.AbstractIssueEventListener
import com.atlassian.jira.event.issue.IssueEvent
import com.atlassian.jira.ComponentManager
import org.apache.log4j.Category
import groovyx.net.http.RESTClient
import static groovyx.net.http.ContentType.JSON
import static com.atlassian.jira.event.type.EventType.*

class CampfireListener extends AbstractIssueEventListener {

    @Override
    void workflowEvent(IssueEvent event) {
        def Category log = Category.getInstance("com.onresolve.jira.groovy.PostFunction")
        def campfire = new RESTClient('https://CAMPFIREID.campfirenow.com/room/ROOMID/')
        def issueBaseUrl = "http://yourjirahost:8080/path-to-jira/browse/"
        campfire.auth.basic 'CAMPFIRE AUTH TOKEN', 'X'
        switch (event.getEventTypeId()) {
            case ISSUE_COMMENTED_ID:
                def resp = campfire.post( path: 'speak.json',
                                      body: [ message: [ type: "TextMessage", body:
                                          String.format("%s added a comment to %s (%s%s):",
                                              event.getUser().getDisplayName(),
                                              event.issue.getKey(),
                                              issueBaseUrl,
                                              event.issue.getKey())] ],
                                      requestContentType: JSON)
                resp = campfire.post( path: 'speak.json',
                                      body: [ message: [ type: "PasteMessage", body:
                                          String.format("%s", event.getComment().getBody()) ] ],
                                      requestContentType: JSON)
                break
            case ISSUE_CREATED_ID:
                def resp = campfire.post( path: 'speak.json',
                        body: [ message: [ type: "TextMessage", body:
                            String.format('%s created a new issue: "%s" (%s%s):',
                                event.getUser().getDisplayName(),
                                event.issue.getSummary(),
                                issueBaseUrl,
                                event.issue.getKey()) ] ],
                        requestContentType: JSON)
                resp = campfire.post( path: 'speak.json',
                      body: [ message: [ type: "PasteMessage", body:
                          String.format("%s", event.getIssue().getDescription()) ] ],
                      requestContentType: JSON)
                break
        }
    }
}
