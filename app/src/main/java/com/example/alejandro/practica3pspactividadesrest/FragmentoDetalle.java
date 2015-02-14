package com.example.alejandro.practica3pspactividadesrest;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;


public class FragmentoDetalle extends Fragment {
    private View v;

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


    public FragmentoDetalle(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_fragmento_detalle, container, false);
        return v;
    }

    public void iniciar(String a){
        capturarElementos();
        idActividad=a;
        Log.v("id", idActividad + "");
        GetActividadID get= new GetActividadID();
        get.execute(URLBASE+"actividad/"+idActividad);
    }

    public void capturarElementos(){
        tvProfesorDetalle = (TextView)v.findViewById(R.id.tvProfesorDetalle);
        tvDepartamentoDetalle = (TextView)v.findViewById(R.id.tvDepartamentoDetalle);
        tvGrupoDetalle = (TextView)v.findViewById(R.id.tvGrupoDetalle);
        tvDescripcionDetalle = (TextView)v.findViewById(R.id.tvDescripcionDetalle);
        lyComplementaria = (LinearLayout)v.findViewById(R.id.layoutComplementaria);
        lyExtrescolar = (LinearLayout)v.findViewById(R.id.layoutExtraescolar);
        tvEFechaiDetalle = (TextView)v.findViewById(R.id.tvEFechaiDetalle);
        tvEFechafDetalle = (TextView)v.findViewById(R.id.tvEFechafDetalle);
        tvELugariDetalle = (TextView)v.findViewById(R.id.tvELugariDetalle);
        tvELugarfDetalle = (TextView)v.findViewById(R.id.tvELugarfDetalle);
        tvEHoraiDetalle = (TextView)v.findViewById(R.id.tvEHoraiDetalle);
        tvEHorafDetalle = (TextView)v.findViewById(R.id.tvEHorafDetalle);
        tvCFechaDetalle = (TextView)v.findViewById(R.id.tvCFechaDetalle);
        tvCLugarDetalle = (TextView)v.findViewById(R.id.tvCLugarDetalle);
        tvCHoraiDetalle = (TextView)v.findViewById(R.id.tvCHoraiDetalle);
        tvCHorafDetalle = (TextView)v.findViewById(R.id.tvCHorafDetalle);


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
            tvCLugarDetalle.setText(aElegida.getFechaInicio());
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
            try {
                JSONObject object = new JSONObject(token);
                pElegido = new Profesor(object);

            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.v("apellidos", pElegido.getApellidos() + "");
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
            Log.v("enlace", URLBASE+"grupo/"+grupos.get(0).getIdGrupo() + "");

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