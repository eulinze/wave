package com.ylz.waveform.tools;

import android.util.Log;
import android.util.Xml;


import com.ylz.waveform.bean.ADInfo;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XMLSerivce {

    // 返回信息集合
    public static List<ADInfo> getNewsInfo(InputStream is) throws Exception {
        XmlPullParser parser = Xml.newPullParser(); // 获取Pull解析器
        parser.setInput(is, "utf-8");
        List<ADInfo> list = null;
        ADInfo bean = null;

        // 得到当前事件的类型
        int type = parser.getEventType();

        while (type != XmlPullParser.END_DOCUMENT) {

            switch (type) {
                // XML文档的开始START_DOCUMENT 例如：<?xml version="1.0" encoding="UTF-8"?> 0
                case XmlPullParser.START_DOCUMENT:
                    list = new ArrayList<>();
                    break;
                // XML文档节点开始START_TAG 例如：<entry> 2
                case XmlPullParser.START_TAG:
                    bean = new ADInfo();
                    if ("entry".equals(parser.getName())) {
                        Log.e("XML", "<ebtry>");
                    } else if ("id".equals(parser.getName())) {
                        String path = parser.nextText();
                        Log.e("XML", "path == " + path + "parse == " + parser.getName());
                    }
                    break;
                // XML文档的结束节点 如</entry> 3
                case XmlPullParser.END_TAG:

                    if ("entry".equals(parser.getName())) {
                        Log.e("XML", "解析xml一个节点完成" + parser.getName());

                        // 处理完一个entry标签
                        list.add(bean);
                        bean = null;
                    }
                    break;
            }
            type = parser.next(); // 解析下一个节点
        }
        return list;
    }

}
