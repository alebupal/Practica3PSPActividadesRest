package com.example.alejandro.practica3pspactividadesrest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Alejandro on 11/12/2014.
 */
public class Actividad implements Serializable{

    private String id, idProfesor, tipo,fechaInicio,fechaFinal,lugarSalida,lugarRegreso,descripcion,alumno;

    public Actividad() {
    }


    public Actividad(String id, String idProfesor, String tipo, String fechaInicio, String fechaFinal, String lugarSalida, String lugarRegreso, String descripcion, String alumno) {
        this.id = id;
        this.idProfesor = idProfesor;
        this.tipo = tipo;
        this.fechaInicio = fechaInicio;
        this.fechaFinal = fechaFinal;
        this.lugarSalida = lugarSalida;
        this.lugarRegreso = lugarRegreso;
        this.descripcion = descripcion;
        this.alumno = alumno;
    }

    public Actividad(JSONObject object) {
        try {
            this.id = object.getString("id");
            this.idProfesor = object.getString("idprofesor");
            this.tipo = object.getString("tipo");
            this.fechaInicio = object.getString("fechai");
            this.fechaFinal = object.getString("fechaf");
            this.lugarSalida = object.getString("lugari");
            this.lugarRegreso = object.getString("lugarf");
            this.descripcion = object.getString("descripcion");
            this.alumno = object.getString("alumno");
        }catch (JSONException e){

        }
    }
    public JSONObject getJSON(){
        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("id",this.id);
            jsonObject.put("idprofesor",this.idProfesor);
            jsonObject.put("tipo",this.tipo);
            jsonObject.put("fechai",this.fechaInicio);
            jsonObject.put("fechaf",this.fechaFinal);
            jsonObject.put("lugari",this.lugarSalida);
            jsonObject.put("lugarf",this.lugarRegreso);
            jsonObject.put("descripcion",this.descripcion);
            jsonObject.put("alumno",this.alumno);
            return jsonObject;
        }catch (Exception e){
            return null;
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdProfesor() {
        return idProfesor;
    }

    public void setIdProfesor(String idProfesor) {
        this.idProfesor = idProfesor;
    }


    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(String fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public String getLugarSalida() {
        return lugarSalida;
    }

    public void setLugarSalida(String lugarSalida) {
        this.lugarSalida = lugarSalida;
    }

    public String getLugarRegreso() {
        return lugarRegreso;
    }

    public void setLugarRegreso(String lugarRegreso) {
        this.lugarRegreso = lugarRegreso;
    }

    public String getAlumno() {
        return alumno;
    }

    public void setAlumno(String alumno) {
        this.alumno = alumno;
    }
}
