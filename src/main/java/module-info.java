module educational.chatbot {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.pdfbox;
    requires java.sql;
    requires com.google.gson;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires lucene.core;
    requires org.json;
    requires ai.djl.api;
    requires ai.djl.tokenizers;
    requires tess4j;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.scratchpad;


    requires org.apache.commons.collections4;
    requires org.apache.commons.compress;
    requires org.apache.logging.log4j;
    requires vosk;
    requires java.desktop;
    requires com.fasterxml.jackson.databind;

    opens com.enset.test to javafx.fxml;
    exports com.enset.test;
}