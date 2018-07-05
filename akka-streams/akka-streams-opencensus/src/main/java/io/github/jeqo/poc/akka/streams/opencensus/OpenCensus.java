package io.github.jeqo.poc.akka.streams.opencensus;

import akka.stream.*;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.GraphDSL;
import akka.stream.stage.GraphStage;
import akka.stream.stage.GraphStageLogic;
import akka.stream.stage.InHandler;
import akka.stream.stage.OutHandler;
import io.opencensus.trace.Span;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.samplers.Samplers;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OpenCensus {

  static <A, B, Mat> Graph<FlowShape<A, B>, Mat> spanFlow(final Flow<A, B, Mat> flow) {


    return GraphDSL.create(flow, (builder, flowShape) -> {
      BidiShape<A, A, B, B> bidiShape = builder.add(new SpanBidiStage<>());

      builder.from(bidiShape.out1()).via(flowShape).toInlet(bidiShape.in2());

      return new FlowShape<>(bidiShape.in1(), bidiShape.out2());
    });
  }

  static class SpanBidiStage<A, B> extends GraphStage<BidiShape<A, A, B, B>> {

    private final Inlet<A> in1 = Inlet.create("SpanBidi.in1");
    private final Inlet<B> in2 = Inlet.create("SpanBidi.in2");
    private final Outlet<A> out1 = Outlet.create("SpanBidi.out1");
    private final Outlet<B> out2 = Outlet.create("SpanBidi.out2");


    @Override
    public GraphStageLogic createLogic(Attributes inheritedAttributes) throws Exception {
      return new GraphStageLogic(shape()) {
        {

          final Tracer tracer = Tracing.getTracer();

          setHandler(in1, new InHandler() {
            @Override
            public void onPush() throws Exception {
              tracer.spanBuilder("monitor")
                  .setRecordEvents(true)
                  .setSampler(Samplers.alwaysSample())
                  .startSpan();
              push(out1, grab(in1));
            }

            @Override
            public void onUpstreamFinish() throws Exception {
//              super.onUpstreamFinish();
            }

            @Override
            public void onUpstreamFailure(Throwable ex) throws Exception {
//              super.onUpstreamFailure(ex);
            }
          });

          setHandler(out1, new OutHandler() {
            @Override
            public void onDownstreamFinish() throws Exception {
//              super.onDownstreamFinish();
            }

            @Override
            public void onPull() throws Exception {
              pull(in1);
            }
          });

          setHandler(in2, new InHandler() {
            @Override
            public void onPush() throws Exception {
              Span currentSpan = tracer.getCurrentSpan();
              currentSpan.end();
              push(out2, grab(in2));
            }

            @Override
            public void onUpstreamFinish() throws Exception {
//              super.onUpstreamFinish();
            }

            @Override
            public void onUpstreamFailure(Throwable ex) throws Exception {
//              super.onUpstreamFailure(ex);
            }
          });

          setHandler(out2, new OutHandler() {
            @Override
            public void onDownstreamFinish() throws Exception {
//              super.onDownstreamFinish();
            }

            @Override
            public void onPull() throws Exception {
              pull(in2);
            }
          });
        }
      };
    }

    @Override
    public BidiShape<A, A, B, B> shape() {
      return new BidiShape<A, A, B, B>(in1, out1, in2, out2);
    }
  }

  static class SpanFlowStage<T> extends GraphStage<FlowShape<T, T>> {

    private final Tracer tracer;

    private final Inlet<T> in = Inlet.create("SpanFanOut.in");
    private final Outlet<T> out = Outlet.create("SpanFanOut.out");

    SpanFlowStage(Tracer tracer) {
      this.tracer = tracer;
    }

    public FlowShape<T, T> shape() {
      return new FlowShape<>(in, out);
    }

    public GraphStageLogic createLogic(Attributes inheritedAttributes) {
      return new GraphStageLogic(shape()) {

        {
          final Tracer tracer = Tracing.getTracer();

          setHandler(in, new InHandler() {
            public void onPush() {
              System.out.println(Thread.currentThread().getId());
              tracer.getCurrentSpan().end();
              push(out, grab(in));
            }

            public void onUpstreamFinish() {
//              super.onUpstreamFinish();
            }

            public void onUpstreamFailure(Throwable ex) {
//              super.onUpstreamFailure(ex);
            }
          });

          setHandler(out, new OutHandler() {
            public void onDownstreamFinish() {
//              super.onDownstreamFinish();
            }

            public void onPull() {
              System.out.println(Thread.currentThread().getId());
//              tracer.getCurrentSpan().end();
              pull(in);
            }
          });
        }

      };
    }
  }
}
