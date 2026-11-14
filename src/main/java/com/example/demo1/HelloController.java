package com.example.demo1;

import com.example.demo1.model.Taller;
import com.example.demo1.model.TallerPresencial;
import com.example.demo1.model.TallerVirtual;
import com.example.demo1.repository.JsonTallerRepository;
import com.example.demo1.repository.TallerRepository;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class HelloController {

    @FXML private ToggleGroup modalidadGroup;
    @FXML private RadioButton rbPresencial;
    @FXML private RadioButton rbVirtual;

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDuracion;
    @FXML private TextField txtCupos;
    @FXML private TextField txtSala;
    @FXML private CheckBox chkMateriales;
    @FXML private TextField txtPlataforma;
    @FXML private CheckBox chkGrabacion;

    @FXML private Label lblMensaje;
    @FXML private TableView<Taller> tblTalleres;
    @FXML private TableColumn<Taller, String> colCodigo;
    @FXML private TableColumn<Taller, String> colNombre;
    @FXML private TableColumn<Taller, Integer> colDuracion;
    @FXML private TableColumn<Taller, Integer> colCupos;

    private final ObservableList<Taller> listaTalleres = FXCollections.observableArrayList();
    private final TallerRepository repository = new JsonTallerRepository();

    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCodigo()));
        colNombre.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombre()));
        colDuracion.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getDuracionHoras()).asObject());
        colCupos.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getCupos()).asObject());

        tblTalleres.setItems(listaTalleres);

        modalidadGroup.selectedToggleProperty().addListener((obs, viejo, nuevo) -> actualizarEstadoCampos());
        actualizarEstadoCampos();

        // Carga inicial desde JSON
        cargarDatosIniciales();
    }

    private void cargarDatosIniciales() {
        try {
            listaTalleres.setAll(repository.cargarTodos());
            if (listaTalleres.isEmpty()) {
                mostrarMensaje("Sin registros previos. Listo para ingresar talleres.", false);
            } else {
                mostrarMensaje("Se cargaron " + listaTalleres.size() + " talleres desde archivo.", false);
            }
        } catch (IOException e) {
            mostrarMensaje("Error al leer archivos JSON: " + e.getMessage(), true);
        }
    }

    private void actualizarEstadoCampos() {
        boolean esPresencial = rbPresencial.isSelected();

        txtSala.setDisable(!esPresencial);
        chkMateriales.setDisable(!esPresencial);

        txtPlataforma.setDisable(esPresencial);
        chkGrabacion.setDisable(esPresencial);

        if (esPresencial) {
            txtPlataforma.clear();
            chkGrabacion.setSelected(false);
        } else {
            txtSala.clear();
            chkMateriales.setSelected(false);
        }
    }

    @FXML
    protected void onHelloButtonClick() {
        try {
            String codigo = txtCodigo.getText();
            String nombre = txtNombre.getText();
            int duracion = Integer.parseInt(txtDuracion.getText().trim());
            int cupos = Integer.parseInt(txtCupos.getText().trim());

            boolean existe = listaTalleres.stream()
                    .anyMatch(t -> t.getCodigo().equalsIgnoreCase(codigo.trim()));
            if (existe) {
                mostrarMensaje("Error: Ya existe un taller con el código " + codigo.trim(), true);
                return;
            }

            Taller nuevoTaller;
            if (rbPresencial.isSelected()) {
                nuevoTaller = new TallerPresencial(
                        codigo, nombre, duracion, cupos,
                        txtSala.getText(), chkMateriales.isSelected()
                );
            } else {
                nuevoTaller = new TallerVirtual(
                        codigo, nombre, duracion, cupos,
                        txtPlataforma.getText(), chkGrabacion.isSelected()
                );
            }

            listaTalleres.add(nuevoTaller);
            repository.guardarTodos(listaTalleres);

            limpiarFormulario();
            mostrarMensaje("Taller guardado. Arancel: $" + nuevoTaller.calcularArancel(), false);

        } catch (NumberFormatException e) {
            // Los campos no se limpian para que el usuario corrija el dato
            mostrarMensaje("Error: Duración y Cupos deben ser números enteros válidos.", true);
        } catch (IllegalArgumentException e) {
            // Los campos no se limpian para que el usuario corrija el dato
            mostrarMensaje("Error: " + e.getMessage(), true);
        } catch (IOException e) {
            mostrarMensaje("Error al guardar en disco: " + e.getMessage(), true);
        }
    }

    @FXML
    protected void onEliminarClick() {
        Taller seleccionado = tblTalleres.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarMensaje("Selecciona un taller de la tabla para eliminar.", true);
            return;
        }

        try {
            listaTalleres.remove(seleccionado);
            repository.guardarTodos(listaTalleres);
            mostrarMensaje("Taller '" + seleccionado.getNombre() + "' eliminado y guardado.", false);
        } catch (IOException e) {
            // Revertir en memoria si falló la escritura
            listaTalleres.add(seleccionado);
            mostrarMensaje("Error al actualizar el archivo JSON: " + e.getMessage(), true);
        }
    }

    private void limpiarFormulario() {
        txtCodigo.clear();
        txtNombre.clear();
        txtDuracion.clear();
        txtCupos.clear();
        txtSala.clear();
        chkMateriales.setSelected(false);
        txtPlataforma.clear();
        chkGrabacion.setSelected(false);
        txtCodigo.requestFocus();
    }

    private void mostrarMensaje(String texto, boolean esError) {
        lblMensaje.setText(texto);
        if (esError) {
            lblMensaje.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 12px; -fx-font-weight: bold;");
        } else {
            lblMensaje.setStyle("-fx-text-fill: #16a34a; -fx-font-size: 12px; -fx-font-weight: bold;");
        }
    }
}