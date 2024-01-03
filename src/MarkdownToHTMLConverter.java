import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownToHTMLConverter {
    public static boolean inCodeBlock = false;

    public static void main(String[] args) {
        String markdownText = "# Hello, *Markdown*!\n" +
                "\n" +
                "This is a **simple** example of *Markdown*.\n" +
                "\n" +
                "- List item 1\n" +
                "- List item 2\n" +
                "\n" +
                "## Second heading\n" +
                "\n" +
                "A paragraph with a [link to Google](https://www.google.com) and another [link](https://www.example.com).\n" +
                "\n" +
                "Here is a code block:\n" +
                "```\n" +
                "public class HelloWorld {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello, World!\");\n" +
                "    }\n" +
                "}\n" +
                "```\n";

        String htmlOutput = convertMarkdownToHTML(markdownText);
        System.out.println(htmlOutput);
    }

    public static String convertMarkdownToHTML(String markdownText) {
        StringBuilder htmlOutput = new StringBuilder();

        String[] lines = markdownText.split("\n");


        for (String line : lines) {
            if (line.startsWith("```")) {
                inCodeBlock = !inCodeBlock;
                if (inCodeBlock) {
                    htmlOutput.append("<pre><code>\n");
                } else {
                    htmlOutput.append("</code></pre>\n");
                }
            }
//            else if (line.startsWith("# ")) {
//                htmlOutput.append(parseHeader1(line)).append("\n");
//            } else if (line.startsWith("## ")) {
//                htmlOutput.append(parseHeader2(line)).append("\n");
//            }
            else if (line.equals("  ")) {
                htmlOutput.append(parseLineBreaks(line)).append("\n");
            } else if (line.startsWith("- ")) {
                htmlOutput.append(parseLists(line)).append("\n");
            } else {
                line = parseHeaders(line);
                line = parseBold(line);
                line = parseItalic(line);
                line = parseLinks(line);
                htmlOutput.append(parseParagraphs(line)).append("\n");
            }
        }

        return htmlOutput.toString();
    }


    private static String parseHeaders(String line) {

        if (line.isEmpty()) {
            return "";
        }

        int level = 0;
        while (level < line.length() && line.charAt(level) == '#') {
            level++;
        }


        if (level < line.length()) {
            String headerText = line.substring(level);
            String emphasis = "";

            if (!headerText.isEmpty() && headerText.charAt(0) == '*' && headerText.charAt(headerText.length() - 1) == '*') {
                emphasis = "<em>";
                headerText = headerText.substring(1, headerText.length() - 1);
            }

            if (level != 0) {
                return "<h" + level + ">" + emphasis + headerText + emphasis + "</h" + level + ">";
            } else {
                return emphasis + headerText + emphasis;
            }

        }
        return "";
    }

    private static String parseItalic(String line) {
        line = line.replace("*", "<em>").replace("*", "</em>");
        return line;
    }


    private static String parseHeader1(String line) {
        return "<h1>" + line.substring(2) + "</h1>";
    }

    private static String parseHeader2(String line) {
        return "<h2>" + line.substring(3) + "</h2>";
    }

    private static String parseLineBreaks(String line) {
        return "<br>";
    }

    private static String parseBold(String line) {
        line = line.replace("**", "<strong>").replace("**", "</strong>");
        // line = line.replace("*", "<em>").replace("*", "</em>");
        return line;
    }

    private static String parseLists(String line) {
        return "<ul><li>" + line.substring(2) + "</li></ul>";
    }

    private static String parseParagraphs(String line) {
        return "<p>" + line + "</p>";
    }

    private static String parseLinks(String line) {
        Pattern pattern = Pattern.compile("\\[([^\\]]+)\\]\\(([^\\)]+)\\)");
        Matcher matcher = pattern.matcher(line);

        while (matcher.find()) {
            String linkText = matcher.group(1);
            String linkURL = matcher.group(2);
            String replacement = "<a href=\"" + linkURL + "\">" + linkText + "</a>";
            line = line.replaceFirst("\\[([^\\]]+)\\]\\(([^\\)]+)\\)", replacement);
        }

        return line;
    }

}
