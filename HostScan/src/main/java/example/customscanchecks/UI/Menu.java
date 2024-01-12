package example.customscanchecks.UI;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import example.customscanchecks.TestModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class Menu implements ContextMenuItemsProvider {
    public final MontoyaApi api;
    public TableTemplate tableModel;

    public Menu(MontoyaApi api, TableTemplate tableModel) {
        this.api = api;
        this.tableModel = tableModel;

    }

    public List<Component> provideMenuItems(ContextMenuEvent event) {
        ArrayList<Component> menuItemList = new ArrayList<>();
        JMenuItem menuItem = new JMenuItem("Do Host attack scan");
        menuItem.addActionListener(new ContextMenuActionListener(event, api, tableModel));
        menuItemList.add(menuItem);
        return menuItemList;
    }

    static class ContextMenuActionListener implements ActionListener {
        ContextMenuEvent invocation;
        public MontoyaApi api;
        public TableTemplate tableModel;

        public ContextMenuActionListener(ContextMenuEvent event, MontoyaApi api, TableTemplate tableModel) {
            this.invocation = event;
            this.api = api;
            this.tableModel = tableModel;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            CompletableFuture.supplyAsync(() -> {
                List<HttpRequestResponse> httpRequestResponses = invocation.selectedRequestResponses();
                for (HttpRequestResponse baseRequestResponse : httpRequestResponses) {
                    TestModel model = new TestModel(this.api, baseRequestResponse);
                    HttpRequestResponse result_package = model.test_engine(this.api, baseRequestResponse, this.tableModel);
                }
                return null;
            });
        }
    }
}
