package com.mobile.dev.quantity.util;

import java.util.HashMap;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.mobile.dev.quantity.LoginActivity;


/**
 * Esta clase gestiona las sesiones para los usuarios
 * @author Luis Carinoo
 *
 */
public class SessionManager {
	// Shared Preferences
	SharedPreferences pref;
	
	// Editor for Shared preferences
	Editor editor;
	
	// Context
	Context _context;
	
	// Shared pref mode
	int PRIVATE_MODE = 0;
	
	// Sharedpref nombre del archivo
	private static final String PREF_NAME = "ArchivoDeSesion";
	
	// Claves para los valores a guardar en el archivo
	private static final String IS_LOGIN = "IsLoggedIn";
	
	// Usuario 
	public static final String KEY_CLAVE = "clave";

	public static final String KEY_PASSWORD = "password";
	
	public static final String KEY_TOKEN = "token";
	
	public static final String KEY_NOMBRE = "nombre";
	
	public static final String KEY_APELLIDO = "apellido";
	// Constructor
	public SessionManager(Context context){
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}
	
	/**
	 * Crea una sesion de login en el archivo shared preferences
	 * */
	public void createLoginSession(String clave, String password, String token, String nombre, String apellido ){
		// se activa una bandera de logeo
		editor.putBoolean(IS_LOGIN, true);
		
		// Guarda los datos de sesion
		editor.putString(KEY_CLAVE, clave);
		editor.putString(KEY_PASSWORD, password);
		editor.putString(KEY_TOKEN, token);
		editor.putString(KEY_NOMBRE, nombre);
		editor.putString(KEY_APELLIDO, apellido);
		
		// guardamos los datos
		editor.commit();
	}


    /**
     * Crea una sesion de login en el archivo shared preferences
     * */
    public void createLoginSession(String username){
        // se activa una bandera de logeo
        editor.putBoolean(IS_LOGIN, true);

        // Se ponen los datos de sesion en el archivo de Shared Preferences
        editor.putString(KEY_CLAVE, username);

        // guardamos los datos
        editor.commit();
    }
	
	/**
	 * Verifica el estado del login del usuario.
	 * En caso de ser falso llevara al usuario a la pantalla de logeo
	 * Si ya se tienen una sesion no hara nada
	 * 
	 * */
	public void checkLogin(){
		// Check login status
		if(!this.isLoggedIn()){
			// user is not logged in redirect him to Login Activity
			Intent i = new Intent(_context, LoginActivity.class);
			// Closing all the Activities
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
			// Add new Flag to start new Activity
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			// Staring Login Activity
			_context.startActivity(i);
		}
		
	}
	
	
	
	/**
	 * Recupera los datos almacenados en la sesion y los devuelve en un HashMap
	 * */
	public HashMap<String, String> getUserDetails(){
		HashMap<String, String> user = new HashMap<String, String>();
		// obtiene los datos del archivo y los guarda en el HashMap
		user.put(KEY_CLAVE, pref.getString(KEY_CLAVE, null));
        if(pref.contains(KEY_PASSWORD))
		    user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));
        if(pref.contains(KEY_TOKEN))
		    user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));
        if(pref.contains(KEY_APELLIDO))
		    user.put(KEY_NOMBRE, pref.getString(KEY_APELLIDO, null));
		
		return user;
	}
	
	/**
	 * Elimina los datos de la sesion
	 * */
	public void logoutUser(){
		// Elimina los datos del Shared Preferences
		editor.clear();
		editor.commit();
		
		// Despues del logout redirecciona al LoginActivity
		Intent i = new Intent(_context, LoginActivity.class);
		// Se cierran todas las activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		// Se inicia la actividad de Login
		_context.startActivity(i);
	}
	
	/**
	 * Verirficación rápida de logeo
	 * **/
	// Get Login State
	public boolean isLoggedIn(){
		return pref.getBoolean(IS_LOGIN, false);
	}
}
