package ktb.domain.seat;


import ktb.domain.product.Product;

public abstract class Seat {
    private final int seatNumber;
    private boolean isInUse;
    private boolean isInPaused;

    public Seat(int seatNumber) {
        this.seatNumber = seatNumber;
        this.isInUse = false;
        this.isInPaused = false;
    }

    public boolean isUse() {
        return this.isInUse;
    }
    public boolean isPaused() { return this.isInPaused; }

    public int getSeatNumber() {
        return this.seatNumber;
    }

    public void startUsage() {
        this.isInUse = true;
        this.isInPaused = false;
    }

    public void pauseUsage() { this.isInPaused = true; }

    public void restartUsage() { this.isInPaused = false; }

    public int getUsageFee() {
        return 0; // 관리자 사용 시 요금 0원 발생
    }

    public void stopUsage() {
        this.isInUse = false;
        this.isInPaused = false;
    }

    public String getSeatStatus() {
        if (isInPaused) return "일시 중지 중";
        if (isInUse) return "사용 중";
        return "빈 좌석";
    }

    public abstract void restart();
    public abstract void pauseTemporary();
    public abstract boolean canOrder();
    public abstract void addProduct(Product product);
    public abstract String getOrderSummary();
    public abstract int getOrderSummaryPee();
}
