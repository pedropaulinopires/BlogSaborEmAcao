package com.saboremacao.blog.service;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;


@Service
public class CommonMarkService {

    /**
     * save database in html
     */
    public String convertToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    /**
     * convert list
     */
    public String convertHtmlToMarkdown(String html) {
        Document doc = Jsoup.parse(html);
        Elements listItems = doc.select("li");
        StringBuilder markdown = new StringBuilder();

        for (Element listItem : listItems) {
            markdown.append("* ").append(listItem.text()).append("\n");
        }
        markdown.append("\n");
        return markdown.toString();
    }


    /**
     *
     */
    public String convertToMarkdown(String html) {
        Document doc = Jsoup.parse(html);
        StringBuilder markdown = new StringBuilder();

        Elements elements = doc.body().children();
        for (Element element : elements) {
            convertElementToMarkdown(element, markdown);
            markdown.append("\n");
            markdown.append("\n");
        }

        return markdown.toString();
    }

    private void convertElementToMarkdown(Element element, StringBuilder markdown) {
        String tagName = element.tagName();

        switch (tagName) {
            case "p":
                markdown.append(element.text());
                break;
            case "ul":
            case "ol":
                convertListToMarkdown(element, markdown);
                break;
            case "li":
                markdown.append("* ").append(element.text());
                break;
            case "h1":
                markdown.append("# ").append(element.text());
                break;
            case "h2":
                markdown.append("## ").append(element.text());
                break;
            // Adicione outros casos conforme necessário para os elementos HTML que você deseja converter
            default:
                markdown.append(element.text());
                break;
        }

        // Recursivamente converte os elementos filhos
        Elements children = element.children();
        for (Element child : children) {
            convertElementToMarkdown(child, markdown);
        }
    }

    private void convertListToMarkdown(Element element, StringBuilder markdown) {
        Elements listItems = element.getElementsByTag("li");
        for (Element listItem : listItems) {
            markdown.append("* ").append(listItem.text()).append("\n");
        }

    }


}
