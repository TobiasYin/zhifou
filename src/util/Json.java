package util;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspContext;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Json {
    private static final Pattern listOrDict = Pattern.compile("^(\\[(.*?,?)*])|(\\{(([\"'].*?[\"']):(.*?),?)*})$");
    private static final Pattern pair = Pattern.compile("([\"'](.*?)[\"']):((\\[.*?])|(\\{.*?})|(.*?),|(.*)})");
    private static final Pattern numBoolStr = Pattern.compile("^(\\d+(.\\d+)?)|(true|false)|([\"'](.*)[\"'])|(.+)$");
    private static final Pattern str = Pattern.compile("^([\"'](.*)[\"'])$");

    /**
     * @param c is a Collection or Map, or will throw IllegalArgumentException
     * @return jsonifiy string
     * @throws IllegalArgumentException c is not Map or Collection
     * @author Tobias
     */
    public static String fromCollection(Object c) throws IllegalArgumentException {
        if (c instanceof Map) {
            return getMapJson((Map) c);
        } else if (c instanceof List) {
            return getListJson((List) c);
        } else
            throw new IllegalArgumentException("This method Can only receive Collection or Map");
    }

    private static void add(StringBuilder sb, Object item) {
        if (item instanceof Map || item instanceof Collection) {
            sb.append(fromCollection(item));
        } else if (item instanceof Number || item instanceof Boolean) {
            sb.append(item);
        } else {
            sb.append('"');
            sb.append(item);
            sb.append('"');
        }
        sb.append(",");
    }

    private static String getMapJson(Map m) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (Object k : m.keySet()) {
            sb.append('"');
            sb.append(k);
            sb.append("\":");
            Object v = m.get(k);
            add(sb, v);
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append('}');
        return sb.toString();
    }

    private static String getListJson(List m) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (Object item : m) {
            add(sb, item);
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

    private Object req;
    private String json;

    public Json(Object req, String json) {
        this.req = req;
        this.json = json;
    }

    /**
     * from String get Json
     *
     * @return json
     * @throws ParseException
     */
    public String fromContext() throws ParseException {
        json = json.replace(" ", "").replace("\n", "").replace("\t", "").replace("\r", "");
        return fromCollection(fromString(json));
    }

    private Object fromString(String json) throws ParseException {
        Matcher ld = listOrDict.matcher(json);
        if (ld.find()) {
            if (ld.group(1) != null)
                return fromListString(json);
            else
                return fromDictString(json);
        } else
            throw new ParseException("Can only receive list or dict like", 0);
    }

    private List fromListString(String json) throws ParseException {
        String[] temps = json.substring(1, json.length() - 1).split(",");
        List<String> realItems = new ArrayList<>();
        List<String> items = Arrays.asList(temps);
        boolean flag = true;
        StringBuilder temp = new StringBuilder();
        for (String item : items) {
            if (item.startsWith("\"") && item.endsWith("\"")) {
                if (flag) realItems.add(item);
                else throw new ParseException("Token error", 0);
            } else if (item.endsWith("\"") && !flag) {
                realItems.add(temp + item);
                temp.delete(0, temp.length());
                flag = true;
            } else if (!flag && item.contains("\""))
                throw new ParseException("Token error", 0);
            else if (item.startsWith("\"")) {
                temp.append(item);
                flag = false;
            } else if (!flag) temp.append(item);
            else realItems.add(item);
        }
        items = realItems;
        items = CheekItem(items, "[", "]");
        realItems = CheekItem(items, "{", "}");
        ArrayList<Object> res = new ArrayList<>();
        for (String item : realItems) {
            res.add(fromStringGetValue(item));
        }
        return res;
    }

    private List<String> CheekItem(List<String> items, String prefix, String postfix) throws ParseException {
        ArrayList<String> realItems = new ArrayList<>();
        int flag = 0;
        StringBuilder temp = new StringBuilder();
        for (String item : items) {
            if (item.startsWith(prefix) && item.endsWith(postfix)) {
                if (flag == 0) realItems.add(item);
                else throw new ParseException("Token error", 0);
            } else if (item.endsWith(postfix) && flag != 0) {
                if (flag == 1) {
                    realItems.add(temp + item);
                    temp.delete(0, temp.length());
                    flag = 0;
                } else {
                    flag--;
                }
            } else if (item.startsWith(prefix)) {
                temp.append(item);
                flag++;
            } else if (item.contains(postfix) || item.contains(postfix)) throw new ParseException("Token error", 0);
            else if (flag != 0) temp.append(item);
            else realItems.add(item);
        }
        return realItems;
    }

    private Map fromDictString(String json) throws ParseException {
        Matcher pairs = pair.matcher(json);
        HashMap<String, Object> res = new HashMap<>();
        int pos = -1;
        while (true) {
            if (pos == -1) {
                if (!pairs.find()) break;
            } else {
                if (!pairs.find(pos)) break;
            }
            Matcher strM = str.matcher(pairs.group(1));
            if (!strM.find()) throw new ParseException("String required", 0);
            String name = pairs.group(2);
            Object value;
            if (pairs.group(6) != null) {
                value = fromStringGetValue(pairs.group(6));
                pos = -1;
            } else if (pairs.group(7) != null) {
                value = fromStringGetValue(pairs.group(7));
                pos = -1;
            } else if (pairs.group(4) != null) {
                int flag = 0;
                int end = -1;
                for (int i = pairs.start(3); i < json.length(); i++) {
                    if (json.charAt(i) == '[') {
                        flag++;
                    } else if (json.charAt(i) == ']') {
                        flag--;
                    }
                    if (flag == 0) {
                        end = i;
                        break;
                    }
                }
                if (flag == 0 && end != -1) {
                    value = fromStringGetValue(json.substring(pairs.start(3), end + 1));
                    pos = end + 1;
                } else throw new ParseException("[] can't pair", 0);
            } else if (pairs.group(5) != null) {
                int flag = 0;
                int end = -1;
                for (int i = pairs.start(3); i < json.length(); i++) {
                    if (json.charAt(i) == '{') {
                        flag++;
                    } else if (json.charAt(i) == '}') {
                        flag--;
                    }
                    if (flag == 0) {
                        end = i;
                        break;
                    }
                }
                if (flag == 0 && end != -1) {
                    value = fromStringGetValue(json.substring(pairs.start(3), end + 1));
                    pos = end + 1;
                } else throw new ParseException("{} can't pair", 0);
            } else {
                value = fromStringGetValue(pairs.group(3));
                pos = -1;
            }
            res.put(name, value);
        }
        return res;
    }

    private Object fromStringGetValue(String value) throws ParseException {
        Object res;
        if (value.equals("")) throw new ParseException("Value Can't be empty", 0);
        Matcher ld = listOrDict.matcher(value);
        Matcher other = numBoolStr.matcher(value);
        if (ld.matches()) res = fromString(value);
        else if (other.find()) {
            if (other.group(1) != null)
                if (other.group(2) == null)
                    res = Integer.parseInt(other.group(1));
                else
                    res = Double.parseDouble(other.group(1));
            else if (other.group(3) != null)
                res = Boolean.parseBoolean(other.group(3));
            else if (other.group(4) != null)
                res = other.group(5);
            else {
                if (req != null) {
                    if (req instanceof JspContext) {
                        res = ((JspContext) req).getAttribute(other.group(6));
                    } else if (req instanceof ServletRequest) {
                        res = ((ServletRequest) req).getAttribute(other.group(6));
                    } else if (req instanceof HttpSession) {
                        res = ((HttpSession) req).getAttribute(other.group(6));
                    } else if (req instanceof Map) {
                        res = ((Map) req).get(other.group(6));
                    } else res = null;
                } else res = null;
                if (res == null)
                    throw new ParseException("Can't read variance", 0);
            }
        } else throw new ParseException("Error", 0);
        return res;
    }

    public static void main(String[] args) {
        String str =
                "{" +
                        "'a':12," +
                        "'b':'ds'," +
                        "'c':[1,2,3,[1,2,3]]," +
                        "'d':{'e':4,'f':[1,{'d':213},4],'g':'sd'}" +
                        "}";
        Json json = new Json(null, str);
        try {
            System.out.print(json.fromContext());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
