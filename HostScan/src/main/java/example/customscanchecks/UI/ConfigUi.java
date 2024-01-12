package example.customscanchecks.UI;

import javax.swing.*;


public class ConfigUi extends JToolBar {
    public JCheckBox autoSendRequestCheckBox;

    public ConfigUi() {
        this.autoSendRequestCheckBox = new JCheckBox("Auto sending");
        // 默认发送
        this.autoSendRequestCheckBox.setSelected(true);
        // 不可悬浮
        this.setFloatable(false);
        this.add(autoSendRequestCheckBox);
    }

    public Boolean getAutoSendRequest() {
        return this.autoSendRequestCheckBox.isSelected();
    }
}