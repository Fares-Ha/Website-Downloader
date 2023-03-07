package models;


import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Model extends URL_operations implements Serializable {
    HashSet<String> all_base=new HashSet<String>();
    ArrayList<Downloadwebsite> obs_list=new ArrayList<Downloadwebsite>();

    public HashSet<String> getAll_base() {
        return all_base;
    }

    public void setAll_base(HashSet<String> all_base) {
        this.all_base = all_base;
    }

    public ArrayList<Downloadwebsite> getObs_list() {
        return obs_list;
    }

    public void setObs_list(ArrayList<Downloadwebsite> obs_list) {
        this.obs_list = obs_list;
    }

    public Downloadwebsite getURL(Downloadwebsite D) throws IOException {
        System.out.println(getpageName(D.BaseURL));
        System.out.println(getDomainName(D.BaseURL));
        if(D.single_page==false) {
            if (all_base.add(getDomainName(D.BaseURL))) {
                obs_list.add(D);
                if (!D.scheduler.getIs_scheduled())
                    D.thread.start();
                else
                    D.start_scheduling();
                return D;
            } else{
                System.out.println("exist");
                return null;
            }


        }
        else if(D.single_page==true) {
            if (all_base.add(getpageName(D.BaseURL))) {
                obs_list.add(D);
                if (!D.scheduler.getIs_scheduled())
                    D.thread.start();
                else
                    D.start_scheduling();
                return D;
            } else{
                System.out.println("exist");
                return null;
            }


        }

        return null;
    }
    public void save(){

        try {
            File f=new File("save.ser");
            f.delete();
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(this);

            oos.flush();
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Model load(){
        try {
            File f=new File("save.ser");
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Model model=(Model) ois.readObject();
            ois.close();
            fis.close();
            return model;
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
