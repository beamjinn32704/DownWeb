
import java.io.File;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author beamj
 * @param <T>
 */
public interface Cacheable<T> {
    String cache();
    
    public static <T extends Cacheable> void cache(ArrayList<T> list, File to){
        String print = "";
        boolean first = true;
        boolean go = true;
        while(go){
            try {
                if(list.isEmpty()){
                    
                } else {
                    for(T t : list){
                        if(!first){
                            print += " ";
                        } else {
                            first = false;
                        }
                        print += t.cache();
                    }
                }
                go = false;
            } catch (ConcurrentModificationException e){
                e.printStackTrace();
                System.out.println("Going again...");
                go = true;
            }
        }
        cache(print, to);
    }
    
    public static <T extends Cacheable> void cache(T t, File to){
        cache(t.cache(), to);
    }
    
    public static void cache(String print, File to){
        if(!Util.writeToFile(to, print)){
            System.out.println("UNABLE TO CACHE!");
        }
    }
    
    public static <T extends Cacheable> ArrayList<T> convert(CacheScanner<T> scanner, Object... params){
        ArrayList<T> converted = new ArrayList<>();
        scanner.setParams(params);
        boolean go = true;
        int count = 1;
        while(go){
            T next = scanner.next();
            if(next == null){
                return converted;
            } else {
                count++;
                converted.add(next);
                System.out.println(count);
            }
        }
        return converted;
    }
    
    public static <T extends Cacheable> T convertFirst(CacheScanner<T> scanner, Object... params){
        scanner.setParams(params);
        T next = scanner.next();
        return next;
    }
    
}
