module ru.kpfu.itis.lldan.bot {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.net.http;


    opens ru.kpfu.itis.lldan.bot to javafx.fxml;
    exports ru.kpfu.itis.lldan.bot;
}