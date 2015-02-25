
package com.mobile.dev.quantity.model;


import com.google.gson.annotations.Expose;

public class Producto {

    @Expose
    private String id;
    @Expose
    private String precio;
    @Expose
    private String imagen;
    @Expose
    private String descripcion;
    @Expose
    private String categoria;
    @Expose
    private String date;
    @Expose
    private String usuario;

    /**
     * 
     * @return
     *     The id
     */
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The precio
     */
    public String getPrecio() {
        return precio;
    }

    /**
     * 
     * @param precio
     *     The precio
     */
    public void setPrecio(String precio) {
        this.precio = precio;
    }

    /**
     * 
     * @return
     *     The imagen
     */
    public String getImagen() {
        return imagen;
    }

    /**
     * 
     * @param imagen
     *     The imagen
     */
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    /**
     * 
     * @return
     *     The descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * 
     * @param descripcion
     *     The descripcion
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * 
     * @return
     *     The categoria
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * 
     * @param categoria
     *     The categoria
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /**
     * 
     * @return
     *     The date
     */
    public String getDate() {
        return date;
    }

    /**
     * 
     * @param date
     *     The date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * 
     * @return
     *     The usuario
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * 
     * @param usuario
     *     The usuario
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }


    @Override
    public String toString() {
        return getDescripcion()+"\n "+"$"+getPrecio();
    }
}
