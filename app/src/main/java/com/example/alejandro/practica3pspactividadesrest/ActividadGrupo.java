package com.example.alejandro.practica3pspactividadesrest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Alejandro on 11/02/2015.
 */
public class ActividadGrupo {

    private String id, idActividad, idGrupo;

    public ActividadGrupo() {
    }

    public ActividadGrupo(String idActividad, String idGrupo) {
        this.idActividad = idActividad;
        this.idGrupo = idGrupo;
    }

    public ActividadGrupo(JSONObject object) {
        try {
            this.id = object.getString("id");
            this.idActividad=object.getString("idactividad");
            this.idGrupo = object.getString("idgrupo");
        } catch (JSONException e) {

        }
    }

    public JSONObject getJSON(){
        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("id",this.id);
            jsonObject.put("idactividad",this.idActividad);
            jsonObject.put("idgrupo",this.idGrupo);
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

    public String getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(String idActividad) {
        this.idActividad = idActividad;
    }

    public String getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(String idGrupo) {
        this.idGrupo = idGrupo;
    }
}
