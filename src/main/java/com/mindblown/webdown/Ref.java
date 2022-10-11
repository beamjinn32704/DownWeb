
import java.net.URL;




/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

/**
 *
 * @author beamj
 */
public class Ref implements Comparable<Ref>{
    private URL url;
    
    private static final String[] commonProtocols = new String[]{"http", "https", "ftp"};
    
    private static final String defaultProtocol = "https";
    
    private static final String[] javascriptVoids = new String[]{"javascript:void(0)", "javascript:void(0);", "javascript:", 
    "javascript:;"};
    
    /**
     * Creates a new Ref object with a URL object.
     * @param url A url that the Ref is based off of.
     */
    public Ref(URL url){
        this.url = url;
        formatUrl();
    }
    
    /**
     * Creates a new Ref object with a url string.
     * @param refString The url string that the Ref object will be made from.
     */
    public Ref(String refString){
        url = formUrl(refString);
        if(url == null){
            url = fixBrokenUrl(refString);
        }
        formatUrl();
    }
    
    /**
     * Creates a new Ref object with a url string and a Ref context. 
     * Like file paths, urls (especially in HTML files) are given relative paths 
     * relative to a context. The url is treated relative to the context. If the context is null, 
     * than the url is treated as an absolute url.
     * @param context The context of which the url should be treated as.
     * @param refString The url string relative to the context.
     */
    public Ref(Ref context, String refString){
        //Form Url
        url = formUrl(context, refString);
        if(url == null){
            //If bad url, fix it
            url = fixBrokenUrl(context, refString);
        }
        formatUrl();
    }
    
    /**
     * Fix any mistakes made in the URL and get rid of www. There are a few features that this function 
     * does. #1 is fixing relative urls. Sometimes a url link 
     * given in an HTML file will have too many ../ 's so the URL will result 
     * in a link with ../ at the front. This function is used to get rid of any extra ../ 's. 
     * #2 is to get rid of any javascript:void that sometimes happens in HTML urls.
     * #3 is to get rid of www if it is in the url. 
     * #4 is to add a slash to the end of the url's path if it isn't referencing a file. 
     * An example would be like changing "bing.com/about" to "bing.com/about/".
     * It would not change "bing.com/about.html" to "bing.com/about.html/". 
     */
    public void formatUrl(){
        if(!isValidUrl()){
            return;
        }
        //Get rid of all ../ 's in the beginning
        //Make a var of the path to act on and then set the path afterwards
        //Make a tempHolder to see if there are any changes. If there are, use setPath.
        //Otherwise, don't. I do this because setPath calls formatUrl, causing an infinite loop.
        String path = url.getPath();
        String tempHolder = Util.strip(path) + "";
        
        if(path.contains("..")){
            System.out.println("..");
        }
        
        path = Util.fullyReplace(path, "../", "");
        
        //Get rid of javascript void's
        //Make another holder. This is so that I can change path to lowercase,
        //and then replace any javascript voids to "". I do this because some javascript voids
        //will have capital letters.
        String origPathHolder = path + "";
        path = path.toLowerCase();
        for(int i = 0; i < javascriptVoids.length; i++){
            int jvIndex;
            int jvLength = javascriptVoids[i].length();
            while((jvIndex = path.indexOf(javascriptVoids[i])) != -1){
                //Keep capital letters from original path and skip over the javascript void
                path = origPathHolder.substring(0, jvIndex) + origPathHolder.substring(jvIndex + jvLength);
                //Udpate origPathHolder so that the indexes as used in the line above aren't messed up.
                origPathHolder = path + "";
                path = path.toLowerCase();
            }
        }
        
        //Set back to update orig path holder that now has no javascript voids
        //and still has capital letters
        path = origPathHolder + "";
        //Set path 
        if(!path.equals(tempHolder)){
            setPath(path);
        }
        
         //Get rid of WWW
        String host = getHost();
        if(host.startsWith("www.")){
            setHost(host.substring(4));
        }
        
        
        //Add slashes to path and host if needed
        if(!Util.isBlank(path)){
            //This gets the last folder or place in the path. If the path is about/who-we-are.html, 
            //then this will return who-we-are.html. If the path is about/, the this will return "".
            //If the path is about, then this returns about.
            String last = path.substring(path.lastIndexOf('/') + 1);
            int period = last.indexOf('.');
            //Basically if there's no period and there isn't already a slash at the end, then add a slash.
            //I look for the slash at the end of the path since last might be blank and no period, but that doesn't
            //mean that I need to add a slash since there's already a slash.
            if(period == -1 && !path.endsWith("/")){
                setPath(path + "/");
            }
        } else {
            //If the path is blank, then set the path to slash because it indicates index.html.
            //Don't put the slash in the host because its illegal in URL to have a slash in the host.
            setPath("/");
        }
    }
    
