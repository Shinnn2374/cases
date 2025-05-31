package org.example;

import org.example.Entity.*;
import org.example.dev.Customer;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Cinema cinema = new Cinema("Star Cinema");
        cinema.addHall(new Hall(1, 5, 10));

        Customer customer = new Customer("123", "Иван Иванов", "ivan@example.com");

        Ticket bookedTicket = cinema.bookTicket(1, 3, 5, customer);
        System.out.println("Забронирован билет: " + bookedTicket.getId());

        Ticket purchasedTicket = cinema.buyTicket(1, 4, 2, customer);
        System.out.println("Куплен билет: " + purchasedTicket.getId());

        Map<Hall, List<Seat>> availableSeats = cinema.getAvailableSeats();
        System.out.println("Свободных мест: " +
                availableSeats.values().stream().mapToInt(List::size).sum());

        cinema.cancelBooking(bookedTicket.getId());
        System.out.println("Бронирование отменено");
    }
}