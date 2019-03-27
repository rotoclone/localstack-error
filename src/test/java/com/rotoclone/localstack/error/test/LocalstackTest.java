package com.rotoclone.localstack.error.test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.amazonaws.services.s3.AmazonS3;

import cloud.localstack.DockerTestUtils;
import cloud.localstack.docker.LocalstackDockerTestRunner;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;

@RunWith(LocalstackDockerTestRunner.class)
@LocalstackDockerProperties(randomizePorts = true, services = { "s3" })
public class LocalstackTest {
    private static final String BUCKET_NAME = "test-bucket";
    private static final int ITERATIONS = 5;
    private static final int TOTAL_THREADS = 20;
    private static final int MAX_THREADS = 20;

    @Test
    public void test() {
        final AmazonS3 s3Client = DockerTestUtils.getClientS3();
        s3Client.createBucket(BUCKET_NAME);

        int errors = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            System.out.println("Iteration " + i);
            if (!doParallelWrites(s3Client)) {
                errors++;
            }
        }

        System.out.println("----------");
        System.out.println(errors + " out of " + ITERATIONS + " iterations failed.");
    }

    private boolean doParallelWrites(final AmazonS3 s3Client) {
        final List<Callable<Boolean>> tasks = new ArrayList<>();
        for (int i = 0; i < TOTAL_THREADS; i++) {
            tasks.add(() -> {
                final String objectKey = "new/" + UUID.randomUUID() + "/file.txt";
                try {
                    s3Client.putObject(BUCKET_NAME, objectKey, "hi im some data");
                    return true;
                } catch (final Exception e) {
                    throw new RuntimeException("An error occurred while writing to " + objectKey, e);
                }
            });
        }

        final CompletionService<Boolean> completionService =
                new ExecutorCompletionService<>(Executors.newFixedThreadPool(MAX_THREADS));
        tasks.forEach(completionService::submit);

        final List<ExecutionException> errors = new ArrayList<>();
        for (int i = 0; i < TOTAL_THREADS; i++) {
            try {
                completionService.take().get();
            } catch (final ExecutionException e) {
                errors.add(e);
            } catch (final InterruptedException e) {
                throw new RuntimeException("Thread was interrupted");
            }
        }
        System.out.println(errors.size() + " out of " + TOTAL_THREADS + " threads failed:");
        errors.forEach(e -> System.out.println(e.getCause().getCause().getMessage()));
        return errors.isEmpty();
    }
}
