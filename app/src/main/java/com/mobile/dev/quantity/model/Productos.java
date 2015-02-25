
package com.mobile.dev.quantity.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;


public class Productos {

    @Expose
    private List<Producto> productos = new ArrayList<Producto>();
    @Expose
    private String kind;
    @Expose
    private String etag;

    /**
     * 
     * @return
     *     The productos
     */
    public List<Producto> getProductos() {
        return productos;
    }

    /**
     * 
     * @param productos
     *     The productos
     */
    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    /**
     * 
     * @return
     *     The kind
     */
    public String getKind() {
        return kind;
    }

    /**
     * 
     * @param kind
     *     The kind
     */
    public void setKind(String kind) {
        this.kind = kind;
    }

    /**
     * 
     * @return
     *     The etag
     */
    public String getEtag() {
        return etag;
    }

    /**
     * 
     * @param etag
     *     The etag
     */
    public void setEtag(String etag) {
        this.etag = etag;
    }

}
