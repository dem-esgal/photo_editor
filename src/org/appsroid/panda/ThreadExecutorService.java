package org.appsroid.panda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.google.common.base.Function;

import android.util.Pair;

public class ThreadExecutorService {

	private static final Logger logger = Logger.getLogger(ThreadExecutorService.class);

	private static final int DEFAULT_POOL_SIZE = 8;

	/**
	 * ������� ���������� �������� ��� ��������� � ������������� ������
	 *
	 * @param objects       ������ �������� ��� ���������� ��������
	 * @param applyFunction ������� ���������� ��������, ����������� ������ �������� � ���������� �����
	 * @param <T>           ��� �������
	 * @param <R>           ��� ������������� � ���������� �������� ��������
	 * @return ������ ����������� ���������� �������� ��� ������� ������
	 */
	public <T, R> List<R> invoke(List<T> objects, Function<List<T>, Callable<R>> applyFunction) {
		List<R> result = Collections.emptyList();

		if ((objects != null) && !objects.isEmpty() && (applyFunction != null)) {
			final int objectsSize = objects.size();
			final int poolSize = Math.min(getThreadPoolSize(), objectsSize);
			final int groupSize = objectsSize / poolSize;
			final int extraSize = objectsSize % poolSize;

			List<Callable<R>> tasks = new ArrayList<>();
			for (int i = 0, fromIndex = 0, toIndex, extra; i < poolSize; i++, fromIndex = toIndex) {
				extra = (i < extraSize) ? 1 : 0;
				toIndex = fromIndex + groupSize + extra;

				tasks.add(applyFunction.apply(objects.subList(fromIndex, toIndex)));
			}

			try {
				ExecutorService executor = Executors.newFixedThreadPool(poolSize);
				List<Future<R>> futures = executor.invokeAll(tasks);

				result = new ArrayList<>();
				for (Future<R> future : futures) {
					result.add(future.get());
				}
				executor.shutdown();

			} catch (InterruptedException e) {
				logger.error("Interrupted exception occurred while invoking");
			} catch (ExecutionException e) {
				logger.error("Execution exception occurred while invoking");
			}
		}
		return result;
	}

	/**
	 * ������� ���������� �������� ��� ������������������ ��������� � ������������� ������
	 *
	 * @param parametrizedObjects ������ �������� ��� ���������� �������� � ��������, ���������� � ���
	 * @param applyFunction       ������� ���������� ��������, ����������� ������ �������� � ���������� �����
	 * @param <T>                 ��� �������
	 * @param <P>                 ��� ���������
	 * @param <R>                 ��� ������������� � ���������� �������� ��������
	 * @return ������ ����������� ���������� �������� ��� ������� ������
	 */
	public <T, P, R> List<R> invoke(Pair<List<T>, P> parametrizedObjects, Function<Pair<List<T>, P>, Callable<R>> applyFunction) {
		List<R> result = Collections.emptyList();
		List<T> objects = parametrizedObjects.first;

		if ((objects != null) && !objects.isEmpty() && (applyFunction != null)) {
			final int objectsSize = objects.size();
			final int poolSize = Math.min(getThreadPoolSize(), objectsSize);
			final int groupSize = objectsSize / poolSize;
			final int extraSize = objectsSize % poolSize;

			List<Callable<R>> tasks = new ArrayList<>();
			for (int i = 0, fromIndex = 0, toIndex, extra; i < poolSize; i++, fromIndex = toIndex) {
				extra = (i < extraSize) ? 1 : 0;
				toIndex = fromIndex + groupSize + extra;

				tasks.add(applyFunction.apply(new Pair<>(objects.subList(fromIndex, toIndex), parametrizedObjects.second)));
			}

			try {
				ExecutorService executor = Executors.newFixedThreadPool(poolSize);
				List<Future<R>> futures = executor.invokeAll(tasks);

				result = new ArrayList<>();
				for (Future<R> future : futures) {
					result.add(future.get());
				}
				executor.shutdown();

			} catch (InterruptedException e) {
				logger.error("Interrupted exception occurred while invoking");
			} catch (ExecutionException e) {
				logger.error("Execution exception occurred while invoking");
			}
		}
		return result;
	}

	private int getThreadPoolSize() {
		int poolSize = DEFAULT_POOL_SIZE;
		return poolSize;
	}

}

