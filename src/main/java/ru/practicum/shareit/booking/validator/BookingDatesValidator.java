package ru.practicum.shareit.booking.validator;

import ru.practicum.shareit.booking.dto.CreatedBookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

/**
 * BookingDatesValidator деалет проверку на валидность startDate и endDate для booking
 */

public class BookingDatesValidator implements ConstraintValidator<ValidBookingDates, CreatedBookingDto> {

    @Override
    public void initialize(ValidBookingDates constraintAnnotation) {
    }

    @Override
    public boolean isValid(CreatedBookingDto dto, ConstraintValidatorContext context) {
        boolean valid = true;

        if (dto.getStart() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Не указано дата и время начала брони.")
                    .addPropertyNode("start")
                    .addConstraintViolation();
            valid = false;
        }

        if (dto.getEnd() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Не указано дата и время окончания брони.")
                    .addPropertyNode("end")
                    .addConstraintViolation();
            valid = false;
        }

        if (dto.getStart() != null && dto.getEnd() != null) {
            if (dto.getStart().isAfter(dto.getEnd())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Дата и время начала брони не может быть позже даты и " +
                                "времени окончания брони.")
                        .addPropertyNode("end")
                        .addConstraintViolation();
                valid = false;
            }

            if (dto.getStart().isEqual(dto.getEnd())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Дата и время начала и окончания брони не могут быть " +
                                "одинаковыми")
                        .addPropertyNode("start")
                        .addConstraintViolation();
                valid = false;
            }

            if (dto.getStart().isBefore(LocalDateTime.now())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Дата и время начала брони не может быть в прошлом")
                        .addPropertyNode("start")
                        .addConstraintViolation();
                valid = false;
            }

            if (dto.getEnd().isBefore(LocalDateTime.now())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Дата и время окончания брони не может быть в прошлом")
                        .addPropertyNode("end")
                        .addConstraintViolation();
                valid = false;
            }
        }
        return valid;
    }
}
