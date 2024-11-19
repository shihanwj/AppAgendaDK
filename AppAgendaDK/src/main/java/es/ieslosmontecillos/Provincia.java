package es.ieslosmontecillos;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.xml.bind.annotation.XmlElement;

public class Provincia {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty codigo = new SimpleStringProperty();
    private final StringProperty nombre = new SimpleStringProperty();

    // Campo id
    @XmlElement(name = "id")
    public Integer getID(){
        return id.get();
    }
    public IntegerProperty idProperty(){
        return id;
    }
    public void setID(Integer id){
        this.id.set(id);
    }

    // Campo codigo
    @XmlElement(name = "codigo")
    public String getCodigo(){
        return codigo.get();
    }
    public StringProperty codigoProperty(){
        return codigo;
    }
    public void setCodigo(String codigo){
        this.codigo.set(codigo);
    }

    // Campo nombre
    @XmlElement (name = "nombre")
    public String getNombre(){
        return nombre.get();
    }
    public StringProperty nombreProperty(){
        return nombre;
    }
    public void setNombre(String nombre){
        this.nombre.set(nombre);
    }
}
