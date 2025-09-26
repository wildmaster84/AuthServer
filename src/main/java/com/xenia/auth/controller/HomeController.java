package com.xenia.auth.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;

@RestController
public class HomeController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String index() throws Exception {
        ClassPathResource resource = new ClassPathResource("resources/readme.md");
        String markdown = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        Node document = parser.parse(markdown);
        String html = renderer.render(document);

        // GitHub-style markdown CSS with robust dark mode
        String template = """
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="UTF-8">
          <title>Xenia Auth API Docs</title>
          <style>
            html, body {
              height: 100%%;
              margin: 0;
              padding: 0;
            }
            body {
              font-family: 'Segoe UI', 'Helvetica Neue', Arial, sans-serif;
              background: #f6f8fa;
              color: #24292f;
              margin: 0;
              min-height: 100vh;
              transition: background 0.2s, color 0.2s;
            }
            .markdown-body {
              box-sizing: border-box;
              min-width: 200px;
              max-width: 900px;
              margin: 40px auto;
              background: #fff;
              border-radius: 10px;
              box-shadow: 0 2px 24px #0002;
              padding: 40px;
              transition: background 0.2s, color 0.2s;
            }
            @media (max-width: 768px) {
              .markdown-body {
                padding: 16px;
                margin: 0;
                border-radius: 0;
                box-shadow: none;
              }
            }
            .dark-mode body {
              background: #181a20;
              color: #c9d1d9;
            }
            .dark-mode .markdown-body {
              background: #23262f;
              color: #c9d1d9;
              box-shadow: 0 2px 24px #0008;
            }
            .dark-mode h1, .dark-mode h2, .dark-mode h3 {
              color: #79c0ff;
            }
            .dark-mode a {
              color: #58a6ff;
            }
            /* Code blocks and inline code */
            pre, code {
              font-family: 'Fira Mono', 'Consolas', 'Monaco', monospace;
              font-size: 14px;
              background: #f6f8fa;
              color: #24292f;
              border-radius: 6px;
            }
            pre {
              padding: 16px;
              overflow-x: auto;
              margin-top: 16px;
              margin-bottom: 16px;
            }
            code {
              padding: 2px 6px;
            }
            .dark-mode pre, .dark-mode code {
              background: #161b22;
              color: #c9d1d9;
            }
            /* Blockquotes */
            blockquote {
              border-left: 4px solid #d0d7de;
              padding: 0 16px;
              color: #6e7781;
              margin: 0;
            }
            .dark-mode blockquote {
              border-left: 4px solid #30363d;
              color: #8b949e;
            }
            /* Table styles */
            table {
              border-collapse: collapse;
              width: 100%%;
              margin: 20px 0;
            }
            th, td {
              border: 1px solid #d0d7de;
              padding: 8px 12px;
            }
            .dark-mode th, .dark-mode td {
              border: 1px solid #30363d;
            }
            tr:nth-child(even) {
              background: #f6f8fa;
            }
            .dark-mode tr:nth-child(even) {
              background: #23262f;
            }
            /* Horizontal rule */
            hr {
              border: none;
              border-top: 1px solid #d0d7de;
              margin: 32px 0;
            }
            .dark-mode hr {
              border-top: 1px solid #30363d;
            }
            /* Button styles */
            .dark-toggle {
              position: fixed;
              top: 1.25em;
              right: 2em;
              background: #3264fe;
              color: #fff;
              border: none;
              border-radius: 100px;
              padding: 0.5em 1.2em;
              cursor: pointer;
              font-size: 1em;
              z-index: 1000;
              transition: background 0.2s;
              box-shadow: 0 2px 8px #0002;
            }
            .dark-toggle:hover {
              background: #233ea5;
            }
          </style>
        </head>
        <body>
          <button class="dark-toggle" onclick="toggleDarkMode()">Dark Mode</button>
          <div class="markdown-body">
            %s
          </div>
          <script>
            function toggleDarkMode() {
              document.documentElement.classList.toggle('dark-mode');
              if(document.documentElement.classList.contains('dark-mode')) {
                document.querySelector('.dark-toggle').textContent = 'Light Mode';
                localStorage.setItem('darkmode', 'true');
              } else {
                document.querySelector('.dark-toggle').textContent = 'Dark Mode';
                localStorage.setItem('darkmode', 'false');
              }
            }
            // Automatically enable dark mode if set
            if(localStorage.getItem('darkmode') === 'true') {
              document.documentElement.classList.add('dark-mode');
              document.addEventListener('DOMContentLoaded', () => {
                document.querySelector('.dark-toggle').textContent = 'Light Mode';
              });
            }
          </script>
        </body>
        </html>
        """;

        return String.format(template, html);
    }
}