package ktb.domain.seat;


import ktb.domain.product.Product;

public abstract class Seat {
    private final int seatNumber;
    private boolean isInUse;

    public Seat(int seatNumber) {
        this.seatNumber = seatNumber;
        this.isInUse = false;
    }

    public boolean isUse() {
        return this.isInUse;
    }

    public int getSeatNumber() {
        return this.seatNumber;
    }

    public void startUsage() {
        this.isInUse = true;
    }

    public int getUsageFee() {
        return 0; // 관리자 사용 시 요금 0원 발생
    }

    public void stopUsage() {
        this.isInUse = false;
    }

    public abstract void restart();
    public abstract void pauseTemporary();
    public abstract boolean canOrder();
    public abstract void addProduct(Product product);
    public abstract String getOrderSummary();
    public abstract int getOrderSummaryPee();
}
