/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interface;

import Modelo.Producto;
import Modelo.Venta;
import java.sql.Connection;
import java.sql.SQLException;

public interface IVentaDAO {
    int insertar(Connection con, Venta v) throws SQLException;
    boolean registrarSalidaStock(Connection con, Producto producto, int cantidad) throws SQLException;
}
