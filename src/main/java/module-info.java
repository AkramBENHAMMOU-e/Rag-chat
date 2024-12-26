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


    opens com.enset.test to javafx.fxml;
    exports com.enset.test;

}