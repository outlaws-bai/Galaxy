package org.m2sec.modules.traffic.hook;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;

import java.io.IOException;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class HttpHookRpcServer {
    private Server server;
    private final int port;

    public HttpHookRpcServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        System.err.println("*** running");
        server =
            Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create()).addService(new RpcServiceImpl()).build().start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Use stderr here since the logger may have been reset by its
            // JVM
            // shutdown hook.
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            HttpHookRpcServer.this.stop();
            System.err.println("*** server shut down");
        }));
    }

    private void stop() {
        if (server != null) server.shutdown();
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) server.awaitTermination();
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws Exception {

        // The port on which the server should run
        int port = 8443; // default
        final HttpHookRpcServer server = new HttpHookRpcServer(port);
        server.start();
        server.blockUntilShutdown();
    }
}