    /**
     * Returns whether this ref provides a link to an html file. This isn't always guaranteed to be right, 
     * as this function looks only at the url string but doesn't actually connect to anything.
     * @return whether this ref links to a html file.
     */
    public boolean isHtml(){
        
        if(!isValidUrl()){
            return false;
        }
        
        String name = getPath();
        name = Util.strip(name);
        //If path is blank then its a link to the index.html so return false.
        if(Util.isBlank(name)){
            return true;
        }
        int slashInd = name.lastIndexOf("/");
        
        //If there's a slash at the end then it's a html file so return false.
        if(slashInd == name.length() - 1){
            return true;
        }
        
        //If there's no slash then last will equal the whole path, otherwise it'll have everything
        //after the last slash.
        String last = name.substring(slashInd + 1);
        int period = last.indexOf(".");
        
        //If there's no period or last ends with .html or .htm then its html so return false
        if(period == -1 || last.endsWith(".html") || last.endsWith(".htm") || last.endsWith(".mhtm")
                || last.endsWith(".mhtml") || last.endsWith(".shtml") || last.endsWith(".xhtml")){;
            return true;
        }
        return false;
    }
    
    /**
     * Creates a new Ref object using a protocol, a host website, and the name of the file path on the
     *  website. The protocol is (or will be changed to) either HTTP or HTTPS. An exammple of a host website 
     * is bing.com. An example of the file path would be "about". Together, these would contribute toward a URL
     *  like https://bing.com/about .
     * @param protocol The protocol of the URL (either HTTP or HTTPS)
     * @param host The host website
     * @param path The file path on the host website.
     */
    public Ref(String protocol, String host, String path){
        url = formUrl(protocol, host, path);
        if(url == null){
            url = fixBrokenUrl(protocol, host, path);
        }
    }
    
    /**
     * Get the host website. An example of what this would return in a ref that contains 
     * https://bing.com/about.html would be bing.com/
     * @return the host website.
     */
    public String getHost(){
        if(isValidUrl()){
            return url.getHost() + "/";
        } else {
            return "";
        }
    }
    
    
    /**
     * Get the protocol. An example of what this would return in a ref that contains 
     * https://bing.com/about.html would be https://
     * @return the protocol.
     */
    public String getProtocol(){
        if(isValidUrl()){
            return url.getProtocol() + "://";
        } else {
            return defaultProtocol + "://";
        }
    }
    
    /**
     * Get the file path. An example of what this would return in a ref that contains 
     * https://bing.com/about.html?doesthiswork=true would be about.html
     * @return the file path.
     */
    public String getPath(){
        if(isValidUrl()){
            String path = url.getPath() + "";
            if(path.startsWith("/")){
                //Make sure it doesn't start with a slash;
                path = path.substring(1);
            }
            return path;
        } else {
            return "";
        }
    }
    
    /**
     * Get the file path along with any query request headers. An example of what this would return in a ref that contains 
     * https://bing.com/about.html?doesthiswork=true would be about.html?doesthiswork-true
     * @return the path with the query request.
     */
    public String getPathAndQuery(){
        if(isValidUrl()){
            String pathAndQuery = url.getFile() + "";
            //Make sure that path and query doesn't start with slash
            if(pathAndQuery.startsWith("/")){
                pathAndQuery = pathAndQuery.substring(1);
            }
            return pathAndQuery;
        } else {
            return "";
        }
    }
    
    /**
     * Returns the file-format version of the ref. An example of what this would return in a ref that contains 
     * https://bing.com/about.html?doesthiswork=true would be bing.com/about.html
     * @return file-formatted url
     */
    public String toFile(){
        if(!isValidUrl()){
            return "";
        }
        
        String file = url.getHost() + url.getPath();
        //Make sure no double slashes and no back slashes
        return fixSlashes(file);
    }
    
    /**
     * Used by the constructor to fix a broken url. 
     * This is used to match the constructor new Ref(String refString)
     * @param refString The url string
     * @return a URL made by the url string.
     */
    private static URL fixBrokenUrl(String refString){
        //Since new URL(string) is equal to new URL(null, string)
        return fixBrokenUrl(null, refString);
    }
    
