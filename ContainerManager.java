package ru.mail.polis.homework.concurrency.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;


/**
 * �����, ������� ��������� ������������ � ����� �� ����������������, ���������, ������������ � ���������.
 * ������ ��� ��� �����������, �� ���� ����� �������������� �� ������ �������.
 *
 * ��� ������� �� 2 ����� �� ������ �����. ����������� �� 1 ����
 * Max 11 ������
 */
public class ContainerManager {

    private final List<CalculateContainer<Double>> calculateContainers = new ArrayList<>();

    private final ExecutorService initExecutor = Executors.newCachedThreadPool();
    private final ExecutorService runExecutor = Executors.newFixedThreadPool(2);
    private final ExecutorService finishExecutor = Executors.newFixedThreadPool(2);
    private final ExecutorService closeExecutor = Executors.newSingleThreadExecutor();
    private final CountDownLatch countDownLatch;
    private static final Random random = new Random();

    /**
     * �������� ������ �� �������� �����������
     */
    public ContainerManager(int containersCount) {
        for (int i = 0; i < containersCount; i++) {
            this.calculateContainers.add(new CalculateContainer<>(random.nextDouble()));
        }
        this.countDownLatch = new CountDownLatch(containersCount);
    }


    /**
     * ����������� executor c ����������� ����������� �������,
     * ������� ����� ������������ ��� ����������, �����-������ �������������� ��������� 1_000_000 ���.
     * (��� ����� ����������� ��������������� ����� operation)
     *
     * ������ ��������� ���� ��������� ��������.
     */
    public void initContainers() {
        for (CalculateContainer<Double> calculateContainer : calculateContainers) {
                initExecutor.execute(() -> calculateContainer.init(operation(Math::cos)));

        }

    }


    /**
     * ����������� executor c 2 �������� (����� � ��������� finish),
     * ������� ����� ��������� ��� ���������� �����-������ �������������� ��������� 1_000_000 ���
     * (��� ����� ����������� ��������������� ����� operation)
     *
     * ������ ��������� ���� ��������� ��������.
     */
    public void runContainers() {
        for (CalculateContainer<Double> calculateContainer : calculateContainers) {
                runExecutor.execute(() -> calculateContainer.run(operation(Math::hypot), random.nextDouble()));
        }
    }


    /**
     * ����������� executor c 2 �������� (����� � ��������� run), ������� ����� ���������
     * ������� �� ����������� � �������� �� � ��������������� ������� �� ����������� ���������
     *
     * ������ ��������� ���� ��������� ��������.
     */
    public void finishContainers() {
        for (CalculateContainer<Double> calculateContainer : calculateContainers) {
            System.out.print("Result of container is ");
            finishExecutor.execute(() -> calculateContainer.finish(System.out::println));
            System.out.println();
        }
    }


    /**
     * ����������� executor c 1 �������, ������� ����� ��������� ������� �� �����������
     * � �������� �� � ��������������� ������� � ��������.
     *
     * ������ ��������� ���� ��������� ��������.
     *
     * ��� ��� ���� ����� ��������� ��������� � �������� ���������,
     * �� ����� �������� ��������� �������������, ������� ��������������,
     * ��� ������ ��������� ��� 10 ����������
     */
    public void closeContainers() throws BrokenBarrierException, InterruptedException {
        for (CalculateContainer<Double> calculateContainer : calculateContainers) {
            closeExecutor.execute(() -> calculateContainer.close(x -> {
                countDownLatch.countDown();
                System.out.println("Container is closed. Value is " + x);
            }));
            System.out.println();
        }
        countDownLatch.await();
    }

    /**
     * ���� ����� ������ �����, ���� ��� ���������� �� ��������� ��� ���� �� ���������� �����.
     * ���� ����� �����, �� ����� ������ ������� false, ����� true.
     *
     * ����� ��� ������ ��������, ������� ����������� � Java ����� ����� � ���������.
     * ������, ��� ����� ���������� � ������������.
     */
    public boolean await(long timeoutMillis) throws Exception {
        return countDownLatch.await(timeoutMillis, TimeUnit.MILLISECONDS);

    }

    public List<CalculateContainer<Double>> getCalculateContainers() {
        return calculateContainers;
    }

    private  <T> UnaryOperator<T> operation(UnaryOperator<T> operator) {
        return param -> {
            T result = param;

            for (int i = 0; i < 1000; i++) {
                result = operator.apply(result);
            }
            return result;
        };
    }

    private <T> BinaryOperator<T> operation(BinaryOperator<T> operator) {
        return (start, delta) -> {
            T result = start;
            for (int i = 0; i < 1000; i++) {
                result = operator.apply(result, delta);
            }
            return result;
        };
    }

}