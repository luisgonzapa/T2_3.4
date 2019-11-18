package agendaapp;

/*
 * Luís González Palomo
 */
import entidades.Persona;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author Luis
 */
public class AgendaViewController implements Initializable 
{

    //Componentes de la vista AgendaView
    private EntityManager entityManager;
    @FXML
    private TableView<Persona> tableViewAgenda;
    @FXML
    private TableColumn<Persona, String> columnNombre;
    @FXML
    private TableColumn<Persona, String> columnApellidos;
    @FXML
    private TableColumn<Persona, String> columnEmail;
    @FXML
    private TableColumn<Persona, String> columnProvincia;
    @FXML
    private TextField TextFieldNombre;
    @FXML
    private TextField TextFielApellidos;

    private Persona personaSeleccionada;
    @FXML
    private AnchorPane rootAgendaView;

    //Setter para EntityManager.
    public void setEntityManager(EntityManager entityManager) 
    {
        this.entityManager = entityManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        //Relaciona las columnas de la tabla.
        columnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnProvincia.setCellValueFactory(new PropertyValueFactory<>("provincia"));
        columnProvincia.setCellValueFactory(cellData -> 
        {
            SimpleStringProperty property = new SimpleStringProperty();
            if (cellData.getValue().getProvincia() != null) 
            {
                property.setValue(cellData.getValue().getProvincia().getNombre());
            }
            return property;
        });

        //Manejador para saber que persona ha sido seleccionada.
        tableViewAgenda.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> 
        {
            personaSeleccionada = newValue;
            //Si la persona seleccionada no es null, obtiene nombre y apellidos.
            if (personaSeleccionada != null) 
            {
                TextFieldNombre.setText(personaSeleccionada.getNombre());
                TextFielApellidos.setText(personaSeleccionada.getApellidos());
            } 
            //Si la persona es null, pone los textfields a "vacío"
            else 
            {
                TextFieldNombre.setText("");
                TextFielApellidos.setText("");
            }
        });

    }

    //Crea la query para realizar consultas a la tabla, las almacena en una lista y las muestra en el tableViewAgenda.
    public void cargarTodasPersonas() 
    {
        Query queryPersonaFindAll = entityManager.createNamedQuery("Persona.findAll");
        List<Persona> listPersona = queryPersonaFindAll.getResultList();
        tableViewAgenda.setItems(FXCollections.observableArrayList(listPersona));
    }

    /*
    Método para almacenar los campos introducidos en la base de datos. FXML Relaciona este método 
    con el componente que tiene la propiedad de hacerlo.
    */
    @FXML
    private void onActionButtonGuardar(ActionEvent event) 
    {

        if (personaSeleccionada != null) 
        {
            personaSeleccionada.setNombre(TextFieldNombre.getText());
            personaSeleccionada.setApellidos(TextFielApellidos.getText());

            entityManager.getTransaction().begin();
            entityManager.merge(personaSeleccionada);
            entityManager.getTransaction().commit();

            int numFilaSeleccionada = tableViewAgenda.getSelectionModel().getSelectedIndex();
            tableViewAgenda.getItems().set(numFilaSeleccionada, personaSeleccionada);

            TablePosition pos = new TablePosition(tableViewAgenda, numFilaSeleccionada, null);
            tableViewAgenda.getFocusModel().focus(pos);
            tableViewAgenda.requestFocus();
        }
    }//Fin ButtonGuardar

    @FXML
    private void onActionButtonNuevo(ActionEvent event) 
    {
        try 
        {
            // Cuando se acciona el botón se cargará la vista de la nueva ventana "PersonaDetalleView".
            // Carga todos sus componentes y asigna el controlador.
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PersonaDetalleView.fxml"));
            Parent rootDetalleView = fxmlLoader.load();
            PersonaDetalleViewController personaDetalleViewController = (PersonaDetalleViewController) fxmlLoader.getController();
            personaDetalleViewController.setRootAgendaView(rootAgendaView);

            //Crea ujna
            personaDetalleViewController.setTableViewPrevio(tableViewAgenda);
            personaSeleccionada = new Persona();
            personaDetalleViewController.setPersona(entityManager, personaSeleccionada, true);
            personaDetalleViewController.mostrarDatos();

            // Oculta la vista de rootAgendaView, para solo mostrar la nueva vista PersonaDetalleView.
            rootAgendaView.setVisible(false);
            StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot();
            rootMain.getChildren().add(rootDetalleView);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(AgendaViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//Fin ButtonNuevo

    //Se encargará de editar parámetros de un contacto preseleccionado.
    @FXML
    private void onActionButtonEditar(ActionEvent event) 
    {
        try 
        {
            // Cuando se acciona el botón se cargará la vista de la nueva ventana "PersonaDetalleView".
            // Carga todos sus componentes y asigna el controlador.
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PersonaDetalleView.fxml"));
            Parent rootDetalleView = fxmlLoader.load();
            PersonaDetalleViewController personaDetalleViewController = (PersonaDetalleViewController) fxmlLoader.getController();
            personaDetalleViewController.setRootAgendaView(rootAgendaView);

            personaDetalleViewController.setTableViewPrevio(tableViewAgenda);
            personaDetalleViewController.setPersona(entityManager, personaSeleccionada, false);
            personaDetalleViewController.mostrarDatos();

            // Oculta la vista de la ventana anterior.
            rootAgendaView.setVisible(false);
            //Añadir la vista detalle al StackPane principal para que se muestre 
            StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot();
            rootMain.getChildren().add(rootDetalleView);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(AgendaViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//Fin ButtonEditar

    @FXML
    private void onActionButtonSuprimir(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar");
        alert.setHeaderText("¿Desea suprimir el siguiente registro?");
        alert.setContentText(personaSeleccionada.getNombre() + " "
                + personaSeleccionada.getApellidos());
        Optional<ButtonType> result = alert.showAndWait();
        //Si presiona el botón ok, borrará el contacto seleccionado.
        if (result.get() == ButtonType.OK) 
        {
            entityManager.getTransaction().begin();
            entityManager.merge(personaSeleccionada);
            entityManager.remove(personaSeleccionada);
            entityManager.getTransaction().commit();
            tableViewAgenda.getItems().remove(personaSeleccionada);
            tableViewAgenda.getFocusModel().focus(null);
            tableViewAgenda.requestFocus();
        } 
        //si presiona el botón cancelar, no realizará el borrado del dato.
        else 
        {
            int numFilaSeleccionada = tableViewAgenda.getSelectionModel().getSelectedIndex();
            tableViewAgenda.getItems().set(numFilaSeleccionada, personaSeleccionada);
            TablePosition pos = new TablePosition(tableViewAgenda, numFilaSeleccionada, null);
            tableViewAgenda.getFocusModel().focus(pos);
            tableViewAgenda.requestFocus();
        }
    }//Fin BotonSuprimir.
}//Fin Controlador.
