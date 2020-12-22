package ru.mail.polis.homework.concurrency.state;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * ���������������� ��������� ��� ����������. ��������� ��������� � �������� ��������� ��������.
 * ����� �������� ����������������, ����������� � �������� � �����������. � ������ ������ ��������� ������ ���������
 * � ������ ��������� ���������� (������� �������� ��� � ������������ �����)
 *
 * ������������������ ��������� �� ��������� � ��������� ������ ����������:
 * START -> INIT -> RUN -> FINISH
 * �� ��������� FINISH ����� ������� ��� � ��������� INIT ��� � ��������� CLOSE.
 * CLOSE - �������� ���������.
 *
 * ���� �����-���� ����� ���������� ����� �������� � ��������� CLOSE
 * �� ������ �������� ������ (�� �������) � ����� �����.
 * ���� ������ �����, ������� �� ������������� �������� ��������� - �� ����,
 * ���� ��������� �� ������ ���������� ��� ���� (��� ���� ��������� CLOSE, ����� �������� ������ � �����)
 *
 *
 * ���� ��� �������� ������� ���� ������.
 * 1) ����� ������ wait and notify - 5 ������
 * 2) ����� Lock and Condition - 5 ������
 * 3) ����� �������� compareAndSet �� Atomic ������� - 9 ������
 * ����� �� ������ �� �����������, ������� ���������� ���� �� ���� �������. (�� ����, ���� �� ������� 1 ����� �� 4 �����,
 * � 3 ����� �� 3 �����, � �������� ����� ������ 4 �����)
 *
 * Max 8 ������
 */
public class CalculateContainer<T> {
    private final AtomicReference<State> state = new AtomicReference<>(State.START);

    private T result;

    public CalculateContainer(T result) {
        this.result = result;
    }

    /**
     * �������������� ��������� � ��������� ��������� � ��������� INIT (�������� ������ �� ��������� START � FINISH)
     */
    public void init(UnaryOperator<T> initOperator)  {
        do {
            if (state.get() == State.CLOSE) {
                System.out.println("Error in init! State was closed!");
                return;
            }

            if ((state.get() == State.START) || (state.get() == State.FINISH)) {
                T oldResult = result;
                result = initOperator.apply(result);
                if (!(state.compareAndSet(State.START, State.INIT)) && !(state.compareAndSet(State.FINISH, State.INIT))) {
                    result = oldResult;
                } else {
                    return;
                }
            }

        } while(true);

    }

    /**
     * ��������� ��������� � ��������� ��������� � ��������� RUN (�������� ������ �� ��������� INIT)
     */
    public void run(BinaryOperator<T> runOperator, T value)  {
        do {
            if (state.get() == State.CLOSE) {
                System.out.println("Error in run! State was closed!");
                return;
            }

            if (state.get() == State.INIT) {
                T oldResult = result;
                result = runOperator.apply(result, value);
                if (!state.compareAndSet(State.INIT, State.RUN)) {
                    result = oldResult;
                } else {
                    return;
                }
            }

        } while(true);
    }


    /**
     * �������� ��������� ����������� � ��������� ��������� � ��������� FINISH (�������� ������ �� ��������� RUN)
     */
    public void finish(Consumer<T> finishConsumer) {
        do {
            if (state.get() == State.CLOSE) {
                System.out.println("Error in finish! State was closed!");
                return;
            }

            if (state.get() == State.RUN) {
                T oldResult = result;
                if (state.compareAndSet(State.RUN, State.FINISH)) {
                    finishConsumer.accept(oldResult);
                    return;
                }
            }

        } while(true);

    }


    /**
     * ��������� ��������� � �������� ��������� �����������. ��������� ��������� � ��������� CLOSE
     * (�������� ������ �� ��������� FINISH)
     */
    public void close(Consumer<T> closeConsumer) {
        do {
            if (state.get() == State.CLOSE) {
                System.out.println("Error in close! State was closed!");
                return;
            }

            if (state.get() == State.FINISH) {
                T oldResult = result;
                if (state.compareAndSet(State.FINISH, State.CLOSE)) {
                    closeConsumer.accept(oldResult);
                    return;
                }
            }

        } while(true);

    }

    public T getResult() {
        return result;
    }

    public State getState() {
        return state.get();
    }
}