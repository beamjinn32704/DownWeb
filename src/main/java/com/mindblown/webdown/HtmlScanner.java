
import java.io.File;
import java.util.Arrays;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author beamj
 */
public class HtmlScanner {
    
    private static final String[] autoClosed = new String[]{
    "area", "base", "br", "col", "embed", "hr", "img", "input", "link",
        "meta", "param", "source", "track", "wbr"
    };
    
    private String html;
    private HtmlElements tags;
    private int htmlIndex = 0;
    
    public HtmlScanner(File h) {
        tags = new HtmlElements();
        if(h.isFile() == false){
            return;
        }
        String text = Util.getText(h);
        if(text == null){
            return;
        }
        html = text.replaceAll("[^\\S ]", "");
//        html = text.replaceAll("[^ && \\s]", "");
        analyzeHtml();
    }

    public HtmlElements getTags() {
        return tags;
    }
    
    private void analyzeHtml(){
        htmlIndex = 0;
        int rank = 1;
        while(hasNextTag()){
            HtmlTag tag = getNextTag(rank);
            if(tag != null){
                HtmlTagData data = tag.getData();
                if(data.isStart()){
                    if(!data.isAutoClose()){
                        rank++;
                    }
                } else {
                    rank--;
                    data.setRank(rank);
                }
                tags.addTag(tag);
            } else {
                return;
            }
        }
    }
    
    private HtmlTag getNextTag(int rank){
        String cleaned = clean();
        if(!hasNextTag()){
            return null;
        }
        int start = html.indexOf("<", htmlIndex);
        int space = html.indexOf(" ", start+1);
        int ender = html.indexOf(">", start+1);
        boolean contin = false;
        if(space < ender){
            contin = true;
        }
        int nameOff = 1;
        if(html.charAt(start+1) == '/'){
            nameOff = 2;
        }
        int tagNameEnd;
        if(space == -1){
            tagNameEnd = ender;
        } else {
            tagNameEnd = Math.min(space, ender);
        }
//        String tagName = html.subSequence(start+nameOff, tagNameEnd).toString();
        String tagName = html.substring(start+nameOff, tagNameEnd);
        if(nameOff == 2){
            HtmlTagData data = new HtmlTagData(new Attrib[0], cleaned, "", false, true, rank);
            htmlIndex = ender + 1;
            return new HtmlTag(tagName, data);
        }
        boolean autoClose = isAutoClose(tagName);
        Attrib[] tribs;
        if(contin){
            tribs = Attrib.getAttribs(html, start + 1 + tagName.length(), ender);
        } else {
            tribs = new Attrib[0];
        }
        HtmlTagData data = new HtmlTagData(tribs, "", cleaned, true, autoClose, rank);
        htmlIndex = ender + 1;
        return new HtmlTag(tagName, data);
    }
    
    private boolean isAutoClose(String name){
        int ind = Arrays.binarySearch(autoClosed, name.toLowerCase());
        if(ind < 0){
            return false;
        } else {
            return true;
        }
    }
    
    private boolean hasNextTag(){
        int ind = html.indexOf("<");
        if(ind == -1){
            return false;
        }
        int end = html.indexOf(">", ind+1);
        if(end == -1){
            return false;
        }
        return true;
    }
    
    private String clean(){
        boolean go = true;
        String cleaned = "";
        while(go){
            int ind = html.indexOf("<", htmlIndex);
            if(ind == -1){
                html = "";
                return "";
            }
//            cleaned += html.subSequence(htmlIndex, ind);
            cleaned += html.substring(htmlIndex, ind);
            if(ignore(ind+1)){
                int i = html.indexOf(">", ind+2);
                if(i == -1){
                    html = "";
                    return cleaned;
                }
                
                htmlIndex = i + 1;
                return cleaned;
            } else {
                go = false;
            }
            //RULE: IF TAG STARTS WITH ! IS EITHER IGNORED OR DOCTYPE IF START WITH NOT ALPHA< THEN NOT TAG AND SHOUILD BE IGNORED
        }
        return cleaned;
    }
    
    private boolean ignore(int look){
        char c = html.charAt(look);
        return !Character.isAlphabetic(c) && c != '/';
    }
}