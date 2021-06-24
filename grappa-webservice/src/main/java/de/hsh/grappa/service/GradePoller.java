package de.hsh.grappa.service;

import de.hsh.grappa.application.GrappaServlet;
import de.hsh.grappa.cache.RedisController;
import de.hsh.grappa.exceptions.GrappaException;
import de.hsh.grappa.proforma.ResponseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeoutException;


/**
 * This is a fairly primitive grading result poller.
 * It blocks for a specified amount of time (eg a total of 10 minutes)
 * while periodically polling for a Proforma response result from
 * cache.
 *
 * One improvement might take using the estimated remaining grading
 * seconds into consideration.
 */
public class GradePoller {
    private static final Logger log = LoggerFactory.getLogger(GradePoller.class);
    private String gradeProcId;
    private Thread t;
    private GrappaException exOccurredWhenWaiting= null;
    private ResponseResource respBlob = null;

    private final long intervalPoll = 2000;

    public GradePoller(String gradeProcId) {
        this.gradeProcId = gradeProcId;
        t = new Thread() {
            @Override
            public void run() {
                synchronized (GradePoller.this) {
                    while(!Thread.currentThread().isInterrupted()) {
                        log.debug("[GradeProcId: '{}']: polling...", gradeProcId);
                        try {
                            respBlob = RedisController.getInstance().getResponse(gradeProcId);
                        } catch (GrappaException e1) {
                            exOccurredWhenWaiting= e1;
                            break;
                        }
                        if(null != respBlob)
                            break;
                        try {
                            Thread.sleep(intervalPoll);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    };
                }
            }
        };
    }

    public ResponseResource poll() throws Exception {
        t.start();
        try {
            t.join(GrappaServlet.CONFIG.getService().getSynchronous_submission_timeout_seconds() * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new GrappaException("Waiting for response interrupted.");
        }
        finally {
            t.interrupt();
        }

        synchronized (this) {
            if(null != respBlob) {
                log.debug("[GradeProcId: '{}']: Response received. Returning.", gradeProcId);
                return respBlob;
            }
            if (null != exOccurredWhenWaiting) {
                throw exOccurredWhenWaiting;
            }
        }

        throw new TimeoutException(String.format("Waiting for grading result ('%s') timed out after %d seconds.",
           gradeProcId, GrappaServlet.CONFIG.getService().getSynchronous_submission_timeout_seconds()));
    }
}
