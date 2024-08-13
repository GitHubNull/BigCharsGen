package org.oxff;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static burp.api.montoya.ui.contextmenu.InvocationType.*;

public class BigCharGenContextMenuItemsProvider implements ContextMenuItemsProvider {
    private final MontoyaApi api;

    public BigCharGenContextMenuItemsProvider(MontoyaApi api) {
        this.api = api;
    }

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent contextMenuEvent)
    {
        // 判断当前菜单是否来自代理、重复器、 intruder工具，并且是否选择了请求报文，并且请求报文是否不为空，且可编辑
        if (!contextMenuEvent.isFrom(MESSAGE_EDITOR_REQUEST)){
            return null;
        }


        List<Component> menuItemList = new ArrayList<>();

        JMenuItem bigCharsGen = new JMenuItem("bigCharsGen");
        bigCharsGen.addActionListener(e -> {
            BigCharsGenDialog dialog = new BigCharsGenDialog(api, contextMenuEvent);
            dialog.setVisible(true);
        });

        menuItemList.add(bigCharsGen);

        return menuItemList;
    }
}
