package converter;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input;
        input = scanner.nextLine();
        if (input.startsWith("<")) {
            System.out.println(convertFromXmlToJson(input));
        } else {
            System.out.println(convertFromJsonToXml(input));
        }
    }

    static String convertFromXmlToJson(String xml) {
        String pattern1 = "<(\\w*)>(.*)</\\1>";
        String pattern2 = "<(\\w*)/>";
        Pattern p1 = Pattern.compile(pattern1);
        Matcher m1 = p1.matcher(xml);
        if (m1.find()) {
            return String.format("{\"%s\":\"%s\"}", m1.group(1), m1.group(2));
        }
        Pattern p2 = Pattern.compile(pattern2);
        Matcher m2 = p2.matcher(xml);
        if (m2.find()) {
            return String.format("{\"%s\": null}", m2.group(1));
        }
        return null;
    }

    static String convertFromJsonToXml(String json) {
        String pattern1 = "\\{\"(.*?)\".*\"(.*?)\"}";
        String pattern2 = "\\{.*\"(.*?)\".*null.*}";
        Pattern p1 = Pattern.compile(pattern1);
        Matcher m1 = p1.matcher(json);
        if (m1.find()) {
            return String.format("<%s>%s</%s>", m1.group(1), m1.group(2), m1.group(1));
        }
        Pattern p2 = Pattern.compile(pattern2);
        Matcher m2 = p2.matcher(json);
        if (m2.find()) {
            return String.format("<%s/>", m2.group(1));
        }
        return null;
    }
}
