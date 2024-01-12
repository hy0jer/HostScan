package example.customscanchecks.UI;

import burp.api.montoya.http.message.HttpRequestResponse;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;


public class TableTemplate extends AbstractTableModel {
    private final List<HttpRequestResponse> log;

    public TableTemplate() {
        this.log = new ArrayList<>();
    }

    @Override
    public synchronized int getRowCount() {
        return log.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        return switch (column) {
            case 0 -> "Status";
            case 1 -> "URL";
            default -> "";
        };
    }

    @Override
    public synchronized Object getValueAt(int rowIndex, int columnIndex) {
        HttpRequestResponse responseReceived = log.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> responseReceived.response().statusCode();
            case 1 -> responseReceived.request().url();
            default -> "";
        };
    }

    public synchronized void add(HttpRequestResponse responseReceived) {
        int index = log.size();
        log.add(responseReceived);
        fireTableRowsInserted(index, index);
    }

    public synchronized HttpRequestResponse get(int rowIndex) {
        return log.get(rowIndex);
    }
}
