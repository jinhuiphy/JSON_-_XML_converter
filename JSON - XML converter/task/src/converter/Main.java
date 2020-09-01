package converter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Path file = Paths.get("test.txt");
        try (InputStream in = Files.newInputStream(file);
             BufferedReader reader =
                     new BufferedReader(new InputStreamReader(in))) {
            String line;
            // combine all lines into one single line
            List<String> stringList = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                stringList.add(line.trim());
            }
            line = String.join("", stringList);
            // deal with xml or json
            if (line.startsWith("<")) {
                System.out.println(XmlToJson(line));
            } else {
                System.out.println(JsonToXml(line));
            }
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    static String XmlToJson(String input) {
        if (!input.contains("=")) {
            return convertFromXmlToJson(input);
        }

        StringBuilder stringBuilder = new StringBuilder();
        Pattern p1 = Pattern.compile("<(\\w*).*?>(.*)</\\1>");
        Matcher m1 = p1.matcher(input);

        Pattern p2 = Pattern.compile("<(\\w*).*?>");
        Matcher m2 = p2.matcher(input);

        String head = null;
        String end = "null";
        if (m1.find()) {
            stringBuilder.append("{\"").append(m1.group(1)).append("\" : {");
            head = m1.group(1);
            end = String.format("\"%s\"", m1.group(2));
        } else if (m2.find()) {
            stringBuilder.append("{\"").append(m2.group(1)).append("\" : {");
            head = m2.group(1);
        }

        Pattern p3 = Pattern.compile("(\\w*)\\s*=\\s*\"(\\w*)\"");
        Matcher m3 = p3.matcher(input);
        while (m3.find()) {
            stringBuilder.append("\"@").append(m3.group(1)).append("\" : ")
                    .append("\"").append(m3.group(2)).append("\",");
        }

        stringBuilder.append("\"#").append(head).append("\" : ")
                .append(end).append("}}");
        return stringBuilder.toString();
    }

    static String JsonToXml(String input) {
        if (!input.contains("#")) {
            return convertFromJsonToXml(input);
        }
        Pattern p1 = Pattern.compile("\"#(\\w*)\"\\s*:\\s*\"?(.*?)\"?(?=})");
        Matcher m1 = p1.matcher(input);
        StringBuilder stringBuilder = new StringBuilder();
        String end = null;
        if (m1.find()) {
            stringBuilder.append("<")
                    .append(m1.group(1));
            if ("null".equals(m1.group(2))) {
                end = " />";
            } else {
                end = String.format(">%s</%s>", m1.group(2), m1.group(1));
            }
        }
        Pattern p2 = Pattern.compile("\"@(\\w*)\"\\s*:\\s*(.*?),");
        Matcher m2 = p2.matcher(input);
        while (m2.find()) {
            stringBuilder.append(" ").append(m2.group(1)).append(" = ");
            if (!m2.group(2).startsWith("\"")) {
                stringBuilder.append(String.format("\"%s\"", m2.group(2)));
            } else {
                stringBuilder.append(m2.group(2));
            }

        }
        stringBuilder.append(end);
        return stringBuilder.toString();
    }
    // simple case
    static String convertFromXmlToJson(String input) {
        Pattern p1 = Pattern.compile("<(\\w*)>(.*)</\\1>");
        Matcher m1 = p1.matcher(input);
        if (m1.find()) {
            return String.format("{\"%s\":\"%s\"}", m1.group(1), m1.group(2));
        }
        Pattern p2 = Pattern.compile("<(\\w*)/>");
        Matcher m2 = p2.matcher(input);
        if (m2.find()) {
            return String.format("{\"%s\": null}", m2.group(1));
        }
        return null;
    }
    // simple case
    static String convertFromJsonToXml(String input) {
        Pattern p1 = Pattern.compile("\\{\"(.*?)\".*\"(.*?)\"}");
        Matcher m1 = p1.matcher(input);
        if (m1.find()) {
            return String.format("<%s>%s</%s>", m1.group(1), m1.group(2), m1.group(1));
        }
        Pattern p2 = Pattern.compile("\\{.*\"(.*?)\".*null.*}");
        Matcher m2 = p2.matcher(input);
        if (m2.find()) {
            return String.format("<%s/>", m2.group(1));
        }
        return null;
    }
}
