package ktb.domain.seat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import ktb.constant.ConfigConstant;
import ktb.domain.product.Product;

public class BasicSeat extends Seat {
    private final List<Product> products = new ArrayList<>();
    private long startTime;
    private long usageTime;
    private long restartTime;

    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);

    public BasicSeat(int seatNumber) {
        super(seatNumber);
    }

    @Override
    public void startUsage() {
        lock.writeLock().lock();
        try {
            super.startUsage();
            this.startTime = System.currentTimeMillis();
            this.usageTime = 0;
            this.restartTime = startTime;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int getUsageFee() {
        lock.readLock().lock();
        try {
            long totalUsage = usageTime;
            
            if (!isPaused()) {
                totalUsage += (System.currentTimeMillis() - restartTime);
            }
            
            long minutes = totalUsage / 1000 / 60;
            return (int) minutes * ConfigConstant.FEE_PER_MINUTE;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void stopUsage() {
        lock.writeLock().lock();
        try {
            super.stopUsage();
            this.startTime = 0;
            this.usageTime = 0;
            this.restartTime = 0;
            this.products.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void restart() {
        lock.writeLock().lock();
        try {
            super.restartUsage();
            restartTime = System.currentTimeMillis();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void pauseTemporary() {
        lock.writeLock().lock();
        try {
            super.pauseUsage();
            usageTime += (System.currentTimeMillis() - restartTime);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean canOrder() {
        return true;
    }

    @Override
    public void addProduct(Product product) {
        lock.writeLock().lock();

        try {
            products.add(product);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public String getOrderSummary() {
        lock.readLock().lock();
        try {
            if (products.isEmpty()) {
                return "없음";
            }

            return products.stream()
                    .map(Product::toReceipt)
                    .collect(Collectors.joining(", "));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int getOrderSummaryPee() {
        lock.readLock().lock();
        try {
            if (products.isEmpty()) return 0;

            return products.stream()
                    .mapToInt(Product::getTotalPrice)
                    .sum();
        } finally {
            lock.readLock().unlock();
        }
    }
}