    /**
     * Used by the constructor to fix a broken url. 
     * This is used to match the constructor new Ref(Ref context, String refString)
     * @param context The context of the url
     * @param string The url
     * @return a URL made by the url string.
     */
    private static URL fixBrokenUrl(Ref context, String string){
        String refString = string + "";
        URL refStringUrl;
        
        //Try Fixing Slashes
        refString = fixSlashes(refString);
        refStringUrl = formUrl(context, refString);
        
        if(refStringUrl != null){
            return refStringUrl;
        }
        
        //If fixing the protocol doesn't work, it doesn't work.
        refStringUrl = formUrl(context, fixProtocol(refString));
        if(refStringUrl == null){
            System.out.println("Failed to Fix");
        }
        return refStringUrl;
    }
    
    /**
     * Used by the constructor to fix broken urls. 
     * This is used to match the constructor new Ref(String protocol, String host, String path)
     * @param proto The protocol.
     * @param tmpHost The host website.
     * @param tmpPath The path of the host website.
     * @return 
     */
    private static URL fixBrokenUrl(String proto, String tmpHost, String tmpPath){
        String protocol = fixProtocol(fixSlashes(proto));
        String host;
        String path;
        
        //If it's empty or null, than it can't be made into an URL.
        if(tmpHost == null || Util.isBlank(tmpHost)){
            return null;
        } else {
            host = tmpHost + "";
        }
        
        //Make sure name is a valid value
        if(tmpPath == null || Util.isBlank(tmpPath)){
            path = "";
        } else {
            path = tmpPath + "";
        }
        
        path = fixSlashes(path);
        
        if(host.endsWith("/")){
            //Make sure that host doesn't end in slash
            host = host.substring(0, host.length() - 1);
        }
        
        //If path isn't blank, make sure it starts with slash
        if(!path.startsWith("/") && !Util.isBlank(path)){
            path = "/" + path;
        }
        
        //if path is equal to just a slash, it's the same as being blank
        if(path.equals("/")){
            path = "";
        }
        
        //If it works, than it works. If not, than it doesn't.
        URL formedUrl =  formUrl(protocol, host, path);
        if(formedUrl == null){
            System.out.println("Failed to fix url");
        }
        return formedUrl;
    }
    
    /**
     * Creates a URL
     * @param string the url string.
     * @return the created URL.
     */
    private static URL formUrl(String string){
        try{
            URL u = new URL(string);
            return u;
        } catch (Exception e){
            return null;
        }
    }
    
    /**
     * Creates a URL
     * @param context the context of the url.
     * @param string the url.
     * @return the created URL.
     */
    private static URL formUrl(Ref context, String string){
        URL contextUrl = null;
        if(context != null){
            contextUrl = context.url;
        }
        try{
            URL u = new URL(contextUrl, string);
            return u;
        } catch (Exception e){
            return null;
        }
    }
    
    /**
     * Creates a URL
     * @param proto The url's protocol
     * @param host The url's host website.
     * @param path The path on the host website.
     * @return the created URL.
     */
    private static URL formUrl(String proto, String host, String path){
        String protocol = proto + "";
        try{
            URL u = new URL(protocol, host, path);
            return u;
        } catch (Exception e){
            return null;
        }
    }
    
    /**
     * Replaces back slashes and double forward slashes with a single forward slash.
     * @param refString The string that needs to have the back slashes and double forward slashes replaced.
     * @return a string with the back slashes and double forward slashes replaced.
     */
    private static String fixSlashes(String refString){
        if(refString == null){
            return "";
        }
        String workingWith = refString + "";
        //Get rid of all back slashes
        workingWith = Util.fullyReplace(workingWith, "\\", "/");
//        //Get rid of all double slashes and replace with single slash.
//        //Regex is "all double slshes except those that have a ":" behind it. (to prevent :// being removed)
//        workingWith = Util.fullyReplaceRegex(workingWith, "(?<!:)//", "/");
        return workingWith;
    }
    
