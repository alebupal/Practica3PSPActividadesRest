package com.example.alejandro.practica3pspactividadesrest;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Alejandro on 11/02/2015.
 */
public class Adaptador extends ArrayAdapter<Actividad> {

    private Context contexto;
    private ArrayList<Actividad> lista;
    private int recurso;
    private static LayoutInflater i;


    public static class ViewHolder{
        public TextView tvFechaDetalle,tvLugarDetalle,tvTipoDetalle;

        public int posicion;
    }

    public Adaptador(Context context, int resource, ArrayList<Actividad> objects) {
        super(context, resource, objects);
        this.contexto = context;
        this.lista=objects;
        this.recurso=resource;
        this.i=(LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ArrayList getLista() {
        return lista;
    }

    public void setLista(ArrayList lista) {
        this.lista = lista;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder vh = null;
        if (convertView == null) {
            convertView = i.inflate(recurso, null);
            vh = new ViewHolder();
            vh.tvFechaDetalle = (TextView) convertView.findViewById(R.id.tvFechaDetalle);
            vh.tvLugarDetalle = (TextView) convertView.findViewById(R.id.tvLugarDetalle);
            vh.tvTipoDetalle = (TextView) convertView.findViewById(R.id.tvTipoDetalle);
            convertView.setTag(vh);

        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.posicion = position;
        vh.tvFechaDetalle.setText(obtenerFechaHora(lista.get(position).getFechaInicio(),"fecha"));
        vh.tvLugarDetalle.setText(lista.get(position).getLugarSalida() + "");
        vh.tvTipoDetalle.setText(lista.get(position).getTipo() + "");

        return convertView;

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
