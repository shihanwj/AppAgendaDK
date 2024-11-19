package es.ieslosmontecillos;

import com.gluonhq.charm.glisten.mvc.View;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.collections.ObservableList;

/**
 * @author Shihan
 */

public class AppAgendaDK extends Application {

    private DataUtil dataUtil;
    private InicioView inicioView = new InicioView();
    private InicioController inicioController;

    @Override
    public void start(Stage primaryStage) throws Exception {

        StackPane rootMain = new StackPane();

        View Inicio = inicioView.getView();
        rootMain.getChildren().add(Inicio);
        inicioController = inicioView.getInicioController();
        inicioController.setRootMain(rootMain);

        // Solicitamos los datos a los servicios de dataUtil
        dataUtil = new DataUtil();
        dataUtil.obtenerTodasProvincias();
        ObservableList<Provincia> olProv = dataUtil.getOlProvincias();
        dataUtil.obtenerTodasPersonas();
        ObservableList<Persona> olPers = dataUtil.getOlPersonas();

        // Pasamos los datos obtenidos a la clase controladora de inicio
        inicioController.setDataUtil(dataUtil);
        inicioController.setOlProv(olProv);
        inicioController.setOlPers(olPers);
        inicioController.setRootMain(rootMain);
        Scene scene = new Scene(rootMain,758,482);
        primaryStage.setTitle("App Agenda");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop(){
        System.out.println("Stop: Se cerró la app");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        launch(args);
    }
}
