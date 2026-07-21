package Controlador;

import Modelo.Cliente;
import Vista.Sistema;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class VentaPDF {

    private Sistema vista;

    private String fechaActual = "";
    private String nombreArchivoPDFVenta = "";

    public VentaPDF(Sistema vista) {
        this.vista = vista;
    }

    public void generarFacturaPDF(Cliente cliente) {

        fechaActual = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        String fechaArchivo = new SimpleDateFormat(
                "yyyy_MM_dd_HH_mm_ss"
        ).format(new Date());

        nombreArchivoPDFVenta =
                "Venta_"
                + cliente.getNombres().replaceAll("[^a-zA-Z0-9]", "_")
                + "_"
                + fechaArchivo
                + ".pdf";

        File file = new File(
                "src/pdf/" + nombreArchivoPDFVenta
        );

        file.getParentFile().mkdirs();

        try (
                FileOutputStream fos = new FileOutputStream(file);
                PdfWriter writer = new PdfWriter(fos);
                PdfDocument pdfDoc = new PdfDocument(writer);
                Document doc = new Document(pdfDoc, PageSize.A4)
        ) {

            doc.setMargins(35, 35, 35, 35);

            // Fuentes
            PdfFont normalFont =
                    PdfFontFactory.createFont("Times-Roman");

            PdfFont boldFont =
                    PdfFontFactory.createFont("Times-Bold");



            Table headerTable = new Table(
                    UnitValue.createPercentArray(
                            new float[]{20, 50, 30}
                    )
            );

            headerTable.setWidth(
                    UnitValue.createPercentValue(100)
            );


            File logoFile = new File(
                    "src/img/logopdf.png"
            );

            if (logoFile.exists()) {

                Image img = new Image(
                        ImageDataFactory.create(
                                logoFile.getAbsolutePath()
                        )
                );

                img.setWidth(80);
                img.setHeight(80);

                Cell logoCell = new Cell()
                        .add(img)
                        .setBorder(Border.NO_BORDER)
                        .setVerticalAlignment(
                                VerticalAlignment.MIDDLE
                        );

                headerTable.addCell(logoCell);

            } else {

                headerTable.addCell(
                        new Cell()
                                .add(
                                        new Paragraph("DAVSOL")
                                )
                                .setFont(boldFont)
                                .setFontSize(18)
                                .setBorder(Border.NO_BORDER)
                );
            }

            Paragraph empresa = new Paragraph()
                    .add(
                            new Paragraph(
                                    "DAVSOL ECO SYSTEMS"
                            )
                                    .setFont(boldFont)
                                    .setFontSize(15)
                    )
                    .add("\nRUC: 20605513825")
                    .add("\nTeléfono: 976 601 735")
                    .add(
                            "\nJr. Ramón Castillo 1451 - "
                            + "San Román - Puno"
                    )
                    .setFont(normalFont)
                    .setFontSize(9);

            Cell empresaCell = new Cell()
                    .add(empresa)
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(
                            VerticalAlignment.MIDDLE
                    );

            headerTable.addCell(empresaCell);

          
            Paragraph comprobante = new Paragraph()
                    .add("COMPROBANTE DE VENTA\n")
                    .add("N° 001\n")
                    .add("Fecha: " + fechaActual)
                    .setFont(boldFont)
                    .setFontSize(11)
                    .setTextAlignment(
                            TextAlignment.CENTER
                    );

            Cell comprobanteCell = new Cell()
                    .add(comprobante)
                    .setBorder(
                            new SolidBorder(
                                    ColorConstants.BLACK,
                                    1
                            )
                    )
                    .setVerticalAlignment(
                            VerticalAlignment.MIDDLE
                    );

            headerTable.addCell(comprobanteCell);

            doc.add(headerTable);

            doc.add(new Paragraph("\n"));


            Paragraph tituloCliente =
                    new Paragraph("DATOS DEL CLIENTE")
                            .setFont(boldFont)
                            .setFontSize(12)
                            .setFontColor(
                                    ColorConstants.WHITE
                            )
                            .setBackgroundColor(
                                    ColorConstants.DARK_GRAY
                            )
                            .setPadding(5);

            doc.add(tituloCliente);

            Table clientTable = new Table(
                    UnitValue.createPercentArray(
                            new float[]{25, 25, 25, 25}
                    )
            );

            clientTable.setWidth(
                    UnitValue.createPercentValue(100)
            );

            agregarDatoCliente(
                    clientTable,
                    "DNI / RUC",
                    cliente.getDniRUC(),
                    boldFont,
                    normalFont
            );

            agregarDatoCliente(
                    clientTable,
                    "NOMBRE",
                    cliente.getNombres(),
                    boldFont,
                    normalFont
            );

            agregarDatoCliente(
                    clientTable,
                    "TELÉFONO",
                    cliente.getTelefono(),
                    boldFont,
                    normalFont
            );

            agregarDatoCliente(
                    clientTable,
                    "CORREO",
                    cliente.getCorreo(),
                    boldFont,
                    normalFont
            );

            doc.add(clientTable);

            doc.add(new Paragraph("\n"));


            
            Table productTable = new Table(
                    UnitValue.createPercentArray(
                            new float[]{15, 45, 20, 20}
                    )
            );

            productTable.setWidth(
                    UnitValue.createPercentValue(100)
            );

            String[] encabezados = {
                "CANTIDAD",
                "PRODUCTO",
                "PRECIO UNIT.",
                "SUBTOTAL"
            };

            for (String encabezado : encabezados) {

                Cell cell = new Cell()
                        .add(
                                new Paragraph(encabezado)
                                        .setFont(boldFont)
                                        .setFontSize(9)
                                        .setTextAlignment(
                                                TextAlignment.CENTER
                                        )
                        )
                        .setBackgroundColor(
                                ColorConstants.DARK_GRAY
                        )
                        .setFontColor(
                                ColorConstants.WHITE
                        )
                        .setPadding(6);

                productTable.addCell(cell);
            }

            
            for (
                    int i = 0;
                    i < vista.t_regVent.getRowCount();
                    i++
            ) {

                String cantidad =
                        vista.t_regVent
                                .getValueAt(i, 0)
                                .toString();

                String producto =
                        vista.t_regVent
                                .getValueAt(i, 1)
                                .toString();

                double precioSinIGV =
                        Double.parseDouble(
                                vista.t_regVent
                                        .getValueAt(i, 2)
                                        .toString()
                        );

                double subtotalSinIGV =
                        Double.parseDouble(
                                vista.t_regVent
                                        .getValueAt(i, 3)
                                        .toString()
                        );

                Cell cantidadCell = new Cell()
                        .add(
                                new Paragraph(cantidad)
                                        .setFont(normalFont)
                                        .setFontSize(10)
                        )
                        .setTextAlignment(
                                TextAlignment.CENTER
                        )
                        .setPadding(6);

                Cell productoCell = new Cell()
                        .add(
                                new Paragraph(producto)
                                        .setFont(normalFont)
                                        .setFontSize(10)
                        )
                        .setPadding(6);

                Cell precioCell = new Cell()
                        .add(
                                new Paragraph(
                                        String.format(
                                                "S/ %.2f",
                                                precioSinIGV
                                        )
                                )
                                        .setFont(normalFont)
                                        .setFontSize(10)
                        )
                        .setTextAlignment(
                                TextAlignment.RIGHT
                        )
                        .setPadding(6);

                Cell subtotalCell = new Cell()
                        .add(
                                new Paragraph(
                                        String.format(
                                                "S/ %.2f",
                                                subtotalSinIGV
                                        )
                                )
                                        .setFont(normalFont)
                                        .setFontSize(10)
                        )
                        .setTextAlignment(
                                TextAlignment.RIGHT
                        )
                        .setPadding(6);

                productTable.addCell(cantidadCell);
                productTable.addCell(productoCell);
                productTable.addCell(precioCell);
                productTable.addCell(subtotalCell);
            }

            doc.add(productTable);


            
            double subtotalGeneral =
                    Double.parseDouble(
                            vista.txtF_subtotal
                                    .getText()
                    );

            double igv =
                    subtotalGeneral * 0.18;

            double totalConIGV =
                    subtotalGeneral + igv;

  
            
            doc.add(new Paragraph("\n"));

            Table totalTable = new Table(
                    UnitValue.createPercentArray(
                            new float[]{70, 30}
                    )
            );

            totalTable.setWidth(
                    UnitValue.createPercentValue(100)
            );

            

            totalTable.addCell(
                    new Cell()
                            .add(
                                    new Paragraph(
                                            "Subtotal:"
                                    )
                            )
                            .setFont(normalFont)
                            .setFontSize(11)
                            .setBorder(
                                    Border.NO_BORDER
                            )
                            .setTextAlignment(
                                    TextAlignment.RIGHT
                            )
            );

            totalTable.addCell(
                    new Cell()
                            .add(
                                    new Paragraph(
                                            String.format(
                                                    "S/ %.2f",
                                                    subtotalGeneral
                                            )
                                    )
                            )
                            .setFont(normalFont)
                            .setFontSize(11)
                            .setBorder(
                                    Border.NO_BORDER
                            )
                            .setTextAlignment(
                                    TextAlignment.RIGHT
                            )
            );


            
            totalTable.addCell(
                    new Cell()
                            .add(
                                    new Paragraph(
                                            "IGV (18%):"
                                    )
                            )
                            .setFont(normalFont)
                            .setFontSize(11)
                            .setBorder(
                                    Border.NO_BORDER
                            )
                            .setTextAlignment(
                                    TextAlignment.RIGHT
                            )
            );

            totalTable.addCell(
                    new Cell()
                            .add(
                                    new Paragraph(
                                            String.format(
                                                    "S/ %.2f",
                                                    igv
                                            )
                                    )
                            )
                            .setFont(normalFont)
                            .setFontSize(11)
                            .setBorder(
                                    Border.NO_BORDER
                            )
                            .setTextAlignment(
                                    TextAlignment.RIGHT
                            )
            );



            totalTable.addCell(
                    new Cell()
                            .add(
                                    new Paragraph(
                                            "TOTAL A PAGAR:"
                                    )
                            )
                            .setFont(boldFont)
                            .setFontSize(14)
                            .setBorder(
                                    Border.NO_BORDER
                            )
                            .setTextAlignment(
                                    TextAlignment.RIGHT
                            )
            );

            totalTable.addCell(
                    new Cell()
                            .add(
                                    new Paragraph(
                                            String.format(
                                                    "S/ %.2f",
                                                    totalConIGV
                                            )
                                    )
                            )
                            .setFont(boldFont)
                            .setFontSize(14)
                            .setTextAlignment(
                                    TextAlignment.RIGHT
                            )
                            .setPadding(8)
                            .setBorder(
                                    new SolidBorder(
                                            ColorConstants.BLACK,
                                            1
                                    )
                            )
            );

            doc.add(totalTable);


            doc.add(new Paragraph("\n\n"));

            Paragraph gracias =
                    new Paragraph(
                            "¡Gracias por su preferencia!"
                    )
                            .setFont(boldFont)
                            .setFontSize(12)
                            .setTextAlignment(
                                    TextAlignment.CENTER
                            );

            doc.add(gracias);

            Paragraph sistema =
                    new Paragraph(
                            "DAVSOL ECO SYSTEMS - "
                            + "Sistema de Gestión de Ventas"
                    )
                            .setFont(normalFont)
                            .setFontSize(9)
                            .setTextAlignment(
                                    TextAlignment.CENTER
                            );

            doc.add(sistema);

            System.out.println(
                    "PDF generado correctamente en: "
                    + file.getAbsolutePath()
            );

        } catch (Exception e) {

            System.err.println(
                    "Error al generar el PDF: "
                    + e.getMessage()
            );

            e.printStackTrace();

            return;
        }


        
        try {

            if (file.exists()) {

                if (Desktop.isDesktopSupported()) {

                    Desktop.getDesktop().open(file);

                } else {

                    System.out.println(
                            "El sistema no permite abrir "
                            + "archivos automáticamente."
                    );
                }

            }

        } catch (IOException e) {

            System.err.println(
                    "No se pudo abrir el archivo: "
                    + e.getMessage()
            );
        }
    }



    private void agregarDatoCliente(
            Table tabla,
            String etiqueta,
            String valor,
            PdfFont boldFont,
            PdfFont normalFont
    ) {

        Cell etiquetaCell = new Cell()
                .add(
                        new Paragraph(etiqueta)
                                .setFont(boldFont)
                                .setFontSize(8)
                )
                .setBackgroundColor(
                        ColorConstants.LIGHT_GRAY
                )
                .setPadding(5);

        Cell valorCell = new Cell()
                .add(
                        new Paragraph(
                                valor != null
                                        ? valor
                                        : "-"
                        )
                                .setFont(normalFont)
                                .setFontSize(9)
                )
                .setPadding(5);

        tabla.addCell(etiquetaCell);
        tabla.addCell(valorCell);
    }
}