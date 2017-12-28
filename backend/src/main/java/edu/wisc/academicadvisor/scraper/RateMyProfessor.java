package edu.wisc.academicadvisor.scraper;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.lang.*;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class RateMyProfessor {

    public RateMyProfessor() { }

    public String RateMyProfessor(String[] professors) throws IOException {
        JSONArray json = new JSONArray();
        for (int i = 0; i < professors.length; i++) {
            String temp = "";
            for (int j = 0; j < professors[i].length(); j++) {
                if (professors[i].charAt(j) == ' ')
                    temp += '+';
                else
                    temp += professors[i].charAt(j);
            }
            String url = "http://www.ratemyprofessors.com/search.jsp?query=" + temp + "+wisconsin+madison";
            System.out.println(url);
            Document doc = Jsoup.connect(url).get();
            Elements links = doc.select("a[href]");
            String proflink = null;
            for (Element link : links) {
                if (link.text().contains("PROFESSOR")) {
                    proflink = link.attr("abs:href");
                }
            }
            JSONArray profarray = new JSONArray();
            if (proflink == null) continue;
            doc = Jsoup.connect(proflink).get();

            // getElements by class returns an Elements object with information of each class with the specified name in the HTML file

            Elements pname = doc.getElementsByClass("profname");
            Elements rating = doc.getElementsByClass("grade"); // difficulty and professor rating both have class=grade
            Elements tags = doc.getElementsByClass("tag-box-choosetags");

            List<String> pn = new LinkedList<String>();
            List<String> rat = new LinkedList<String>();
            List<String> proftag = new LinkedList<String>();


            // eachText returns a list of strings of the content of the Elements object
            pn = pname.eachText();
            rat = rating.eachText();
            proftag = tags.eachText();

            if (pn.size() < 1) continue;
            profarray.add(pn.get(0));
            profarray.add(Float.parseFloat(rat.get(0)));
            profarray.add(Float.parseFloat(rat.get(2)));
            profarray.add(proftag);
            json.add(profarray);

        }

        StringWriter out = new StringWriter();
        json.writeJSONString(out);

        String jsonText = out.toString();
        System.out.println(jsonText);
        return jsonText;
    }
}