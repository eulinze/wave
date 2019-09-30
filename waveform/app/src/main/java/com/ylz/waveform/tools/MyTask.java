package com.ylz.waveform.tools;

import android.os.AsyncTask;
import android.util.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by eulinze on 2019/4/12.
 */

public class MyTask  {
    private String cuurentTag = "";

    public Object getEntity() {
        try {
            URL url = new URL("http://192.168.2.116:8080/icarweb/html/main/app_activity/test.xml");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //设置请求方式
            connection.setRequestMethod("GET");
            //设置连接超时的时间（优化）
            connection.setConnectTimeout(5000);
            //结果码（状态）//成功200   失败 未修改304
            //获取结果码
            int code = connection.getResponseCode();
            if (code == 200) {
                //获取服务器返回来额结果
                InputStream is = connection.getInputStream();
                //解析XMLSax解析=====================================
                //(边读边解析)
                SAXParserFactory saxParseFactory = SAXParserFactory.newInstance();
                SAXParser saxParse = saxParseFactory.newSAXParser();
                saxParse.parse(is, new DefaultHandler() {
                    @Override
                    public void startDocument() throws SAXException {
                        super.startDocument();
                        Log.e("test", "开始文档");
                    }

                    @Override
                    public void endDocument() throws SAXException {
                        super.endDocument();
                        Log.e("test", "结束文档");
                    }

                    @Override
                    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                        super.startElement(uri, localName, qName, attributes);
                        cuurentTag = localName;
                        //获取开始标签的名字
                        if ("person".equals(localName)) {
                            //取属性的值
                            String id = attributes.getValue(0);
                            Log.e("test", id);
                        }
                    }

                    @Override
                    public void endElement(String uri, String localName, String qName) throws SAXException {
                        super.endElement(uri, localName, qName);
                        cuurentTag = null;
                    }

                    @Override
                    public void characters(char[] ch, int start, int length) throws SAXException {
                        super.characters(ch, start, length);
                        if ("name".equals(cuurentTag)) {
                            //获取<name>的值
                            String name = new String(ch, start, length);
                            Log.e("test", "   " + name);
                        } else if ("age".equals(cuurentTag)) {
                            //获取<name>的值
                            String age = new String(ch, start, length);
                            Log.e("test", "   " + age);
                        }
                    }
                });
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return null;
    }
}

