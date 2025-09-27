package com.hirehack.hirehack.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.Phonenumber;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PhoneNumberUtil {

    private static final com.google.i18n.phonenumbers.PhoneNumberUtil phoneUtil = 
            com.google.i18n.phonenumbers.PhoneNumberUtil.getInstance();

    public static String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }

        try {
            // Try to parse as international number first
            Phonenumber.PhoneNumber parsedNumber = phoneUtil.parse(phoneNumber, null);
            
            if (phoneUtil.isValidNumber(parsedNumber)) {
                // Return in E.164 format (e.g., +1234567890)
                return phoneUtil.format(parsedNumber, com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.E164);
            } else {
                throw new IllegalArgumentException("Invalid phone number: " + phoneNumber);
            }
        } catch (NumberParseException e) {
            throw new IllegalArgumentException("Invalid phone number format: " + phoneNumber, e);
        }
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        try {
            normalizePhoneNumber(phoneNumber);
            return true;
        } catch (IllegalArgumentException e) {
            log.warn("Invalid phone number: {}", phoneNumber);
            return false;
        }
    }

    public static String formatPhoneNumber(String phoneNumber, com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat format) {
        try {
            Phonenumber.PhoneNumber parsedNumber = phoneUtil.parse(phoneNumber, null);
            return phoneUtil.format(parsedNumber, format);
        } catch (NumberParseException e) {
            throw new IllegalArgumentException("Invalid phone number format: " + phoneNumber, e);
        }
    }
}