    /**
     * Fixes a url string so that it has a valid protocol.
     * @param brokenProto The url that needs to be fixed.
     * @return a url with a valid protocol.
     */
    private static String fixProtocol(String brokenProto){
        if(brokenProto == null){
            return defaultProtocol + "://";
        }
        
        //If it's actually a good protocol then just return
        if(brokenProto.startsWith("http://") || brokenProto.startsWith("https://")){
            return brokenProto;
        }
        
        String brokenProtocol = brokenProto + "";
        
        //Find index of ://
        int protoIdentifierIndex = brokenProtocol.indexOf("://");
        
        //If no identifier
        if(protoIdentifierIndex == -1){
            //Go through common protocols and see if brokenProto starts with them plus ":/" (common :// typo)
            //If so, fix it.
            for(int i = 0; i < commonProtocols.length; i++){
                String commonProtocol = commonProtocols[i];
                if(brokenProtocol.startsWith(commonProtocols[i] + ":/")){
                    brokenProtocol = commonProtocol + "://" + brokenProtocol.substring(commonProtocol.length() + 2);
                    //Reset ProtoIdentifierIndex
                    protoIdentifierIndex = commonProtocol.length();
                    i = commonProtocols.length;
                    //If hasn't fixed
                } else if(i == commonProtocols.length - 1){
                    brokenProtocol = "http://" + brokenProtocol;
                    return brokenProtocol;
                }
            }
        }
        //Make these bottom conditionals seperate from the top conditional since there still might
        //be work to do
        if(protoIdentifierIndex == 0){
            //If :// is at the front, then just add the default protocol so ://blabla.com turns into
            //http://blabla.com
            brokenProtocol = defaultProtocol + brokenProtocol;
        } else {
            //Find given protocol
            String protocolFound = brokenProtocol.substring(0, protoIdentifierIndex);
//            //Replace all :// with / so to get rid of extra :// 's.
//            brokenProtocol = Util.fullyReplace(brokenProtocol, "://", "/");
            
            //If protocol not HTTP or HTTPS, set it as HTTP.
            if(!protocolFound.equals("http") && !protocolFound.equals("https")){
                protocolFound = defaultProtocol + "";
            }
            
            //Add protocol and ://.
            brokenProtocol = protocolFound + "://" + brokenProtocol.substring(protoIdentifierIndex);
        }
        return brokenProtocol;
    }
    
    //This is the original part of fixProtocol at the last else statement. Keep it for backups
//    //Find given protocol
//            String protocolFound = brokenProtocol.substring(0, protoIdentifierIndex);
//            //Replace all :// with / so to get rid of extra :// 's.
//            brokenProtocol = Util.fullyReplace(brokenProtocol, "://", "/");
//            
//            //If protocol not HTTP or HTTPS, set it as HTTP.
//            if(!protocolFound.equals("http") && !protocolFound.equals("https")){
//                protocolFound = defaultProtocol + "";
//            }
//            
//            //Add protocol and :/ .
//            brokenProtocol = protocolFound + ":/" + brokenProtocol.substring(protoIdentifierIndex);
    
    /**
     * Used to see if a Ref is a link to an email
     * @param ref The ref to be checked
     * @return whether the Ref is a link to an email.
     */
    public static boolean isMailto(Ref ref){
        if(!ref.isValidUrl()){
            return false;
        }
        //The ref is only mailto if a level (/.../) starts with mailto: or if the host starts with it.
        return ref.getPath().startsWith("mailto:") || ref.getPath().contains("/mailto:")
                || ref.getHost().startsWith("mailto:");
    }
    
    /**
     * Compares Ref o to this object. 
     * They are compared by comparing the toFile() 's of each Ref object.
     * @param o The Ref object to be compared
     * @return the comparison result
     */
    @Override
    public int compareTo(Ref o) {
        //A valid ref is more than an invalid ref
        if(!o.isValidUrl() || !isValidUrl()){
            if(!isValidUrl() && !o.isValidUrl()){
                return 0;
            } else if(!isValidUrl()){
                return -1;
            } else {
                return 1;
            }
        }
        String str1 = toFile() + "";
        String str2 = o.toFile() + "";
        if(str1.equals(str2)){
            return 0;
        }
        
        return str1.compareToIgnoreCase(str2);
    }
    
    /**
     * Determines whether Ref o and this object 
     * share the same host website.
     * @param o The other Ref.
     * @return whether o and this object share the same host.
     */
    public boolean sameHost(Ref o){
        return getHost().toLowerCase().equals(o.getHost().toLowerCase());
    }

    /**
     * Set this object's URL
     * @param url The URL to be set.
     */
    public void setUrl(URL url) {
        this.url = url;
        formatUrl();
    }

    /**
     * Get this object's URL
     * @return This object's URL
     */
    public URL getUrl() {
        return url;
    }
    
