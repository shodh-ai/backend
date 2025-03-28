package com.shodhAI.ShodhAI.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class SharedUtilityService
{
    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    public Boolean validateDate(String startDate, String dateFormatInString,String dateTypeName) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatInString);
        dateFormat.setLenient(false);

        try {
            if (!isValidDateFormat(startDate, dateFormat)) {
                throw new IllegalArgumentException(dateTypeName+ " must be in " + dateFormatInString + " format");
            }
            return true;
        } catch (IllegalArgumentException ex) {
            exceptionHandlingService.handleException(ex);
            throw ex; // Rethrow with meaningful context
        }
    }

    private boolean isValidDateFormat(String dateStr, SimpleDateFormat dateFormat) {
        try {
            Date parsedDate = dateFormat.parse(dateStr);
            String formattedDate = dateFormat.format(parsedDate);
            return formattedDate.equals(dateStr);
        } catch (ParseException e) {
            return false;
        }
    }

    public void compareTwoDates(Date startDate,Date endDate,String dateTypeName) throws ParseException {
        if (endDate.before(startDate)) {
            throw new IllegalArgumentException(dateTypeName+ " end date cannot be before "+ dateTypeName+" start date");
        }
    }
}
