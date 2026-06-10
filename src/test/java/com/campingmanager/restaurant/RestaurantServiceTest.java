package com.campingmanager.restaurant;

import com.campingmanager.email.EmailService;
import com.campingmanager.exceptions.BadRequestException;
import com.campingmanager.exceptions.ConflictException;
import com.campingmanager.exceptions.ResourceNotFoundException;
import com.campingmanager.restaurant.dto.CreateBookingRequest;
import com.campingmanager.restaurant.dto.TableBookingDTO;
import com.campingmanager.restaurant.entity.RestaurantTable;
import com.campingmanager.restaurant.entity.TableBookingStatus;
import com.campingmanager.restaurant.repository.RestaurantTableRepository;
import com.campingmanager.restaurant.repository.TableBookingRepository;
import com.campingmanager.restaurant.service.RestaurantService;
import com.campingmanager.users.entity.Ospite;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantTableRepository tableRepository;
    @Mock
    private TableBookingRepository bookingRepository;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private RestaurantService service;

    private RestaurantTable table(int seats) {
        RestaurantTable t = new RestaurantTable();
        t.setId(1L);
        t.setNumber(5);
        t.setSeats(seats);
        return t;
    }

    private CreateBookingRequest request(int people) {
        CreateBookingRequest req = new CreateBookingRequest();
        req.setTableId(1L);
        req.setBookingDate(LocalDate.of(2026, 7, 1));
        req.setBookingTime(LocalTime.of(20, 30));
        req.setNumPeople(people);
        return req;
    }

    @Test
    void shouldThrowWhenTableNotFound() {
        when(tableRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.bookTable(new Ospite(), request(2)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldThrowWhenTooManyPeople() {
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table(2)));

        assertThatThrownBy(() -> service.bookTable(new Ospite(), request(4)))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void shouldThrowWhenTableAlreadyBooked() {
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table(4)));
        when(bookingRepository.existsByTableIdAndBookingDateAndStatus(
                eq(1L), any(), eq(TableBookingStatus.CONFERMATA))).thenReturn(true);

        assertThatThrownBy(() -> service.bookTable(new Ospite(), request(2)))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void shouldBookTable() {
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table(4)));
        lenient().when(bookingRepository.existsByTableIdAndBookingDateAndStatus(anyLong(), any(), any()))
                .thenReturn(false);
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TableBookingDTO dto = service.bookTable(new Ospite(), request(2));

        assertThat(dto.getStatus()).isEqualTo("CONFERMATA");
        assertThat(dto.getNumPeople()).isEqualTo(2);
    }
}
