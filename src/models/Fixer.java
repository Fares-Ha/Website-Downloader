package models;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashSet;

public class Fixer extends URL_operations implements Serializable {
    public static void fix_locations(Downloadwebsite d)  {
        System.out.println("done ... start converting");
        int i=0;
        int lastelement=d.converted_links;

        for(String link:d.links) {
            try {

            if (i >= lastelement) {
                Document newDoc = Jsoup.connect(URLDecoder.decode(link,"UTF8") ).ignoreContentType(true)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                        .referrer("http://www.google.com")
                        .ignoreHttpErrors(true)
                        .get();
                File downloadedPage = new File(d.dirs+"/html/"+getFileName(link)+".html");
                if(downloadedPage.exists()) {
                    System.out.println("im in ..");
                    //    Document newDoc = Jsoup.parse(downloadedPage, "UTF-8");

                    Elements newLinks = newDoc.select("link[href]");
                    for (Element newLink : newLinks) {
                        if (newLink.attr("rel").equals("stylesheet")) {
                            if(d.cssHashSet.contains(newLink.attr("abs:href")))
                                newLink.attr("href",
                                        "../css/" + getFileName(newLink.attr("abs:href")));
                        }

                        Elements newSrcs = newDoc.select("script[src]");
                        for (Element newSrc : newSrcs) {
                            if(d.jsHashSet.contains(newSrc.attr("abs:src")))
                                newSrc.attr("src",
                                        "../js/" + getFileName(newSrc.attr("abs:src")));

                        }
                        Elements newimgs = newDoc.select("img[src]");
                        for (Element newimg : newimgs) {
                            if(d.imgHashSet.contains(newimg.attr("abs:src")))

                                newimg.attr("src",
                                        "../media/" + getFileName(newimg.attr("abs:src")));

                        }
                        Elements newAs = newDoc.select("a[href]");
                        for (Element newA : newAs) {
                            if(d.links.contains(newA.attr("abs:href"))){
                                newA.attr("href",
                                        "../html/" + getFileName(newA.attr("abs:href"))+".html");
                            }

                        }
                        File downloadedPage1 = new File(d.dirs+"/html/"+getFileName(link)+".html");
                        BufferedWriter writer1 =
                                new BufferedWriter(new FileWriter(d.dirs+"/html/"+getFileName(link)+".html"));
                        writer1.write(newDoc.html());
                        writer1.close();

                    }
                    d.setConverted_links(d.getConverted_links()+1);
                }
                else System.out.println("does not exist");
            }

            else i++;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}
