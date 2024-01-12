package example.customscanchecks;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.scanner.AuditResult;
import burp.api.montoya.scanner.ConsolidationAction;
import burp.api.montoya.scanner.audit.insertionpoint.AuditInsertionPoint;
import burp.api.montoya.scanner.audit.issues.AuditIssue;
import burp.api.montoya.scanner.audit.issues.AuditIssueConfidence;
import burp.api.montoya.scanner.audit.issues.AuditIssueSeverity;
import example.customscanchecks.UI.ConfigUi;
import example.customscanchecks.UI.TableTemplate;

import java.util.ArrayList;
import java.util.List;

import static burp.api.montoya.scanner.AuditResult.auditResult;
import static burp.api.montoya.scanner.ConsolidationAction.KEEP_BOTH;
import static burp.api.montoya.scanner.ConsolidationAction.KEEP_EXISTING;
import static burp.api.montoya.scanner.audit.issues.AuditIssue.auditIssue;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;


class ScanCheck implements burp.api.montoya.scanner.ScanCheck {
    private final TableTemplate tableModel;
    private static final String GREP_STRING = "www.test.com";
    private final MontoyaApi api;
    public ConfigUi configUi;

    ScanCheck(MontoyaApi api, TableTemplate tableModel, ConfigUi configUi) {
        this.tableModel = tableModel;
        this.api = api;
        this.configUi = configUi;

    }

    @Override
    public AuditResult activeAudit(HttpRequestResponse baseRequestResponse, AuditInsertionPoint auditInsertionPoint) {
        return null;
    }

    @Override
    public AuditResult passiveAudit(HttpRequestResponse baseRequestResponse) {
        List<AuditIssue> auditIssueList = new ArrayList<>();
        if (!this.configUi.getAutoSendRequest()) {
            return auditResult(auditIssueList);
        }
        TestModel model = new TestModel(this.api, baseRequestResponse);
        HttpRequestResponse result_package = model.test_engine(this.api, baseRequestResponse, this.tableModel);
        if (result_package != null) {
            auditIssueList = singletonList(
                    auditIssue(
                            "Host header attack",
                            "This response packet contains incorrect host field information: " + GREP_STRING,
                            "Web applications should use SERVER_NAME instead of host header",
                            result_package.request().url(),
                            AuditIssueSeverity.LOW,
                            AuditIssueConfidence.CERTAIN,
                            null,
                            null,
                            AuditIssueSeverity.LOW,
                            result_package));
        } else {
            auditIssueList = emptyList();
        }
        return auditResult(auditIssueList);
    }

    @Override
    public ConsolidationAction consolidateIssues(AuditIssue newIssue, AuditIssue existingIssue) {
        return existingIssue.name().equals(newIssue.name()) ? KEEP_EXISTING : KEEP_BOTH;
    }
}