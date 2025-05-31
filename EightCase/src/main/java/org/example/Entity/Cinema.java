package org.example.Entity;

import org.example.dev.Customer;
import org.example.dev.TicketManager;

import java.util.*;

public class Cinema {
    private String name;
    private List<Hall> halls;
    private TicketManager ticketManager;

    public Cinema(String name) {
        this.name = name;
        this.halls = new ArrayList<>();
        this.ticketManager = new TicketManager();
    }

    public void addHall(Hall hall) {
        halls.add(hall);
    }

    public Ticket bookTicket(int hallNumber, int row, int seat, Customer customer) {
        Hall hall = getHall(hallNumber);
        if (hall == null) throw new IllegalArgumentException("Hall not found");
        return ticketManager.bookTicket(hall, row, seat, customer);
    }

    public Ticket buyTicket(int hallNumber, int row, int seat, Customer customer) {
        Hall hall = getHall(hallNumber);
        if (hall == null) throw new IllegalArgumentException("Hall not found");
        return ticketManager.buyTicket(hall, row, seat, customer);
    }

    public boolean cancelBooking(String ticketId) {
        return ticketManager.cancelBooking(ticketId);
    }

    public boolean returnTicket(String ticketId) {
        return ticketManager.returnTicket(ticketId);
    }

    public Map<Hall, List<Seat>> getAvailableSeats() {
        Map<Hall, List<Seat>> availableSeats = new HashMap<>();
        for (Hall hall : halls) {
            availableSeats.put(hall, hall.getAvailableSeats());
        }
        return availableSeats;
    }

    private Hall getHall(int hallNumber) {
        return halls.stream()
                .filter(h -> h.getNumber() == hallNumber)
                .findFirst()
                .orElse(null);
    }
}