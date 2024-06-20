package com.quicklink.javalinfly;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public final class FileResponse {
    private final String nameFile;
    private final byte[] data;

    public static FileResponse of(@NotNull String nameFile, byte[] data) {
        return new FileResponse(nameFile, data);
    }

    private FileResponse(String nameFile, byte[] data) {
        this.nameFile = nameFile;
        this.data = data;
    }

    public String nameFile() {
        return nameFile;
    }

    public byte[] data() {
        return data;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (FileResponse) obj;
        return Objects.equals(this.nameFile, that.nameFile) &&
                Objects.equals(this.data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameFile, data);
    }

    @Override
    public String toString() {
        return "FileResponse[" +
                "nameFile=" + nameFile + ", " +
                "data=" + data + ']';
    }

}
