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
        sheet1 = wb.createSheet(context.getString(R.string.excel_sheet_name));

        populateRows(null);

        sheet1.setColumnWidth(0, (15 * 1000));
        sheet1.setColumnWidth(1, (15 * 500));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            wb.write(baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] xls = baos.toByteArray();

        return xls;
    }

    public byte[] generateExcelFile(Event event) {
        sheet1 = wb.createSheet(context.getString(R.string.excel_sheet_name));

        populateRows(event.getIdEvent());

        sheet1.setColumnWidth(0, (15 * 1000));
        sheet1.setColumnWidth(1, (15 * 500));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            wb.write(baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] xls = baos.toByteArray();

        return xls;
    }

    private void populateRows(Integer eventId) {
        try {
            attendeeEventPaymentDao = databaseHelper.getAttendeeEventPaymentDao();
            eventDao = databaseHelper.getEventsDao();
            attendeeDao = databaseHelper.getAttendeeDao();
            paymentDao = databaseHelper.getPaymentsDao();
            placeDao = databaseHelper.getPlacesDao();

            if (eventId != null) {
                attendeeEventPayments = attendeeEventPaymentDao.queryForAll();
            } else {
                attendeeEventPayments = attendeeEventPaymentDao.queryForEq("idEvent", eventId);
            }


            int rowNumber = 0;

            Integer oldEventId = null;
            Double total = 0.0;

            // Generate column headings
            //createHeadersRow(rowNumber);
            //rowNumber++;

            for(AttendeeEventPayment attendeeEventPayment : attendeeEventPayments) {
                Integer newEventId = attendeeEventPayment.getEvent().getIdEvent();
                if(oldEventId == null) {
                    createEventNameHeader(rowNumber, attendeeEventPayment);
                    createHeadersRow(++rowNumber);
                    rowNumber++;
                    oldEventId = newEventId;
                }

                //create total row and separator row if there is a diferent event
                if(!oldEventId.equals(newEventId)) {
                    oldEventId = newEventId;
                    createTotalRow(total, rowNumber);
                    createSpaceRow(++rowNumber);
                    createEventNameHeader(++rowNumber, attendeeEventPayment);
                    createHeadersRow(++rowNumber);
                    total = 0.0;
                    rowNumber++;
                    total += createRowContent(rowNumber, attendeeEventPayment);
                    rowNumber++;
                } else {
                    total += createRowContent(rowNumber, attendeeEventPayment);
                    rowNumber++;
                }
            }
            createTotalRow(total, rowNumber);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void createEventNameHeader(int rowNumber, AttendeeEventPayment attendeeEventPayment) {
        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cs.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        Row row = sheet1.createRow(rowNumber);
        Cell c = null;

        c = row.createCell(0);
        try {
            Event event = eventDao.queryForId(attendeeEventPayment.getEvent().getIdEvent());
            Place place = placeDao.queryForId(eventDao.queryForId(attendeeEventPayment.getEvent().getIdEvent()).getPlace().getIdplace());
            String date = formatDate(eventDao.queryForId(attendeeEventPayment.getEvent().getIdEvent()));
            c.setCellValue(context.getString(R.string.excel_header_day) + ": " + date + " / " +
                            context.getString(R.string.excel_header_hour) + ": " + event.getHour() + ":" + event.getMinutes() + " / " +
                            context.getString(R.string.excel_header_place) + ": " + place.getName() + " / " +
                            context.getString(R.string.excel_header_amount) + ": " + event.getPaymentAmount());
            c.setCellStyle(cs);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void createHeadersRow(int rowNumber) {
        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cs.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        Row row = sheet1.createRow(rowNumber);
        Cell c = null;

        c = row.createCell(0);
        c.setCellValue(context.getString(R.string.excel_header_attendee_name));
        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue(context.getString(R.string.excel_header_amount_payed));
        c.setCellStyle(cs);
    }

    private Double createRowContent(int rowNumber, AttendeeEventPayment attendeeEventPayment) {
        Double amount = 0.0;
        CellStyle cs2 = wb.createCellStyle();
        cs2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        Row row = sheet1.createRow(rowNumber);
        Cell c = null;

        try {

            c = row.createCell(0);
            c.setCellValue(attendeeDao.queryForId(attendeeEventPayment.getAttendee().getAttendeeId()).getAlias());
            c.setCellStyle(cs2);

            c = row.createCell(1);
            amount = paymentDao.queryForId(attendeeEventPayment.getPayment().getIdPayment()).getAmount();
            c.setCellValue(amount);
            c.setCellStyle(cs2);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return  amount;
    }

    private void createSpaceRow(int rowNumber) {
        CellStyle cs = wb.createCellStyle();
        cs.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        Row spaceRow = sheet1.createRow(rowNumber);
        Cell c2 = spaceRow.createCell(0);
        c2.setCellStyle(cs);

        c2 = spaceRow.createCell(1);
        c2.setCellStyle(cs);
    }

    private void createTotalRow(Double total, int rowNumber) {
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cs.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        Row totalRow = sheet1.createRow(rowNumber);
        Cell totalCellLabel = totalRow.createCell(0);
        totalCellLabel.setCellValue(context.getString(R.string.excel_footer_total));
        totalCellLabel.setCellStyle(cs);

        Cell totalCellValue = totalRow.createCell(1);
        totalCellValue.setCellValue("$" + total);
        totalCellValue.setCellStyle(cs);
    }

    private String formatDate (Event event) {
        String day = event.getDay().toString();
        String month = event.getMonth().toString();
        String year = event.getYear().toString();

        String date = day + "-" + month + "-" + year;
        return  date;
    }
}
