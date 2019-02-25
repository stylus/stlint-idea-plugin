package tmp;

import com.intellij.openapi.fileTypes.*;
import org.jetbrains.annotations.NotNull;

public class StylusFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(StylusFileType.INSTANCE);
    }
}
