module practice.igoroffline.tagmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.commons.io;
    requires org.apache.commons.validator;

    opens practice.igoroffline.tagmanager to
            javafx.fxml;

    exports practice.igoroffline.tagmanager;
    exports practice.igoroffline.javafx;
    exports practice.igoroffline.javafx.models;
}
