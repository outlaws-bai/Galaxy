package org.m2sec.common.rpc;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;

import java.io.IOException;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class RpcServerMain {
    private Server server;
    private final int port;

    public RpcServerMain(int port) {
        this.port = port;
    }

    private void start() throws IOException {
        System.err.println("*** running");
        server =
                Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
                        .addService(new RpcServiceImpl())
                        .build()
                        .start();
        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread(
                                () -> {
                                    // Use stderr here since the logger may have been reset by its
                                    // JVM
                                    // shutdown hook.
                                    System.err.println(
                                            "*** shutting down gRPC server since JVM is shutting down");
                                    RpcServerMain.this.stop();
                                    System.err.println("*** server shut down");
                                }));
    }

    private void stop() {
        if (server != null) server.shutdown();
    }

    /** Await termination on the main thread since the grpc library uses daemon threads. */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) server.awaitTermination();
    }

    /** Main launches the server from the command line. */
    public static void main(String[] args) throws IOException, InterruptedException {

        // The port on which the server should run
        int port = 8443; // default
        if (args.length > 0) port = Integer.parseInt(args[0]);

        final RpcServerMain server = new RpcServerMain(port);
        server.start();
        server.blockUntilShutdown();
    }
}
