package com.schwab.agentic.orchestrator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.function.Supplier;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowNode {
    private String id;
    private String name;
    private List<String> dependencies;
    private Supplier<Boolean> taskAction;
    private boolean requiresHumanApproval;
    private boolean executed;
    private boolean successful;
}