package DAO;

import Modelo.Cliente;
import org.junit.jupiter.api.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClienteDAOTest {

    private static ClienteDAO clienteDAO;

    // DNI ÚNICO y no registrado en tu BD para evitar violar la restricción UNIQUE
    private static final String DNI_NUEVO_UNICO = "65489984"; 
    private static final String NOMBRE_NUEVO = "Jacinto";
    private static final String TEL_NUEVO = "987554399";
    private static final String CORREO_NUEVO = "jacinto@gmail.com";

    // DNI de cliente EXISTENTE en tu tabla real (Valeria - ID 18)
    private static final String DNI_EXISTENTE_VALERIA = "75489906";
    private static final String CORREO_NUEVO_VALERIA = "valeria.oficial@gmail.com";

    @BeforeAll
    static void setUp() {
        clienteDAO = new ClienteDAO();
    }

    @Test
    @Order(1)
    @DisplayName("1. Insertar cliente nuevo con DNI único")
    void test1_Insertar() {
        Cliente nuevo = new Cliente();
        nuevo.setDniRUC(DNI_NUEVO_UNICO);
        nuevo.setNombres(NOMBRE_NUEVO);
        nuevo.setTelefono(TEL_NUEVO);
        nuevo.setCorreo(CORREO_NUEVO);

        boolean insertado = clienteDAO.insertar(nuevo);
        assertTrue(insertado, "El cliente con DNI " + DNI_NUEVO_UNICO + " debe insertarse correctamente");

        // Verificamos que se haya guardado
        Cliente recuperado = clienteDAO.buscarPorRUC(DNI_NUEVO_UNICO);
        assertNotNull(recuperado, "El cliente recién insertado debe existir en la BD");
    }

    @Test
    @Order(2)
    @DisplayName("2. Buscar cliente existente (Valeria) y modificar su correo")
    void test2_BuscarYModificarCorreoExistente() {
        // Buscar cliente existente de la tabla real
        Cliente valeria = clienteDAO.buscarPorRUC(DNI_EXISTENTE_VALERIA);
        assertNotNull(valeria, "Valeria debe ser localizada en la BD");

        // Modificar solo el correo
        valeria.setCorreo(CORREO_NUEVO_VALERIA);
        boolean actualizado = clienteDAO.actualizar(valeria);
        assertTrue(actualizado, "El correo de Valeria debe actualizarse con éxito");

        // Comprobar cambio
        Cliente valeriaActualizada = clienteDAO.buscarPorRUC(DNI_EXISTENTE_VALERIA);
        assertEquals(CORREO_NUEVO_VALERIA, valeriaActualizada.getCorreo());
    }

    @Test
    @Order(3)
    @DisplayName("3. Eliminar (desactivar) el cliente que acabamos de insertar")
    void test3_EliminarClienteInsertado() {
        Cliente gonzalo = clienteDAO.buscarPorRUC(DNI_NUEVO_UNICO);
        assertNotNull(gonzalo, "Debe existir el cliente insertado para eliminarlo");

        boolean eliminado = clienteDAO.eliminar(gonzalo.getIdcliente());
        assertTrue(eliminado, "El cliente insertado debe desactivarse (activo = 0)");

        // Verificar que ya no figure como activo
        Cliente gonzaloDesactivado = clienteDAO.buscarPorRUC(DNI_NUEVO_UNICO);
        assertNull(gonzaloDesactivado, "El cliente desactivado ya no debe retornar en la búsqueda");
    }

    @Test
    @Order(4)
    @DisplayName("4. Listar clientes activos de la base de datos")
    void test4_Listar() {
        List<Cliente> lista = clienteDAO.listar();

        assertNotNull(lista, "La lista no debe ser nula");
        assertFalse(lista.isEmpty(), "La lista debe retornar los clientes activos de la BD");
        
        System.out.println("Total de clientes activos recuperados: " + lista.size());
    }
}