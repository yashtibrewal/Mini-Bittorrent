
package uf.cs.cn.utils;
import java.io.IOException;
import java.util.stream.*;
import java.nio.file.*;

/*
    This class is used for providing methods to generate BitField messages

 */
public class BitFieldUtils {
    public static int getNumberOfChunks(int peer_id) throws IOException {
        String currentDirectory = System.getProperty("user.dir");
        Path basePath = Paths.get(currentDirectory, String.valueOf(peer_id));
        Path pattern = Paths.get(String.valueOf(basePath), "piece_*");
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);

        Stream<Path> paths = Files.find(basePath, Integer.MAX_VALUE, (path, f)->pathMatcher.matches(path));
        return (int) paths.count();
    }

}
