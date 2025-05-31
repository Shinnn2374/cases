package org.example.Entity;

import java.util.ArrayList;
import java.util.List;

public class Hall {
    private int number;
    private int rows;
    private int seatsPerRow;
    private Seat[][] seats;

    public Hall(int number, int rows, int seatsPerRow) {
        this.number = number;
        this.rows = rows;
        this.seatsPerRow = seatsPerRow;
        initializeSeats();
    }

    private void initializeSeats() {
        seats = new Seat[rows][seatsPerRow];
        for (int row = 0; row < rows; row++) {
            for (int seat = 0; seat < seatsPerRow; seat++) {
                seats[row][seat] = new Seat(row + 1, seat + 1);
            }
        }
    }

    public Seat getSeat(int row, int seat) {
        if (row < 1 || row > rows || seat < 1 || seat > seatsPerRow) {
            throw new IllegalArgumentException("Invalid seat coordinates");
        }
        return seats[row - 1][seat - 1];
    }

    public List<Seat> getAvailableSeats() {
        List<Seat> available = new ArrayList<>();
        for (Seat[] row : seats) {
            for (Seat seat : row) {
                if (seat.isAvailable()) {
                    available.add(seat);
                }
            }
        }
        return available;
    }

    public int getNumber() {
        return number;
    }
}