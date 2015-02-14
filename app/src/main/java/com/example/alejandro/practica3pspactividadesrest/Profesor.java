package com.example.alejandro.practica3pspactividadesrest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Alejandro on 11/02/2015.
 */
public class Profesor {

    private String id, nombre,apellidos, departamento;

    public Profesor() {
    }




    public Profesor(JSONObject object) {
        try {
            this.id = object.getString("id");
            this.nombre = object.getString("nombre");
            this.apellidos=object.getString("apellidos");
            this.departamento=object.getString("departamento");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String toString() {
        return this.getNombre()+" "+this.getApellidos();
    }
}
