package ar.com.madrefoca.alumnospagos.utils;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import ar.com.madrefoca.alumnospagos.helpers.DatabaseHelper;
import ar.com.madrefoca.alumnospagos.model.Attendee;
import ar.com.madrefoca.alumnospagos.model.AttendeeEventPayment;
import ar.com.madrefoca.alumnospagos.model.Event;
import ar.com.madrefoca.alumnospagos.model.Payment;

public class ExportToExcelUtil {

    Context context;
    DatabaseHelper databaseHelper;

    Dao<AttendeeEventPayment, Integer> attendeeEventPaymentDao;
    Dao<Event, Integer> eventDao;
    Dao<Attendee, Integer> attendeeDao;
    Dao<Payment, Integer> paymentDao;

    List<AttendeeEventPayment> attendeeEventPayments = null;

    //New Workbook
    Workbook wb = new HSSFWorkbook();
    //New Sheet
    Sheet sheet1 = null;

    public ExportToExcelUtil(Context applicationContext, DatabaseHelper databaseHelper) {
        this.context = applicationContext;
        this.databaseHelper = databaseHelper;
    }

    public byte[] generateExcelFile() {

        String result = "";

        boolean success = false;

        Cell c = null;

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);


        sheet1 = wb.createSheet("mis-pagos");

        // Generate column headings
        Row row = sheet1.createRow(0);

        c = row.createCell(0);
        c.setCellValue("Clase");
        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("Alumno");
        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue("Pago");
        c.setCellStyle(cs);

        populateRows();

        sheet1.setColumnWidth(0, (15 * 500));
        sheet1.setColumnWidth(1, (15 * 500));
        sheet1.setColumnWidth(2, (15 * 500));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            wb.write(baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] xls = baos.toByteArray();

        return xls;
    }

    private void populateRows() {
        try {
            attendeeEventPaymentDao = databaseHelper.getAttendeeEventPaymentDao();
            eventDao = databaseHelper.getEventsDao();
            attendeeDao = databaseHelper.getAttendeeDao();
            paymentDao = databaseHelper.getPaymentsDao();

            attendeeEventPayments = attendeeEventPaymentDao.queryForAll();

            int rowNumber = 1;
            Cell c = null;

            for(AttendeeEventPayment attendeeEventPayment : attendeeEventPayments) {
                Row row = sheet1.createRow(rowNumber);
                c = row.createCell(0);
                c.setCellValue(eventDao.queryForId(attendeeEventPayment.getEvent().getIdEvent()).getName());

                c = row.createCell(1);
                c.setCellValue(attendeeDao.queryForId(attendeeEventPayment.getAttendee().getAttendeeId()).getAlias());

                c = row.createCell(2);
                c.setCellValue(paymentDao.queryForId(attendeeEventPayment.getPayment().getIdPayment()).getAmount());

                rowNumber++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}
