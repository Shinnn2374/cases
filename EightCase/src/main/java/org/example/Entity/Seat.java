package org.example.Entity;

import org.example.utils.SeatStatus;

public class Seat {
    private int row;
    private int number;
    private SeatStatus status;
    private String ticketId;

    public Seat(int row, int number) {
        this.row = row;
        this.number = number;
        this.status = SeatStatus.AVAILABLE;
    }

    public boolean isAvailable() {
        return status == SeatStatus.AVAILABLE;
    }

    public void book(String ticketId) {
        if (!isAvailable()) throw new IllegalStateException("Seat is not available");
        this.status = SeatStatus.BOOKED;
        this.ticketId = ticketId;
    }

    public void sell(String ticketId) {
        if (!isAvailable()) throw new IllegalStateException("Seat is not available");
        this.status = SeatStatus.SOLD;
        this.ticketId = ticketId;
    }

    public void release() {
        this.status = SeatStatus.AVAILABLE;
        this.ticketId = null;
    }

    public int getRow() {
        return row;
    }

    public int getNumber() {
        return number;
    }

    public SeatStatus getStatus() {
        return status;
    }
}