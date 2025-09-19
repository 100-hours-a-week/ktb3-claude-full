package ktb.domain.seat;

import ktb.constant.ConfigConstant;
import ktb.domain.product.Product;

public class CustomerSeat extends Seat {
    private long startTime;
    private long usageTime;
    private long restartTime;

    public CustomerSeat(int seatNumber) {
        super(seatNumber);
    }

    @Override
    public void startUsage() {
        super.startUsage();
        this.startTime = System.currentTimeMillis();
        this.usageTime = 0;
        this.restartTime = 0;
    }

    @Override
    public int getUsageFee() {
        setUsageTime();

        long currentUsageTime = this.usageTime / 1_000 / 60;

        return (int)currentUsageTime * ConfigConstant.FEE_PER_MINUTE;
    }

    private void setUsageTime() {
        if (restartTime == 0) {
            usageTime = System.currentTimeMillis() - startTime;
        }

        usageTime += (System.currentTimeMillis() - restartTime);
    }

    @Override
    public void stopUsage() {
        super.stopUsage();

        startTime = 0;
        usageTime = 0;
        this.restartTime = 0;
    }

    @Override
    public void restart() {
        restartTime = System.currentTimeMillis();
    }

    @Override
    public void pauseTemporary() {
        setUsageTime();
        restartTime = 0;
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
