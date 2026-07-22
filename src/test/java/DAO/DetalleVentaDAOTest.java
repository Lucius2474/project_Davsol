package DAO;

import Modelo.DetalleVenta;
import Modelo.Venta;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import util.ConnectionMySQL;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DetalleVentaDAOTest {

    private static DetalleVentaDAO detalleVentaDAO;
    private static VentaDAO ventaDAO;

 
    private static final int ID_CLIENTE_PRUEBA = 1;
    private static final int ID_PRODUCTO_PRUEBA = 1; // "Terma Solar Eco 80L"

    @BeforeAll
    static void setUp() {
        detalleVentaDAO = new DetalleVentaDAO();
        ventaDAO = new VentaDAO();
    }

    @Test
    @Order(1)
    @DisplayName("1. Insertar detalle de venta en transacción y verificar subtotal calculado")
    void test1_InsertarDetalleVentaExitoso() {
        try (Connection con = ConnectionMySQL.getConexion()) {
            con.setAutoCommit(false); // Iniciar transacción explícita

        
            Venta v = new Venta();
            v.setIdcliente(ID_CLIENTE_PRUEBA);
            v.setTotal(700.00);

            int idVentaGenerado = ventaDAO.insertar(con, v);
            assertTrue(idVentaGenerado > 0, "Se debe generar un ID de venta válido para asociar el detalle");

           
            DetalleVenta dv = new DetalleVenta();
            dv.setIdVenta(idVentaGenerado);
            dv.setIdProducto(ID_PRODUCTO_PRUEBA);
            dv.setCantidad(2);
            dv.setPrecioUnitario(350.00);

          
            assertDoesNotThrow(() -> detalleVentaDAO.insertar(con, dv), 
                    "La inserción del detalle no debe lanzar excepciones SQL");

           
            String sqlVerificar = "SELECT cantidad, precio_unitario, subtotal FROM detalle_venta WHERE id_venta = ? AND id_producto = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlVerificar)) {
                ps.setInt(1, idVentaGenerado);
                ps.setInt(2, ID_PRODUCTO_PRUEBA);
                ResultSet rs = ps.executeQuery();

                assertTrue(rs.next(), "El registro debe existir en la tabla detalle_venta");
                assertEquals(2, rs.getInt("cantidad"));
                assertEquals(350.00, rs.getDouble("precio_unitario"));
                assertEquals(700.00, rs.getDouble("subtotal"), 0.001, "El subtotal debe guardarse como 700.00");
            }

            con.commit(); 
            System.out.println("DetalleVenta insertado correctamente para Venta ID: " + idVentaGenerado);

        } catch (SQLException e) {
            fail("Error durante la prueba de inserción de detalle de venta: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    @DisplayName("2. Validar fallo por Foreign Key cuando la Venta no existe")
    void test2_InsertarDetalleVentaInexistente() {
        try (Connection con = ConnectionMySQL.getConexion()) {
            con.setAutoCommit(false);

            DetalleVenta dvInvalido = new DetalleVenta();
            dvInvalido.setIdVenta(-9999); // ID inexistente
            dvInvalido.setIdProducto(ID_PRODUCTO_PRUEBA);
            dvInvalido.setCantidad(1);
            dvInvalido.setPrecioUnitario(100.00);

    
            assertThrows(SQLException.class, () -> {
                detalleVentaDAO.insertar(con, dvInvalido);
            }, "Debe arrojar SQLException por violar Foreign Key (id_venta inexistente)");

            con.rollback(); 

        } catch (SQLException e) {
            fail("Excepción inesperada al probar FK inválida: " + e.getMessage());
        }
    }
}