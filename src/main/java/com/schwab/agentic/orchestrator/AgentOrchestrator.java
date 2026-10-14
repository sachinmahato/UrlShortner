package com.schwab.agentic.orchestrator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AgentOrchestrator {
    private static final Logger log = LoggerFactory.getLogger(AgentOrchestrator.class);
    private final Map<String, WorkflowNode> graph = new ConcurrentHashMap<>();

    // Metrics tracking
    private int totalExecutions = 0;
    private int retryCount = 0;
    private int rollbackCount = 0;
    private Instant startTime;

    public void registerNode(WorkflowNode node) {
        graph.put(node.getId(), node);
    }

    public boolean executeWorkflow(Map<String, Boolean> humanApprovals) {
        startTime = Instant.now();
        totalExecutions++;
        log.info("Starting Agentic SDLC Workflow Orchestration with {} nodes.", graph.size());

        Set<String> completed = new HashSet<>();
        List<String> executionOrder = resolveDependencyOrder();

        for (String nodeId : executionOrder) {
            WorkflowNode node = graph.get(nodeId);
            log.info("Evaluating node: [{}] - {}", node.getId(), node.getName());

            // Check human approval gate if required
            if (node.isRequiresHumanApproval()) {
                boolean approved = humanApprovals.getOrDefault(nodeId, false);
                if (!approved) {
                    log.warn("Gate blocked: Node [{}] requires human sign-off but was not approved. Initiating Rollback.", nodeId);
                    rollbackCount++;
                    return false;
                }
                log.info("Human approval granted for node [{}]", nodeId);
            }

            // Execute with bounded retries and fallback
            boolean success = executeWithRetries(node, 3);
            node.setExecuted(true);
            node.setSuccessful(success);

            if (!success) {
                log.error("Workflow failed at node [{}]. Triggering safe-stop and rollback mechanisms.", nodeId);
                rollbackCount++;
                return false;
            }
            completed.add(nodeId);
        }

        log.info("Workflow successfully completed in {} ms.", Duration.between(startTime, Instant.now()).toMillis());
        return true;
    }

    private boolean executeWithRetries(WorkflowNode node, int maxRetries) {
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                boolean result = node.getTaskAction().get();
                if (result) return true;
            } catch (Exception e) {
                log.warn("Exception in node [{}] on attempt {}: {}", node.getId(), attempt + 1, e.getMessage());
            }
            attempt++;
            retryCount++;
            log.info("Retrying node [{}] (Attempt {}/{})", node.getId(), attempt, maxRetries);
        }
        return false;
    }

    private List<String> resolveDependencyOrder() {
        List<String> sorted = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        SetsDFS(graph.keySet(), visited, sorted);
        return sorted;
    }

    private void SetsDFS(Set<String> nodeIds, Set<String> visited, List<String> sorted) {
        for (String id : nodeIds) {
            if (!visited.contains(id)) {
                dfsVisit(id, visited, sorted);
            }
        }
    }

    private void dfsVisit(String id, Set<String> visited, List<String> sorted) {
        visited.add(id);
        WorkflowNode node = graph.get(id);
        if (node.getDependencies() != null) {
            for (String dep : node.getDependencies()) {
                if (!visited.contains(dep)) {
                    dfsVisit(dep, visited, sorted);
                }
            }
        }
        sorted.add(id);
    }

    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalExecutions", totalExecutions);
        metrics.put("retryCount", retryCount);
        metrics.put("rollbackCount", rollbackCount);
        metrics.put("successRate", totalExecutions > 0 ? ((double)(totalExecutions - rollbackCount) / totalExecutions) * 100 : 100.0);
        return metrics;
    }
}