package models;


import javafx.application.Platform;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import java.io.*;
import java.net.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.time.format.DateTimeFormatter.ofPattern;


public class Downloadwebsite extends URL_operations implements Runnable,Serializable {

    int linksCount=0;


      Collecter collecter= new Collecter();
      Fixer fixer=new Fixer();
      Scheduler scheduler;
      LocalDateTime date;
      double size=1;
      boolean is_paused=false;
      Download_Thread thread;
      boolean single_page=false;

    public Download_Thread getThread() {
        return thread;
    }

    public void setThread(Download_Thread thread) {
        this.thread = thread;
    }

    public double getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(double downloaded) {
        this.downloaded = downloaded;
    }

    double downloaded=0;
      Float down_per;
      Integer speed_limit ;


    public Float getDown_per() {
        return (float) ((downloaded/size)*100);
    }

    public void setDown_per(Float down_per) {
        this.down_per = down_per;
    }

    String status;
      String mainDir;
      GenerateDirs gDirs;
      String dirs;

    public int getConverted_links() {
        return converted_links;
    }

    public void setConverted_links(int converted_links) {
        this.converted_links = converted_links;
    }

    String BaseURL ;
      String DomainName="";
      int converted_links=0;

      HashSet<String> links=new HashSet<String>();
      HashSet<String> cssHashSet=new HashSet<String>();
      HashSet<String> jsHashSet=new HashSet<String>();
      HashSet<String> imgHashSet=new HashSet<String>();


    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Downloadwebsite(String url,String dir,Scheduler scheduler,boolean single_page) throws IOException {
          status="non";
          BaseURL =url;
          mainDir=dir;
          DomainName=getDomainName(url);
          this.single_page=single_page;
          thread=new Download_Thread(this);
          speed_limit=new Integer(Integer.MAX_VALUE);
          if(!single_page)
            gDirs= new GenerateDirs(mainDir, DomainName);
          else
              gDirs= new GenerateDirs(mainDir, getpageName(BaseURL));
          dirs=gDirs.getMainDir();
          date=LocalDateTime.now();
          DateTimeFormatter f=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
          date.format(f);
          this.scheduler=scheduler;

      }

    public boolean getIs_paused() {
        return is_paused;
    }

    public void setIs_paused(boolean is_paused) {
        this.is_paused = is_paused;
    }

    public void startDownload() throws IOException, InterruptedException {
         status="collecting html";
         collecter.getPageLinks(BaseURL,this);
         System.out.println("done grapping");
         for(String link:links) {
             System.out.println(link);
         }
        status="collecting files";
         for(String link:links) {
            collecter.getPagefiles(link,this);
            linksCount++;
         }
         System.out.println("done grapping");
         System.out.println("Size="+size);
         //Downloading html links
         download_html_pages(0);
         download_css_files(0);
         download_js_files(0);
         download_images(0);
         status="converting";
         fixer.fix_locations(this);
         status="completed";
         System.out.println("finished");

     }

