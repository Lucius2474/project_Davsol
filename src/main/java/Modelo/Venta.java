/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;



/**
 *
 * @author aalex
 */
public class Venta {
    private int idventa;
    private int idcliente;
    private Usuario usuario;
    private double total;

    public Venta() {
        
    }


    public void setIdcliente(int idcliente) {
        this.idcliente = idcliente;
    }

    public int getIdcliente() {
        return idcliente;
    }

    public int getIdventa() {
        return idventa;
    }

    public void setIdventa(int idventa) {
        this.idventa = idventa;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    
    
    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
    
    
    


    
    
    
    
}
