package es.ieslosmontecillos;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;


import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;


public class PersonaDetalleViewController
{
    // Constantes para el estado civil
    public static final String CASADO = "C";
    public static final String SOLTERO = "S";
    public static final String VIUDO = "V";
    public static final String CARPETA_FOTOS = "Fotos";

    private Pane rootAgendaView;
    private TableView tableViewPrevio;
    private Persona persona;
    private DataUtil dataUtil;
    private boolean nuevaPersona;

    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldTelefono;
    @FXML
    private RadioButton radioButtonViudo;
    @FXML
    private DatePicker datePickerFechaNacimiento;
    @FXML
    private TextField textFieldNumHijos;
    @FXML
    private CheckBox checkBoxJubilado;
    @FXML
    private RadioButton radioButtonSoltero;
    @FXML
    private TextField textFieldSalario;
    @FXML
    private RadioButton radioButtonCasado;
    @FXML
    private ImageView imageViewFoto;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private ComboBox<Provincia> comboBoxProvincia;
    @FXML
    private TextField textFieldApellidos;
    @FXML
    private AnchorPane rootPersonaDetalleView;
    @FXML
    private ToggleGroup tgEstadoCivil;

    public void setRootAgendaView(Pane rootAgendaView){
        this.rootAgendaView = rootAgendaView;
    }

    public void setTableViewPrevio(TableView<Persona> tableViewPrevio){
        this.tableViewPrevio = tableViewPrevio;
    }

    public void setPersona(Persona persona, Boolean nuevaPersona){

        if(!nuevaPersona){
            this.persona = persona;
        }else{
            this.persona = new Persona();
        }

        this.nuevaPersona = nuevaPersona;
    }

    public void setDataUtil(DataUtil dataUtil){
        this.dataUtil = dataUtil;
    }