    public void download_text_file(String url, String folder){

        try {

            long before=System.currentTimeMillis();

            Document doc = Jsoup.connect(URLDecoder.decode(url,"UTF8") ).ignoreContentType(true)
                   .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                   .referrer("http://www.google.com")
                   .ignoreHttpErrors(true)
                   .get();
            String page =doc.text();

            BufferedWriter writer =
                    new BufferedWriter(new FileWriter(dirs+"/"+folder+"/"+getFileName(url)));
            writer.write(page);
            writer.close();
            downloaded+=page.getBytes().length;
            long time=System.currentTimeMillis()-before;
            if(speed_limit!=null&&(page.getBytes().length/speed_limit.intValue())*1000>time){
                Thread.sleep(((page.getBytes().length/speed_limit.intValue())*1000 - time));
            }


        } catch (HttpStatusException e){
            System.err.println("err caused by"+ url );
        }catch (IOException e) {
            System.err.println("err caused by"+ url );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void download_binary_file(String url, String folder)  {
        try {
        URL u = new URL(url);
        InputStream in = null;

            in = new BufferedInputStream(u.openStream());

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] buf = new byte[1024];
        int n = 0;
        int nps=0;
        long file_size = 0;
        long before=System.currentTimeMillis();
        while (-1!=(n=in.read(buf)))
        {
            out.write(buf, 0, n);
            file_size+=n;
            nps+=n;
            long time=System.currentTimeMillis()-before;
            if(speed_limit!=null&&nps>speed_limit.intValue()&&((nps/speed_limit.intValue())*1000)>time){
                Thread.sleep((((nps/speed_limit.intValue())*1000)-time));
                before=System.currentTimeMillis();
                nps=0;
            }

        }

        byte[] response = out.toByteArray();
        //And you may then want to save the image so do:

        FileOutputStream fos = new FileOutputStream(dirs+"/"+folder+"/"+getFileName(url));
        fos.write(response);
        fos.close();
        out.close();
        in.close();
        downloaded+=file_size;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
        e.printStackTrace();
    }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean getSingle_page() {
        return single_page;
    }

    public void setSingle_page(boolean single_page) {
        this.single_page = single_page;
    }

    public void run()  {
        try {
            if(!single_page)
                startDownload();
            else {
                status="collecting files";
                links.add(BaseURL);
                Document document = Jsoup.connect(URLDecoder.decode(BaseURL,"UTF8"))
                        .ignoreContentType(true)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                        .referrer("http://www.google.com")
                        .ignoreHttpErrors(true)
                        .get();
                size+=document.html().getBytes().length;

                this.resume();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void resume() throws IOException, InterruptedException {

        thread=new Download_Thread(new Runnable() {
            @Override
            public void run() {
                if(status.equals("non")&&!Downloadwebsite.this.single_page){
                    try {
                        Downloadwebsite.this.startDownload();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (status.equals("collecting html")&&!Downloadwebsite.this.single_page){
                    String lastElement="";
                    for(String link:links){
                        lastElement=link;
                    }
                    collecter.getPageLinks(lastElement, Downloadwebsite.this);
                    status="collecting files";
                    for(String link:links) {
                        collecter.getPagefiles(link, Downloadwebsite.this);
                        linksCount++;
                    }
                    System.out.println("done grapping");
                    System.out.println("Size="+size);
                    //Downloading html links

                    try {
                        download_html_pages(0);
                        download_css_files(0);
                        download_js_files(0);
                        download_images(0);
                        status="converting";
                        fixer.fix_locations(Downloadwebsite.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    status="completed";
                    System.out.println("finished");
                }
                else if (status.equals("collecting files")){
                    int i=0;
                    int lastelement=linksCount;
                    for(String link:links){

                        if(i>=lastelement){
                            collecter.getPagefiles(link, Downloadwebsite.this);
                            linksCount++;
                        }
                        else i++;
                    }
                    System.out.println("done grapping");
                    System.out.println("Size="+size);
                    //Downloading html links
                    try {
                        download_html_pages(0);
                        download_css_files(0);
                        download_js_files(0);
                        download_images(0);
                        status="converting";
                        fixer.fix_locations(Downloadwebsite.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    status="completed";
                    System.out.println("finished");
                }
                else if (status.equals("downloading html")){
                    try {
                        download_html_pages(linksCount);
                        download_css_files(0);
                        download_js_files(0);
                        download_images(0);
                        status="converting";
                        fixer.fix_locations(Downloadwebsite.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    status="completed";
                    System.out.println("finished");
                }
                else if (status.equals("downloading css")) {
                    try {
                        download_css_files(linksCount);
                        download_js_files(0);
                        download_images(0);
                        status = "converting";
                        fixer.fix_locations(Downloadwebsite.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }



                    status="completed";
                    System.out.println("finished");
                }
                else if (status.equals("downloading js")){
                    try {
                        download_js_files(linksCount);
                        download_images(0);
                        status="converting";
                        fixer.fix_locations(Downloadwebsite.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    status="completed";
                    System.out.println("finished");
                }
                else if (status.equals("downloading images")){
                    try {
                        download_images(linksCount);
                        status="converting";
                        fixer.fix_locations(Downloadwebsite.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    status="completed";
                    System.out.println("finished");
                }
                else if (status.equals("converting")){

                    fixer.fix_locations(Downloadwebsite.this);
                    status="completed";
                    System.out.println("finished");
                }
            }
        });
        thread.start();


    }

    public Collecter getCollecter() {
        return collecter;
    }

    public void setCollecter(Collecter collecter) {
        this.collecter = collecter;
    }

    public Integer getSpeed_limit() {
        return speed_limit;
    }

    public void setSpeed_limit(Integer speed_limit) {
        this.speed_limit = speed_limit;
    }

    public Fixer getFixer() {
        return fixer;
    }

    public void setFixer(Fixer fixer) {
        this.fixer = fixer;
    }

    public String getMainDir() {
        return mainDir;
    }

    public void setMainDir(String mainDir) {
        this.mainDir = mainDir;
    }

    public GenerateDirs getgDirs() {
        return gDirs;
    }

    public void setgDirs(GenerateDirs gDirs) {
        this.gDirs = gDirs;
    }

    public String getDirs() {
        return dirs;
    }

    public void setDirs(String dirs) {
        this.dirs = dirs;
    }

    public String getDomainName() {
        return DomainName;
    }

    public void setDomainName(String domainName) {
        DomainName = domainName;
    }

    public HashSet<String> getLinks() {
        return links;
    }

    public void setLinks(HashSet<String> links) {
        this.links = links;
    }

    public HashSet<String> getCssHashSet() {
        return cssHashSet;
    }

    public void setCssHashSet(HashSet<String> cssHashSet) {
        this.cssHashSet = cssHashSet;
    }

    public HashSet<String> getJsHashSet() {
        return jsHashSet;
    }

    public void setJsHashSet(HashSet<String> jsHashSet) {
        this.jsHashSet = jsHashSet;
    }

    public HashSet<String> getImgHashSet() {
        return imgHashSet;
    }

    public void setImgHashSet(HashSet<String> imgHashSet) {
        this.imgHashSet = imgHashSet;
    }

    public String getBaseURL() {
        return BaseURL;
    }

    public void setBaseURL(String baseURL) {
        BaseURL = baseURL;
    }
    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }


    public void pause() throws InterruptedException {

    }
    public void download_html_pages(int lastelement) throws IOException {
        linksCount=lastelement;
        int i=0;
        status="downloading html";

        for(String link:links){
            if(i>=lastelement){
                try {

                    long before = System.currentTimeMillis();
                    Document doc = Jsoup.connect(URLDecoder.decode(link,"UTF8") ).ignoreContentType(true)
                            .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                            .referrer("http://www.google.com")
                            .ignoreHttpErrors(true)
                            .get();

                    String page =doc.html();
                    BufferedWriter writer =
                            new BufferedWriter(new FileWriter(dirs+"/html/"+getFileName(link)+".html"));
                    writer.write(page);
                    writer.close();
                    System.out.println(downloaded/size+"%");
                    downloaded+=page.getBytes().length;
                    linksCount++;

                    long time=System.currentTimeMillis()-before;
                    if(speed_limit!=null&&(page.getBytes().length/speed_limit.intValue())>time){
                        Thread.sleep(((page.getBytes().length/speed_limit.intValue())*1000 - time));
                    }


                } catch (HttpStatusException e){
                    System.out.println("err "+link+" "+e.getMessage());
                }catch (IOException e) {
                    System.err.println("err " + link +" "+e.getMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else
                i++;

        }
    }
    public void download_css_files(int lastelement) throws IOException {
        int i=0;
        linksCount=lastelement;
        status="downloading css";

        for(String cssHash: cssHashSet)
        {
            if(i>=lastelement){
                download_text_file(cssHash,"css");
                System.out.println(downloaded/size+"%");
                System.out.println(cssHash);
                linksCount++;
            }
            else i++;

        }



    }
    public void download_js_files(int lastelement) throws IOException {
        linksCount=lastelement;
        int i=0;

        status="downloading js";
        for(String jsHash: jsHashSet)
        {
            if (i>=lastelement){
                download_text_file(jsHash, "js");
                System.out.println(downloaded/size+"%");
                System.out.println(jsHash);
                linksCount++;
            }
            else i++;

        }

    }
    public void download_images(int lastelement) throws IOException, InterruptedException {
        linksCount=lastelement;
        int i=0;
        status="downloading images";

        for(String imgHash: imgHashSet) {
            if (i>=lastelement){
                    download_binary_file(imgHash, "media");
                System.out.println(downloaded/size+"%");
                System.out.println(imgHash);
                linksCount++;
            }
            else  i++;
            }

    }
    public void start_scheduling(){
         Timer timer = new Timer();
        timer.schedule(new TimerTask() { @Override public void run() {
            Platform.runLater(new Runnable() { @Override public void run() {
                if(LocalTime.now().compareTo(scheduler.getStartTime())>0&&LocalTime.now().compareTo(scheduler.getEndTime())<0){
                    Downloadwebsite.this.setIs_paused(false);
                    try {
                        Downloadwebsite.this.resume();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Downloadwebsite.this.end_scheduling();
                    timer.cancel();
                }

            }});
        }}, 0,1000 );

    }
    public void end_scheduling(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() { @Override public void run() {
            Platform.runLater(new Runnable() { @Override public void run() {
                if(LocalTime.now().compareTo(scheduler.getStartTime())<0||LocalTime.now().compareTo(scheduler.getEndTime())>0){
                    Downloadwebsite.this.setIs_paused(true);
                    Downloadwebsite.this.getThread().stop();
                    if(!Downloadwebsite.this.status.equals("completed"))
                        Downloadwebsite.this.start_scheduling();
                    timer.cancel();
                }

            }});
        }}, 0,1000 );

    }


}

