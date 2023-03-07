package models;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashSet;

public class Collecter extends URL_operations implements Serializable {

    public void getPageLinks(String URL, Downloadwebsite wb){

        if (validURL(URL)) {
            try {

                if (wb.getLinks().add(URL)) {
                    System.out.println(URL);
                }

                Document document = Jsoup.connect(URLDecoder.decode(URL,"UTF8"))
                        .ignoreContentType(true)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                        .referrer("http://www.google.com")
                        .ignoreHttpErrors(true)
                        .get();
                wb.setSize(wb.getSize()+document.html().getBytes().length);

                Elements linksOnPage = document.select("a[href]");

                for (Element page : linksOnPage) {
                    if (getDomainName(page.attr("abs:href"))!=null&&validURL(page.attr("abs:href"))&& getDomainName(page.attr("abs:href")).equals(wb.getDomainName())&&wb.getLinks().add(page.attr("abs:href"))) {
                        System.out.println(page.attr("abs:href"));
                        getPageLinks(page.attr("abs:href"),wb);
                    }

                }

            } catch (IOException e) {
                System.err.println("For " + URL + " : " + e.getMessage());

            }
            catch (NullPointerException e) {
                System.err.println("For " + URL + " : " + e.getMessage());

            }
        }
        else
        {
            System.out.println("0+ "+URL);
            return;
        }

    }

    public void getPagefiles(String URL,Downloadwebsite wb){

        try {
            Document doc = Jsoup.connect(URLDecoder.decode(URL,"UTF8"))
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com")
                    .ignoreHttpErrors(true)
                    .get();

            //fill css hashSet with links
            Elements cssLinks=doc.select("link[rel=stylesheet]");
            for(Element cssLink:cssLinks){
                //    if(cssLink.attr("abs:href").contains(DomainName))
                if(!wb.getCssHashSet().contains(cssLink.attr("abs:href"))){
                    wb.getCssHashSet().add(cssLink.attr("abs:href"));
                    wb.setSize(wb.getSize()+gettxtsize(cssLink.attr("abs:href")));
                }

            }

            // fill js hashSet with links
            Elements jsLinks=doc.select("script[src]");
            for(Element jsLink:jsLinks){
                //add this page's js links to js hashSet
                //  if(jsLink.attr("abs:src").contains(DomainName))
                if (!wb.getJsHashSet().contains(jsLink.attr("abs:src"))){
                    wb.getJsHashSet().add(jsLink.attr("abs:src"));
                    wb.setSize(wb.getSize()+gettxtsize(jsLink.attr("abs:src")));

                }

            }

            // fill img hashSet with links
            Elements imgLinks=doc.select("img[src*=.png],img[src*=.jpg],img[src*=.svg],img[src*=.jpeg],img[src*=.gif],img[src*=.webp],img[src*=.heic],img[src*=.heif]");
            for(Element imgLink:imgLinks){
                //add this page's img links to img hashSet
                //  if(imgLink.attr("abs:src").contains(DomainName))
                if (!wb.getImgHashSet().contains(imgLink.attr("abs:src"))){
                    wb.getImgHashSet().add(imgLink.attr("abs:src"));
                    wb.setSize(wb.getSize()+getbinarysize(imgLink.attr("abs:src")));
                }


            }
        } catch (IOException e) {
            System.err.println("For " + URL + " : " + e.getMessage());
        }catch (NullPointerException e){
            System.err.println("For " + URL + " : " + e.getMessage());
        }
    }


}
