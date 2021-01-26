package graphs.objects;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.IntStream;

public class IntVectorCollector implements Collector<Integer, IntVector, IntVector> {

  public static IntVectorCollector toIntVector() {
    return new IntVectorCollector();
  }

  @Override
  public Supplier<IntVector> supplier() {
    return IntVector::new;
  }

  @Override
  public BiConsumer<IntVector, Integer> accumulator() {
    return (vector, i) -> new IntVector(
        IntStream.range(0, vector.getDimension() + 1)
            .map(j -> j < vector.getDimension() ? vector.get(j) : i).toArray());
  }

  @Override
  public BinaryOperator<IntVector> combiner() {
    return (IntVector::add);
  }

  @Override
  public Function<IntVector, IntVector> finisher() {
    return Function.identity();
  }

  @Override
  public Set<Characteristics> characteristics() {
    return null;
  }

}
