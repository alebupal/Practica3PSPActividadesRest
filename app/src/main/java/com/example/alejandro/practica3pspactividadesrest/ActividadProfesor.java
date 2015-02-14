package com.example.alejandro.practica3pspactividadesrest;

import org.json.JSONObject;

/**
 * Created by Alejandro on 14/02/2015.
 */
public class ActividadProfesor {

    private String id, idactividad, idprofesor;

    public ActividadProfesor() {
    }

    public ActividadProfesor(String idactividad, String idprofesor) {
        this.idactividad = idactividad;
        this.idprofesor = idprofesor;
    }

    public ActividadProfesor(JSONObject object) {
        try {
            this.id = object.getString("id");
            this.idactividad=object.getString("idactividad");
            this.idprofesor = object.getString("idprofesor");
        } catch (Exception e) {

        }
    }
    public JSONObject getJSON(){
        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("id",this.id);
            jsonObject.put("idactividad",this.idactividad);
            jsonObject.put("idprofesor",this.idprofesor);
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

    public String getIdactividad() {
        return idactividad;
    }

    public void setIdactividad(String idactividad) {
        this.idactividad = idactividad;
    }

    public String getIdprofesor() {
        return idprofesor;
    }

    public void setIdprofesor(String idprofesor) {
        this.idprofesor = idprofesor;
    }


}