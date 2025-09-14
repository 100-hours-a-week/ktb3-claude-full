package ktb.domain;

import ktb.constant.ConfigConstant;

public class CustomerSeat extends Seat {
    private long startTime;

    public CustomerSeat(int seatNumber) {
        super(seatNumber);
    }

    @Override
    public void startUsage() {
        super.startUsage();
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public int getUsageFee() {
        long minutes = (System.currentTimeMillis() - startTime) / 1000 / 60;
        return (int) minutes * ConfigConstant.FEE_PER_MINUTE;
    }

    @Override
    public void addProduct(Product product) {
        throw new UnsupportedOperationException("주문 불가 좌석입니다.");
    }

    @Override
    public String getOrderSummary() {
        return "내역 없음";
    }

    @Override
    public boolean canOrder() {
        return false;
    };

    @Override
    public int getOrderSummaryPee() {
        return 0;
    }
}
