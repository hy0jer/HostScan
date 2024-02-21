package example.customscanchecks;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.editor.HttpRequestEditor;
import burp.api.montoya.ui.editor.HttpResponseEditor;
import example.customscanchecks.UI.ConfigUi;
import example.customscanchecks.UI.Menu;
import example.customscanchecks.UI.TableTemplate;

import javax.swing.*;
import java.awt.*;

import static burp.api.montoya.ui.editor.EditorOptions.READ_ONLY;


public class BurpExtender implements BurpExtension {
    private MontoyaApi api;

    @Override
    public void initialize(MontoyaApi api) {
        Logging logging = api.logging();
        logging.logToOutput("""
                ===================================
                HostScan v2.1 load success!
                Author: hy0jer
                ===================================""");
        this.api = api;
        TableTemplate tableModel = new TableTemplate();
        ConfigUi config = new ConfigUi();
        api.extension().setName("HostScan");
        api.userInterface().registerSuiteTab("HostScan", constructLoggerTab(tableModel, config));
        api.userInterface().registerContextMenuItemsProvider(new Menu(api, tableModel));
        api.scanner().registerScanCheck(new ScanCheck(api, tableModel, config));
    }

    private Component constructLoggerTab(TableTemplate tableModel, ConfigUi config) {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JSplitPane downSplitPane = new JSplitPane();
        downSplitPane.setResizeWeight(0.5D);

        JTabbedPane tabs = new JTabbedPane();
        JTabbedPane tabx = new JTabbedPane();

        UserInterface userInterface = api.userInterface();

        HttpRequestEditor requestViewer = userInterface.createHttpRequestEditor(READ_ONLY);
        HttpResponseEditor responseViewer = userInterface.createHttpResponseEditor(READ_ONLY);

        tabs.addTab("Request", requestViewer.uiComponent());
        tabx.addTab("Response", responseViewer.uiComponent());

        downSplitPane.add(tabs, "left");
        downSplitPane.add(tabx, "right");

        splitPane.setRightComponent(downSplitPane);

        JTable table = new JTable(tableModel) {
            @Override
            public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
                HttpRequestResponse responseReceived = tableModel.get(rowIndex);
                requestViewer.setRequest(responseReceived.request());
                responseViewer.setResponse(responseReceived.response());
                super.changeSelection(rowIndex, columnIndex, toggle, extend);
            }
        };

        JSplitPane upSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        upSplitPane.setEnabled(false);
        upSplitPane.add(config, "left");

        JScrollPane scrollPane = new JScrollPane(table);
        upSplitPane.add(scrollPane, "right");
        splitPane.setLeftComponent(upSplitPane);

        return splitPane;
    }
}
