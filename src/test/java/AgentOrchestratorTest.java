import com.schwab.agentic.orchestrator.AgentOrchestrator;
import com.schwab.agentic.orchestrator.WorkflowNode;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class AgentOrchestratorTest {

    @Test
    void testSuccessfulWorkflowExecution() {
        AgentOrchestrator orchestrator = new AgentOrchestrator();
        orchestrator.registerNode(new WorkflowNode("req", "Requirement Analysis", List.of(), () -> true, false, false, false));
        orchestrator.registerNode(new WorkflowNode("impl", "Implementation", List.of("req"), () -> true, true, false, false));

        boolean success = orchestrator.executeWorkflow(Map.of("impl", true));
        assertTrue(success);
        assertEquals(100.0, orchestrator.getMetrics().get("successRate"));
    }

    @Test
    void testWorkflowBlockedByHumanApproval() {
        AgentOrchestrator orchestrator = new AgentOrchestrator();
        orchestrator.registerNode(new WorkflowNode("impl", "Implementation", List.of(), () -> true, true, false, false));

        boolean success = orchestrator.executeWorkflow(Map.of("impl", false));
        assertFalse(success);
        assertEquals(1, orchestrator.getMetrics().get("rollbackCount"));
    }
}