    public void mostrarDatos() {

        textFieldNombre.setText(persona.getNombre());
        textFieldApellidos.setText(persona.getApellidos());
        textFieldTelefono.setText(persona.getTelefono());
        textFieldEmail.setText(persona.getEmail());

        if(persona.getNumHijos() != null){
            textFieldNumHijos.setText(persona.getNumHijos().toString());
        }
        if(persona.getSalario() != null){
            textFieldSalario.setText(persona.getSalario().toString());
        }

        if(persona.getJubilado() != null && persona.getJubilado() == 1){
            checkBoxJubilado.setSelected(true);
        } else {
            checkBoxJubilado.setSelected(false);
        }

        if (persona.getEstadoCivil() != null){
            switch (persona.getEstadoCivil()){
                case CASADO:
                    radioButtonCasado.setSelected(true);
                    break;
                case SOLTERO:
                    radioButtonSoltero.setSelected(true);
                    break;
                case VIUDO:
                    radioButtonViudo.setSelected(true);
                    break;
            }
        }

        if(persona.getFechaNacimiento() != null){
            try{
                SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
                Date fecNac = formato.parse(persona.getFechaNacimiento());
                LocalDate fechaNac = fecNac.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                datePickerFechaNacimiento.setValue(fechaNac);
            } catch (ParseException e) {
                System.out.println(e.getMessage());
            }
        }

        comboBoxProvincia.setItems(dataUtil.getOlProvincias());

        comboBoxProvincia.setCellFactory(
                (ListView<Provincia> l) -> new ListCell<Provincia>(){
                    @Override
                    protected void updateItem(Provincia provincia, boolean empty) {
                        super.updateItem(provincia, empty);
                        if(provincia == null || empty){
                            setText("");
                        } else {
                            setText(provincia.getCodigo() + "-" + provincia.getNombre());
                        }
                    }
                }
        );

        comboBoxProvincia.setConverter((new StringConverter<Provincia>(){
            @Override
            public String toString(Provincia provincia){
                if (provincia == null){
                    return null;
                } else {
                    return provincia.getCodigo() + "-" + provincia.getNombre();
                }
            }

            @Override
            public Provincia fromString(String userId) {
                return null;
            }
        }));

        if(persona.getProvincia() != null){
            comboBoxProvincia.setValue(persona.getProvincia());
        }

        if(persona.getFoto() != null){
            String imageFileName = persona.getFoto();
            File file = new File(CARPETA_FOTOS+"/"+imageFileName);
            if(file.exists()){
                Image image = new Image(file.toURI().toString());
                imageViewFoto.setImage(image);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "No se encuentra la imagen en "+file.toURI().toString());
                alert.showAndWait();
            }
        }
    }

    @FXML
    public void initialize() {
    }

    @FXML
    public void onActionButtonCancelar(ActionEvent event) {

        StackPane rootMain = (StackPane) rootPersonaDetalleView.getScene().getRoot();
        rootMain.getChildren().remove(rootPersonaDetalleView);

        rootAgendaView.setVisible(true);

        int numFilaSeleccionada = tableViewPrevio.getSelectionModel().getSelectedIndex();
        TablePosition pos = new TablePosition(tableViewPrevio, numFilaSeleccionada, null);
        tableViewPrevio.getFocusModel().focus(pos);
        tableViewPrevio.requestFocus();
    }

    @FXML
    public void onActionButtonGuardar(ActionEvent event) {

        // Informa si el formato de los datos introducidos es correcto o no
        boolean errorFormato = false;

        StackPane rootMain = (StackPane) rootPersonaDetalleView.getScene().getRoot();
        rootMain.getChildren().remove(rootPersonaDetalleView);

        if(textFieldNombre.getText() != null && textFieldNombre.getText().length() < 20)
        {
            persona.setNombre(textFieldNombre.getText());
        }
        else
        {
            errorFormato = true;
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Nombre no válido");
            alert.showAndWait();
            textFieldNombre.requestFocus();
        }

        if(textFieldApellidos.getText() != null && textFieldApellidos.getText().length() < 40)
        {
            persona.setApellidos(textFieldApellidos.getText());
        }
        else
        {
            errorFormato = true;
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Apellidos no válidos");
            alert.showAndWait();
            textFieldApellidos.requestFocus();
        }

        persona.setTelefono(textFieldTelefono.getText());
        persona.setEmail(textFieldEmail.getText());

        if(!textFieldNumHijos.getText().isEmpty()){
            try{
                persona.setNumHijos(Integer.valueOf(textFieldNumHijos.getText()));
            }catch (NumberFormatException ex){
                errorFormato = true;
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Número de hijos no válido");
                alert.showAndWait();
                textFieldNumHijos.requestFocus();
            }
        }

        if(!textFieldSalario.getText().isEmpty()) {
            try {
                Double dSalario = Double.valueOf(textFieldSalario.getText());
                persona.setSalario(dSalario);
            } catch (NumberFormatException ex) {
                errorFormato = true;
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Salario no válido");
                alert.showAndWait();
                textFieldSalario.requestFocus();
            }
        }

        persona.setJubilado(checkBoxJubilado.isSelected()?1:0);

        if(radioButtonCasado.isSelected()){
            persona.setEstadoCivil(CASADO);
        } else if (radioButtonSoltero.isSelected()){
            persona.setEstadoCivil(SOLTERO);
        } else if (radioButtonViudo.isSelected()){
            persona.setEstadoCivil(VIUDO);
        }

        if(datePickerFechaNacimiento.getValue() != null){
            LocalDate localDate = datePickerFechaNacimiento.getValue();
            ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
            Instant instant = zonedDateTime.toInstant();
            Date date = Date.from(instant);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fechaComoCadena = sdf.format(date);
            persona.setFechaNacimiento(fechaComoCadena);
        } else {
            persona.setFechaNacimiento(null);
        }

        if( comboBoxProvincia.getValue() != null){
            persona.setProvincia(comboBoxProvincia.getValue());
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Debe indicar una provincia");
            alert.showAndWait();
            errorFormato = true;
        }

        // Recoger datos de pantalla
        if(!errorFormato){
            try{
                /* Guarda el objeto en BD, envía al servidor y oculta la vista actual*/
                if (nuevaPersona){
                    dataUtil.addPersona(persona);
                }else{
                    dataUtil.actualizarPersona(persona);
                }

                rootAgendaView.setVisible(true);

                int numFilaSeleccionada;
                if (nuevaPersona){
                    tableViewPrevio.getItems().add(persona);
                    numFilaSeleccionada = tableViewPrevio.getItems().size()-1;
                    tableViewPrevio.getSelectionModel().select(numFilaSeleccionada);
                    tableViewPrevio.scrollTo(numFilaSeleccionada);
                }else{
                    numFilaSeleccionada = tableViewPrevio.getSelectionModel().getSelectedIndex();
                    tableViewPrevio.getItems().set(numFilaSeleccionada, persona);
                }

                TablePosition pos = new TablePosition(tableViewPrevio, numFilaSeleccionada, null);
                tableViewPrevio.getFocusModel().focus(pos);
                tableViewPrevio.requestFocus();

            } catch (Exception ex){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("no se han podido guardar los cambios. "
                + "Compruebe que los datos cumplean los requisitos");
                alert.setContentText(ex.getLocalizedMessage());
                alert.showAndWait();
            }
        }

    }

    @FXML
    public void onActionButtonExaminar(ActionEvent event) {
        File carpetaFotos = new File(CARPETA_FOTOS);
        if(!carpetaFotos.exists()){
            carpetaFotos.mkdir();
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes (jpg, png)", "*.jpg", "*.png", "*.gif"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );
        File file = fileChooser.showOpenDialog(
                rootPersonaDetalleView.getScene().getWindow()
        );
        if(file != null){
            try {
                Files.copy(file.toPath(), new File(CARPETA_FOTOS+"/"+file.getName()).toPath());
                persona.setFoto(file.getName());
                Image image = new Image(file.toURI().toString());
                imageViewFoto.setImage(image);
            }catch (FileAlreadyExistsException ex){
                Alert alert = new Alert(Alert.AlertType.WARNING, "Nombre de archivo duplicado");
                alert.showAndWait();
            } catch (IOException ex){
                Alert alert = new Alert(Alert.AlertType.WARNING, "No se ha podido guardar la imagen");
                alert.showAndWait();
            }
        }
    }

    @FXML
    public void onActionSuprimirFoto(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar supresión de imagen");
        alert.setHeaderText("¿Desea SUPRIMIR el archivo asociado a la imagen,\n" +
                "quitar la foto pero MANTENER el archivo, \n" +
                "o CANCELAR la operación?");
        alert.setContentText("Elija la opción deseada");

        ButtonType buttonTypeEliminar = new ButtonType("Suprimir");
        ButtonType buttonTypeMantener = new ButtonType("Mantener");
        ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeEliminar, buttonTypeMantener, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeEliminar) {
            String imageFileName = persona.getFoto();
            File file = new File(CARPETA_FOTOS+"/"+imageFileName);
            if(file.exists()){
                file.delete();
            }
            persona.setFoto(null);
            imageViewFoto.setImage(null);
        } else if (result.get() == buttonTypeMantener) {
            persona.setFoto(null);
            imageViewFoto.setImage(null);
        }
    }
}