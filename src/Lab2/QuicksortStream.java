package Lab2;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QuicksortStream {

    public static List<Integer> quicksort(List<Integer> dataArray){
        if(dataArray.isEmpty()) return new ArrayList<>();
        return Stream.concat(Stream.concat(
                quicksort(dataArray.parallelStream().skip(1).filter(x->x<dataArray.get(0)).collect(Collectors.toList())).parallelStream(),
                Stream.of(dataArray.get(0))),
                quicksort(dataArray.parallelStream().skip(1).filter(x->x>dataArray.get(0)).collect(Collectors.toList())).parallelStream())
                .collect(Collectors.toList());


    }

}
