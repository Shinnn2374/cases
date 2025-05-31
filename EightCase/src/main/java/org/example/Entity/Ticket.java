package org.example.Entity;

import org.example.dev.Customer;
import org.example.utils.TicketType;

import java.time.LocalDateTime;

public class Ticket {
    private String id;
    private Hall hall;
    private Seat seat;
    private Customer customer;
    private LocalDateTime purchaseTime;
    private TicketType type;

    public Ticket(String id, Hall hall, Seat seat, Customer customer, TicketType type) {
        this.id = id;
        this.hall = hall;
        this.seat = seat;
        this.customer = customer;
        this.purchaseTime = LocalDateTime.now();
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public Hall getHall() {
        return hall;
    }

    public Seat getSeat() {
        return seat;
    }

    public Customer getCustomer() {
        return customer;
    }

    public LocalDateTime getPurchaseTime() {
        return purchaseTime;
    }

    public TicketType getType() {
        return type;
    }
}