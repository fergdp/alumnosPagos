package ar.com.madrefoca.alumnospagos.utils;

import android.content.Context;
import android.content.res.Resources;

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

import ar.com.madrefoca.alumnospagos.R;
import ar.com.madrefoca.alumnospagos.helpers.DatabaseHelper;
import ar.com.madrefoca.alumnospagos.model.Attendee;
import ar.com.madrefoca.alumnospagos.model.AttendeeEventPayment;
import ar.com.madrefoca.alumnospagos.model.Event;
import ar.com.madrefoca.alumnospagos.model.Payment;
import ar.com.madrefoca.alumnospagos.model.Place;

public class ExportToExcelUtil {

    Context context;
    DatabaseHelper databaseHelper;

    Dao<AttendeeEventPayment, Integer> attendeeEventPaymentDao;
    Dao<Event, Integer> eventDao;
    Dao<Attendee, Integer> attendeeDao;
    Dao<Payment, Integer> paymentDao;
    Dao<Place, Integer> placeDao;

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

        Cell c = null;

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cs.setAlignment(HSSFCellStyle.ALIGN_CENTER);


        sheet1 = wb.createSheet("mis-pagos");

        // Generate column headings
        Row row = sheet1.createRow(0);

        c = row.createCell(0);
        c.setCellValue(context.getString(R.string.excel_header_day));
        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue(context.getString(R.string.excel_header_hour));
        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue(context.getString(R.string.excel_header_place));
        c.setCellStyle(cs);

        c = row.createCell(3);
        c.setCellValue(context.getString(R.string.excel_header_amount));
        c.setCellStyle(cs);

        c = row.createCell(4);
        c.setCellValue(context.getString(R.string.excel_header_attendee_name));
        c.setCellStyle(cs);

        c = row.createCell(5);
        c.setCellValue(context.getString(R.string.excel_header_amount_payed));
        c.setCellStyle(cs);

        populateRows();

        sheet1.setColumnWidth(0, (15 * 500));
        sheet1.setColumnWidth(1, (15 * 500));
        sheet1.setColumnWidth(2, (15 * 500));
        sheet1.setColumnWidth(3, (15 * 500));
        sheet1.setColumnWidth(4, (15 * 500));

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
            placeDao = databaseHelper.getPlacesDao();

            attendeeEventPayments = attendeeEventPaymentDao.queryForAll();

            int rowNumber = 1;
            Cell c = null;
            String oldDate = null;

            for(AttendeeEventPayment attendeeEventPayment : attendeeEventPayments) {
                CellStyle cs2 = wb.createCellStyle();
                cs2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                Row row = sheet1.createRow(rowNumber);
                c = row.createCell(0);
                String date = formatDate(eventDao.queryForId(attendeeEventPayment.getEvent().getIdEvent()));
                if(oldDate == null) {
                    oldDate = date;
                }

                //create separator if there is a diferent event
                if(!oldDate.equals(date)) {
                    oldDate = date;

                    CellStyle cs = wb.createCellStyle();
                    cs.setFillForegroundColor(HSSFColor.LIME.index);
                    cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                    cs.setAlignment(HSSFCellStyle.ALIGN_CENTER);

                    Row spaceRow = sheet1.createRow(++rowNumber);
                    Cell c2 = spaceRow.createCell(0);
                    c2.setCellStyle(cs);

                    c2 = spaceRow.createCell(1);
                    c2.setCellStyle(cs);

                    c2 = spaceRow.createCell(2);
                    c2.setCellStyle(cs);

                    c2 = spaceRow.createCell(3);
                    c2.setCellStyle(cs);

                    c2 = spaceRow.createCell(4);
                    c2.setCellStyle(cs);

                    c2 = spaceRow.createCell(5);
                    c2.setCellStyle(cs);

                    rowNumber++;
                } else {
                    c.setCellValue(date);
                    c.setCellStyle(cs2);

                    c = row.createCell(1);
                    c.setCellValue(eventDao.queryForId(attendeeEventPayment.getEvent().getIdEvent()).getHour() + "hs.");
                    c.setCellStyle(cs2);

                    Place place = placeDao.queryForId(eventDao.queryForId(attendeeEventPayment.getEvent().getIdEvent()).getPlace().getIdplace());
                    c = row.createCell(2);
                    c.setCellValue(place.getName());
                    c.setCellStyle(cs2);

                    c = row.createCell(3);
                    c.setCellValue(eventDao.queryForId(attendeeEventPayment.getEvent().getIdEvent()).getPaymentAmount());
                    c.setCellStyle(cs2);

                    c = row.createCell(4);
                    c.setCellValue(attendeeDao.queryForId(attendeeEventPayment.getAttendee().getAttendeeId()).getAlias());
                    c.setCellStyle(cs2);

                    c = row.createCell(5);
                    c.setCellValue(paymentDao.queryForId(attendeeEventPayment.getPayment().getIdPayment()).getAmount());
                    c.setCellStyle(cs2);

                    rowNumber++;
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private String formatDate (Event event) {
        String day = event.getDay().toString();
        String month = event.getMonth().toString();
        String year = event.getYear().toString();

        String date = day + "-" + month + "-" + year;
        return  date;
    }


}
