package org.example;

import java.time.LocalDateTime;
import java.util.*;

public class TicketManager {
    private Map<String, Ticket> tickets = new HashMap<>();
    private static final int BOOKING_EXPIRE_HOURS = 2;

    public Ticket bookTicket(Hall hall, int row, int seat, Customer customer) {
        Seat seatObj = hall.getSeat(row, seat);
        String ticketId = UUID.randomUUID().toString();

        synchronized (seatObj) {
            if (!seatObj.isAvailable()) {
                throw new IllegalStateException("Seat is not available");
            }

            seatObj.book(ticketId);
            Ticket ticket = new Ticket(ticketId, hall, seatObj, customer, TicketType.BOOKED);
            tickets.put(ticketId, ticket);
            return ticket;
        }
    }

    public Ticket buyTicket(Hall hall, int row, int seat, Customer customer) {
        Seat seatObj = hall.getSeat(row, seat);
        String ticketId = UUID.randomUUID().toString();

        synchronized (seatObj) {
            if (!seatObj.isAvailable()) {
                throw new IllegalStateException("Seat is not available");
            }

            seatObj.sell(ticketId);
            Ticket ticket = new Ticket(ticketId, hall, seatObj, customer, TicketType.PURCHASED);
            tickets.put(ticketId, ticket);
            return ticket;
        }
    }

    public boolean cancelBooking(String ticketId) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket == null || ticket.getType() != TicketType.BOOKED) {
            return false;
        }

        synchronized (ticket.getSeat()) {
            ticket.getSeat().release();
            tickets.remove(ticketId);
            return true;
        }
    }

    public boolean returnTicket(String ticketId) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket == null || ticket.getType() != TicketType.PURCHASED) {
            return false;
        }

        synchronized (ticket.getSeat()) {
            ticket.getSeat().release();
            tickets.remove(ticketId);
            return true;
        }
    }

    public void checkExpiredBookings() {
        LocalDateTime now = LocalDateTime.now();
        List<String> toRemove = new ArrayList<>();

        for (Ticket ticket : tickets.values()) {
            if (ticket.getType() == TicketType.BOOKED &&
                    ticket.getPurchaseTime().plusHours(BOOKING_EXPIRE_HOURS).isBefore(now)) {
                toRemove.add(ticket.getId());
            }
        }

        for (String ticketId : toRemove) {
            cancelBooking(ticketId);
        }
    }
}
