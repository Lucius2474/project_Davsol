package DAO;

import Modelo.Producto;
import Modelo.Venta;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import util.ConnectionMySQL;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VentaDAOTest {

    private static VentaDAO ventaDAO;

    // IDs de referencia para las pruebas
    private static final int ID_CLIENTE_PRUEBA = 1;
    private static final int ID_PRODUCTO_PRUEBA = 1; // "Terma Solar Eco 80L"
    private static int idVentaGenerado = -1;

    @BeforeAll
    static void setUp() {
        ventaDAO = new VentaDAO();
    }

    @Test
    @Order(1)
    @DisplayName("1. Insertar venta dentro de una transacción")
    void test1_InsertarVenta() {
        try (Connection con = ConnectionMySQL.getConexion()) {
            con.setAutoCommit(false); // Inicio de transacción

            Venta v = new Venta();
            v.setIdcliente(ID_CLIENTE_PRUEBA);
            v.setTotal(350.00);

            idVentaGenerado = ventaDAO.insertar(con, v);
            con.commit(); // Confirmación de la transacción

            assertTrue(idVentaGenerado > 0, "El ID de la venta insertada debe ser mayor a 0");
            System.out.println("Venta registrada con éxito. ID asignado: " + idVentaGenerado);

        } catch (SQLException e) {
            fail("Falló el registro de la venta: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    @DisplayName("2. Registrar salida de stock y movimiento de inventario exitosamente")
    void test2_RegistrarSalidaStockExitoso() {
        try (Connection con = ConnectionMySQL.getConexion()) {
            con.setAutoCommit(false);

            // Aseguramos que el producto tenga stock previo suficiente para la prueba
            prepararStockAuxiliar(con, ID_PRODUCTO_PRUEBA, 10);

            Producto p = new Producto();
            p.setIdproducto(ID_PRODUCTO_PRUEBA);
            int cantidadSalida = 2;

            boolean resultado = ventaDAO.registrarSalidaStock(con, p, cantidadSalida);
            con.commit();

            assertTrue(resultado, "Debe reducir el stock y registrar el movimiento tipo 'SALIDA'");

        } catch (SQLException e) {
            fail("Falló el registro de salida de stock: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    @DisplayName("3. Rechazar salida de stock si la cantidad supera el stock disponible")
    void test3_RegistrarSalidaStockInsuficiente() {
        try (Connection con = ConnectionMySQL.getConexion()) {
            con.setAutoCommit(false);

            Producto p = new Producto();
            p.setIdproducto(ID_PRODUCTO_PRUEBA);
            int cantidadExcesiva = 9999; // Cantidad mayor al stock existente

            boolean resultado = ventaDAO.registrarSalidaStock(con, p, cantidadExcesiva);
            con.rollback(); // Deshacemos cualquier modificación no deseada

            assertFalse(resultado, "No debe permitir la salida de stock si no hay suficiente disponibilidad");

        } catch (SQLException e) {
            fail("Error durante la prueba de stock insuficiente: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar para asegurar que exista stock disponible antes del test.
     */
    private void prepararStockAuxiliar(Connection con, int idProducto, int stock) throws SQLException {
        String sql = "UPDATE inventario SET stock_actual = ? WHERE id_producto = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, stock);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        }
    }
}