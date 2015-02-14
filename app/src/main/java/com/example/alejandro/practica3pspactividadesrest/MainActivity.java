package com.example.alejandro.practica3pspactividadesrest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class MainActivity extends Activity {
    private ArrayList<Actividad> datos = new ArrayList<Actividad>();
    private Adaptador ad;
    private final int ACTIVIDAD_DETALLE = 2;
    private final static String URLBASE = "http://ieszv.x10.bz/restful/api/";
    private Calendar fechaactual;
    private  ListView ls;
    private String fecha,hora;
    private LinearLayout layoutdetalle;
    private ImageView alguna;

    /*Añadir*/
    private Button btAnadirCPFecha,btAnadirCPHorai,btAnadirCPHoraf,btAnadirEXFechai,btAnadirEXFechaf,btAnadirEXHorai,btAnadirEXHoraf;
    private EditText etAnadirDescripcion,etAnadirCPLugar,etAnadirEXLugarS,etAnadirEXLugarR,etAnadirDepartamento;
    private Spinner spAnadirGrupo,spAnadirProfesor;
    private DatePickerDialog dpdAnadir;
    private TimePickerDialog tpdAnadir;
    private LinearLayout lyAnadirComplementaria,lyAnadirExtrescolar;
    private String idGrupo,idProfesor,idActividadAnadida,idActividadGrupo,idActividadProfesor;
    private String tipoAnadirActividad="complementaria";
    private ArrayList<Profesor> lProfesores = new ArrayList<Profesor>();
    private ArrayList<Grupo> lGrupos = new ArrayList<Grupo>();
    private ArrayList<ActividadGrupo> laGrupos = new ArrayList<ActividadGrupo>();

    /*Editar*/
    private Button btEditarCPFecha,btEditarCPHorai,btEditarCPHoraf,btEditarEXFechai,btEditarEXFechaf,btEditarEXHorai,btEditarEXHoraf;
    private EditText etEditarDescripcion,etEditarCPLugar,etEditarEXLugarS,etEditarEXLugarR,etEditarDepartamento;
    private Spinner spEditarGrupo,spEditarProfesor;
    private LinearLayout lyEditarComplementaria,lyEditarExtrescolar;
    private String tipoEditarActividad="complementaria";
    private DatePickerDialog dpdEditar;
    private TimePickerDialog tpdEditar;
    private RadioButton rbEditarComplementaria,rbEditarExtraescolar;
    private int idLaGrupos;
    private int posicionSpinnerProfesor,posicionSpinnerGrupo,indexspinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fechaactual = new GregorianCalendar();
        cargarActividades();
    }
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        if (id == R.id.action_borrar) {
            borrar(index);
        } else if (id == R.id.action_editar) {
            editar(index);
        }
        return super.onContextItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contextual, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.m_anadir) {
            anadir();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    /* REST Visualizar*/

    public void cargarActividades(){
        ls = (ListView) findViewById(R.id.lvActividades);
        datos.clear();
        ad = new Adaptador(this, R.layout.lista_detalle, datos);
        ls.setAdapter(ad);
        registerForContextMenu(ls);

        final FragmentoDetalle fDetalle = (FragmentoDetalle)getFragmentManager().findFragmentById(R.id.fragmentDetalle2);
        final boolean horizontal = fDetalle!=null && fDetalle.isInLayout(); //Saber que orientación tengo

        GetActividades get= new GetActividades();
        get.execute(URLBASE+"actividad/alejandrobp");
        if(horizontal){
            layoutdetalle = (LinearLayout)findViewById(R.id.layoutdetalle);
            alguna = (ImageView)findViewById(R.id.alguna);
            layoutdetalle.setVisibility(View.INVISIBLE);
            alguna.setVisibility(View.VISIBLE);
        }
        ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Actividad act = (Actividad)ls.getItemAtPosition(position);
                view.setSelected(true);
                if(horizontal){
                    layoutdetalle.setVisibility(View.VISIBLE);
                    alguna.setVisibility(View.INVISIBLE);
                    String idActividad=act.getId();
                    fDetalle.iniciar(idActividad);
                }else{
                    Intent i = new Intent(MainActivity.this,Detalle.class);
                    i.putExtra("idActividad",act.getId());
                    startActivityForResult(i, ACTIVIDAD_DETALLE);
                }
            }
        });
    }
    class GetActividades extends AsyncTask<String,Void,String> {

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
                    Actividad a = new Actividad(object);
                    ad.notifyDataSetChanged();
                    datos.add(a);
                }
            } catch (Exception e) {

            }
        }
    }

    /*************BORRAR****************/

    private boolean borrar(final int pos) {
        final String id;
        id = datos.get(pos).getId();
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.tBorrar));
        LayoutInflater inflater = LayoutInflater.from(this);
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                DeleteActividad borraractividad = new DeleteActividad();
                borraractividad.execute(URLBASE+"actividad/"+id);
            }
        });
        alert.setNegativeButton(android.R.string.no, null);
        alert.show();
        return true;
    }
    public class DeleteActividad extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String[] params) {
            String r = ClienteRestFul.delete(params[0]);
            return r;
        }
        @Override
        protected void onPostExecute(String r) {
            super.onPostExecute(r);
            cargarActividades();
        }
    }


    /*************ANADIR****************/

    private boolean anadir() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.menuAnadir));
        LayoutInflater inflater = LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.anadir, null);
        alert.setView(vista);
        capturarAnadir(vista);
        GetProfesoresAnadir get= new GetProfesoresAnadir();
        get.execute(URLBASE+"profesor");

        GetGruposAnadir get2= new GetGruposAnadir();
        get2.execute(URLBASE+"grupo");
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int whichButton) {
                Actividad a = new Actividad();
               if(tipoAnadirActividad.compareToIgnoreCase("complementaria")==0){
                   if(btAnadirCPFecha.getText().toString().trim().compareToIgnoreCase("")==0 ||
                      btAnadirCPHorai.getText().toString().trim().compareToIgnoreCase("")==0 ||
                      btAnadirCPHoraf.getText().toString().trim().compareToIgnoreCase("")==0 ||
                      idProfesor.toString().trim().compareToIgnoreCase("")==0 ||
                      tipoAnadirActividad.toString().trim().compareToIgnoreCase("")==0 ||
                      etAnadirCPLugar.getText().toString().trim().compareToIgnoreCase("")==0 ||
                      etAnadirDescripcion.getText().toString().trim().compareToIgnoreCase("")==0){

                      Toast.makeText(MainActivity.this,getString(R.string.msgVacios),Toast.LENGTH_SHORT).show();

                   }else{
                       String fechaiC=btAnadirCPFecha.getText()+" "+btAnadirCPHorai.getText()+":00";
                       String fechafC="0000-00-00 "+btAnadirCPHoraf.getText()+":00";
                       a = new Actividad(idProfesor,
                               tipoAnadirActividad,
                               fechaiC,
                               fechafC,
                               etAnadirCPLugar.getText().toString(),
                               "",
                               etAnadirDescripcion.getText().toString(),
                               "alejandrobp");
                       PostActividad posta = new PostActividad();
                       ParametrosPost pp = new ParametrosPost();
                       pp.url=URLBASE+"actividad";
                       pp.jsonObject=a.getJSON();
                       posta.execute(pp);
                   }


               }else if (tipoAnadirActividad.compareToIgnoreCase("extraescolar")==0){
                    if(btAnadirEXFechai.getText().toString().trim().compareToIgnoreCase("")==0 ||
                           btAnadirEXHorai.getText().toString().trim().compareToIgnoreCase("")==0 ||
                           btAnadirEXFechaf.getText().toString().trim().compareToIgnoreCase("")==0 ||
                           btAnadirEXFechaf.getText().toString().trim().compareToIgnoreCase("")==0 ||
                           idProfesor.toString().trim().compareToIgnoreCase("")==0 ||
                           tipoAnadirActividad.toString().trim().compareToIgnoreCase("")==0 ||
                           etAnadirEXLugarS.toString().trim().compareToIgnoreCase("")==0 ||
                           etAnadirEXLugarR.toString().trim().compareToIgnoreCase("")==0 ||
                           etAnadirDescripcion.toString().trim().compareToIgnoreCase("")==0) {

                       Toast.makeText(MainActivity.this,getString(R.string.msgVacios),Toast.LENGTH_SHORT).show();

                    }else{

                        String fechaiE=btAnadirEXFechai.getText()+" "+btAnadirEXHorai.getText()+":00";
                        String fechafE=btAnadirEXFechaf.getText()+" "+btAnadirEXFechaf.getText();
                        a = new Actividad(idProfesor,
                                tipoAnadirActividad,
                                fechaiE,
                                fechafE,
                                etAnadirEXLugarS.getText().toString(),
                                etAnadirEXLugarR.getText().toString(),
                                etAnadirDescripcion.getText().toString(),
                                "alejandrobp");
                        PostActividad posta = new PostActividad();
                        ParametrosPost pp = new ParametrosPost();
                        pp.url=URLBASE+"actividad";
                        pp.jsonObject=a.getJSON();
                        posta.execute(pp);

                    }
                }
            }
        });
        alert.setNegativeButton(android.R.string.no, null);
        alert.show();
        return true;
    }
    public void capturarAnadir(View vista){

        lyAnadirComplementaria = (LinearLayout)vista.findViewById(R.id.layoutAnadirComplementaria);
        lyAnadirExtrescolar = (LinearLayout)vista.findViewById(R.id.layoutAnadirExtraescolar);
        etAnadirDescripcion = (EditText)vista.findViewById(R.id.etAnadirDescripcion);
        etAnadirDepartamento = (EditText)vista.findViewById(R.id.etAnadirDepartamento);

        spAnadirGrupo = (Spinner)vista.findViewById(R.id.spAnadirGrupo);
        spAnadirProfesor=(Spinner)vista.findViewById(R.id.spAnadirProfesor);

        /*Complementaria*/
        btAnadirCPFecha=(Button)vista.findViewById(R.id.btAnadirCPFecha);
        btAnadirCPHorai=(Button)vista.findViewById(R.id.btAnadirCPHorai);
        btAnadirCPHoraf=(Button)vista.findViewById(R.id.btAnadirCPHoraf);
        etAnadirCPLugar = (EditText)vista.findViewById(R.id.etAnadirCPLugar);

        /*Extraescolar*/
        btAnadirEXFechai=(Button)vista.findViewById(R.id.btAnadirEXFechai);
        btAnadirEXFechaf=(Button)vista.findViewById(R.id.btAnadirEXFechaf);
        btAnadirEXHorai=(Button)vista.findViewById(R.id.btAnadirEXHorai);
        btAnadirEXHoraf=(Button)vista.findViewById(R.id.btAnadirEXHoraf);
        etAnadirEXLugarS = (EditText)vista.findViewById(R.id.etAnadirEXLugarS);
        etAnadirEXLugarR = (EditText)vista.findViewById(R.id.etAnadirEXLugarR);
        etAnadirDepartamento.setText("Aaaaaaaaaaaaaaaaa");

    }
    public void onRadioButtonClickedAnadir(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.rbAnadirComplementaria:
                if (checked)
                btAnadirCPFecha.setText("");
                btAnadirCPHorai.setText("");
                btAnadirCPHoraf.setText("");
                etAnadirCPLugar.setText("");
                lyAnadirExtrescolar.setVisibility(View.INVISIBLE);
                lyAnadirComplementaria.setVisibility(View.VISIBLE);
                tipoAnadirActividad="complementaria";
                break;
            case R.id.rbAnadirExtraescolar:
                if (checked)
                btAnadirEXFechai.setText("");
                btAnadirEXFechaf.setText("");
                btAnadirEXHorai.setText("");
                btAnadirEXHoraf.setText("");
                etAnadirEXLugarR.setText("");
                etAnadirEXLugarS.setText("");
                lyAnadirExtrescolar.setVisibility(View.VISIBLE);
                lyAnadirComplementaria.setVisibility(View.INVISIBLE);
                tipoAnadirActividad="extraescolar";
                break;
        }
    }

    /* Fechas horas Complementaria Añadir*/

    public class ObtenercpfechaAnadir implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year,int monthOfYear,int dayOfMonth) {

            fecha=year+"-"+(monthOfYear+1)+"-"+dayOfMonth+"";
            btAnadirCPFecha.setText(fecha);
        }
    }
    public void cpfechaAnadir(View v){
        dpdAnadir = new DatePickerDialog(this,new ObtenercpfechaAnadir(), fechaactual.get(Calendar.YEAR), fechaactual.get(Calendar.MONTH), fechaactual.get(Calendar.DAY_OF_MONTH));
        dpdAnadir.show();
    }
    public class ObtenercphoraiAnadir implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(TimePicker tp, int hour, int minute){
            hora=hour+":"+minute;
            btAnadirCPHorai.setText(hora);
        }
    }
    public void cphoraiAnadir(View v){
        tpdAnadir = new TimePickerDialog(this,new ObtenercphoraiAnadir(), fechaactual.get(Calendar.HOUR_OF_DAY), fechaactual.get(Calendar.MINUTE), true);
        tpdAnadir.show();

    }
    public class ObtenercphorafAnadir implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(TimePicker tp, int hour, int minute){
            hora=hour+":"+minute;
            btAnadirCPHoraf.setText(hora);
        }
    }
    public void cphorafAnadir(View v){
        tpdAnadir = new TimePickerDialog(this,new ObtenercphorafAnadir(), fechaactual.get(Calendar.HOUR_OF_DAY), fechaactual.get(Calendar.MINUTE), true);
        tpdAnadir.show();
    }

    /*Fechas horas Extraexcolar Añadir*/

    public class ObtenerexfechaiAnadir implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year,int monthOfYear,int dayOfMonth) {

            fecha=year+"-"+(monthOfYear+1)+"-"+dayOfMonth+"";
            btAnadirEXFechai.setText(fecha);
        }
    }
    public void exfechaiAnadir(View v){
        dpdAnadir = new DatePickerDialog(this,new ObtenerexfechaiAnadir(), fechaactual.get(Calendar.YEAR), fechaactual.get(Calendar.MONTH), fechaactual.get(Calendar.DAY_OF_MONTH));
        dpdAnadir.show();
    }
    public class ObtenerexfechafAnadir implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year,int monthOfYear,int dayOfMonth) {

            fecha=year+"-"+(monthOfYear+1)+"-"+dayOfMonth+"";
            btAnadirEXFechaf.setText(fecha);
        }
    }
    public void exfechafAnadir(View v){
        dpdAnadir = new DatePickerDialog(this,new ObtenerexfechafAnadir(), fechaactual.get(Calendar.YEAR), fechaactual.get(Calendar.MONTH), fechaactual.get(Calendar.DAY_OF_MONTH));
        dpdAnadir.show();
    }
    public class ObtenerexhorafAnadir implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(TimePicker tp, int hour, int minute){
            hora=hour+":"+minute;
            btAnadirEXHoraf.setText(hora);
        }
    }
    public void exhorafAnadir(View v){
        tpdAnadir = new TimePickerDialog(this,new ObtenerexhorafAnadir(), fechaactual.get(Calendar.HOUR_OF_DAY), fechaactual.get(Calendar.MINUTE), true);
        tpdAnadir.show();
    }
    public class ObtenerexhoraiAnadir implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(TimePicker tp, int hour, int minute){
            hora=hour+":"+minute;
            btAnadirEXHorai.setText(hora);
        }
    }
    public void exhoraiAnadir(View v){
        tpdAnadir = new TimePickerDialog(this,new ObtenerexhoraiAnadir(), fechaactual.get(Calendar.HOUR_OF_DAY), fechaactual.get(Calendar.MINUTE), true);
        tpdAnadir.show();
    }

    /* REST */

    class GetProfesoresAnadir extends AsyncTask<String,Void,String> {

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
                    Profesor p = new Profesor(object);
                    lProfesores.add(p);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ArrayAdapter<Profesor> dataAdapter = new ArrayAdapter<Profesor>(MainActivity.this, android.R.layout.simple_spinner_item, lProfesores);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spAnadirProfesor.setAdapter(dataAdapter);

            spAnadirProfesor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    idProfesor = ((Profesor) parent.getItemAtPosition(pos)).getId();
                    etAnadirDepartamento.setText(((Profesor) parent.getItemAtPosition(pos)).getDepartamento());
                }
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }
    class GetGruposAnadir extends AsyncTask<String,Void,String> {

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
                    Grupo g = new Grupo(object);
                    lGrupos.add(g);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            ArrayAdapter<Grupo> dataAdapter = new ArrayAdapter<Grupo>(MainActivity.this, android.R.layout.simple_spinner_item, lGrupos);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spAnadirGrupo.setAdapter(dataAdapter);

            spAnadirGrupo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    idGrupo = ((Grupo) parent.getItemAtPosition(pos)).getId();
                }
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }
    class PostActividad extends AsyncTask<ParametrosPost,Void,String> {
        @Override
        protected String doInBackground(ParametrosPost[] params) {
            idActividadAnadida=ClienteRestFul.post(params[0].url,params[0].jsonObject);
            return idActividadAnadida;
        }
        @Override
        protected void onPostExecute(String r) {
            super.onPostExecute(r);
            idActividadAnadida=idActividadAnadida.substring(5,idActividadAnadida.length()-1);
            if (idActividadAnadida.compareTo("0")==0){
                Toast.makeText(MainActivity.this,getString(R.string.msgError),Toast.LENGTH_SHORT).show();
            }else {
                ActividadGrupo ag = new ActividadGrupo(idActividadAnadida,idGrupo);
                PostActividadGrupo postag = new PostActividadGrupo();
                ParametrosPost pp = new ParametrosPost();
                pp.url=URLBASE+"actividadgrupo";
                pp.jsonObject=ag.getJSON();
                postag.execute(pp);
            }
        }
    }
    class PostActividadGrupo extends AsyncTask<ParametrosPost,Void,String> {
        @Override
        protected String doInBackground(ParametrosPost[] params) {
            idActividadGrupo=ClienteRestFul.post(params[0].url,params[0].jsonObject);
            return idActividadGrupo;
        }
        @Override
        protected void onPostExecute(String r) {
            super.onPostExecute(r);
            if (idActividadAnadida.compareTo("0")==0){
                Toast.makeText(MainActivity.this,getString(R.string.msgError),Toast.LENGTH_SHORT).show();
            }else {

                Toast.makeText(MainActivity.this,getString(R.string.msgBien),Toast.LENGTH_SHORT).show();
                ActividadProfesor ap = new ActividadProfesor(idActividadAnadida,""+idProfesor);
                PostActividadProfesor postap = new PostActividadProfesor();
                ParametrosPost pp = new ParametrosPost();
                pp.url=URLBASE+"actividadprofesor";
                pp.jsonObject=ap.getJSON();
                postap.execute(pp);
            }
        }
    }
    class PostActividadProfesor extends AsyncTask<ParametrosPost,Void,String> {
        @Override
        protected String doInBackground(ParametrosPost[] params) {
            idActividadProfesor=ClienteRestFul.post(params[0].url,params[0].jsonObject);
            return idActividadProfesor;
        }
        @Override
        protected void onPostExecute(String r) {
            super.onPostExecute(r);
            if (idActividadProfesor.compareTo("0")==0){
                Toast.makeText(MainActivity.this,getString(R.string.msgError),Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(MainActivity.this,getString(R.string.msgBien),Toast.LENGTH_SHORT).show();
                cargarActividades();
            }

        }
    }

    /*************EDITAR****************/

    private boolean editar(final int index) {
        final String id;
        id = datos.get(index).getId();
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.mEditar));
        LayoutInflater inflater = LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.editar, null);
        alert.setView(vista);
        capturarEditar(vista);

        indexspinner=index;
        GetProfesoresEditar get= new GetProfesoresEditar();
        get.execute(URLBASE+"profesor");

        comprobarTipo(index);
        valoresTipoActividad(index);

        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                Actividad a = new Actividad();
                if(tipoEditarActividad.compareToIgnoreCase("complementaria")==0){
                    if(btEditarCPFecha.getText().toString().trim().compareToIgnoreCase("")==0 ||
                            btEditarCPHorai.getText().toString().trim().compareToIgnoreCase("")==0 ||
                            btEditarCPHoraf.getText().toString().trim().compareToIgnoreCase("")==0 ||
                            idProfesor.toString().trim().compareToIgnoreCase("")==0 ||
                            tipoEditarActividad.toString().trim().compareToIgnoreCase("")==0 ||
                            etEditarCPLugar.getText().toString().trim().compareToIgnoreCase("")==0 ||
                            etEditarDescripcion.getText().toString().trim().compareToIgnoreCase("")==0){

                        Toast.makeText(MainActivity.this,getString(R.string.msgVacios),Toast.LENGTH_SHORT).show();

                    }else {
                        DeleteActividad borraractividad = new DeleteActividad();
                        borraractividad.execute(URLBASE+"actividad/"+id);
                        String fechaiC = btEditarCPFecha.getText() + " " + btEditarCPHorai.getText() + ":00";
                        String fechafC = "0000-00-00 " + btEditarCPHoraf.getText() + ":00";
                        a = new Actividad(idProfesor,
                                tipoEditarActividad,
                                fechaiC,
                                fechafC,
                                etEditarCPLugar.getText().toString(),
                                "",
                                etEditarDescripcion.getText().toString(),
                                "alejandrobp");
                        PostActividad posta = new PostActividad();
                        ParametrosPost pp = new ParametrosPost();
                        pp.url=URLBASE+"actividad";
                        pp.jsonObject=a.getJSON();
                        posta.execute(pp);
                    }
                }else if (tipoEditarActividad.compareToIgnoreCase("extraescolar")==0){
                    if(btEditarCPFecha.getText().toString().trim().compareToIgnoreCase("")==0 ||
                            btEditarEXFechai.getText().toString().trim().compareToIgnoreCase("")==0 ||
                            btEditarEXHorai.getText().toString().trim().compareToIgnoreCase("")==0 ||
                            btEditarEXFechaf.toString().trim().compareToIgnoreCase("")==0 ||
                            btEditarEXHoraf.toString().trim().compareToIgnoreCase("")==0 ||
                            idProfesor.toString().trim().compareToIgnoreCase("")==0 ||
                            tipoEditarActividad.toString().trim().compareToIgnoreCase("")==0 ||
                            etEditarEXLugarS.getText().toString().trim().compareToIgnoreCase("")==0 ||
                            etEditarEXLugarR.getText().toString().trim().compareToIgnoreCase("")==0 ||
                            etEditarDescripcion.getText().toString().trim().compareToIgnoreCase("")==0){

                        Toast.makeText(MainActivity.this,getString(R.string.msgVacios),Toast.LENGTH_SHORT).show();

                    }else {
                        DeleteActividad borraractividad = new DeleteActividad();
                        borraractividad.execute(URLBASE+"actividad/"+id);
                        String fechaiE = btEditarEXFechai.getText() + " " + btEditarEXHorai.getText() + ":00";
                        String fechafE = btEditarEXFechaf.getText() + " " + btEditarEXHoraf.getText();
                        a = new Actividad(idProfesor,
                                tipoEditarActividad,
                                fechaiE,
                                fechafE,
                                etEditarEXLugarS.getText().toString(),
                                etEditarEXLugarR.getText().toString(),
                                etEditarDescripcion.getText().toString(),
                                "alejandrobp");
                        PostActividad posta = new PostActividad();
                        ParametrosPost pp = new ParametrosPost();
                        pp.url=URLBASE+"actividad";
                        pp.jsonObject=a.getJSON();
                        posta.execute(pp);
                    }
                }



            }
        });
        alert.setNegativeButton(android.R.string.no, null);
        alert.show();
        return true;
    }
    public void capturarEditar(View vista){
        lyEditarExtrescolar = (LinearLayout)vista.findViewById(R.id.layoutEditarExtraescolar);
        lyEditarComplementaria = (LinearLayout)vista.findViewById(R.id.layoutEditarComplementaria);
        etEditarDescripcion = (EditText)vista.findViewById(R.id.etEditarDescripcion);
        etEditarDepartamento = (EditText)vista.findViewById(R.id.etEditarDepartamento);

        spEditarGrupo = (Spinner)vista.findViewById(R.id.spEditarGrupo);
        spEditarProfesor=(Spinner)vista.findViewById(R.id.spEditarProfesor);

        rbEditarComplementaria = (RadioButton)vista.findViewById(R.id.rbEditarComplementaria);
        rbEditarExtraescolar = (RadioButton)vista.findViewById(R.id.rbEditarExtraescolar);
        /*Complementaria*/
        btEditarCPFecha=(Button)vista.findViewById(R.id.btEditarCPFecha);
        btEditarCPHorai=(Button)vista.findViewById(R.id.btEditarCPHorai);
        btEditarCPHoraf=(Button)vista.findViewById(R.id.btEditarCPHoraf);
        etEditarCPLugar = (EditText)vista.findViewById(R.id.etEditarCPLugar);

        /*Extraescolar*/
        btEditarEXFechai=(Button)vista.findViewById(R.id.btEditarEXFechai);
        btEditarEXFechaf=(Button)vista.findViewById(R.id.btEditarEXFechaf);
        btEditarEXHorai=(Button)vista.findViewById(R.id.btEditarEXHorai);
        btEditarEXHoraf=(Button)vista.findViewById(R.id.btEditarEXHoraf);
        etEditarEXLugarS = (EditText)vista.findViewById(R.id.etEditarEXLugarS);
        etEditarEXLugarR = (EditText)vista.findViewById(R.id.etEditarEXLugarR);
    }
    public void onRadioButtonClickedEditar(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.rbEditarComplementaria:
                if (checked)
                btEditarCPFecha.setText("");
                btEditarCPHorai.setText("");
                btEditarCPHoraf.setText("");
                etEditarCPLugar.setText("");
                lyEditarExtrescolar.setVisibility(View.INVISIBLE);
                lyEditarComplementaria.setVisibility(View.VISIBLE);
                tipoEditarActividad="complementaria";
                break;
            case R.id.rbEditarExtraescolar:
                if (checked)
                btEditarEXFechai.setText("");
                btEditarEXFechaf.setText("");
                btEditarEXHorai.setText("");
                btEditarEXHoraf.setText("");
                etEditarEXLugarR.setText("");
                etEditarEXLugarS.setText("");
                lyEditarExtrescolar.setVisibility(View.VISIBLE);
                lyEditarComplementaria.setVisibility(View.INVISIBLE);
                tipoEditarActividad="extraescolar";
                break;
        }
    }
    public void comprobarTipo(int index){
        if(datos.get(index).getTipo().compareToIgnoreCase("complementaria")==0){
            rbEditarExtraescolar.setChecked(false);
            rbEditarComplementaria.setChecked(true);
            btEditarCPFecha.setText("");
            btEditarCPHorai.setText("");
            btEditarCPHoraf.setText("");
            etEditarCPLugar.setText("");
            lyEditarExtrescolar.setVisibility(View.INVISIBLE);
            lyEditarComplementaria.setVisibility(View.VISIBLE);
            tipoEditarActividad="complementaria";
        }else if(datos.get(index).getTipo().compareToIgnoreCase("extraescolar")==0){
            rbEditarExtraescolar.setChecked(true);
            rbEditarComplementaria.setChecked(false);
            btEditarEXFechai.setText("");
            btEditarEXFechaf.setText("");
            btEditarEXHorai.setText("");
            btEditarEXHoraf.setText("");
            etEditarEXLugarR.setText("");
            etEditarEXLugarS.setText("");
            lyEditarExtrescolar.setVisibility(View.VISIBLE);
            lyEditarComplementaria.setVisibility(View.INVISIBLE);
            tipoEditarActividad="extraescolar";
        }
    }
    public void posicionIniciarSpinner(int index){
        for (int i = 0; i <lProfesores.size() ; i++) {
            if(lProfesores.get(i).getId().compareToIgnoreCase(datos.get(index).getIdProfesor())==0){
                posicionSpinnerProfesor=i;
            }
        }
        Log.v("posicionSpinnerProfesor",posicionSpinnerProfesor+"");
        spEditarProfesor.setSelection(posicionSpinnerProfesor);

        for (int i = 0; i < laGrupos.size(); i++) {
            if(datos.get(index).getId().compareToIgnoreCase(laGrupos.get(i).getIdActividad())==0){
                idLaGrupos=i;
            }
        }
        for (int i = 0; i <lGrupos.size() ; i++) {
            if(lGrupos.get(i).getId().compareToIgnoreCase(laGrupos.get(idLaGrupos).getIdGrupo())==0){
                posicionSpinnerGrupo=i;
            }
        }
        spEditarGrupo.setSelection(posicionSpinnerGrupo);

        Log.v("posicionSpinnerGrupo",posicionSpinnerGrupo+"");
    }
    public void valoresTipoActividad(int index){
        etEditarDescripcion.setText(datos.get(index).getDescripcion());
        if(datos.get(index).getTipo().compareToIgnoreCase("complementaria")==0){
            btEditarCPFecha.setText(obtenerFechaHora(datos.get(index).getFechaInicio(),"fecha"));
            etEditarCPLugar.setText(datos.get(index).getLugarSalida());
            btEditarCPHorai.setText(obtenerFechaHora(datos.get(index).getFechaInicio(),"hora"));
            btEditarCPHoraf.setText(obtenerFechaHora(datos.get(index).getFechaFinal(),"hora"));
        }else if(datos.get(index).getTipo().compareToIgnoreCase("extraescolar")==0){
            btEditarEXFechai.setText(obtenerFechaHora(datos.get(index).getFechaInicio(),"fecha"));
            btEditarEXFechaf.setText(obtenerFechaHora(datos.get(index).getFechaFinal(),"fecha"));
            etEditarEXLugarS.setText(datos.get(index).getLugarSalida());
            etEditarEXLugarR.setText(datos.get(index).getLugarRegreso());
            btEditarEXHorai.setText(obtenerFechaHora(datos.get(index).getFechaInicio(),"hora"));
            btEditarEXHoraf.setText(obtenerFechaHora(datos.get(index).getFechaFinal(),"hora"));
        }
    }

    /*REST*/
    class GetProfesoresEditar extends AsyncTask<String,Void,String> {

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
                    Profesor p = new Profesor(object);
                    lProfesores.add(p);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ArrayAdapter<Profesor> dataAdapter = new ArrayAdapter<Profesor>(MainActivity.this, android.R.layout.simple_spinner_item, lProfesores);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spEditarProfesor.setAdapter(dataAdapter);

            spEditarProfesor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    idProfesor = ((Profesor) parent.getItemAtPosition(pos)).getId();
                    etEditarDepartamento.setText(((Profesor) parent.getItemAtPosition(pos)).getDepartamento());
                }
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            GetGruposEditar get2= new GetGruposEditar();
            get2.execute(URLBASE+"grupo");
        }
    }
    class GetGruposEditar extends AsyncTask<String,Void,String> {

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
                    Grupo g = new Grupo(object);
                    lGrupos.add(g);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            ArrayAdapter<Grupo> dataAdapter = new ArrayAdapter<Grupo>(MainActivity.this, android.R.layout.simple_spinner_item, lGrupos);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spEditarGrupo.setAdapter(dataAdapter);

            spEditarGrupo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    idGrupo = ((Grupo) parent.getItemAtPosition(pos)).getId();
                }
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            GetActividadGruposEditar get3= new GetActividadGruposEditar();
            get3.execute(URLBASE+"actividadgrupo");
        }
    }
    class GetActividadGruposEditar extends AsyncTask<String,Void,String> {

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
                    ActividadGrupo ag = new ActividadGrupo(object);
                    laGrupos.add(ag);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            posicionIniciarSpinner(indexspinner);
        }
    }


    /* Fechas horas Complementaria Editar*/

    public class ObtenercpfechaEditar implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year,int monthOfYear,int dayOfMonth) {

            fecha=year+"-"+(monthOfYear+1)+"-"+dayOfMonth+"";
            btEditarCPFecha.setText(fecha);
        }
    }
    public void cpfechaEditar(View v){
        dpdEditar = new DatePickerDialog(this,new ObtenercpfechaEditar(), fechaactual.get(Calendar.YEAR), fechaactual.get(Calendar.MONTH), fechaactual.get(Calendar.DAY_OF_MONTH));
        dpdEditar.show();
    }
    public class ObtenercphoraiEditar implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(TimePicker tp, int hour, int minute){
            hora=hour+":"+minute;
            btEditarCPHorai.setText(hora);
        }
    }
    public void cphoraiEditar(View v){
        tpdEditar = new TimePickerDialog(this,new ObtenercphoraiEditar(), fechaactual.get(Calendar.HOUR_OF_DAY), fechaactual.get(Calendar.MINUTE), true);
        tpdEditar.show();

    }
    public class ObtenercphorafEditar implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(TimePicker tp, int hour, int minute){
            hora=hour+":"+minute;
            btEditarCPHoraf.setText(hora);
        }
    }
    public void cphorafEditar(View v){
        tpdEditar = new TimePickerDialog(this,new ObtenercphorafEditar(), fechaactual.get(Calendar.HOUR_OF_DAY), fechaactual.get(Calendar.MINUTE), true);
        tpdEditar.show();
    }

    /*Fechas horas Extraexcolar Editar*/

    public class ObtenerexfechaiEditar implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year,int monthOfYear,int dayOfMonth) {

            fecha=year+"-"+(monthOfYear+1)+"-"+dayOfMonth+"";
            btEditarEXFechai.setText(fecha);
        }
    }
    public void exfechaiEditar(View v){
        dpdEditar = new DatePickerDialog(this,new ObtenerexfechaiEditar(), fechaactual.get(Calendar.YEAR), fechaactual.get(Calendar.MONTH), fechaactual.get(Calendar.DAY_OF_MONTH));
        dpdEditar.show();
    }
    public class ObtenerexfechafEditar implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year,int monthOfYear,int dayOfMonth) {

            fecha=year+"-"+(monthOfYear+1)+"-"+dayOfMonth+"";
            btEditarEXFechaf.setText(fecha);
        }
    }
    public void exfechafEditar(View v){
        dpdEditar = new DatePickerDialog(this,new ObtenerexfechafEditar(), fechaactual.get(Calendar.YEAR), fechaactual.get(Calendar.MONTH), fechaactual.get(Calendar.DAY_OF_MONTH));
        dpdEditar.show();
    }
    public class ObtenerexhorafEditar implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(TimePicker tp, int hour, int minute){
            hora=hour+":"+minute;
            btEditarEXHoraf.setText(hora);
        }
    }
    public void exhorafEditar(View v){
        tpdEditar = new TimePickerDialog(this,new ObtenerexhorafEditar(), fechaactual.get(Calendar.HOUR_OF_DAY), fechaactual.get(Calendar.MINUTE), true);
        tpdEditar.show();
    }
    public class ObtenerexhoraiEditar implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(TimePicker tp, int hour, int minute){
            hora=hour+":"+minute;
            btEditarEXHorai.setText(hora);
        }
    }
    public void exhoraiEditar(View v){
        tpdEditar = new TimePickerDialog(this,new ObtenerexhoraiEditar(), fechaactual.get(Calendar.HOUR_OF_DAY), fechaactual.get(Calendar.MINUTE), true);
        tpdEditar.show();
    }

    /*PROPIO*/

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