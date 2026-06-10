/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interface;

import Modelo.Inventario;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IInventarioDAO {
    int obtenerStockActual(Connection con, int idProducto) throws SQLException;
    void actualizarStock(Connection con, int idInventario, int nuevoStock) throws SQLException;
    Inventario obtenerPorProducto(int idProducto);
    Inventario obtenerPorProductoConConexion(Connection con, int idProducto) throws SQLException;
    List<Inventario> listar();
    int obtenerIdInventario(Connection con, int idProducto) throws SQLException;
    void actualizarStockRV(Connection con, int idProducto, int nuevaCantidad) throws SQLException;
}
