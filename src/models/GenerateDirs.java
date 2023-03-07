package models;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;


public class GenerateDirs implements Serializable {
   String path;
   String dirName;
   String mainDir;

    public GenerateDirs(String path, String dirName) throws IOException {
        this.path = path;
        this.dirName=dirName;
        this.mainDir=this.path+this.dirName;


        File dir =new File(mainDir);
        if(dir.exists()) {
            System.out.println("already exist");
        }
        else{
            dir.mkdir();
            new File(mainDir+"\\css").mkdir();
            new File(mainDir+"\\js").mkdir();
            new File(mainDir+"\\media").mkdir();
            new File(mainDir+"\\html").mkdir();
            new File(mainDir+"\\temp").mkdir();
        }
    }
    String getMainDir(){
        return mainDir;
    }

}
