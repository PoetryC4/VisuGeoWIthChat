package com.C4.ipgeo.util;

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;

public class URLUtils {

    private static final String IPV4_REGEX =
            "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$";

    private static final Pattern IPv4_PATTERN = Pattern.compile(IPV4_REGEX);

    public static boolean isValidIPV4ByCustomRegex(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return false;
        }
        if (!IPv4_PATTERN.matcher(ip).matches()) {
            return false;
        }
        String[] parts = ip.split("\\.");
        try {
            for (String segment : parts) {
                if (Integer.parseInt(segment) > 255 ||
                        (segment.length() > 1 && segment.startsWith("0"))) {
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static String getContent(String url,String method) {
        BufferedReader reader = null;
        String bookHSONString = null;

        HttpURLConnection conn = null;
        URL requestUrl = null;
        try {
            requestUrl = new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        try {
            conn = (HttpURLConnection) requestUrl.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            conn.setRequestMethod(method);
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }
        conn.setConnectTimeout(5000);
        try {
            conn.connect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        InputStream inputstream = null;
        try {
            inputstream = conn.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(inputstream == null) return "E";

        reader = new BufferedReader(new InputStreamReader(inputstream));

        StringBuilder builder = new StringBuilder();

        String line;
        while(true){
            try {
                if (!((line= reader.readLine())!=null)) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            builder.append(line);
            builder.append("\n");
        }
        if(builder.length()==0){
            return null;
        }

        conn.disconnect();
        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return builder.toString();
    }

    public static String RequestWithBody(String protocol, String host, String port, String path, List<String> names, List<Object> values) throws Exception {

        URL url = new URL(protocol+"://"+host+":"+port+"/"+path);
        System.out.println(url);
        // 打开和URL之间的连接
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");//请求post方式
        con.setUseCaches(false); // Post请求不能使用缓存
        con.setDoInput(true);// 设置是否从HttpURLConnection输入，默认值为 true
        con.setDoOutput(true);// 设置是否使用HttpURLConnection进行输出，默认值为 false

        //设置header内的参数 connection.setRequestProperty("健, "值");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("isTree", "true");
        con.setRequestProperty("isLastPage", "true");

        //设置body内的参数，put到JSONObject中
        JSONObject param = new JSONObject();
        for (int i = 0; i < names.size(); i++) {
            param.put(names.get(i), values.get(i));
        }

        System.out.println(param.toString());
        // 建立实际的连接
        con.connect();

        // 得到请求的输出流对象
        OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream(),"UTF-8");
        writer.write(param.toString());
        writer.flush();

        // 获取服务端响应，通过输入流来读取URL的响应
        InputStream is = con.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuffer sbf = new StringBuffer();
        String strRead = null;
        while ((strRead = reader.readLine()) != null) {
            sbf.append(strRead);
            sbf.append("\r\n");
        }
        reader.close();

        // 关闭连接
        con.disconnect();

        return sbf.toString();

    }

    public static Map<String, Object> getIps(String url) {
        List<String> Ips = new ArrayList<>();
        List<String> Hosts = new ArrayList<>();
        Map<String, Object> res = new HashMap<>();

        if(url.contains(":")) {
            int i;
            for (i = url.length()-1; i >= 0; i--) {
                if(url.charAt(i) == ':') {
                    break;
                }
            }
            if(isValidIPV4ByCustomRegex(url.substring(0, i))) {
                Ips.add(url.substring(0, i));
                Hosts.add(url);
            } else {
                InetAddress[] ips = new InetAddress[0];
                try {
                    ips = InetAddress.getAllByName(url.substring(0, i));
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
                for (InetAddress ip : ips) {
                    if(ip.getHostAddress().charAt(2)=='.'&&Integer.parseInt(ip.getHostAddress().substring(0,2))==10) {
                        System.out.println(1);
                        continue;
                    }
                    if(ip.getHostAddress().charAt(2)!='.'&&Integer.parseInt(ip.getHostAddress().substring(0,3))==192 && Integer.parseInt(ip.getHostAddress().substring(4,7)) == 168) {
                        System.out.println(2);
                        continue;
                    }
                    if(ip.getHostAddress().charAt(2)!='.'&&Integer.parseInt(ip.getHostAddress().substring(0,3))==172 && Integer.parseInt(ip.getHostAddress().substring(4,6)) >=16 && Integer.parseInt(ip.getHostAddress().substring(4,6)) <=31 && ip.getHostAddress().charAt(6)=='.') {
                        System.out.println(3);
                        continue;
                    }
                    Ips.add(ip.getHostAddress());
                    Hosts.add(ip.getHostName());
                }
            }
        } else {
            InetAddress[] ips = new InetAddress[0];
            try {
                ips = InetAddress.getAllByName(url);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            for (InetAddress ip : ips) {
                if(ip.getHostAddress().charAt(2)=='.'&&Integer.parseInt(ip.getHostAddress().substring(0,2))==10) {
                    System.out.println(1);
                    continue;
                }
                if(ip.getHostAddress().charAt(2)!='.'&&Integer.parseInt(ip.getHostAddress().substring(0,3))==192 && Integer.parseInt(ip.getHostAddress().substring(4,7)) == 168) {
                    System.out.println(2);
                    continue;
                }
                if(ip.getHostAddress().charAt(2)!='.'&&Integer.parseInt(ip.getHostAddress().substring(0,3))==172 && Integer.parseInt(ip.getHostAddress().substring(4,6)) >=16 && Integer.parseInt(ip.getHostAddress().substring(4,6)) <=31 && ip.getHostAddress().charAt(6)=='.') {
                    System.out.println(3);
                    continue;
                }
                Ips.add(ip.getHostAddress());
                Hosts.add(ip.getHostName());
            }
        }
        res.put("ips",Ips);
        res.put("hosts", Hosts);
        return res;
    }
}
