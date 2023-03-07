package models;

import java.io.Serializable;

public class Download_Thread extends Thread implements Serializable {
    Download_Thread(Runnable R){
        super(R);
    }

}
