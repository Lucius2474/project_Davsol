/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interface;

import Modelo.DetalleVenta;
import java.sql.Connection;
import java.sql.SQLException;

public interface IDetalleVentaDAO {
    void insertar(Connection con, DetalleVenta d) throws SQLException;
}
