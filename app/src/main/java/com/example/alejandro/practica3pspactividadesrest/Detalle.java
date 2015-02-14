package com.example.alejandro.practica3pspactividadesrest;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;


public class Detalle extends Activity {

    private Actividad aElegida = new Actividad();
    private Profesor pElegido = new Profesor();
    private ActividadGrupo agElegido = new ActividadGrupo();

    private LinearLayout lyComplementaria,lyExtrescolar;

    private Grupo gElegido = new Grupo();


    private ArrayList<ActividadGrupo> grupos = new ArrayList<ActividadGrupo>();


    private TextView tvProfesorDetalle, tvDepartamentoDetalle,tvGrupoDetalle,tvDescripcionDetalle,
            tvEFechaiDetalle,tvEHoraiDetalle,tvEFechafDetalle,tvELugariDetalle,tvELugarfDetalle,tvCHoraiDetalle,
            tvEHorafDetalle,tvCFechaDetalle,tvCLugarDetalle,tvCHorafDetalle;
    private String idActividad;
    private final static String URLBASE = "http://ieszv.x10.bz/restful/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        capturarElementos();
        consultarDatos();

    }


    public void capturarElementos(){
        tvProfesorDetalle = (TextView)findViewById(R.id.tvProfesorDetalle);
        tvDepartamentoDetalle = (TextView)findViewById(R.id.tvDepartamentoDetalle);
        tvGrupoDetalle = (TextView)findViewById(R.id.tvGrupoDetalle);
        tvDescripcionDetalle = (TextView)findViewById(R.id.tvDescripcionDetalle);
        lyComplementaria = (LinearLayout)findViewById(R.id.layoutComplementaria);
        lyExtrescolar = (LinearLayout)findViewById(R.id.layoutExtraescolar);
        tvEFechaiDetalle = (TextView)findViewById(R.id.tvEFechaiDetalle);
        tvEFechafDetalle = (TextView)findViewById(R.id.tvEFechafDetalle);
        tvELugariDetalle = (TextView)findViewById(R.id.tvELugariDetalle);
        tvELugarfDetalle = (TextView)findViewById(R.id.tvELugarfDetalle);
        tvEHoraiDetalle = (TextView)findViewById(R.id.tvEHoraiDetalle);
        tvEHorafDetalle = (TextView)findViewById(R.id.tvEHorafDetalle);
        tvCFechaDetalle = (TextView)findViewById(R.id.tvCFechaDetalle);
        tvCLugarDetalle = (TextView)findViewById(R.id.tvCLugarDetalle);
        tvCHoraiDetalle = (TextView)findViewById(R.id.tvCHoraiDetalle);
        tvCHorafDetalle = (TextView)findViewById(R.id.tvCHorafDetalle);


    }
    public void consultarDatos(){
        idActividad = getIntent().getExtras().getString("idActividad");
        GetActividadID get= new GetActividadID();
        get.execute(URLBASE+"actividad/"+idActividad);

    }
    public void asignarDatos(){

        tvProfesorDetalle.setText(pElegido.getNombre());
        tvDepartamentoDetalle.setText(pElegido.getDepartamento());
        tvGrupoDetalle.setText(gElegido.getGrupo());
        tvDescripcionDetalle.setText(aElegida.getDescripcion());


        if(aElegida.getTipo().compareToIgnoreCase("extraescolar")==0){
            tvEFechaiDetalle.setText(obtenerFechaHora(aElegida.getFechaInicio(),"fecha"));
            tvEFechafDetalle.setText(obtenerFechaHora(aElegida.getFechaFinal(),"fecha"));
            tvELugariDetalle.setText(aElegida.getLugarSalida());
            tvELugarfDetalle.setText(aElegida.getLugarRegreso());
            tvEHoraiDetalle.setText(obtenerFechaHora(aElegida.getFechaInicio(),"hora"));
            tvEHorafDetalle.setText(obtenerFechaHora(aElegida.getFechaFinal(),"hora"));

        }else if(aElegida.getTipo().compareToIgnoreCase("complementaria")==0){
            tvCFechaDetalle.setText(obtenerFechaHora(aElegida.getFechaInicio(),"fecha"));
            tvCLugarDetalle.setText(aElegida.getLugarSalida());
            tvCHoraiDetalle.setText(obtenerFechaHora(aElegida.getFechaInicio(),"hora"));
            tvCHorafDetalle.setText(obtenerFechaHora(aElegida.getFechaFinal(),"hora"));
        }

    }


    class GetActividadID extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String[] params) {
            String r = ClienteRestFul.get(params[0]);
            return r;
        }
        @Override
        protected void onPostExecute(String r) {
            super.onPostExecute(r);
            JSONTokener token = new JSONTokener(r);

            Toast.makeText(Detalle.this, r, Toast.LENGTH_SHORT).show();
            try {
                JSONArray array = new JSONArray(token);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    aElegida = new Actividad(object);
                }

            } catch (Exception e) {

            }
            if(aElegida.getTipo().compareToIgnoreCase("extraescolar")==0){
                lyExtrescolar.setVisibility(View.VISIBLE);
                lyComplementaria.setVisibility(View.INVISIBLE);
            }else if(aElegida.getTipo().compareToIgnoreCase("complementaria")==0){
                lyExtrescolar.setVisibility(View.INVISIBLE);
                lyComplementaria.setVisibility(View.VISIBLE);
            }
            GetProfesorID get= new GetProfesorID();
            get.execute(URLBASE+"profesor/"+aElegida.getIdProfesor());
        }
    }
    class GetProfesorID extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String[] params) {
            String r = ClienteRestFul.get(params[0]);
            return r;
        }
        @Override
        protected void onPostExecute(String r) {
            super.onPostExecute(r);
            JSONTokener token = new JSONTokener(r);
            Toast.makeText(Detalle.this, r, Toast.LENGTH_SHORT).show();
            try {
                JSONObject object = new JSONObject(token);
                pElegido = new Profesor(object);

            } catch (Exception e) {
                e.printStackTrace();
            }

            getActividadGrupo get= new getActividadGrupo();
            get.execute(URLBASE+"actividadgrupo/"+aElegida.getId());
        }
    }


    class getActividadGrupo extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String[] params) {
            String r = ClienteRestFul.get(params[0]);
            return r;
        }
        @Override
        protected void onPostExecute(String r) {
            super.onPostExecute(r);
            JSONTokener token = new JSONTokener(r);

            Toast.makeText(Detalle.this, r, Toast.LENGTH_SHORT).show();
            try {
                JSONArray array = new JSONArray(token);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    agElegido = new ActividadGrupo(object);
                    grupos.add(agElegido);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            getGrupoID get= new getGrupoID();
            get.execute(URLBASE+"grupo/"+grupos.get(0).getIdGrupo());


        }
    }
    class getGrupoID extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String[] params) {
            String r = ClienteRestFul.get(params[0]);
            return r;
        }
        @Override
        protected void onPostExecute(String r) {
            super.onPostExecute(r);
            JSONTokener token = new JSONTokener(r);

            Toast.makeText(Detalle.this, r, Toast.LENGTH_SHORT).show();
            try {
                JSONObject object = new JSONObject(token);
                gElegido = new Grupo(object);
            } catch (Exception e) {
                e.printStackTrace();
            }

            asignarDatos();
        }
    }

    public String obtenerFechaHora(String a,String que){
        String devolver=a;
        String[] conjunto=devolver.split(" ");

        if(que.compareToIgnoreCase("fecha")==0){
            devolver=conjunto[0];
        }else if(que.compareToIgnoreCase("hora")==0){
            String[] conjuntoHora=conjunto[1].split(":");
            devolver=conjuntoHora[0]+":"+conjuntoHora[1];
        }
        return  devolver;
    }



}
