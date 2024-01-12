package example.customscanchecks;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import example.customscanchecks.UI.TableTemplate;

import java.util.List;
import java.util.Objects;


public class TestModel {
    public MontoyaApi api;
    public HttpRequestResponse baseRequestResponse;

    public TestModel(MontoyaApi api, HttpRequestResponse baseRequestResponse) {
        this.api = api;
        this.baseRequestResponse = baseRequestResponse;
    }

    public HttpRequestResponse test_engine(MontoyaApi api, HttpRequestResponse baseRequestResponse, TableTemplate tableModel) {
        HttpRequest send_package = baseRequestResponse.request();
        Logging logging = api.logging();
        logging.logToOutput("Scanning " + send_package.url());
        HttpRequestResponse later_package = this.api.http().sendRequest(send_package);
        String path = later_package.request().path();
        int statusCode = later_package.response().statusCode();
        if (statusCode < 300 && statusCode >= 200) {
            if (Utils.count_string(path, '/') != 0) {
                StringBuilder sb = new StringBuilder(path);
                sb.delete(Utils.count_string(path, '/'), sb.length());
                String test_path = sb.toString();
                return poc_sender(api, baseRequestResponse, test_path, tableModel);
            }
        } else if (statusCode < 400 && statusCode >= 300) {
            return poc_sender(api, baseRequestResponse, path, tableModel);
        }
        return null;
    }

    public HttpRequestResponse poc_sender(MontoyaApi api, HttpRequestResponse baseRequestResponse, String path, TableTemplate tableModel) {
        HttpRequest request_package = baseRequestResponse.request().withUpdatedHeader("host", "test.com.cn").withPath(path);
        HttpRequestResponse later_package = this.api.http().sendRequest(request_package);
        //该分支判断回包是否为空
        if (later_package.response() == null) {
            return null;
        }
        if (later_package.response().statusCode() / 100 == 3) {
            List<HttpHeader> headers = later_package.response().headers();
            for (HttpHeader item : headers) {
                if (Objects.equals(item.name(), "Location") && item.value().contains("test")) {
                    tableModel.add(later_package);
                    return later_package;
                }
            }
        }
        return null;
    }
}

