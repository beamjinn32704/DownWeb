
import com.formdev.flatlaf.FlatDarkLaf;
import java.net.MalformedURLException;
import java.net.URISyntaxException;


/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

/**
 *
 * @author beamj
 */
public class Main {
    
    private static final String[] javascriptVoids = new String[]{"javascript:void(0)", "javascript:void(0);", "javascript:", 
    "javascript:;"};
    
    public static void main(String[] args) throws MalformedURLException, URISyntaxException{
        
//        URL u = new URL("http://www.ss64.com/convert/access.html");
//        System.out.println(new Ref(u).getHost());
//        Ref u = new Ref(new Ref("http://ss64.com/convert/access.html"), "hello.html");
//        System.out.println(u.getHost());
//        System.out.println(u.getPath());
//        System.out.println(u.getPathAndQuery());
//        System.out.println(u.getProtocol());
//        
//        
//        System.out.println(new Ref("https://bing.com/about").getUrl().toURI);
//            MainFrame.serverContainerHeadFile = new File("D:\\Servers\\ss64.com");
//        ServerTree mock = new ServerTree(new Ref("ss64.com"), new MultiModel());
//        ServerPartData d = mock.makeData(new File("D:\\Servers\\ss64.com\\web.archive.org"
//                + "\\web\\20190609084206\\http]\\~\\www.unixprogram.com\\bc.pdf"));
//        System.out.println(d.getFile());
//        System.out.println(d.getRef());
//        System.out.println("http://web.archive.org/web/20190609084206/http://www.unixprogram.com/bc.pdf");
//
//        System.out.println(new Ref("https://bing.com/about.html?doesthiswork=true").getHostRef());
//        System.out.println(FileExtensionAnalyzer.isHtmlFile(new File("D:\\Servers\\ss64.com\\pement.org\\awk\\awk1line.txt2")));
//        System.out.println(Arrays.toString(AudioSystem.get));   
//        System.out.println(FileType.JPG.getFileExtension(1));
//        if(true){
//            return;
//        }
        
        if(CalendarHelper.dayOfTheWeek() == CalendarHelper.SUNDAY){
            return;
        }
        FlatDarkLaf.install();
//        FlatLightLaf.install();
        Util.startupShortcut("WD", "WD.exe");

        new TRY().setVisible(true);
//        new MainFrame().setVisible(true);
    }
}