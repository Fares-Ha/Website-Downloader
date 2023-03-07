package models;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.Serializable;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class URL_operations implements Serializable {

    public static boolean validURL(String url){
        String [] type= {"?","#",".png",".svg",".jpg",".tiff",".jpeg",".gif",".pdf",".zip"};
        for(String s:type)
        {
            if (url.contains(s))
                return false;
        }
        if(url.isEmpty())
            return false;
        return true;
    }

    public static String getDomainName(String url)  {
        String domain="";
        try {
            URI uri = new URI(url);
            domain = uri.getHost();


        } catch (URISyntaxException e) {
            System.err.println(url);
        }catch (NullPointerException e){
            System.err.println("");
        }
        return domain;
        //.startsWith("www.") ? domain.substring(4) : domain;

    }
    public static String getpageName(String url)  {
        String page="";
        try {
            URI uri = new URI(url);
            page =uri.getHost()+uri.getPath();


        } catch (URISyntaxException e) {
            System.err.println(url);
        }catch (NullPointerException e){
            System.err.println("");
        }
        page=page.replace('/','-');
        return page;
        //.startsWith("www.") ? domain.substring(4) : domain;

    }
    public static String getFileName(String url) throws IOException {
//        String name=url;
        int index=0;
//        if (name.contains("?"))
//        { index = name.indexOf("?");
//
//            name= name.substring(0,index);
//
//        }
//        name = name.substring(name.indexOf("/")+1);
//        name = name.substring(name.indexOf("/")+1);
//        name = name.substring(name.indexOf("/") + 1);
//        if(name.endsWith("/")){
//           name= name.substring(0,name.length()-1);
//        }
//        name= name.replace('/','_');
//        return name;

        if (url.contains("?"))
        { index = url.indexOf("?");
            url= url.substring(0,index);

        }
        url=url.replace(':','_');
        url=url.replace('/','_');
        Path path = Paths.get(url);
        return path.toString();
    }
    public long gettxtsize(String URL){
        Document doc = null;
        try {
            doc = Jsoup.connect(URLDecoder.decode(URL,"UTF8"))
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com")
                    .ignoreHttpErrors(true)
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc.text().getBytes().length;
    }

    public long getbinarysize(String URL)  {
        try {
        java.net.URL url = null;
        try {
            url = new URL(URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection conn;


            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            long l=conn.getContentLengthLong();
            if(l==-1)
                return 0;
            else
                return conn.getContentLengthLong();
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Connection failed");
        }
        return 0;
    }
}
