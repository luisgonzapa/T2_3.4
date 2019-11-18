/*
 * Luís González Palomo
 */
package agendaapp;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Luis
 */
public class Main extends Application 
{
    //Atributos necesarios para establecer la conexión con la base de datos.
    private EntityManagerFactory emf;
    private EntityManager em;

    //Método start, inicia la aplicación.
    @Override
    public void start(Stage primaryStage) throws IOException 
    {
        //Crea el Pane, y carga los componentes de AgendaView.
        StackPane rootMain = new StackPane(); 
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AgendaView.fxml")); 
        Pane rootAgendaView=fxmlLoader.load(); 
        rootMain.getChildren().add(rootAgendaView); 
        //Carga del EntityManager, etc… 
        Scene scene = new Scene(rootMain,600,400);
        //Carga en el agendaViewController los componentes de AgendaView.
        AgendaViewController agendaViewController = (AgendaViewController)fxmlLoader.getController();
       //Crea la conexión con la base de datos
        emf = Persistence.createEntityManagerFactory("AgendaAppPU");
        em = emf.createEntityManager();
        //Setea a agendaViewController el atributo para la conexión de la base de datos y que tenga acceso a realizar modificaciones.
        agendaViewController.setEntityManager(em);
        agendaViewController.cargarTodasPersonas();
        //Crea la escena y la muestra.
        primaryStage.setTitle("App Agenda");
        primaryStage.setScene(scene);
        primaryStage.show();
    }//Fin start

    //Cierra el flujo con la base de datos y la conexión entre la aplicación y la BD.
    @Override
    public void stop() throws Exception 
    {
        em.close();
        emf.close();
        try 
        {
            DriverManager.getConnection("jdbc:derby:BDAgenda;shutdown=true");
        } 
        catch (SQLException ex) 
        {
        }
    }//Fin stop
    
    public static void main(String[] args) {
        launch(args);
    }

}