    /**
     * Sets the Ref's protocol
     * @param proto The protocol to be set.
     */
    public void setProtocol(String proto){
        if(!isValidUrl()){
            return;
        }
        System.out.println("SET PROTOCOL");
        //Fix protocol if broken
        String protocol = fixProtocol(fixSlashes(proto)) + "";
        
        //Save a copy of the old url in case the protocol isn't completely fixed.
        URL oldUrl = formUrl(url.getProtocol(), url.getHost(), url.getFile());
        
        if(protocol.endsWith("://")){
            //Make sure that protocol doesn't end with ://
            protocol = protocol.substring(0, protocol.length() - 3);
        }
        
        
        url = formUrl(protocol, url.getHost(), url.getFile());
        
        //If protocol isn't completely fixed, set url to original url
        if(url == null){
            System.err.println("Ref::setProtocol() ERROR!! " + protocol + " isn't a valid protocol!");
            url = oldUrl;
        }
    }
    
    /**
     * Get the Ref's query.
     * @return query
     */
    public String getQuery(){
        String query;
        //If not valid or if the query is null
        if(!isValidUrl() || (query = url.getQuery()) == null){
            return "";
        }
        return query + "";
    }
    
    /**
     * Set the Ref's host website.
     * @param h the host website to be set to
     */
    public void setHost(String h){
        if(!isValidUrl()){
            return;
        }
        
        //Fix host if broken
        String host = fixSlashes(h) + "";
        
        //Save a copy of the old url in case the protocol isn't completely fixed.
        URL oldUrl = formUrl(url.getProtocol(), url.getHost(), url.getFile());
        
        if(host.endsWith("/")){
            //Make sure that host doesn't end with a slash, since it's illegal in URL
            host = host.substring(0, host.length() - 1);
        }
        
        url = formUrl(url.getProtocol(), host, url.getFile());
        
        //If host isn't completely fixed, set url to original url
        if(url == null){
            System.err.println("Ref::setHost() ERROR!! " + host + " isn't a valid host!");
            url = oldUrl;
        }
    }
    
    /**
     * Set the Ref's path. When doing this, the original 
     * query will be kept the same. If you don't want to keep the 
     * query, use setPathAndQuery()
     * @param p the path to be set to
     */
    public void setPath(String p){
        if(!isValidUrl()){
            return;
        }
        
        //Fix path if broken
        String path = fixSlashes(p) + "";
        
        //Save a copy of the old url in case the protocol isn't completely fixed.
        URL oldUrl = formUrl(url.getProtocol(), url.getHost(), url.getFile());
        
        if(!path.startsWith("/")){
            //Make sure that path starts with a slash. (Following URL protocol)
            path = "/" + path;
        }
        
        url = formUrl(url.getProtocol(), url.getHost(), path + getQuery());
        
        //If path isn't completely fixed, set url to original url
        if(url == null){
            System.err.println("Ref::setPath() ERROR!! " + path + " isn't a valid path!");
            url = oldUrl;
        } else {
            formatUrl();
        }
    }
    
    /**
     * Set the Ref's path and query.
     * @param p the path and query to be set
     */
    public void setPathAndQuery(String p){
        if(!isValidUrl()){
            return;
        }
        
        //Fix path and query if broken
        String pathAndQuery = fixSlashes(p) + "";
        
        //Save a copy of the old url in case the protocol isn't completely fixed.
        URL oldUrl = formUrl(url.getProtocol(), url.getHost(), url.getFile());
        
        if(!pathAndQuery.startsWith("/")){
            //Make sure that path and query starts with slash (according to URL protocol)
            pathAndQuery = "/" + pathAndQuery;
        }
        
        url = formUrl(url.getProtocol(), url.getHost(), pathAndQuery);
        
        //If the path and query isn't completely fixed, set url to original url
        if(url == null){
            System.err.println("Ref::setPathAndQuery() ERROR!! " + pathAndQuery + " isn't a valid path and query!");
            url = oldUrl;
        } else {
            formatUrl();
        }
    }
    
    /**
     * Says whether this object has a valid url.
     * @return whether the object has a valid url
     */
    public boolean isValidUrl(){
        return url != null;
    }
    
    /**
     * This function returns a ref that contains the host of the ref. If this ref was 
     * https://bing.com/about.html, this function would return a ref that is https://bing.com/
     * @return the host ref
     */
    public Ref getHostRef(){
        if(!isValidUrl()){
            return null;
        }
        return new Ref(getProtocol() + getHost());
    }

    @Override
    public String toString() {
        if(isValidUrl()){
            return url.toString();
        } else {
            return "";
        }
    }
}