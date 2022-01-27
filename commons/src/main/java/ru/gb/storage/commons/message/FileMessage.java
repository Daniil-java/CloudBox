package ru.gb.storage.commons.message;

public class FileMessage extends Message{
    private byte[] content;

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }
}
